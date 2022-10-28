package com.github.hank9999.mcbot.database

import com.github.hank9999.mcbot.database.DB.Companion.tokens
import com.github.hank9999.mcbot.database.DB.Companion.permissions
import com.github.hank9999.mcbot.database.types.Permission
import com.github.hank9999.mcbot.database.types.Token
import org.litote.kmongo.*

class DBRead {
    companion object {

        suspend fun getToken(token: String): Token? {
            return tokens.findOne(Token::token eq token)
        }

        suspend fun getTokenByGuild(guild: String): Token? {
            return tokens.findOne(Token::guild eq guild)
        }

        suspend fun readPermission(token: Token): Permission? {
            return permissions.findOne(Permission::token eq token.token)
        }
    }
}