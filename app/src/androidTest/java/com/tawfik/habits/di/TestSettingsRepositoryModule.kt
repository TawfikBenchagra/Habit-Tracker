package com.tawfik.habits.di

import com.tawfik.habits.data.repository.SettingsRepository
import com.tawfik.habits.fake.repository.FakeSettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton


@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [SettingsRepositoryModule::class]
)
class TestSettingsRepositoryModule {

    @Singleton
    @Provides
    fun provideFakeSettingsRepository(): SettingsRepository {
        return FakeSettingsRepository()
    }


}