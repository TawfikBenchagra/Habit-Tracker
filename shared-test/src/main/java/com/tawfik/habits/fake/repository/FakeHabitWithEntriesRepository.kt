package com.tawfik.habits.fake.repository

import com.tawfik.habits.data.database.dao.HabitWithEntriesDao
import com.tawfik.habits.data.model.HabitWithEntries
import com.tawfik.habits.data.repository.EntryRepository
import com.tawfik.habits.data.repository.HabitRepository
import com.tawfik.habits.data.repository.HabitWithEntriesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class FakeHabitWithEntriesRepository : HabitWithEntriesRepository {

    private var habitRepository: HabitRepository? = null
    private var entryRepository: EntryRepository? = null
    private var habitWithEntriesDao: HabitWithEntriesDao? = null

    constructor(habitRepository: HabitRepository, entryRepository: EntryRepository) {
        this.habitRepository = habitRepository
        this.entryRepository = entryRepository
    }

    constructor(habitWithEntriesDao: HabitWithEntriesDao?) {
        this.habitWithEntriesDao = habitWithEntriesDao
    }

    override fun getHabitsWithEntries(): Flow<List<HabitWithEntries>> {
        if (habitWithEntriesDao != null) return habitWithEntriesDao!!.getHabitsWithEntriesStream()
        return combine(
            habitRepository!!.getAllHabitsStream(),
            entryRepository!!.getAllEntriesStream()
        ) { habitList, entryList ->
            habitList.map { habit ->
                HabitWithEntries(habit, entryList.filter { it.habitId == habit.id })
            }
        }
    }

}