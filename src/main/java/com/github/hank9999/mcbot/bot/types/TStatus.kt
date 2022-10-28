package com.github.hank9999.mcbot.bot.types

data class TStatus(
    val sn: Int,
    val list: MutableList<StatusInfo> = mutableListOf()
)