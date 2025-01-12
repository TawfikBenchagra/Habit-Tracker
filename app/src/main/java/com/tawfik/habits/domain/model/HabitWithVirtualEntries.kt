package com.tawfik.habits.domain.model

import com.tawfik.habits.data.model.Habit

data class HabitWithVirtualEntries(
    val habit: Habit,
    val entries: List<VirtualEntry>
)