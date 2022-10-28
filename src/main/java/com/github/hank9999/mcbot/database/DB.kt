package com.github.hank9999.mcbot.database

import com.github.hank9999.mcbot.Config
import com.github.hank9999.mcbot.database.types.Permission
import com.github.hank9999.mcbot.database.types.Token
import org.litote.kmongo.reactivestreams.*
import org.litote.kmongo.coroutine.*

class DB {
    companion object {
        private val conn  = if (Config.DataBase.user!!.isEmpty() || Config.DataBase.password!!.isEmpty()) {
            "mongodb://${Config.DataBase.host}:${Config.DataBase.port}"
        } else {
            "mongodb://${Config.DataBase.user}:${Config.DataBase.password}@${Config.DataBase.host}:${Config.DataBase.port}"
        }
        private val client = KMongo.createClient(conn).coroutine
        private val db = client.getDatabase(Config.DataBase.database!!)
        val tokens = db.getCollection<Token>()
        val permissions = db.getCollection<Permission>()
    }
}