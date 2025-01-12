package com.tawfik.habits.domain.model

import java.time.LocalDate

data class VirtualEntry(
    val id: Int?,
    val habitId: Int,
    val date: LocalDate
)