package com.taoufikcode.chat.database

import androidx.room.RoomDatabaseConstructor

@Suppress("KotlinNoActualForExpect", "NO_ACTUAL_FOR_EXPECT")
expect object KrossChatDatabaseConstructor: RoomDatabaseConstructor<KrossChatDatabase> {
    override fun initialize(): KrossChatDatabase
}