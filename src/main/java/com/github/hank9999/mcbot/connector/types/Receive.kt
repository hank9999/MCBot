package com.github.hank9999.mcbot.connector.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Receive(
    val type: String,
    val log: String? = null,
    val username: String? = null,
    val uuid: String? = null,
    val text: String? = null,
    val command: String? = null,
    val sn: Int? = null,
    val version: String? = null,
    val onlinePlayer: List<String>? = null,
    @SerialName("return") val commandReturn: String? = null
)
