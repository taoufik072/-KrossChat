package com.taoufikcode.chat.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.taoufikcode.chat.database.dao.ChatDao
import com.taoufikcode.chat.database.dao.ChatMessageDao
import com.taoufikcode.chat.database.dao.ChatParticipantDao
import com.taoufikcode.chat.database.dao.ChatParticipantsCrossRefDao
import com.taoufikcode.chat.database.entities.ChatEntity
import com.taoufikcode.chat.database.entities.ChatMessageEntity
import com.taoufikcode.chat.database.entities.ChatParticipantCrossRef
import com.taoufikcode.chat.database.entities.ChatParticipantEntity
import com.taoufikcode.chat.database.view.LastMessageView

@Database(
    entities = [
        ChatEntity::class,
        ChatParticipantEntity::class,
        ChatMessageEntity::class,
        ChatParticipantCrossRef::class,
    ],
    views = [
        LastMessageView::class
    ],
    version = 1,
)
abstract class KrossChatDatabase: RoomDatabase() {
    abstract val chatDao: ChatDao
    abstract val chatParticipantDao: ChatParticipantDao
    abstract val chatMessageDao: ChatMessageDao
    abstract val chatParticipantsCrossRefDao: ChatParticipantsCrossRefDao

    companion object {
        const val DB_NAME = "kross.db"
    }
}