package com.github.hank9999.mcbot.bot

import com.github.hank9999.kook.card.Card
import com.github.hank9999.kook.card.CardMessage
import com.github.hank9999.kook.card.Element
import com.github.hank9999.kook.card.Module
import com.github.hank9999.kook.types.Type
import com.github.hank9999.mcbot.MCBot
import com.github.hank9999.mcbot.bot.Utils.Companion.Banner
import com.github.hank9999.mcbot.bot.types.StatusInfo
import com.github.hank9999.mcbot.database.types.Token
import com.github.hank9999.mcbot.permission.PMCheck

class Generators {
    companion object {
        fun genInfo(funcChannelId: String): String {
            return when (funcChannelId) {
                "-1" -> "状态: **未启用**"
                "0" -> "状态: **已启用**\n频道: **默认频道**"
                else -> "状态: **已启用**\n频道: **(chn)$funcChannelId(chn)**"
            }
        }

        fun genInfo(bool: Boolean): String {
            return when (bool) {
                false -> "状态: **未启用**"
                true -> "状态: **已启用**"
            }
        }

        fun genControlPanelCard(channelId: String): Card {
            return Card(
                theme = Type.Theme.SECONDARY,
                Module.Container(Element.Image(Banner.main), Element.Image(Banner.controlPanel)),
                Module.Divider(),
                Module.ActionGroup(
                    Element.Button(Type.Theme.PRIMARY, Element.Text("服务器信息"), "{\"type\":\"openInfo\"}", Type.Click.RETURN_VAL),
                    Element.Button(Type.Theme.PRIMARY, Element.Text("功能面板"), "{\"type\":\"openFunc\"}", Type.Click.RETURN_VAL),
                    Element.Button(Type.Theme.PRIMARY, Element.Text("权限面板"), "{\"type\":\"openPerm\"}", Type.Click.RETURN_VAL),
                    Element.Button(Type.Theme.WARNING, Element.Text("关闭"), "{\"type\":\"closePanel\"}", Type.Click.RETURN_VAL)
                ),
                Module.Divider(),
                Module.Context(Element.Text("目前所在频道: (chn)$channelId(chn)", type = Type.Text.KMD))
            )
        }

        fun genInfoCard(token: Token): Card {
            val card = Card(
                theme = Type.Theme.SECONDARY,
                Module.Container(Element.Image(Banner.main), Element.Image(Banner.info)),
                Module.Section(Element.Text("默认频道: **(chn)${token.mainChannel}(chn)**", type = Type.Text.KMD)),
                Module.Divider()
            )
            card.append(
                Module.Header(Element.Text("日志 - log")),
                Module.Section(Element.Text(genInfo(token.log), type = Type.Text.KMD)),
                Module.Divider()
            )
            card.append(
                Module.Header(Element.Text("聊天 - chat")),
                Module.Section(Element.Text(genInfo(token.chat), type = Type.Text.KMD)),
                Module.Divider()
            )
            card.append(
                Module.Header(Element.Text("玩家指令转发 - playerCommand")),
                Module.Section(Element.Text(genInfo(token.playerCommand), type = Type.Text.KMD)),
                Module.Divider()
            )
            card.append(
                Module.Header(Element.Text("Rcon 指令转发 - rconCommand")),
                Module.Section(Element.Text(genInfo(token.rconCommand), type = Type.Text.KMD)),
                Module.Divider()
            )
            card.append(
                Module.Header(Element.Text("登陆日志 - login")),
                Module.Section(Element.Text(genInfo(token.login), type = Type.Text.KMD)),
                Module.Divider()
            )
            card.append(
                Module.Header(Element.Text("退出日志 - logout")),
                Module.Section(Element.Text(genInfo(token.logout), type = Type.Text.KMD)),
                Module.Divider()
            )
            card.append(
                Module.Header(Element.Text("Tellraw 格式")),
                Module.Section(Element.Text(token.tellraw)),
                Module.Divider()
            )
            card.append(
                Module.Header(Element.Text("远程执行指令 - command")),
                Module.Section(Element.Text(genInfo(token.command), type = Type.Text.KMD)),
                Module.Divider()
            )
            card.append(
                Module.Header(Element.Text("查询服务器状态 - status")),
                Module.Section(Element.Text(genInfo(token.status), type = Type.Text.KMD)),
                Module.Divider()
            )
            card.append(
                Module.ActionGroup(
                    Element.Button(Type.Theme.PRIMARY, Element.Text("返回主菜单"), "{\"type\":\"returnMain\"}", Type.Click.RETURN_VAL),
                    Element.Button(Type.Theme.WARNING, Element.Text("关闭"), "{\"type\":\"closePanel\"}", Type.Click.RETURN_VAL)
                )
            )
            return card
        }

        fun genFuncButton(funcChannelId: String, func: FuncList): Module.Section {
            return when (funcChannelId) {
                "-1" -> {
                    Module.Section(
                        text = Element.Text(genInfo(funcChannelId), type = Type.Text.KMD),
                        mode = Type.SectionMode.RIGHT,
                        accessory = Element.Button(Type.Theme.SUCCESS, Element.Text("开启"), "{\"type\":\"funcEnable\", \"func\": \"${func.str}\"}", Type.Click.RETURN_VAL)
                    )
                }
                "0" -> {
                    Module.Section(
                        text = Element.Text(genInfo(funcChannelId), type = Type.Text.KMD),
                        mode = Type.SectionMode.RIGHT,
                        accessory = Element.Button(Type.Theme.DANGER, Element.Text("关闭"), "{\"type\":\"funcDisable\", \"func\": \"${func.str}\"}", Type.Click.RETURN_VAL)
                    )
                }
                else -> {
                    Module.Section(
                        text = Element.Text(genInfo(funcChannelId), type = Type.Text.KMD),
                        mode = Type.SectionMode.RIGHT,
                        accessory = Element.Button(Type.Theme.DANGER, Element.Text("关闭"), "{\"type\":\"funcDisable\", \"func\": \"${func.str}\"}", Type.Click.RETURN_VAL)
                    )
                }
            }
        }

        fun genFuncButton(bool: Boolean, func: FuncList): Module.Section {
            return when (bool) {
                false -> {
                    Module.Section(
                        text = Element.Text(genInfo(false), type = Type.Text.KMD),
                        mode = Type.SectionMode.RIGHT,
                        accessory = Element.Button(Type.Theme.SUCCESS, Element.Text("开启"), "{\"type\":\"funcEnable\", \"func\": \"${func.str}\"}", Type.Click.RETURN_VAL)
                    )
                }
                true -> {
                    Module.Section(
                        text = Element.Text(genInfo(true), type = Type.Text.KMD),
                        mode = Type.SectionMode.RIGHT,
                        accessory = Element.Button(Type.Theme.DANGER, Element.Text("关闭"), "{\"type\":\"funcDisable\", \"func\": \"${func.str}\"}", Type.Click.RETURN_VAL)
                    )
                }
            }
        }

        fun genFuncCard(token: Token): Card {
            val card = Card(
                theme = Type.Theme.SECONDARY,
                Module.Container(Element.Image(Banner.main), Element.Image(Banner.func)),
                Module.Section(Element.Text("默认频道: **(chn)${token.mainChannel}(chn)**", type = Type.Text.KMD)),
                Module.Divider()
            )
            card.append(
                Module.Header(Element.Text("日志 - log")),
                genFuncButton(token.log, FuncList.Log),
                Module.Divider()
            )
            card.append(
                Module.Header(Element.Text("聊天 - chat")),
                genFuncButton(token.chat, FuncList.Chat),
                Module.Divider()
            )
            card.append(
                Module.Header(Element.Text("玩家指令转发 - playerCommand")),
                genFuncButton(token.playerCommand, FuncList.PlayerCommand),
                Module.Divider()
            )
            card.append(
                Module.Header(Element.Text("Rcon 指令转发 - rconCommand")),
                genFuncButton(token.rconCommand, FuncList.RconCommand),
                Module.Divider()
            )
            card.append(
                Module.Header(Element.Text("登陆日志 - login")),
                genFuncButton(token.login, FuncList.Login),
                Module.Divider()
            )
            card.append(
                Module.Header(Element.Text("退出日志 - logout")),
                genFuncButton(token.logout, FuncList.Logout),
                Module.Divider()
            )
            card.append(
                Module.Header(Element.Text("远程执行指令 - command")),
                genFuncButton(token.command, FuncList.Command),
                Module.Divider()
            )
            card.append(
                Module.Header(Element.Text("查询服务器状态 - status")),
                genFuncButton(token.status, FuncList.Status),
                Module.Divider()
            )
            card.append(
                Module.ActionGroup(
                    Element.Button(Type.Theme.PRIMARY, Element.Text("返回主菜单"), "{\"type\":\"returnMain\"}", Type.Click.RETURN_VAL),
                    Element.Button(Type.Theme.WARNING, Element.Text("关闭"), "{\"type\":\"closePanel\"}", Type.Click.RETURN_VAL)
                )
            )
            return card
        }

        fun genPermissionCard(): Card {
            return Card(
                theme = Type.Theme.SECONDARY,
                Module.Container(Element.Image(Banner.main), Element.Image(Banner.perm)),
                Module.Divider(),
                Module.ActionGroup(
                    Element.Button(Type.Theme.INFO, Element.Text("查询服务器状态 - status"), "{\"type\":\"openPermStatus\"}", Type.Click.RETURN_VAL)
                ),
                Module.ActionGroup(
                    Element.Button(Type.Theme.INFO, Element.Text("远程执行指令 - command"), "{\"type\":\"openPermCommand\"}", Type.Click.RETURN_VAL)
                ),
                Module.ActionGroup(
                    Element.Button(Type.Theme.INFO, Element.Text("MCBot 管理员 - admin"), "{\"type\":\"openPermAdmin\"}", Type.Click.RETURN_VAL)
                ),
                Module.Divider(),
                Module.ActionGroup(
                    Element.Button(Type.Theme.PRIMARY, Element.Text("返回主菜单"), "{\"type\":\"returnMain\"}", Type.Click.RETURN_VAL),
                    Element.Button(Type.Theme.WARNING, Element.Text("关闭"), "{\"type\":\"closePanel\"}", Type.Click.RETURN_VAL)
                )
            )
        }

        suspend fun genPermissionStatusCard(token: Token): CardMessage {
            val cardMessage = CardMessage()
            var card = Card(
                theme = Type.Theme.SECONDARY,
                Module.Container(Element.Image(Banner.main), Element.Image(Banner.perm)),
                Module.Header(Element.Text("权限 | 查询服务器状态 - status")),
                Module.Divider()
            )
            val roleList = MCBot.kookApi.GuildRole().list(token.guild)
            var clearUsed = false
            for (it in roleList) {
                val status = PMCheck.checkStatus(token, it.roleId)
                val statusText = when (status) {
                    true -> "权限: **有**"
                    false -> "权限: **无**"
                }
                val role = if (it.roleId == 0) "@全体成员" else "(rol)${it.roleId}(rol)"
                val text = "> $role\n$statusText\n\n"
                if (card.length() + 2 > 50) {
                    if (cardMessage.length() == 4) {
                        card.clear()
                        clearUsed = true
                    } else {
                        cardMessage.append(card)
                        card = Card()
                    }
                }
                if (status) {
                    card.append(
                        Module.Section(
                            text = Element.Text(text, type = Type.Text.KMD),
                            mode = Type.SectionMode.RIGHT,
                            accessory = Element.Button(Type.Theme.DANGER, Element.Text("移除"), "{\"type\":\"permRemove\", \"perm\": \"status\", \"role\": ${it.roleId}}", Type.Click.RETURN_VAL)
                        )
                    )
                } else {
                    card.append(
                        Module.Section(
                            text = Element.Text(text, type = Type.Text.KMD),
                            mode = Type.SectionMode.RIGHT,
                            accessory = Element.Button(Type.Theme.SUCCESS, Element.Text("给予"), "{\"type\":\"permGive\", \"perm\": \"status\", \"role\": ${it.roleId}}", Type.Click.RETURN_VAL)
                        )
                    )
                }
                card.append(Module.Divider())
            }
            if (card.length() >= 48 && cardMessage.length() < 4) {
                cardMessage.append(card)
                card = Card()
            }
            if (clearUsed) {
                if (card.length() == 50) {
                    card.removeAt(49)
                    card.removeAt(48)
                } else if (card.length() == 49) {
                    card.removeAt(48)
                }
                card.append(Module.Section(Element.Text("因卡片消息限制, 部分服务器信息无法发送")))
            }
            card.append(
                Module.ActionGroup(
                    Element.Button(Type.Theme.PRIMARY, Element.Text("返回权限主面板"), "{\"type\":\"openPerm\"}", Type.Click.RETURN_VAL),
                    Element.Button(Type.Theme.WARNING, Element.Text("关闭"), "{\"type\":\"closePanel\"}", Type.Click.RETURN_VAL)
                )
            )
            cardMessage.append(card)
            return cardMessage
        }

        suspend fun genPermissionCommandCard(token: Token): CardMessage {
            val cardMessage = CardMessage()
            var card = Card(
                theme = Type.Theme.SECONDARY,
                Module.Container(Element.Image(Banner.main), Element.Image(Banner.perm)),
                Module.Header(Element.Text("权限 | 远程执行指令 - command")),
                Module.Divider()
            )
            var clearUsed = false
            val roleList = MCBot.kookApi.GuildRole().list(token.guild)
            for (it in roleList) {
                val command = PMCheck.checkCommand(token, it.roleId)
                val commandText = when (command) {
                    true -> "权限: **有**"
                    false -> "权限: **无**"
                }
                val role = if (it.roleId == 0) "@全体成员" else "(rol)${it.roleId}(rol)"
                val text = "> $role\n$commandText\n\n"
                if (card.length() + 2 > 50) {
                    if (cardMessage.length() == 4) {
                        card.clear()
                        clearUsed = true
                    } else {
                        cardMessage.append(card)
                        card = Card()
                    }
                }
                if (command) {
                    card.append(
                        Module.Section(
                            text = Element.Text(text, type = Type.Text.KMD),
                            mode = Type.SectionMode.RIGHT,
                            accessory = Element.Button(Type.Theme.DANGER, Element.Text("移除"), "{\"type\":\"permRemove\", \"perm\": \"command\", \"role\": ${it.roleId}}", Type.Click.RETURN_VAL)
                        )
                    )
                } else {
                    card.append(
                        Module.Section(
                            text = Element.Text(text, type = Type.Text.KMD),
                            mode = Type.SectionMode.RIGHT,
                            accessory = Element.Button(Type.Theme.SUCCESS, Element.Text("给予"), "{\"type\":\"permGive\", \"perm\": \"command\", \"role\": ${it.roleId}}", Type.Click.RETURN_VAL)
                        )
                    )
                }
                card.append(Module.Divider())
            }
            if (card.length() >= 48 && cardMessage.length() < 4) {
                cardMessage.append(card)
                card = Card()
            }
            if (clearUsed) {
                if (card.length() == 50) {
                    card.removeAt(49)
                    card.removeAt(48)
                } else if (card.length() == 49) {
                    card.removeAt(48)
                }
                card.append(Module.Section(Element.Text("因卡片消息限制, 部分服务器信息无法发送")))
            }
            card.append(
                Module.ActionGroup(
                    Element.Button(Type.Theme.PRIMARY, Element.Text("返回权限主面板"), "{\"type\":\"openPerm\"}", Type.Click.RETURN_VAL),
                    Element.Button(Type.Theme.WARNING, Element.Text("关闭"), "{\"type\":\"closePanel\"}", Type.Click.RETURN_VAL)
                )
            )
            cardMessage.append(card)
            return cardMessage
        }

        suspend fun genPermissionAdminCard(token: Token): CardMessage {
            val cardMessage = CardMessage()
            var card = Card(
                theme = Type.Theme.SECONDARY,
                Module.Container(Element.Image(Banner.main), Element.Image(Banner.perm)),
                Module.Header(Element.Text("权限 | MCBot 管理员 - admin")),
                Module.Divider()
            )
            val roleList = MCBot.kookApi.GuildRole().list(token.guild)
            var clearUsed = false
            for (it in roleList) {
                val command = PMCheck.checkAdmin(token, it.roleId)
                val commandText = when (command) {
                    true -> "权限: **有**"
                    false -> "权限: **无**"
                }
                val role = if (it.roleId == 0) "@全体成员" else "(rol)${it.roleId}(rol)"
                val text = "> $role\n$commandText\n\n"
                if (card.length() + 2 > 50) {
                    if (cardMessage.length() == 4) {
                        card.clear()
                        clearUsed = true
                    } else {
                        cardMessage.append(card)
                        card = Card()
                    }
                }
                if (command) {
                    card.append(
                        Module.Section(
                            text = Element.Text(text, type = Type.Text.KMD),
                            mode = Type.SectionMode.RIGHT,
                            accessory = Element.Button(Type.Theme.DANGER, Element.Text("移除"), "{\"type\":\"permRemove\", \"perm\": \"admin\", \"role\": ${it.roleId}}", Type.Click.RETURN_VAL)
                        )
                    )
                } else {
                    card.append(
                        Module.Section(
                            text = Element.Text(text, type = Type.Text.KMD),
                            mode = Type.SectionMode.RIGHT,
                            accessory = Element.Button(Type.Theme.SUCCESS, Element.Text("给予"), "{\"type\":\"permGive\", \"perm\": \"admin\", \"role\": ${it.roleId}}", Type.Click.RETURN_VAL)
                        )
                    )
                }
                card.append(Module.Divider())
            }
            if (card.length() >= 48 && cardMessage.length() < 4) {
                cardMessage.append(card)
                card = Card()
            }
            if (clearUsed) {
                if (card.length() == 50) {
                    card.removeAt(49)
                    card.removeAt(48)
                } else if (card.length() == 49) {
                    card.removeAt(48)
                }
                card.append(Module.Section(Element.Text("因卡片消息限制, 部分服务器信息无法发送")))
            }
            card.append(
                Module.ActionGroup(
                    Element.Button(Type.Theme.PRIMARY, Element.Text("返回权限主面板"), "{\"type\":\"openPerm\"}", Type.Click.RETURN_VAL),
                    Element.Button(Type.Theme.WARNING, Element.Text("关闭"), "{\"type\":\"closePanel\"}", Type.Click.RETURN_VAL)
                )
            )
            cardMessage.append(card)
            return cardMessage
        }

        fun genStatusCard(statusInfos: List<StatusInfo>): CardMessage {
            var card = Card(
                theme = Type.Theme.SECONDARY,
                Module.Container(Element.Image(Banner.main), Element.Image(Banner.status)),
                Module.Divider()
            )
            if (statusInfos.isEmpty()) {
                card.append(Module.Section(Element.Text("无服务器在线")))
                return CardMessage((card))
            }
            val cardMessage = CardMessage()
            var clearUsed = false
            statusInfos.forEach {
                if (card.length() + 3 > 50) {
                    if (cardMessage.length() == 4) {
                        card.clear()
                        clearUsed = true
                    } else {
                        cardMessage.append(card)
                        card = Card()
                    }
                }
                card.append(
                    Module.Header(Element.Text(it.name)),
                    Module.Section(
                        Element.Text(
                            "> 在线: ${it.onlinePlayer.size} 人\n版本: ${it.version}\n在线玩家: ${it.onlinePlayer.joinToString(", ")}\n\n",
                            type = Type.Text.KMD
                        )
                    ),
                    Module.Divider()
                )
            }
            if (card.length() != 0) {
                card.removeAt(card.length() - 1)
            }
            if (clearUsed) {
                if (card.length() == 50) {
                    card.removeAt(49)
                }
                card.append(Module.Section(Element.Text("因卡片消息限制, 部分服务器信息无法发送")))
            }
            cardMessage.append(card)
            return cardMessage
        }
    }
}