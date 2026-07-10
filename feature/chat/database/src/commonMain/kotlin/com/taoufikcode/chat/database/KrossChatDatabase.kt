package com.taoufikcode.chat.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import com.taoufikcode.chat.database.dao.ChatDao
import com.taoufikcode.chat.database.dao.ChatReadStateDao
import com.taoufikcode.chat.database.dao.MessageDao
import com.taoufikcode.chat.database.dao.ParticipantDao
import com.taoufikcode.chat.database.dao.ChatParticipantsJoinDao
import com.taoufikcode.chat.database.entities.ChatEntity
import com.taoufikcode.chat.database.entities.ChatReadStateEntity
import com.taoufikcode.chat.database.entities.MessageEntity
import com.taoufikcode.chat.database.entities.ChatParticipantJoin
import com.taoufikcode.chat.database.entities.ParticipantEntity
import com.taoufikcode.chat.database.view.LastMessageView

@Database(
    entities = [
        ChatEntity::class,
        ParticipantEntity::class,
        MessageEntity::class,
        ChatParticipantJoin::class,
        ChatReadStateEntity::class,
    ],
    views = [
        LastMessageView::class
    ],
    version = 2,
)
@ConstructedBy(KrossChatDatabaseConstructor::class)
abstract class KrossChatDatabase: RoomDatabase() {
    abstract val chatDao: ChatDao
    abstract val participantDao: ParticipantDao
    abstract val messageDao: MessageDao
    abstract val chatParticipantsJoinDao: ChatParticipantsJoinDao
    abstract val chatReadStateDao: ChatReadStateDao

    companion object {
        const val DB_NAME = "kross.db"
    }
}