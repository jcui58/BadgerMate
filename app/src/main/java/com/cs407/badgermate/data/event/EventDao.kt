package com.cs407.badgermate.data.event

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEvent(event: EventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(events: List<EventEntity>)

    @Query("SELECT * FROM events ORDER BY startTime ASC")
    fun getAllEvents(): List<EventEntity>

    @Query("UPDATE events SET isMyEvent = :flag WHERE id = :eventId")
    fun updateInterested(eventId: Int, flag: Boolean)

    @Query("DELETE FROM events")
    fun clearAll()
}
