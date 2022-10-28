package com.github.hank9999.mcbot.connector

import com.github.hank9999.kook.json.JSON.Companion.json
import com.github.hank9999.kook.types.types.MessageTypes
import com.github.hank9999.kook.utils.NamedThreadFactory
import com.github.hank9999.mcbot.MCBot.kookApi
import com.github.hank9999.mcbot.bot.Command.Companion.receiveCommand
import com.github.hank9999.mcbot.bot.Status.Companion.receiveStatus
import com.github.hank9999.mcbot.bot.Utils.Companion.escape
import com.github.hank9999.mcbot.connector.types.Receive
import com.github.hank9999.mcbot.database.types.Token
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import java.util.regex.Pattern

class Handler {
    private val logger: Logger = LoggerFactory.getLogger(Handler::class.java)
    private val coroutineScope = CoroutineScope(Executors.newSingleThreadExecutor(NamedThreadFactory("ConnectorHandler")).asCoroutineDispatcher())
    private val strangeCharsPatten = Pattern.compile("(?i)\u007f[0-9A-Z]")
    var sn = AtomicInteger(0)

    fun handle(msg: String, token: Token, name: String) {
        try {
            val data = json.decodeFromString<Receive>(msg)
            when (data.type) {
                "log" -> sendLog(data, token, name)
                "Chat" -> sendChat(data, token, name)
                "PlayerCommand" -> sendPlayerCommand(data, token, name)
                "Login" -> sendLogin(data, token, name)
                "Logout" -> sendLogout(data, token, name)
                "RconCommand" -> sendRconCommand(data, token, name)
                "status" -> receiveStatus(data, token, name)
                "command" -> receiveCommand(data, token, name)
            }
        } catch (ex: Exception) {
            logger.error(ex.stackTraceToString())
        }
    }

    private fun sendLog(data: Receive, token: Token, name: String) {
        var log = ""
        data.log!!.split("\n").forEach {
            val text = it.trim()
            if (text.isNotEmpty()) {
                log += "[$name] $text\n"
            }
        }
        log = strangeCharsPatten.matcher(log).replaceAll("")
        log = log.escape()
        val channelId = when (token.log) {
            "-1" -> return
            "0" -> token.mainChannel
            else -> token.log
        }
        coroutineScope.launch {
            try {
                kookApi.Message().create(channelId, log, MessageTypes.KMD)
            } catch (ex: Exception) {
                logger.error(ex.stackTraceToString())
                kookApi.Message().create(channelId, "**${name.escape()}**\n发送消息时发生错误, 请联系维护\n${ex.stackTraceToString()}", MessageTypes.KMD)
            }
        }
    }

    private fun sendChat(data: Receive, token: Token, name: String) {
        val username = data.username!!
        val chat = data.text!!.escape()
        val channelId = when (token.chat) {
            "-1" -> return
            "0" -> token.mainChannel
            else -> token.chat
        }
        coroutineScope.launch {
            try {
                kookApi.Message().create(channelId, "\\[${name.escape()}\\] <$username> $chat", MessageTypes.KMD)
            } catch (ex: Exception) {
                logger.error(ex.stackTraceToString())
                kookApi.Message().create(channelId, "**${name.escape()}**\n发送消息时发生错误, 请联系维护\n${ex.stackTraceToString()}", MessageTypes.KMD)
            }
        }
    }

    private fun sendPlayerCommand(data: Receive, token: Token, name: String) {
        val username = data.username!!
        val command = data.command!!.escape()
        val channelId = when (token.playerCommand) {
            "-1" -> return
            "0" -> token.mainChannel
            else -> token.playerCommand
        }
        coroutineScope.launch {
            try {
                kookApi.Message().create(channelId, "行为: 玩家执行指令\n服务器: ${name.escape()}\nID: $username\n$command", MessageTypes.KMD)
            } catch (ex: Exception) {
                logger.error(ex.stackTraceToString())
                kookApi.Message().create(channelId, "**${name.escape()}**\n发送消息时发生错误, 请联系维护\n${ex.stackTraceToString()}", MessageTypes.KMD)
            }
        }
    }

    private fun sendRconCommand(data: Receive, token: Token, name: String) {
        val command = data.command!!.escape()
        val channelId = when (token.rconCommand) {
            "-1" -> return
            "0" -> token.mainChannel
            else -> token.rconCommand
        }
        coroutineScope.launch {
            try {
                kookApi.Message().create(channelId, "行为: Rcon执行指令\n服务器: ${name.escape()}\n$command", MessageTypes.KMD)
            } catch (ex: Exception) {
                logger.error(ex.stackTraceToString())
                kookApi.Message().create(channelId, "**${name.escape()}**\n发送消息时发生错误, 请联系维护\n${ex.stackTraceToString()}", MessageTypes.KMD)
            }
        }
    }

    private fun sendLogin(data: Receive, token: Token, name: String) {
        val username = data.username!!
        val channelId = when (token.login) {
            "-1" -> return
            "0" -> token.mainChannel
            else -> token.login
        }
        coroutineScope.launch {
            try {
                kookApi.Message().create(channelId, "玩家 $username **登入** 服务器 ${name.escape()}", MessageTypes.KMD)
            } catch (ex: Exception) {
                logger.error(ex.stackTraceToString())
                kookApi.Message().create(channelId, "**${name.escape()}**\n发送消息时发生错误, 请联系维护\n${ex.stackTraceToString()}", MessageTypes.KMD)
            }
        }
    }

    private fun sendLogout(data: Receive, token: Token, name: String) {
        val username = data.username!!
        val channelId = when (token.logout) {
            "-1" -> return
            "0" -> token.mainChannel
            else -> token.logout
        }
        coroutineScope.launch {
            try {
                kookApi.Message().create(channelId, "玩家 $username **退出** 服务器 ${name.escape()}", MessageTypes.KMD)
            } catch (ex: Exception) {
                logger.error(ex.stackTraceToString())
                kookApi.Message().create(channelId, "**${name.escape()}**\n发送消息时发生错误, 请联系维护\n${ex.stackTraceToString()}", MessageTypes.KMD)
            }
        }
    }
}
