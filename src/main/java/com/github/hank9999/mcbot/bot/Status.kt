package com.github.hank9999.mcbot.bot

import com.github.hank9999.mcbot.MCBot.connector
import com.github.hank9999.mcbot.bot.types.StatusInfo
import com.github.hank9999.mcbot.bot.types.TStatus
import com.github.hank9999.mcbot.connector.types.Receive
import com.github.hank9999.mcbot.database.types.Token
import kotlinx.coroutines.delay
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class Status {
    companion object {
        private val statusMap: MutableMap<String, MutableList<TStatus>> = mutableMapOf()

//        fun sendStatusMessage(guild: String) {
//            val token = DBRead.getTokenByGuild(guild) ?: return
//            sendStatusMessage(token)
//        }

        suspend fun execStatus(token: Token): List<StatusInfo> {
            val sn = sendStatusMessage(token)
            if (sn == -1) {
                return listOf()
            }
            delay(500)
            return checkStatus(token, sn)
        }

        fun sendStatusMessage(token: Token): Int {
            val ctxMap = connector.getCtxMap()
            if (!ctxMap.containsKey(token.token)) {
                return -1
            }
            ctxMap[token.token]!!.forEach {
                val sn = connector.handler.sn.incrementAndGet()
                if (!statusMap.containsKey(token.token)) {
                    statusMap[token.token] = mutableListOf()
                }
                statusMap[token.token]!!.add(TStatus(sn))
                it.ctx.send(buildJsonObject {
                    put("type", "status")
                    put("name", "__ALL__")
                    put("sn", sn)
                }.toString())
                return sn
            }
            return -1
        }

        fun receiveStatus(data: Receive, token: Token, name: String) {
            if (!statusMap.containsKey(token.token) || statusMap[token.token]!!.isEmpty()) {
                return
            }
            val result = statusMap[token.token]!!.find { it.sn == data.sn } ?: return
            result.list.add(StatusInfo(name, data.version!!, data.onlinePlayer!!))
        }

//        fun checkStatus(guild: String, sn: Int) {
//            val token = DBRead.getTokenByGuild(guild) ?: return
//            checkStatus(token, sn)
//        }

        fun checkStatus(token: Token, sn: Int): List<StatusInfo> {
            if (!statusMap.containsKey(token.token) || statusMap[token.token]!!.isEmpty()) {
                return listOf()
            }
            val result = statusMap[token.token]!!.find { it.sn == sn } ?: return listOf()
            val returnResult = result.list.toList()
            statusMap[token.token]!!.remove(result)
            val check = statusMap[token.token]!!.find { it.sn == sn }
            if (check != null) {
                statusMap[token.token]!!.remove(check)
            }
            return returnResult
        }
    }
}