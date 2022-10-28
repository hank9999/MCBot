package com.github.hank9999.mcbot.bot

enum class FuncList(val str: String) {
    Log("log"),
    Chat("chat"),
    PlayerCommand("playerCommand"),
    RconCommand("rconCommand"),
    Login("login"),
    Logout("logout"),
    Status("status"),
    Command("command");

    companion object {
        fun fromString(str: String): FuncList? {
            var data: FuncList? = null
            FuncList.values().forEach {
                if (it.str.equals(str, ignoreCase = true)) {
                    data = it
                }
            }
            return data
        }
    }
}