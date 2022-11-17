package com.github.hank9999.mcbot.database.types

import kotlinx.serialization.Serializable

@Serializable
data class Permission(
    val token: String,
    val admins: RoleOrUser = RoleOrUser(),
    val chat: RoleOrUser = RoleOrUser(),
    val status: RoleOrUser = RoleOrUser(),
    val command: RoleOrUser = RoleOrUser()
) {
    fun clear() {
        admins.clear()
        chat.clear()
        status.clear()
        command.clear()
    }
}