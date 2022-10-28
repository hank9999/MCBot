package com.github.hank9999.mcbot

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.spongepowered.configurate.CommentedConfigurationNode
import org.spongepowered.configurate.hocon.HoconConfigurationLoader
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess


class Config {

    object Bot {
        var token: String? = null
        var verify_token: String? = null
        var cmd_prefix: List<String>? =  listOf(".", "。", "/")
        object WebHook {
            var host: String? = ""
            var port: Int? = 3000
            var path: String? = "/webhook"
        }
    }
    object Ws {
        var host: String? = "localhost"
        var port: Int? = 3001
        var path: String? = "/ws"
    }
    object DataBase {
        var host: String? = "localhost"
        var port: Int? = 27017
        var user: String? = null
        var password: String? = null
        var database: String? = "mcbot"
    }

    object LoggerLevel {
        var main: String? = "INFO"
        var jetty: String? = "INFO"
        var mongodb: String? = "INFO"
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(Config::class.java)
        private const val configFile = "config.conf"

        fun checkExists(): Boolean {
            val file = File(configFile)
            if (file.exists()) {
                return true
            } else {
                val inputStream: InputStream = Config::class.java.getResourceAsStream("/$configFile")!!
                try {
                    Files.copy(inputStream, Paths.get(configFile))
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                    logger.error("配置文件错误: 复制配置文件时未找到程序内文件")
                    exitProcess(1)
                } catch (e: IOException) {
                    e.printStackTrace()
                    logger.error("配置文件错误: 复制配置文件时IO错误")
                    exitProcess(1)
                }
            }
            return false
        }

        private fun checkBotConfig(): Boolean {
            if (Bot.token == null || Bot.token!!.isEmpty()) {
                logger.error("配置文件错误: bot.token 不存在或为空")
                return false
            } else if (Bot.cmd_prefix == null || Bot.cmd_prefix!!.isEmpty()) {
                logger.error("配置文件错误: bot.cmd_prefix 不存在或为空")
                return false
            }
            if (Bot.WebHook.host != null && Bot.WebHook.host!!.isNotEmpty()) {
                if (Bot.verify_token == null || Bot.verify_token!!.isEmpty()) {
                    logger.error("配置文件错误: bot.verify_token 不存在或为空")
                    return false
                } else if (Bot.WebHook.port == null || (Bot.WebHook.port!! < 0 || Bot.WebHook.port!! > 65535)) {
                    logger.error("配置文件错误: bot.webhook.port 不存在或不合法")
                    return false
                } else if (Bot.WebHook.path == null || Bot.WebHook.path!!.isEmpty()) {
                    logger.error("配置文件错误: bot.webhook.path 不存在或为空")
                    return false
                }
            }
            return true
        }

        private fun checkWsConfig(): Boolean {
            if (Ws.host == null || Ws.host!!.isEmpty()) {
                logger.error("配置文件错误: ws.host 不存在或为空")
                return false
            } else if (Ws.port == null || (Ws.port!! < 0 || Ws.port!! > 65535)) {
                logger.error("配置文件错误: ws.port 不存在或不合法")
                return false
            } else if (Ws.path == null || Ws.path!!.isEmpty()) {
                logger.error("配置文件错误: ws.path 不存在或为空")
                return false
            }
            return true
        }

        private fun checkDataBaseConfig(): Boolean {
            if (DataBase.host == null || DataBase.host!!.isEmpty()) {
                logger.error("配置文件错误: database.mysql.host 不存在或为空")
                return false
            } else if (DataBase.port == null || (DataBase.port!! < 0 || DataBase.port!! > 65535)) {
                logger.error("配置文件错误: database.mysql.port 不存在或不合法")
                return false
            } else if (DataBase.user == null) {
                logger.error("配置文件错误: database.mysql.user 不存在")
                return false
            } else if (DataBase.password == null) {
                logger.error("配置文件错误: database.mysql.password 不存在")
                return false
            } else if (DataBase.database == null || DataBase.database!!.isEmpty()) {
                logger.error("配置文件错误: database.mysql.database 不存在或为空")
                return false
            }
            return true
        }

        private fun checkLoggerLevel(): Boolean {
            if (LoggerLevel.main == null || LoggerLevel.main!!.isEmpty()) {
                logger.error("配置文件错误: loggerLevel.main 不存在或为空")
                return false
            } else if (LoggerLevel.jetty == null || LoggerLevel.jetty!!.isEmpty()) {
                logger.error("配置文件错误: loggerLevel.main 不存在或为空")
                return false
            } else if (LoggerLevel.mongodb == null || LoggerLevel.mongodb!!.isEmpty()) {
                logger.error("配置文件错误: loggerLevel.main 不存在或为空")
                return false
            }
            return true
        }

        fun checkConfig() {
            if (!checkBotConfig() || !checkWsConfig() || !checkDataBaseConfig() || !checkLoggerLevel()) {
                exitProcess(1)
            }
        }

        fun setValue() {
            val loader = HoconConfigurationLoader.builder()
                .path(Paths.get(configFile))
                .build()
            val root: CommentedConfigurationNode
            try {
                root = loader.load()
            } catch (e: IOException) {
                logger.error("加载配置文件时发生错误: " + e.message)
                if (e.cause != null) {
                    e.cause!!.printStackTrace()
                }
                exitProcess(1)
            }

            Bot.token = root.node("bot", "token").string
            Bot.verify_token = root.node("bot", "verify_token").string
            if (!root.node("bot", "cmd_prefix").isList) {
                logger.error("配置文件错误: bot.cmd_prefix类型错误 非List类型")
                exitProcess(1)
            }
            Bot.cmd_prefix = root.node("bot", "cmd_prefix").getList(String::class.java)
            Bot.WebHook.host = root.node("bot", "webhook", "host").string
            Bot.WebHook.port = root.node("bot", "webhook", "port").int
            Bot.WebHook.path = root.node("bot", "webhook", "path").string
            Ws.host = root.node("ws", "host").string
            Ws.port = root.node("ws", "port").int
            Ws.path = root.node("ws", "path").string
            DataBase.host = root.node("database", "host").string
            DataBase.port = root.node("database", "port").int
            DataBase.user = root.node("database", "user").string
            DataBase.password = root.node("database", "password").string
            DataBase.database = root.node("database", "database").string
            LoggerLevel.main = root.node("loggerLevel", "main").string
            LoggerLevel.jetty = root.node("loggerLevel", "jetty").string
            LoggerLevel.mongodb = root.node("loggerLevel", "mongodb").string
        }
    }

}