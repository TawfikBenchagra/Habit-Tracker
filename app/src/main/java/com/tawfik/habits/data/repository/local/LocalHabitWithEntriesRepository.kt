package com.tawfik.habits.data.repository.local

import com.tawfik.habits.data.database.dao.HabitWithEntriesDao
import com.tawfik.habits.data.model.HabitWithEntries
import com.tawfik.habits.data.repository.HabitWithEntriesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalHabitWithEntriesRepository @Inject constructor(
    private val habitWithEntriesDao: HabitWithEntriesDao
) : HabitWithEntriesRepository {

    override fun getHabitsWithEntries(): Flow<List<HabitWithEntries>> {
        return habitWithEntriesDao.getHabitsWithEntriesStream()
    }

}