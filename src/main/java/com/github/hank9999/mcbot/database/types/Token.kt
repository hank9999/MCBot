package com.github.hank9999.mcbot.database.types


// -1 -> 未设置频道ID 不启用, 0 -> 启用在默认频道, 其他 -> 启用且在对应频道
data class Token(
    val token: String,
    var guild: String = "",
    var mainChannel: String = "",
    var log: String = "-1",
    var chat: String = "-1",
    var playerCommand: String = "-1",
    var rconCommand: String = "-1",
    var login: String = "-1",
    var logout: String = "-1",
    var tellraw: String = "[\"\",{\"text\":\"<\",\"color\":\"white\"},{\"text\":\"%playerId%\",\"color\":\"gold\"},{\"text\":\"> \",\"color\":\"white\"},{\"text\":\"%text%\",\"color\":\"white\"}]",
    var command: Boolean = false,
    var status: Boolean = false
)