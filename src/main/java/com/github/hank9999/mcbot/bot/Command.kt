package com.github.hank9999.mcbot.bot

import com.github.hank9999.mcbot.MCBot
import com.github.hank9999.mcbot.bot.types.TCommand
import com.github.hank9999.mcbot.connector.types.Receive
import com.github.hank9999.mcbot.database.types.Token
import kotlinx.coroutines.delay
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class Command {
    companion object {
        private val commandMap: MutableMap<String, MutableList<TCommand>> = mutableMapOf()

//        suspend fun executeCommand(guild: String, command: String, name: String): TCommand? {
//            val token = DBRead.getTokenByGuild(guild) ?: return
//            return executeCommand(token, command, name)
//        }

        suspend fun execCommand(token: Token, command: String, name: String): TCommand? {
            val sn = sendCommandMessage(token, command, name)
            var result: TCommand?
            val startTime = System.currentTimeMillis()
            while (true) {
                result = checkCommand(token, sn)
                if (result != null) {
                    break
                }
                if ((System.currentTimeMillis() - startTime) > 5 * 1000) {
                    break
                }
                delay(100)
            }
            return result
        }

//        fun sendCommandMessage(guild: String, command: String, name: String) {
//            val token = DBRead.getTokenByGuild(guild) ?: return
//            sendCommandMessage(token, command, name)
//        }

        fun sendCommandMessage(token: Token, command: String, name: String): Int {
            val ctxMap = MCBot.connector.getCtxMap()
            if (!ctxMap.containsKey(token.token)) {
                return -1
            }
            ctxMap[token.token]!!.forEach {
                val sn = MCBot.connector.handler.sn.incrementAndGet()
                it.ctx.send(buildJsonObject {
                    put("type", "command")
                    put("name", name)
                    put("sn", sn)
                    put("command", command)
                }.toString())
                return sn
            }
            return -1
        }

        fun receiveCommand(data: Receive, token: Token, name: String) {
            if (!commandMap.containsKey(token.token)) {
                commandMap[token.token] = mutableListOf()
            }
            commandMap[token.token]!!.add(TCommand(data.sn!!, name, data.commandReturn!!))
        }

//        fun checkCommand(guild: String, sn: Int) {
//            val token = DBRead.getTokenByGuild(guild) ?: return
//            checkCommand(token, sn)
//        }

        fun checkCommand(token: Token, sn: Int): TCommand? {
            if (!commandMap.containsKey(token.token) || commandMap[token.token]!!.isEmpty()) {
                return null
            }
            val result = commandMap[token.token]!!.find { it.sn == sn } ?: return null
            commandMap[token.token]!!.remove(result)
            return result
        }
    }
}