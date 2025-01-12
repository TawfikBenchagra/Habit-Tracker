package com.tawfik.habits.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.tawfik.habits.data.model.HabitWithEntries
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitWithEntriesDao {

    @Transaction
    @Query("SELECT * FROM habit")
    fun getHabitsWithEntriesStream(): Flow<List<HabitWithEntries>>

}