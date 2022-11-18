package com.github.hank9999.mcbot.permission

import com.github.hank9999.kook.types.User
import com.github.hank9999.mcbot.database.DBRead
import com.github.hank9999.mcbot.database.types.Token
import com.github.hank9999.mcbot.MCBot.kookApi

class PMCheck {
    data class GuildMasterCacheData(
        val guild: String,
        val master: String,
        val time: Long
    )

    companion object {
        private val guildMasterCache: MutableList<GuildMasterCacheData> = mutableListOf()
        suspend fun checkGuildMaster(token: Token, userId: String): Boolean {
            return checkGuildMaster(token.guild, userId)
        }

        fun removeCache(item: GuildMasterCacheData) {
            guildMasterCache.remove(item)
        }

        fun getCache(): List<GuildMasterCacheData> {
            return guildMasterCache.toList()
        }

        suspend fun checkGuildMaster(guildId: String, userId: String): Boolean {
            var cacheData = guildMasterCache.find { it.guild == guildId }
            if (cacheData != null) {
                if (System.currentTimeMillis() - cacheData.time >= 10 * 60 * 1000) {
                    guildMasterCache.remove(cacheData)
                    cacheData = null
                }
            }
            val guildMasterId: String = cacheData?.master ?: kookApi.Guild().view(guildId).masterId
            if (cacheData == null) {
                guildMasterCache.add(GuildMasterCacheData(guildId, guildMasterId, System.currentTimeMillis()))
            }
            return guildMasterId == userId
        }

        suspend fun checkAdmin(token: Token, user: User): Boolean {
            val permission = DBRead.readPermission(token) ?: return false
            if (permission.admins.roles.contains(0)) {
                return true
            }
            return when {
                permission.admins.users.contains(user.id) -> true
                else -> (permission.admins.roles.toSet() intersect user.roles.toSet()).isNotEmpty()
            }
        }

        suspend fun checkChat(token: Token, user: User): Boolean {
            val permission = DBRead.readPermission(token) ?: return false
            if (permission.chat.roles.contains(0)) {
                return true
            }
            return when {
                permission.chat.users.contains(user.id) -> true
                else -> (permission.chat.roles.toSet() intersect user.roles.toSet()).isNotEmpty()
            }
        }

        suspend fun checkStatus(token: Token, user: User): Boolean {
            val permission = DBRead.readPermission(token) ?: return false
            if (permission.status.roles.contains(0)) {
                return true
            }
            return when {
                permission.status.users.contains(user.id) -> true
                else -> (permission.status.roles.toSet() intersect user.roles.toSet()).isNotEmpty()
            }
        }

        suspend fun checkCommand(token: Token, user: User): Boolean {
            val permission = DBRead.readPermission(token) ?: return false
            if (permission.command.roles.contains(0)) {
                return true
            }
            return when {
                permission.command.users.contains(user.id) -> true
                else -> (permission.command.roles.toSet() intersect user.roles.toSet()).isNotEmpty()
            }
        }

        suspend fun checkStatus(token: Token, roleId: Int): Boolean {
            val permission = DBRead.readPermission(token) ?: return false
            return permission.status.roles.find { it == roleId } != null
        }

        suspend fun checkChat(token: Token, roleId: Int): Boolean {
            val permission = DBRead.readPermission(token) ?: return false
            return permission.chat.roles.find { it == roleId } != null
        }

        suspend fun checkCommand(token: Token, roleId: Int): Boolean {
            val permission = DBRead.readPermission(token) ?: return false
            return permission.command.roles.find { it == roleId } != null
        }

        suspend fun checkAdmin(token: Token, roleId: Int): Boolean {
            val permission = DBRead.readPermission(token) ?: return false
            return permission.admins.roles.find { it == roleId } != null
        }
    }
}