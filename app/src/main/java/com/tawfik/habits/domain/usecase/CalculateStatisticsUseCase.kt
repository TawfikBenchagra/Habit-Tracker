package com.tawfik.habits.domain.usecase

import com.tawfik.habits.data.repository.EntryRepository
import com.tawfik.habits.domain.model.Statistics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CalculateStatisticsUseCase @Inject constructor(
    private val entryRepository: EntryRepository
) {

    operator fun invoke(habitId: Int): Flow<Statistics> {

        return entryRepository.getAllEntriesStream(habitId).map { list ->
            Statistics(list.size, entryRepository.getOldestEntry(habitId)?.date)
        }

    }

}