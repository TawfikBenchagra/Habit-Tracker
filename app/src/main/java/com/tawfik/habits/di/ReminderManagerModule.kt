package com.tawfik.habits.di

import com.tawfik.habits.util.LocalReminderManager
import com.tawfik.habits.util.ReminderManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ReminderManagerModule {

    @Binds
    abstract fun bindReminderManager(
        localReminderManager: LocalReminderManager
    ): ReminderManager

}