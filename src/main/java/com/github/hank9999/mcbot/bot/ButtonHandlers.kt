package com.github.hank9999.mcbot.bot

import com.github.hank9999.kook.card.CardMessage
import com.github.hank9999.mcbot.MCBot
import com.github.hank9999.mcbot.bot.Generators.Companion.genFuncCard
import com.github.hank9999.mcbot.bot.Generators.Companion.genPermissionAdminCard
import com.github.hank9999.mcbot.bot.Generators.Companion.genPermissionCommandCard
import com.github.hank9999.mcbot.bot.Generators.Companion.genPermissionStatusCard
import com.github.hank9999.mcbot.database.DBRead
import com.github.hank9999.mcbot.database.DBUpdate
import com.github.hank9999.mcbot.database.types.Token
import com.github.hank9999.mcbot.permission.PMAdd
import com.github.hank9999.mcbot.permission.PMCheck
import com.github.hank9999.mcbot.permission.PMDel
import com.github.hank9999.mcbot.permission.types.Status

class ButtonHandlers {
    companion object {
        suspend fun permGiveButtonHandler(perm: String, roleId: Int, token: Token, channelId: String, msgId: String, userId: String) {
            when (perm) {
                "status" -> {
                    if (PMCheck.checkStatus(token, roleId)) {
                        MCBot.kookApi.Message().create(channelId, "角色 $roleId 已有权限 $perm", tempTargetId = userId)
                    } else {
                        if (PMAdd.addStatus(token, roles = listOf(roleId)) == Status.Success) {
                            val newToken = DBRead.getToken(token.token) ?: token
                            MCBot.kookApi.Message().update(msgId, genPermissionStatusCard(newToken))
                        } else {
                            MCBot.kookApi.Message().create(channelId, "权限 $perm 更新失败, 请稍后再试或联系维护", tempTargetId = userId)
                        }
                    }
                }
                "command" -> {
                    if (PMCheck.checkCommand(token, roleId)) {
                        MCBot.kookApi.Message().create(channelId, "角色 $roleId 已有权限 $perm", tempTargetId = userId)
                    } else {
                        if (PMAdd.addCommand(token, roles = listOf(roleId)) == Status.Success) {
                            val newToken = DBRead.getToken(token.token) ?: token
                            MCBot.kookApi.Message().update(msgId, genPermissionCommandCard(newToken))
                        } else {
                            MCBot.kookApi.Message().create(channelId, "权限 $perm 更新失败, 请稍后再试或联系维护", tempTargetId = userId)
                        }
                    }
                }
                "admin" -> {
                    if (!PMCheck.checkGuildMaster(token, userId)) {
                        MCBot.kookApi.Message().create(channelId, "您没有权限进行此操作", tempTargetId = userId)
                        return
                    }
                    if (PMCheck.checkAdmin(token, roleId)) {
                        MCBot.kookApi.Message().create(channelId, "角色 $roleId 已有权限 $perm", tempTargetId = userId)
                    } else {
                        if (PMAdd.addAdmin(token, roles = listOf(roleId)) == Status.Success) {
                            val newToken = DBRead.getToken(token.token) ?: token
                            MCBot.kookApi.Message().update(msgId, genPermissionAdminCard(newToken))
                        } else {
                            MCBot.kookApi.Message().create(channelId, "权限 $perm 更新失败, 请稍后再试或联系维护", tempTargetId = userId)
                        }
                    }
                }
                else -> {
                    MCBot.kookApi.Message().create(channelId, "未知权限 $perm", tempTargetId = userId)
                }
            }
        }

        suspend fun permRemoveButtonHandler(perm: String, roleId: Int, token: Token, channelId: String, msgId: String, userId: String) {
            when (perm) {
                "status" -> {
                    if (!PMCheck.checkStatus(token, roleId)) {
                        MCBot.kookApi.Message().create(channelId, "角色 $roleId 已没有权限 $perm", tempTargetId = userId)
                    } else {
                        if (PMDel.delStatus(token, roles = listOf(roleId)) == Status.Success) {
                            val newToken = DBRead.getToken(token.token) ?: token
                            MCBot.kookApi.Message().update(msgId, genPermissionStatusCard(newToken))
                        } else {
                            MCBot.kookApi.Message().create(channelId, "权限 $perm 更新失败, 请稍后再试或联系维护", tempTargetId = userId)
                        }
                    }
                }
                "command" -> {
                    if (!PMCheck.checkCommand(token, roleId)) {
                        MCBot.kookApi.Message().create(channelId, "角色 $roleId 已没有权限 $perm", tempTargetId = userId)
                    } else {
                        if (PMDel.delCommand(token, roles = listOf(roleId)) == Status.Success) {
                            val newToken = DBRead.getToken(token.token) ?: token
                            MCBot.kookApi.Message().update(msgId, genPermissionCommandCard(newToken))
                        } else {
                            MCBot.kookApi.Message().create(channelId, "权限 $perm 更新失败, 请稍后再试或联系维护", tempTargetId = userId)
                        }
                    }
                }
                "admin" -> {
                    if (!PMCheck.checkGuildMaster(token, userId)) {
                        MCBot.kookApi.Message().create(channelId, "您没有权限进行此操作", tempTargetId = userId)
                        return
                    }
                    if (!PMCheck.checkAdmin(token, roleId)) {
                        MCBot.kookApi.Message().create(channelId, "角色 $roleId 已没有权限 $perm", tempTargetId = userId)
                    } else {
                        if (PMDel.delAdmin(token, roles = listOf(roleId)) == Status.Success) {
                            val newToken = DBRead.getToken(token.token) ?: token
                            MCBot.kookApi.Message().update(msgId, genPermissionAdminCard(newToken))
                        } else {
                            MCBot.kookApi.Message().create(channelId, "权限 $perm 更新失败, 请稍后再试或联系维护", tempTargetId = userId)
                        }
                    }
                }
                else -> {
                    MCBot.kookApi.Message().create(channelId, "未知权限 $perm", tempTargetId = userId)
                }
            }
        }

        suspend fun funcEnableButtonHandler(func: String, token: Token, channelId: String, msgId: String, userId: String) {
            when (func) {
                "log" -> {
                    if (token.log != "-1") {
                        MCBot.kookApi.Message().create(channelId, "功能 $func 已处于开启状态", tempTargetId = userId)
                    } else {
                        token.log = "0"
                        if (DBUpdate.updateTokenLog(token).modifiedCount == 1L) {
//                            kookApi.Message().create(channelId, "功能 $func 已开启", tempTargetId = userId)
                            MCBot.kookApi.Message().update(msgId, CardMessage(genFuncCard(token)))
                        } else {
                            MCBot.kookApi.Message().create(channelId, "功能 $func 设置更新失败, 请稍后再试或联系维护", tempTargetId = userId)
                        }
                    }
                }
                "chat" -> {
                    if (token.chat != "-1") {
                        MCBot.kookApi.Message().create(channelId, "功能 $func 已处于开启状态", tempTargetId = userId)
                    } else {
                        token.chat = "0"
                        if (DBUpdate.updateTokenChat(token).modifiedCount == 1L) {
//                            kookApi.Message().create(channelId, "功能 $func 已开启", tempTargetId = userId)
                            MCBot.kookApi.Message().update(msgId, CardMessage(genFuncCard(token)))
                        } else {
                            MCBot.kookApi.Message().create(channelId, "功能 $func 设置更新失败, 请稍后再试或联系维护", tempTargetId = userId)
                        }
                    }
                }
                "playerCommand" -> {
                    if (token.playerCommand != "-1") {
                        MCBot.kookApi.Message().create(channelId, "功能 $func 已处于开启状态", tempTargetId = userId)
                    } else {
                        token.playerCommand = "0"
                        if (DBUpdate.updateTokenPlayerCommand(token).modifiedCount == 1L) {
//                            kookApi.Message().create(channelId, "功能 $func 已开启", tempTargetId = userId)
                            MCBot.kookApi.Message().update(msgId, CardMessage(genFuncCard(token)))
                        } else {
                            MCBot.kookApi.Message().create(channelId, "功能 $func 设置更新失败, 请稍后再试或联系维护", tempTargetId = userId)
                        }
                    }
                }
                "rconCommand" -> {
                    if (token.rconCommand != "-1") {
                        MCBot.kookApi.Message().create(channelId, "功能 $func 已处于开启状态", tempTargetId = userId)
                    } else {
                        token.rconCommand = "0"
                        if (DBUpdate.updateTokenRconCommand(token).modifiedCount == 1L) {
//                            kookApi.Message().create(channelId, "功能 $func 已开启", tempTargetId = userId)
                            MCBot.kookApi.Message().update(msgId, CardMessage(genFuncCard(token)))
                        } else {
                            MCBot.kookApi.Message().create(channelId, "功能 $func 设置更新失败, 请稍后再试或联系维护", tempTargetId = userId)
                        }
                    }
                }
                "login" -> {
                    if (token.login != "-1") {
                        MCBot.kookApi.Message().create(channelId, "功能 $func 已处于开启状态", tempTargetId = userId)
                    } else {
                        token.login = "0"
                        if (DBUpdate.updateTokenLogin(token).modifiedCount == 1L) {
//                            kookApi.Message().create(channelId, "功能 $func 已开启", tempTargetId = userId)
                            MCBot.kookApi.Message().update(msgId, CardMessage(genFuncCard(token)))
                        } else {
                            MCBot.kookApi.Message().create(channelId, "功能 $func 设置更新失败, 请稍后再试或联系维护", tempTargetId = userId)
                        }
                    }
                }
                "logout" -> {
                    if (token.logout != "-1") {
                        MCBot.kookApi.Message().create(channelId, "功能 $func 已处于开启状态", tempTargetId = userId)
                    } else {
                        token.logout = "0"
                        if (DBUpdate.updateTokenLogout(token).modifiedCount == 1L) {
//                            kookApi.Message().create(channelId, "功能 $func 已开启", tempTargetId = userId)
                            MCBot.kookApi.Message().update(msgId, CardMessage(genFuncCard(token)))
                        } else {
                            MCBot.kookApi.Message().create(channelId, "功能 $func 设置更新失败, 请稍后再试或联系维护", tempTargetId = userId)
                        }
                    }
                }
                "status" -> {
                    if (token.status) {
                        MCBot.kookApi.Message().create(channelId, "功能 $func 已处于开启状态", tempTargetId = userId)
                    } else {
                        token.status = true
                        if (DBUpdate.updateTokenStatus(token).modifiedCount == 1L) {
//                            kookApi.Message().create(channelId, "功能 $func 已开启", tempTargetId = userId)
                            MCBot.kookApi.Message().update(msgId, CardMessage(genFuncCard(token)))
                        } else {
                            MCBot.kookApi.Message().create(channelId, "功能 $func 设置更新失败, 请稍后再试或联系维护", tempTargetId = userId)
                        }
                    }
                }
                "command" -> {
                    if (token.command) {
                        MCBot.kookApi.Message().create(channelId, "功能 $func 已处于开启状态", tempTargetId = userId)
                    } else {
                        token.command = true
                        if (DBUpdate.updateTokenCommand(token).modifiedCount == 1L) {
//                            kookApi.Message().create(channelId, "功能 $func 已开启", tempTargetId = userId)
                            MCBot.kookApi.Message().update(msgId, CardMessage(genFuncCard(token)))
                        } else {
                            MCBot.kookApi.Message().create(channelId, "功能 $func 设置更新失败, 请稍后再试或联系维护", tempTargetId = userId)
                        }
                    }
                }
                else -> {
                    MCBot.kookApi.Message().create(channelId, "未知功能 $func", tempTargetId = userId)
                }
            }
        }

        suspend fun funcDisableButtonHandler(func: String, token: Token, channelId: String, msgId: String, userId: String) {
            when (func) {
                "log" -> {
                    if (token.log == "-1") {
                        MCBot.kookApi.Message().create(channelId, "功能 $func 已处于关闭状态", tempTargetId = userId)
                    } else {
                        token.log = "-1"
                        if (DBUpdate.updateTokenLog(token).modifiedCount == 1L) {
//                            kookApi.Message().create(channelId, "功能 $func 已关闭", tempTargetId = userId)
                            MCBot.kookApi.Message().update(msgId, CardMessage(genFuncCard(token)))
                        } else {
                            MCBot.kookApi.Message().create(channelId, "功能 $func 设置更新失败, 请稍后再试或联系维护", tempTargetId = userId)
                        }
                    }
                }
                "chat" -> {
                    if (token.chat == "-1") {
                        MCBot.kookApi.Message().create(channelId, "功能 $func 已处于关闭状态", tempTargetId = userId)
                    } else {
                        token.chat = "-1"
                        if (DBUpdate.updateTokenChat(token).modifiedCount == 1L) {
//                            kookApi.Message().create(channelId, "功能 $func 已关闭", tempTargetId = userId)
                            MCBot.kookApi.Message().update(msgId, CardMessage(genFuncCard(token)))
                        } else {
                            MCBot.kookApi.Message().create(channelId, "功能 $func 设置更新失败, 请稍后再试或联系维护", tempTargetId = userId)
                        }
                    }
                }
                "playerCommand" -> {
                    if (token.playerCommand == "-1") {
                        MCBot.kookApi.Message().create(channelId, "功能 $func 已处于关闭状态", tempTargetId = userId)
                    } else {
                        token.playerCommand = "-1"
                        if (DBUpdate.updateTokenPlayerCommand(token).modifiedCount == 1L) {
//                            kookApi.Message().create(channelId, "功能 $func 已关闭", tempTargetId = userId)
                            MCBot.kookApi.Message().update(msgId, CardMessage(genFuncCard(token)))
                        } else {
                            MCBot.kookApi.Message().create(channelId, "功能 $func 设置更新失败, 请稍后再试或联系维护", tempTargetId = userId)
                        }
                    }
                }
                "rconCommand" -> {
                    if (token.rconCommand == "-1") {
                        MCBot.kookApi.Message().create(channelId, "功能 $func 已处于关闭状态", tempTargetId = userId)
                    } else {
                        token.rconCommand = "-1"
                        if (DBUpdate.updateTokenRconCommand(token).modifiedCount == 1L) {
//                            kookApi.Message().create(channelId, "功能 $func 已关闭", tempTargetId = userId)
                            MCBot.kookApi.Message().update(msgId, CardMessage(genFuncCard(token)))
                        } else {
                            MCBot.kookApi.Message().create(channelId, "功能 $func 设置更新失败, 请稍后再试或联系维护", tempTargetId = userId)
                        }
                    }
                }
                "login" -> {
                    if (token.login == "-1") {
                        MCBot.kookApi.Message().create(channelId, "功能 $func 已处于关闭状态", tempTargetId = userId)
                    } else {
                        token.login = "-1"
                        if (DBUpdate.updateTokenLogin(token).modifiedCount == 1L) {
//                            kookApi.Message().create(channelId, "功能 $func 已关闭", tempTargetId = userId)
                            MCBot.kookApi.Message().update(msgId, CardMessage(genFuncCard(token)))
                        } else {
                            MCBot.kookApi.Message().create(channelId, "功能 $func 设置更新失败, 请稍后再试或联系维护", tempTargetId = userId)
                        }
                    }
                }
                "logout" -> {
                    if (token.logout == "-1") {
                        MCBot.kookApi.Message().create(channelId, "功能 $func 已处于关闭状态", tempTargetId = userId)
                    } else {
                        token.logout = "-1"
                        if (DBUpdate.updateTokenLogout(token).modifiedCount == 1L) {
//                            kookApi.Message().create(channelId, "功能 $func 已关闭", tempTargetId = userId)
                            MCBot.kookApi.Message().update(msgId, CardMessage(genFuncCard(token)))
                        } else {
                            MCBot.kookApi.Message().create(channelId, "功能 $func 设置更新失败, 请稍后再试或联系维护", tempTargetId = userId)
                        }
                    }
                }
                "status" -> {
                    if (!token.status) {
                        MCBot.kookApi.Message().create(channelId, "功能 $func 已处于关闭状态", tempTargetId = userId)
                    } else {
                        token.status = false
                        if (DBUpdate.updateTokenStatus(token).modifiedCount == 1L) {
//                            kookApi.Message().create(channelId, "功能 $func 已关闭", tempTargetId = userId)
                            MCBot.kookApi.Message().update(msgId, CardMessage(genFuncCard(token)))
                        } else {
                            MCBot.kookApi.Message().create(channelId, "功能 $func 设置更新失败, 请稍后再试或联系维护", tempTargetId = userId)
                        }
                    }
                }
                "command" -> {
                    if (!token.command) {
                        MCBot.kookApi.Message().create(channelId, "功能 $func 已处于关闭状态", tempTargetId = userId)
                    } else {
                        token.command = false
                        if (DBUpdate.updateTokenCommand(token).modifiedCount == 1L) {
//                            kookApi.Message().create(channelId, "功能 $func 已关闭", tempTargetId = userId)
                            MCBot.kookApi.Message().update(msgId, CardMessage(genFuncCard(token)))
                        } else {
                            MCBot.kookApi.Message().create(channelId, "功能 $func 设置更新失败, 请稍后再试或联系维护", tempTargetId = userId)
                        }
                    }
                }
                else -> {
                    MCBot.kookApi.Message().create(channelId, "未知功能 $func", tempTargetId = userId)
                }
            }
        }
    }
}