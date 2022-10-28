package com.github.hank9999.mcbot.database

import com.github.hank9999.mcbot.database.types.Permission
import com.github.hank9999.mcbot.database.DB.Companion.permissions
import com.github.hank9999.mcbot.database.DB.Companion.tokens
import com.github.hank9999.mcbot.database.types.Token
import com.mongodb.client.result.UpdateResult
import org.litote.kmongo.eq
import org.litote.kmongo.setValue

class DBUpdate {
    companion object {
        suspend fun updatePermission(permission: Permission): UpdateResult {
            return permissions.replaceOne(Permission::token eq permission.token, permission)
        }

        suspend fun updatePermissionAdmin(permission: Permission): UpdateResult {
            return permissions.updateOne(Permission::token eq permission.token, setValue(Permission::admins, permission.admins))
        }

        suspend fun updatePermissionChat(permission: Permission): UpdateResult {
            return permissions.updateOne(Permission::token eq permission.token, setValue(Permission::chat, permission.chat))
        }

        suspend fun updatePermissionStatus(permission: Permission): UpdateResult {
            return permissions.updateOne(Permission::token eq permission.token, setValue(Permission::status, permission.status))
        }

        suspend fun updatePermissionCommand(permission: Permission): UpdateResult {
            return permissions.updateOne(Permission::token eq permission.token, setValue(Permission::command, permission.command))
        }

        suspend fun updateToken(token: Token): UpdateResult {
            return tokens.replaceOne(Token::token eq token.token, token)
        }

        suspend fun updateTokenGuild(token: Token): UpdateResult {
            return tokens.updateOne(Token::token eq token.token, setValue(Token::guild, token.guild))
        }

        suspend fun updateTokenMainChannel(token: Token): UpdateResult {
            return tokens.updateOne(Token::token eq token.token, setValue(Token::mainChannel, token.mainChannel))
        }

        suspend fun updateTokenLog(token: Token): UpdateResult {
            return tokens.updateOne(Token::token eq token.token, setValue(Token::log, token.log))
        }

        suspend fun updateTokenChat(token: Token): UpdateResult {
            return tokens.updateOne(Token::token eq token.token, setValue(Token::chat, token.chat))
        }

        suspend fun updateTokenPlayerCommand(token: Token): UpdateResult {
            return tokens.updateOne(Token::token eq token.token, setValue(Token::playerCommand, token.playerCommand))
        }

        suspend fun updateTokenRconCommand(token: Token): UpdateResult {
            return tokens.updateOne(Token::token eq token.token, setValue(Token::rconCommand, token.rconCommand))
        }

        suspend fun updateTokenLogin(token: Token): UpdateResult {
            return tokens.updateOne(Token::token eq token.token, setValue(Token::login, token.login))
        }

        suspend fun updateTokenLogout(token: Token): UpdateResult {
            return tokens.updateOne(Token::token eq token.token, setValue(Token::logout, token.logout))
        }

        suspend fun updateTokenTellraw(token: Token): UpdateResult {
            return tokens.updateOne(Token::token eq token.token, setValue(Token::tellraw, token.tellraw))
        }

        suspend fun updateTokenCommand(token: Token): UpdateResult {
            return tokens.updateOne(Token::token eq token.token, setValue(Token::command, token.command))
        }

        suspend fun updateTokenStatus(token: Token): UpdateResult {
            return tokens.updateOne(Token::token eq token.token, setValue(Token::status, token.status))
        }
    }
}