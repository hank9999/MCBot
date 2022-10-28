package com.github.hank9999.mcbot.database

import com.github.hank9999.mcbot.database.DB.Companion.permissions
import com.github.hank9999.mcbot.database.DB.Companion.tokens
import com.github.hank9999.mcbot.database.types.Permission
import com.github.hank9999.mcbot.database.types.Token
import com.mongodb.client.result.InsertOneResult

class DBCreate {
    companion object {
        suspend fun createToken(token: Token): InsertOneResult {
            return tokens.insertOne(token)
        }

        suspend fun createPermission(permission: Permission): InsertOneResult {
            return permissions.insertOne(permission)
        }
    }
}