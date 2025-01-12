package com.tawfik.habits.domain.model

import java.time.LocalDate

data class Statistics(
    val total: Int,
    val started: LocalDate?
)
