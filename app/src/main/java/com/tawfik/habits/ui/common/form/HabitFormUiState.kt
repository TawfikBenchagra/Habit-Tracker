package com.tawfik.habits.ui.common.form

import com.tawfik.habits.common.HABIT_NAME_MAX_CHARACTER_LIMIT
import com.tawfik.habits.common.HABIT_NAME_MIN_CHARACTER_LIMIT
import com.tawfik.habits.data.model.HabitFrequency
import com.tawfik.habits.domain.model.HabitData
import com.tawfik.habits.domain.model.HabitReminderType
import java.time.DayOfWeek
import java.time.LocalTime

sealed class HabitFormUiState {

    data object Loading : HabitFormUiState()

    data class Data(
        val name: String = "",
        val frequency: HabitFrequency = HabitFrequency.DAILY,
        val repeat: Int = 1,
        val reminderType: HabitReminderType = HabitReminderType.NONE,
        val reminderTime: LocalTime = LocalTime.NOON,
        val reminderDays: Set<DayOfWeek> = setOf(),
        val nameIsInvalid: Boolean = false,
        val daysIsInvalid: Boolean = false,
        val notificationPermissionDialogShown: Boolean = false,
        val alarmPermissionDialogShown: Boolean = false
    ) : HabitFormUiState() {


        fun isDaysValid(): Boolean {
            return !(reminderType == HabitReminderType.SPECIFIC && reminderDays.isEmpty())
        }

        fun isNameValid(): Boolean {
            return (name.length in HABIT_NAME_MIN_CHARACTER_LIMIT..HABIT_NAME_MAX_CHARACTER_LIMIT &&
                    !name.contains("\n"))
        }

    }

}

fun HabitFormUiState.Data.toHabitData(): HabitData {
    return HabitData(
        name = this.name,
        frequency = this.frequency,
        repeat = this.repeat,
        reminderType = this.reminderType,
        reminderTime = this.reminderTime,
        reminderDays = this.reminderDays
    )
}