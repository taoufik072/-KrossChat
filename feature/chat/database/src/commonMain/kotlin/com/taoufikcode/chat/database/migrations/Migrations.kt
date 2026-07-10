package com.taoufikcode.chat.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `ChatReadStateEntity` (
                `chatId` TEXT NOT NULL,
                `lastReadAt` INTEGER NOT NULL,
                PRIMARY KEY(`chatId`),
                FOREIGN KEY(`chatId`) REFERENCES `ChatEntity`(`chatId`) ON DELETE CASCADE
            )
            """.trimIndent()
        )
    }
}
