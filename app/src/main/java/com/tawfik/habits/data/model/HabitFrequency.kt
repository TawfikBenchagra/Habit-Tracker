package com.tawfik.habits.data.model

import androidx.annotation.StringRes
import com.tawfik.habits.R

enum class HabitFrequency(@StringRes val userReadableStringRes: Int) {
    DAILY(R.string.frequency_daily),
    WEEKLY(R.string.frequency_weekly)
}
