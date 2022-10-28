package com.github.hank9999.mcbot.permission

import com.github.hank9999.mcbot.database.DBRead
import com.github.hank9999.mcbot.database.DBUpdate
import com.github.hank9999.mcbot.database.types.Token
import com.github.hank9999.mcbot.permission.types.Status

class PMDel {
    companion object {
        suspend fun delAdmin(token: Token, users: List<String> = emptyList(), roles: List<Int> = emptyList()): Status {
            val permission = DBRead.readPermission(token) ?: return Status.NotFound
            users.forEach { if (permission.admins.users.contains(it)) {
                permission.admins.users.remove(it)
            } }
            roles.forEach { if (permission.admins.roles.contains(it)) {
                permission.admins.roles.remove(it)
            } }
            return when (DBUpdate.updatePermissionAdmin(permission).modifiedCount) {
                0L -> Status.NoChange
                1L -> Status.Success
                else -> Status.Failed
            }
        }

        suspend fun delChat(token: Token, users: List<String> = emptyList(), roles: List<Int> = emptyList()): Status {
            val permission = DBRead.readPermission(token) ?: return Status.NotFound
            users.forEach { if (permission.chat.users.contains(it)) {
                permission.chat.users.remove(it)
            } }
            roles.forEach { if (permission.chat.roles.contains(it)) {
                permission.chat.roles.remove(it)
            } }
            return when (DBUpdate.updatePermissionChat(permission).modifiedCount) {
                0L -> Status.NoChange
                1L -> Status.Success
                else -> Status.Failed
            }
        }

        suspend fun delStatus(token: Token, users: List<String> = emptyList(), roles: List<Int> = emptyList()): Status {
            val permission = DBRead.readPermission(token) ?: return Status.NotFound
            users.forEach { if (permission.status.users.contains(it)) {
                permission.status.users.remove(it)
            } }
            roles.forEach { if (permission.status.roles.contains(it)) {
                permission.status.roles.remove(it)
            } }
            return when (DBUpdate.updatePermissionStatus(permission).modifiedCount) {
                0L -> Status.NoChange
                1L -> Status.Success
                else -> Status.Failed
            }
        }

        suspend fun delCommand(token: Token, users: List<String> = emptyList(), roles: List<Int> = emptyList()): Status {
            val permission = DBRead.readPermission(token) ?: return Status.NotFound
            users.forEach { if (permission.command.users.contains(it)) {
                permission.command.users.remove(it)
            } }
            roles.forEach { if (permission.command.roles.contains(it)) {
                permission.command.roles.remove(it)
            } }
            return when (DBUpdate.updatePermissionCommand(permission).modifiedCount) {
                0L -> Status.NoChange
                1L -> Status.Success
                else -> Status.Failed
            }
        }
    }
}