package com.github.hank9999.mcbot.bot

import com.github.hank9999.kook.Bot
import com.github.hank9999.kook.card.Card
import com.github.hank9999.kook.card.CardMessage
import com.github.hank9999.kook.card.Element
import com.github.hank9999.kook.card.Module
import com.github.hank9999.kook.json.JSON.Companion.json
import com.github.hank9999.kook.json.JSON.Extension.Int
import com.github.hank9999.kook.json.JSON.Extension.String
import com.github.hank9999.kook.json.JSON.Extension.get
import com.github.hank9999.kook.types.Event
import com.github.hank9999.kook.types.Message
import com.github.hank9999.kook.types.Type
import com.github.hank9999.kook.types.User
import com.github.hank9999.kook.types.types.EventTypes
import com.github.hank9999.kook.types.types.MessageTypes
import com.github.hank9999.mcbot.MCBot.connector
import com.github.hank9999.mcbot.MCBot.kookApi
import com.github.hank9999.mcbot.bot.ButtonHandlers.Companion.funcDisableButtonHandler
import com.github.hank9999.mcbot.bot.ButtonHandlers.Companion.funcEnableButtonHandler
import com.github.hank9999.mcbot.bot.ButtonHandlers.Companion.permGiveButtonHandler
import com.github.hank9999.mcbot.bot.ButtonHandlers.Companion.permRemoveButtonHandler
import com.github.hank9999.mcbot.bot.Command.Companion.execCommand
import com.github.hank9999.mcbot.bot.Generators.Companion.genControlPanelCard
import com.github.hank9999.mcbot.bot.Generators.Companion.genFuncCard
import com.github.hank9999.mcbot.bot.Generators.Companion.genInfoCard
import com.github.hank9999.mcbot.bot.Generators.Companion.genPermissionAdminCard
import com.github.hank9999.mcbot.bot.Generators.Companion.genPermissionCard
import com.github.hank9999.mcbot.bot.Generators.Companion.genPermissionChatCard
import com.github.hank9999.mcbot.bot.Generators.Companion.genPermissionCommandCard
import com.github.hank9999.mcbot.bot.Generators.Companion.genPermissionStatusCard
import com.github.hank9999.mcbot.bot.Generators.Companion.genStatusCard
import com.github.hank9999.mcbot.bot.Status.Companion.execStatus
import com.github.hank9999.mcbot.bot.Utils.Companion.createEx
import com.github.hank9999.mcbot.bot.Utils.Companion.deleteEx
import com.github.hank9999.mcbot.bot.Utils.Companion.escape
import com.github.hank9999.mcbot.bot.Utils.Companion.getParams
import com.github.hank9999.mcbot.bot.Utils.Companion.getTokenAndCheckAdmin
import com.github.hank9999.mcbot.bot.Utils.Companion.getTokenAndCheckChat
import com.github.hank9999.mcbot.bot.Utils.Companion.getTokenAndCheckCommand
import com.github.hank9999.mcbot.bot.Utils.Companion.getTokenAndCheckMaster
import com.github.hank9999.mcbot.bot.Utils.Companion.getTokenAndCheckStatus
import com.github.hank9999.mcbot.bot.Utils.Companion.removeEscape
import com.github.hank9999.mcbot.bot.Utils.Companion.replyEx
import com.github.hank9999.mcbot.bot.Utils.Companion.updateEx
import com.github.hank9999.mcbot.database.DBRead
import com.github.hank9999.mcbot.database.DBUpdate
import com.github.hank9999.mcbot.database.types.Token
import com.github.hank9999.mcbot.permission.PMCheck
import kotlinx.serialization.json.decodeFromJsonElement
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Commands {
    val logger: Logger = LoggerFactory.getLogger(Commands::class.java)

    @Bot.OnCommand("ping")
    suspend fun hello(msg: Message) {
        val netDelay = System.currentTimeMillis() - msg.msgTimestamp
        msg.replyEx("MCBot v2.0.0-Alpha2\nPong! 机器人正常运行中~\n服务器ID: ${msg.extra.guildId}\n频道ID: ${msg.targetId}\n用户ID: ${msg.authorId}\n延迟: $netDelay ms")
    }

    @Bot.OnCommand("getuserid", aliases = ["获取用户ID"])
    suspend fun getUserId(msg: Message) {
        if (msg.extra.kmarkdown.mentionPart.isEmpty()) {
            msg.replyEx("没有AT到用户")
        } else {
            var message = ""
            msg.extra.kmarkdown.mentionPart.forEach { user ->
                message += "${user.fullName}\n"
            }
            msg.replyEx(message)
        }
    }

    @Bot.OnCommand("listrole", aliases = ["角色列表"])
    suspend fun listRole(msg: Message) {
        val roleData = kookApi.GuildRole().list(msg.extra.guildId)
        val card = Card(
            theme = Type.Theme.SECONDARY,
            Module.Header(Element.Text("角色列表")),
            Module.Divider()
        )
        val paragraph = Element.Paragraph(2,
            Element.Text("**角色名称**\n---", type = Type.Text.KMD),
            Element.Text("**角色ID**\n---", type = Type.Text.KMD)
        )
        var text1 = ""
        var text2 = ""
        roleData.forEach {
            text1 += "${it.name}\n---\n"
            text2 += "${it.roleId}\n---\n"
        }
        paragraph.append(Element.Text(text1, type = Type.Text.KMD))
        paragraph.append(Element.Text(text2, type = Type.Text.KMD))
        card.append(Module.Section(paragraph))
        msg.replyEx(CardMessage(card))
    }

    @Bot.OnCommand("settoken", aliases = ["绑定token"])
    suspend fun setToken(msg: Message) {
        if (!PMCheck.checkGuildMaster(msg.extra.guildId, msg.authorId)) {
            msg.replyEx("你没有权限执行此操作")
            return
        }
        val params = getParams(msg.content)
        if (params.isEmpty()) {
            msg.replyEx("参数不能为空")
            return
        }
        val token = DBRead.getToken(params[0])
        if (token == null) {
            msg.replyEx("token 不存在")
            return
        }
        if (token.guild.isNotEmpty()) {
            msg.replyEx("token 已被其他服务器绑定")
            return
        }
        token.guild = msg.extra.guildId
        token.mainChannel = msg.targetId
        val updateStatus = DBUpdate.updateToken(token)
        if (updateStatus.modifiedCount == 1L) {
            msg.replyEx("设置 token 成功, 所在频道已设为默认频道")
        } else {
            msg.replyEx("设置失败, 请稍后重试")
        }
    }

    @Bot.OnCommand("unsettoken", aliases = ["解绑token"])
    suspend fun unsetToken(msg: Message) {
        val result = getTokenAndCheckMaster(msg.extra.guildId, msg.authorId)
        if (!result.bool) {
            msg.replyEx(result.message)
            return
        }
        val token = result.token
        val updateStatus = DBUpdate.updateToken(Token(token.token))
        if (updateStatus.modifiedCount == 1L) {
            msg.replyEx("解绑 token 成功")
        } else {
            msg.replyEx("解绑失败, 请稍后重试")
        }
    }

    @Bot.OnCommand("setchannel", aliases = ["设置频道"])
    suspend fun setChannel(msg: Message) {
        val result = getTokenAndCheckAdmin(msg.extra.guildId, msg.extra.author)
        if (!result.bool) {
            msg.replyEx(result.message)
            return
        }
        val token = result.token
        val params = getParams(msg.content)
        if (params.isEmpty()) {
            token.mainChannel = msg.targetId
            val updateStatus = DBUpdate.updateToken(token)
            if (updateStatus.modifiedCount == 1L) {
                msg.replyEx("已设为默认频道")
            } else {
                msg.replyEx("设置失败, 请稍后重试")
            }
        } else if (params.size != 1) {
            msg.replyEx("参数过多")
        } else {
            val functionName = params[0].lowercase()
            when (FuncList.fromString(functionName)) {
                FuncList.Log -> token.log = msg.targetId
                FuncList.Chat -> token.chat = msg.targetId
                FuncList.PlayerCommand -> token.playerCommand = msg.targetId
                FuncList.RconCommand -> token.rconCommand = msg.targetId
                FuncList.Login -> token.login = msg.targetId
                FuncList.Logout -> token.logout = msg.targetId
                else -> {
                    msg.replyEx("未知功能名称, 或该功能不需要设置频道")
                    return
                }
            }
            val updateStatus = DBUpdate.updateToken(token)
            if (updateStatus.modifiedCount == 1L) {
                msg.replyEx("已修改 $functionName 频道为本频道")
            } else {
                msg.replyEx("设置失败, 请稍后重试")
            }
        }
    }

    @Bot.OnCommand("controlPanel", aliases = ["控制面板"])
    suspend fun controlPanel(msg: Message) {
        val result = getTokenAndCheckAdmin(msg.extra.guildId, msg.extra.author)
        if (!result.bool) {
            msg.replyEx(result.message)
            return
        }
        val card = genControlPanelCard(msg.targetId)
        msg.replyEx(CardMessage(card))
    }

    @Bot.OnCommand("infoPanel", aliases = ["信息面板"])
    suspend fun infoPanel(msg: Message) {
        val result = getTokenAndCheckAdmin(msg.extra.guildId, msg.extra.author)
        if (!result.bool) {
            msg.replyEx(result.message)
            return
        }
        val card = genInfoCard(result.token)
        msg.replyEx(CardMessage(card))
    }

    @Bot.OnCommand("functionPanel", aliases = ["功能面板"])
    suspend fun functionPanel(msg: Message) {
        val result = getTokenAndCheckAdmin(msg.extra.guildId, msg.extra.author)
        if (!result.bool) {
            msg.replyEx(result.message)
            return
        }
        val card = genFuncCard(result.token)
        logger.info(CardMessage(card).toString())
        msg.replyEx(CardMessage(card))
    }

    @Bot.OnCommand("permissionPanel", aliases = ["权限面板"])
    suspend fun permissionPanel(msg: Message) {
        val result = getTokenAndCheckAdmin(msg.extra.guildId, msg.extra.author)
        if (!result.bool) {
            msg.replyEx(result.message)
            return
        }
        val card = genPermissionCard()
        msg.replyEx(CardMessage(card))
    }

    @Bot.OnCommand("status", aliases = ["状态", "服务器状态"])
    suspend fun status(msg: Message) {
        val result = getTokenAndCheckStatus(msg.extra.guildId, msg.extra.author)
        if (!result.bool) {
            msg.replyEx(result.message)
            return
        }
        val token = result.token
        val statusInfos = execStatus(token)
        msg.replyEx(genStatusCard(statusInfos))
    }

    @Bot.OnCommand("command", aliases = ["指令", "执行", "run", "exec"])
    suspend fun command(msg: Message) {
        val result = getTokenAndCheckCommand(msg.extra.guildId, msg.extra.author)
        if (!result.bool) {
            msg.replyEx(result.message)
            return
        }
        val token = result.token
        val params = getParams(msg.content.removeEscape()).toMutableList()
        if (params.size < 2) {
            msg.replyEx("参数不够")
            return
        }
        val name = params.removeAt(0)
        val ctxMap = connector.getCtxMap()
        if (!ctxMap.containsKey(token.token)) {
            msg.replyEx("没有服务器在线")
            return
        }
        val ctxName = ctxMap[token.token]!!.find { it.name.equals(name, ignoreCase = true) }
        if (ctxName == null) {
            msg.replyEx("未找到该服务器, 请检查服务器名称")
            return
        }
        val serverName = ctxName.name
        val command = params.joinToString(" ")
        val commandReturn = execCommand(token, command, serverName)
        if (commandReturn == null) {
            msg.replyEx("服务器执行指令后, 超时未返回执行结果")
        } else {
            var message = ""
            commandReturn.message.split("\n").forEach {
                val text = it.trim()
                if (text.isNotEmpty()) {
                    message += "[$serverName] $text\n"
                }
            }
            msg.replyEx(message)
        }
    }

    @Bot.OnCommand("say", aliases = ["说", "发消息", "caht"])
    suspend fun say(msg: Message) {
        val result = getTokenAndCheckChat(msg.extra.guildId, msg.extra.author)
        if (!result.bool) {
            msg.replyEx(result.message)
            return
        }
        val token = result.token
        val params = getParams(msg.content.removeEscape()).toMutableList()
        if (params.size < 2) {
            msg.replyEx("参数不够")
            return
        }
        val name = params.removeAt(0)
        val ctxMap = connector.getCtxMap()
        if (!ctxMap.containsKey(token.token)) {
            msg.replyEx("没有服务器在线")
            return
        }
        val ctxName = ctxMap[token.token]!!.find { it.name.equals(name, ignoreCase = true) }
        if (ctxName == null) {
            msg.replyEx("未找到该服务器, 请检查服务器名称")
            return
        }
        val serverName = ctxName.name
        val chatMessage = params.joinToString(" ")
        val player = msg.extra.author.nickname.ifEmpty { msg.extra.author.username }
        val command = token.tellraw.replaceFirst("%%playerId%%", player).replaceFirst("%text%", chatMessage.replace("\"", "\\\""))
        execCommand(token, command, serverName)
        val channelId = when (token.chat) {
            "-1" -> return
            "0" -> token.mainChannel
            else -> token.chat
        }
        try {
            kookApi.Message().create(channelId, "\\[$serverName\\] <$player> ${chatMessage.escape()}", MessageTypes.KMD)
        } catch (ex: Exception) {
            logger.error(ex.stackTraceToString())
            kookApi.Message().createEx(channelId, "**$serverName**\n发送消息时发生错误, 请联系维护\n${ex.stackTraceToString()}", MessageTypes.KMD)
        }
    }

    @Bot.OnEvent(EventTypes.MESSAGE_BTN_CLICK)
    suspend fun buttonClickHandler(event: Event) {
        try {
            val user = json.decodeFromJsonElement<User>(event.extra.body["user_info"])
            val result = getTokenAndCheckAdmin(event.extra.body["guild_id"].String, user)
            if (!result.bool) {
                kookApi.Message().createEx(event.extra.body["target_id"].String, result.message, tempTargetId = user.id)
                return
            }
            val value = json.parseToJsonElement(event.extra.body["value"].String)
            val token = result.token
            val msgId = event.extra.body["msg_id"].String
            val channelId = event.extra.body["target_id"].String
            when (value["type"].String) {
                "closePanel" -> kookApi.Message().deleteEx(msgId)
                "openInfo" -> kookApi.Message().updateEx(msgId, CardMessage(genInfoCard(token)))
                "openFunc" -> kookApi.Message().updateEx(msgId, CardMessage(genFuncCard(token)))
                "openPerm" -> kookApi.Message().updateEx(msgId, CardMessage(genPermissionCard()))
                "openPermStatus" -> kookApi.Message().updateEx(msgId, genPermissionStatusCard(token))
                "openPermCommand" -> kookApi.Message().updateEx(msgId, genPermissionCommandCard(token))
                "openPermAdmin" -> kookApi.Message().updateEx(msgId, genPermissionAdminCard(token))
                "openPermChat" -> kookApi.Message().updateEx(msgId, genPermissionChatCard(token))
                "funcEnable" -> funcEnableButtonHandler(value["func"].String, token, channelId, msgId, user.id)
                "funcDisable" -> funcDisableButtonHandler(value["func"].String, token, channelId, msgId, user.id)
                "permRemove" -> permRemoveButtonHandler(value["perm"].String, value["role"].Int, token, channelId, msgId, user.id)
                "permGive" -> permGiveButtonHandler(value["perm"].String, value["role"].Int, token, channelId, msgId, user.id)
                "returnMain" -> kookApi.Message().updateEx(msgId, CardMessage(genControlPanelCard(channelId)))
                else -> {}
            }
        } catch (ex: Exception) {
            logger.error("${ex.message}\n${ex.stackTraceToString()}")
        }
    }
}