package com.tawfik.habits.data.repository.local

import com.tawfik.habits.data.database.dao.HabitDao
import com.tawfik.habits.data.model.Habit
import com.tawfik.habits.data.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalHabitRepository @Inject constructor(
    private val habitDao: HabitDao
) : HabitRepository {

    override fun getAllHabitsStream(): Flow<List<Habit>> = habitDao.getAllHabitsStream()

    override fun getHabitStream(habitId: Int): Flow<Habit?> = habitDao.getHabitStream(habitId)

    override suspend fun getHabit(habitId: Int): Habit? = habitDao.getHabit(habitId)

    override suspend fun insertHabit(habit: Habit): Long = habitDao.insert(habit)

    override suspend fun upsertHabit(habit: Habit) =
        habitDao.upsert(habit)

    override suspend fun deleteHabit(habitId: Int) {
        val habit = habitDao.getHabit(habitId)
        if (habit != null) {
            habitDao.delete(habit)
        }
    }

}