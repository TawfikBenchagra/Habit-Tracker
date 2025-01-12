package com.tawfik.habits.data.repository

import com.tawfik.habits.data.model.HabitWithEntries
import kotlinx.coroutines.flow.Flow

interface HabitWithEntriesRepository {

    fun getHabitsWithEntries(): Flow<List<HabitWithEntries>>

}