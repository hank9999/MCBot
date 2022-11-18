package com.github.hank9999.mcbot.connector

import com.github.hank9999.kook.utils.NamedThreadFactory
import com.github.hank9999.mcbot.database.DBRead
import io.javalin.Javalin
import io.javalin.http.Context
import io.javalin.websocket.WsContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress
import java.util.concurrent.Executors

class WebSocketServer {
    private val logger: Logger = LoggerFactory.getLogger(WebSocketServer::class.java)
    private val ctxMap: MutableMap<String, MutableList<Ctx>> = mutableMapOf()
    val handler = Handler()
    private val coroutineScope = CoroutineScope(Executors.newSingleThreadExecutor(NamedThreadFactory("ConnectorServer")).asCoroutineDispatcher())

    fun getCtxMap(): MutableMap<String, MutableList<Ctx>> {
        return ctxMap
    }

    fun initialize(host: String, port: Int, path: String): Javalin {
        val app = Javalin.create { config ->
            config.showJavalinBanner = false
            config.jetty.wsFactoryConfig {
                it.maxTextMessageSize = 262144
            }
            config.accessManager { handler, ctx, _ ->
                val remoteIp = ctx.remoteIp()
                val ctxPath = ctx.path()
                logger.debug("[WebServer] HTTP Connection, remoteIp: $remoteIp, path: $ctxPath")
                if (ctxPath != path) {
                    handler.handle(ctx)
                    return@accessManager
                }
                val tokenSting = ctx.queryParam("token")
                if (tokenSting == null) {
                    logger.info("[Connector] $remoteIp missing token")
                    ctx.status(401).result("missing token")
                    return@accessManager
                }
                val name = ctx.queryParam("name")
                if (name == null) {
                    logger.info("[Connector] $remoteIp missing name")
                    ctx.status(401).result("missing name")
                    return@accessManager
                }
                handler.handle(ctx)
            }
        }.start(host, port)
        return app
    }

    fun initWsHandler(app: Javalin, path: String) {
        app.ws(path) { ws ->
            ws.onConnect { ctx ->
                coroutineScope.launch {
                    val remoteIp = ctx.remoteAddress()
                    val tokenSting = ctx.queryParam("token")
                    if (tokenSting == null) {
                        logger.info("[Connector] $remoteIp missing token")
                        ctx.closeSession(1000, "missing token")
                        return@launch
                    }
                    val name = ctx.queryParam("name")
                    if (name == null) {
                        logger.info("[Connector] $remoteIp missing name")
                        ctx.closeSession(1000, "missing name")
                        return@launch
                    }
                    val token = DBRead.getToken(tokenSting)
                    if (token == null) {
                        logger.info("[Connector] $remoteIp invalid token, $tokenSting, $name")
                        ctx.closeSession(1000, "invalid token")
                        return@launch
                    }
                    if (!ctxMap.containsKey(tokenSting)) {
                        ctxMap[tokenSting] = mutableListOf()
                    }
                    val ctxObj = Ctx(name, ctx)
                    val repeatedObj: MutableList<Ctx> = mutableListOf()
                    ctxMap[tokenSting]!!.forEach {
                        if (it.name == name) {
                            repeatedObj.add(it)
                        }
                    }
                    repeatedObj.forEach {
                        ctxMap[tokenSting]!!.remove(it)
                    }
                    ctxMap[tokenSting]!!.add(ctxObj)
                    logger.info("[Connector] $remoteIp connected, token: $tokenSting, name: $name, sessionId: ${ctx.sessionId}")
                }
            }
            ws.onMessage { ctx ->
                coroutineScope.launch {
                    val message = ctx.message()
                    val name = ctx.queryParam("name")!!
                    val tokenSting = ctx.queryParam("token")!!
                    logger.debug("[Connector] Received connector ws message, sessionId: ${ctx.sessionId}, token: $tokenSting, name: $name, message: $message")
                    val token = DBRead.getToken(tokenSting)
                    if (token == null) {
                        logger.info("[Connector] ${ctx.remoteAddress()} invalid token, $tokenSting, $name")
                        ctx.closeSession(1000, "invalid token")
                        return@launch
                    }
                    handler.handle(message, token, name)
                }
            }
            ws.onError { err ->
                val error = err.error()
                if (error != null) {
                    if (error.cause.toString().indexOf("Connection reset by peer", ignoreCase = true) != -1) {
                        logger.error("[Connector] ${err.remoteAddress()} connection error, unexpected reset by remote, sessionId: ${err.sessionId}")
                    } else {
                        logger.error("[Connector] ${err.remoteAddress()} connection error, sessionId: ${err.sessionId}\n${error.stackTraceToString()}")
                    }
                }
            }
            ws.onClose { ctx ->
                logger.info("[Connector] ${ctx.remoteAddress()} disconnected, reason: ${ctx.reason()}, sessionId: ${ctx.sessionId}")
                val needRemove: MutableMap<String, Ctx> = mutableMapOf()
                ctxMap.forEach { token ->
                    token.value.forEach { ctxMapObj ->
                        if (ctxMapObj.ctx.sessionId == ctx.sessionId) {
                            needRemove[token.key] = ctxMapObj
                        }
                    }
                }
                needRemove.forEach {
                    if (ctxMap.containsKey(it.key)) {
                        ctxMap[it.key]!!.remove(it.value)
                    }
                }
            }
        }
    }
}

private fun WsContext.remoteAddress(): String {
    val xForwardedFor = header("X-Forwarded-For")
    return if (xForwardedFor == null) {
        (session.remoteAddress as InetSocketAddress).address.hostAddress
    } else {
        if (xForwardedFor.indexOf(",") != -1) {
            xForwardedFor.substring(xForwardedFor.lastIndexOf(",") + 2)
        } else {
            xForwardedFor
        }
    }
}

private fun Context.remoteIp(): String {
    val xForwardedFor = req().getHeader("X-Forwarded-For")
    val remoteIp = if (xForwardedFor == null) {
        req().remoteAddr
    } else {
        if (xForwardedFor.indexOf(",") != -1) {
            xForwardedFor.substring(xForwardedFor.lastIndexOf(",") + 2)
        } else {
            xForwardedFor
        }
    }
    return remoteIp
}
