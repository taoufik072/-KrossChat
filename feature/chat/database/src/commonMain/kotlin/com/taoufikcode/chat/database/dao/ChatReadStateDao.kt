package com.taoufikcode.chat.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Upsert
import com.taoufikcode.chat.database.entities.ChatReadStateEntity

@Dao
interface ChatReadStateDao {

    @Upsert
    suspend fun upsertReadState(state: ChatReadStateEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertReadStatesIfAbsent(states: List<ChatReadStateEntity>)
}
