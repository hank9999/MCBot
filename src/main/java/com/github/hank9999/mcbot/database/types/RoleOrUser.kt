package com.github.hank9999.mcbot.database.types

import kotlinx.serialization.Serializable

@Serializable
data class RoleOrUser(
    val roles: MutableList<Int> = mutableListOf(),
    val users: MutableList<String> = mutableListOf()
) {
    fun clear() {
        roles.clear()
        users.clear()
    }
}
