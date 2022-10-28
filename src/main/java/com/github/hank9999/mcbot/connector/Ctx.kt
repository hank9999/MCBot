package com.github.hank9999.mcbot.connector

import io.javalin.websocket.WsConnectContext

data class Ctx(
    val name: String,
    val ctx: WsConnectContext
)