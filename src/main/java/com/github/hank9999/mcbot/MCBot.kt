package com.github.hank9999.mcbot

import com.github.hank9999.kook.Bot
import com.github.hank9999.kook.http.HttpApi
import com.github.hank9999.kook.http.KookApi
import com.github.hank9999.mcbot.bot.Commands
import com.github.hank9999.mcbot.connector.WebSocketServer
import com.github.hank9999.mcbot.task.CacheTask
import com.github.hank9999.mcbot.utils.LoggerLevels
import io.javalin.util.JavalinLogger
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.config.Configurator
import org.fusesource.jansi.AnsiConsole
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.system.exitProcess


object MCBot {

    private val logger: Logger = LoggerFactory.getLogger(MCBot::class.java)
    lateinit var kookApi: KookApi
    lateinit var httpApi: HttpApi
    val connector = WebSocketServer()

    @JvmStatic
    fun main(args: Array<String>) {
        AnsiConsole.systemInstall()
        logger.info(
            """
            |
            |    __  ___  ______  __               __       ___
            |   /  |/  / / ____/ / /_    ____    _/ /_     |__ \
            |  / /|_/ / / /     / __ \  / __ \ /__ __/     __/ /
            | / /  / / / /___  / /_/ / / /_/ /  / /_      / __/
            |/_/  /_/  \____/ /_.___/  \____/   \__/     /____/
            |
            """.trimMargin()
        )
        if (!Config.checkExists()) {
            logger.error("未找到配置文件")
            logger.info("已生成配置文件，请配置后再启动程序")
            exitProcess(1)
        }
        Config.setValue()
        Config.checkConfig()

        Configurator.setAllLevels(LogManager.getRootLogger().name, LoggerLevels.fromString(Config.LoggerLevel.main!!))
        Configurator.setAllLevels("org.eclipse.jetty", LoggerLevels.fromString(Config.LoggerLevel.jetty!!))
        Configurator.setAllLevels("org.mongodb.driver", LoggerLevels.fromString(Config.LoggerLevel.mongodb!!))

        JavalinLogger.startupInfo = false
        val javalin = connector.initialize(Config.Ws.host!!, Config.Ws.port!!, Config.Ws.path!!)
        val bot = if (Config.Bot.WebHook.port == Config.Ws.port && Config.Bot.WebHook.host == Config.Ws.host) {
            Bot(
                com.github.hank9999.kook.Config(
                    token = Config.Bot.token!!,
                    cmd_prefix = Config.Bot.cmd_prefix!!,
                    verify_token = Config.Bot.verify_token!!,
                    path = Config.Bot.WebHook.path!!
                ),
                javalinApp = javalin
            )
        } else {
            Bot(com.github.hank9999.kook.Config(
                token = Config.Bot.token!!,
                cmd_prefix = Config.Bot.cmd_prefix!!,
                verify_token = Config.Bot.verify_token!!,
                host = Config.Bot.WebHook.host!!,
                port = Config.Bot.WebHook.port!!,
                path = Config.Bot.WebHook.path!!
            ))
        }
        httpApi = bot.httpApi
        kookApi = bot.kookApi
        bot.registerClass(Commands())
        connector.initWsHandler(javalin, Config.Ws.path!!)
        // 每 5 分钟检查一次 Cache, 清除过期缓存
        Timer().schedule(CacheTask(), Date(), 5 * 60 * 1000)
    }
}
