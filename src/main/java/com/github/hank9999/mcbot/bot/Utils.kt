package com.github.hank9999.mcbot.bot

import com.github.hank9999.kook.http.types.apiResponse.MessageCreate
import com.github.hank9999.kook.types.Message
import com.github.hank9999.kook.types.User
import com.github.hank9999.kook.types.types.MessageTypes
import com.github.hank9999.mcbot.database.DBRead
import com.github.hank9999.mcbot.database.types.Token
import com.github.hank9999.mcbot.permission.PMCheck
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Utils {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(Utils::class.java)
        private val needRemoveEscapePatten = listOf("\\*", "\\~", "\\[", "\\]", "\\(", "\\)", "\\-", "\\>", "\\:", "\\`", "\\\"")
        private val needEscape = listOf("*", "~", "[", "]", "(", ")", "-", ">", ":", "`", "\"")

        object Banner {
            const val main = "https://img.kookapp.cn/attachments/2022-08/23/ZNy8afRkcT0rs046.gif"
            const val info = "https://img.kookapp.cn/assets/2022-08/KmvGcv7kVt0rs046.png"
            const val controlPanel = "https://img.kookapp.cn/assets/2022-08/iPMCGlbmb80rs046.png"
            const val func = "https://img.kookapp.cn/assets/2022-08/HfMeUD4Xjr0rs046.png"
            const val perm = "https://img.kookapp.cn/assets/2022-08/Nkldw4Z6Kx0rs046.png"
            const val status = "https://img.kookapp.cn/assets/2022-09/KC11606HAN0rs046.png"
        }

        data class GetTokenAndCheckPermissionResult (val bool: Boolean, val token: Token = Token(""), val message: String = "")
        suspend fun getTokenAndCheckMaster(guildId: String, userId: String): GetTokenAndCheckPermissionResult {
            val token = DBRead.getTokenByGuild(guildId) ?: return GetTokenAndCheckPermissionResult(false, message = "未绑定 token")
            if (!PMCheck.checkGuildMaster(token, userId)) {
                return GetTokenAndCheckPermissionResult(false, message = "你没有权限执行此操作")
            }
            return GetTokenAndCheckPermissionResult(true, token)
        }

        suspend fun getTokenAndCheckAdmin(guildId: String, user: User): GetTokenAndCheckPermissionResult {
            val token = DBRead.getTokenByGuild(guildId) ?: return GetTokenAndCheckPermissionResult(false, message = "未绑定 token")
            if (!PMCheck.checkAdmin(token, user) && !PMCheck.checkGuildMaster(token, user.id)) {
                return GetTokenAndCheckPermissionResult(false, message = "你没有权限执行此操作")
            }
            return GetTokenAndCheckPermissionResult(true, token)
        }

        suspend fun getTokenAndCheckStatus(guildId: String, user: User): GetTokenAndCheckPermissionResult {
            val token = DBRead.getTokenByGuild(guildId) ?: return GetTokenAndCheckPermissionResult(false, message = "未绑定 token")
            if (!PMCheck.checkStatus(token, user) && !PMCheck.checkAdmin(token, user) && !PMCheck.checkGuildMaster(token, user.id)) {
                return GetTokenAndCheckPermissionResult(false, message = "你没有权限执行此操作")
            }
            return GetTokenAndCheckPermissionResult(true, token)
        }

        suspend fun getTokenAndCheckChat(guildId: String, user: User): GetTokenAndCheckPermissionResult {
            val token = DBRead.getTokenByGuild(guildId) ?: return GetTokenAndCheckPermissionResult(false, message = "未绑定 token")
            if (!PMCheck.checkChat(token, user) && !PMCheck.checkAdmin(token, user) && !PMCheck.checkGuildMaster(token, user.id)) {
                return GetTokenAndCheckPermissionResult(false, message = "你没有权限执行此操作")
            }
            return GetTokenAndCheckPermissionResult(true, token)
        }

        suspend fun getTokenAndCheckCommand(guildId: String, user: User): GetTokenAndCheckPermissionResult {
            val token = DBRead.getTokenByGuild(guildId) ?: return GetTokenAndCheckPermissionResult(false, message = "未绑定 token")
            if (!PMCheck.checkCommand(token, user) && !PMCheck.checkAdmin(token, user) && !PMCheck.checkGuildMaster(token, user.id)) {
                return GetTokenAndCheckPermissionResult(false, message = "你没有权限执行此操作")
            }
            return GetTokenAndCheckPermissionResult(true, token)
        }

        fun getParams(content: String): List<String> {
            val params = content.split(" ").toMutableList()
            params.removeAt(0)
            return params.toList()
        }

        suspend fun Message.replyEx(content: Any, type: MessageTypes? = null, nonce: String? = null, tempTargetId: String? = null): MessageCreate {
            return try {
                this.reply(content, type, nonce, tempTargetId)
            } catch (ex: Exception) {
                logger.error(ex.stackTraceToString())
                try {
                    this.reply("发生错误, 请稍后再试或联系维护\n${ex.message}")
                } catch (ex2: Exception) {
                    logger.error(ex2.stackTraceToString())
                    MessageCreate()
                }
            }
        }

        suspend fun com.github.hank9999.kook.http.kookapis.Message.createEx(targetId: String, content: Any, type: MessageTypes? = null, quote: String? = null, nonce: String? = null, tempTargetId: String? = null): MessageCreate {
            return try {
                this.create(targetId, content, type, quote, nonce, tempTargetId)
            } catch (ex: Exception) {
                logger.error(ex.stackTraceToString())
                MessageCreate()
            }
        }

        suspend fun com.github.hank9999.kook.http.kookapis.Message.deleteEx(msgId: String) {
            return try {
                this.delete(msgId)
            } catch (ex: Exception) {
                logger.error(ex.stackTraceToString())
            }
        }

        suspend fun com.github.hank9999.kook.http.kookapis.Message.updateEx(msgId: String, content: Any, quote: String? = null, tempTargetId: String? = null) {
            return try {
                this.update(msgId, content, quote, tempTargetId)
            } catch (ex: Exception) {
                logger.error(ex.stackTraceToString())
            }
        }

        fun String.removeEscape(): String {
            var output = this
            needRemoveEscapePatten.forEach {
                output = replace(it, "${it.last()}")
            }
            return output
        }

        fun String.escape(): String {
            var output = this
            needEscape.forEach {
                output = replace(it, "\\$it")
            }
            return output
        }
    }
}