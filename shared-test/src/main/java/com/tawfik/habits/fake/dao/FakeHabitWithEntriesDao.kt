package com.tawfik.habits.fake.dao

import com.tawfik.habits.data.database.dao.EntryDao
import com.tawfik.habits.data.database.dao.HabitDao
import com.tawfik.habits.data.database.dao.HabitWithEntriesDao
import com.tawfik.habits.data.model.HabitWithEntries
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class FakeHabitWithEntriesDao(
    private val habitDao: HabitDao,
    private val entryDao: EntryDao
) : HabitWithEntriesDao {

    override fun getHabitsWithEntriesStream(): Flow<List<HabitWithEntries>> {
        return combine(
            habitDao.getAllHabitsStream(),
            entryDao.getAllEntriesStream()
        ) { habitList, entryList ->
            habitList.map { habit ->
                HabitWithEntries(habit, entryList.filter { it.habitId == habit.id })
            }
        }
    }

}