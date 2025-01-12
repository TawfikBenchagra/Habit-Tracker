package com.tawfik.habits.domain.usecase

import com.tawfik.habits.data.model.Reminder
import com.tawfik.habits.data.repository.HabitRepository
import com.tawfik.habits.data.repository.ReminderRepository
import com.tawfik.habits.domain.model.HabitData
import com.tawfik.habits.domain.model.HabitReminderType
import com.tawfik.habits.domain.model.toHabit
import com.tawfik.habits.util.ReminderManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import javax.inject.Inject

class SaveHabitUseCase(
    private val habitRepository: HabitRepository,
    private val reminderRepository: ReminderRepository,
    private val reminderManager: ReminderManager,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    @Inject
    constructor(
        habitRepository: HabitRepository,
        reminderRepository: ReminderRepository,
        reminderManager: ReminderManager
    ) : this(habitRepository, reminderRepository, reminderManager, Dispatchers.IO)

    suspend operator fun invoke(data: HabitData, habitId: Int? = null) =
        withContext(ioDispatcher) {
            if (habitId == null) {
                val id = habitRepository.insertHabit(data.toHabit()).toInt()
                saveAndScheduleReminders(data, id)
            } else {
                habitRepository.upsertHabit(data.toHabit(habitId))
                clearAllReminders(habitId)
                saveAndScheduleReminders(data, habitId)
            }
        }

    private suspend fun clearAllReminders(habitId: Int) {
        reminderManager.unscheduleAllReminders(habitId)
        reminderRepository.clearReminders(habitId)
    }

    private suspend fun saveAndScheduleReminders(data: HabitData, habitId: Int) {
        saveReminders(data, habitId)
        reminderManager.scheduleAllReminders(habitId)
    }

    private suspend fun saveReminders(data: HabitData, habitId: Int) {
        when (data.reminderType) {
            HabitReminderType.NONE -> return

            HabitReminderType.EVERYDAY -> {
                for (day in DayOfWeek.entries) {
                    val reminder = Reminder(
                        habitId = habitId,
                        time = data.reminderTime,
                        day = day
                    )
                    reminderRepository.insertReminder(reminder)
                }
            }

            HabitReminderType.SPECIFIC -> {
                for (day in data.reminderDays) {
                    val reminder = Reminder(
                        habitId = habitId,
                        time = data.reminderTime,
                        day = day
                    )
                    reminderRepository.insertReminder(reminder)
                }
            }
        }
    }

}