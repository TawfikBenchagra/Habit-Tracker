package com.tawfik.habits.domain.model

import java.time.LocalDate

data class Streak(
    val length: Int,
    val endDate: LocalDate,
)
