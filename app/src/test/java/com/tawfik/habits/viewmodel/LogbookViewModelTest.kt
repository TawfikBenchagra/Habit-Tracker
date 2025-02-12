package com.tawfik.habits.viewmodel

import com.tawfik.habits.data.TestData.entryListF
import com.tawfik.habits.data.TestData.habit1
import com.tawfik.habits.data.TestData.habit2
import com.tawfik.habits.data.TestData.habit3
import com.tawfik.habits.data.TestData.habit4
import com.tawfik.habits.domain.usecase.GetHabitsWithVirtualEntriesUseCase
import com.tawfik.habits.domain.usecase.GetVirtualEntriesUseCase
import com.tawfik.habits.fake.repository.FakeEntryRepository
import com.tawfik.habits.fake.repository.FakeHabitRepository
import com.tawfik.habits.rules.TestDispatcherRule
import com.tawfik.habits.ui.screens.logbook.LogbookUiState
import com.tawfik.habits.ui.screens.logbook.LogbookViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@OptIn(ExperimentalCoroutinesApi::class)
class LogbookViewModelTest {

    @get:Rule
    val testDispatcher = TestDispatcherRule()

    private val date = LocalDate.parse("2025-01-09")
    private val time = "2025-01-09T13:00:00+01:00" // Heure locale de Beni Mellal
    private val habitRepository = FakeHabitRepository()
    private val entryRepository = FakeEntryRepository()
    private val virtualEntries = GetVirtualEntriesUseCase(habitRepository, entryRepository)
    private lateinit var getVirtualEntries: GetHabitsWithVirtualEntriesUseCase
    private lateinit var viewModel: LogbookViewModel
    private lateinit var clock: Clock

    @Before
    fun setup() {
        clock = Clock.fixed(Instant.parse(date.toString() + time), ZoneOffset.UTC)
        getVirtualEntries = GetHabitsWithVirtualEntriesUseCase(habitRepository, virtualEntries)
        viewModel = LogbookViewModel(getVirtualEntries, habitRepository, entryRepository, clock)
    }

    @Test
    fun uiState_whenEmpty_thenNoHabitsState() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        assertEquals(LogbookUiState.NoHabits, viewModel.uiState.first())
        collectJob.cancel()
    }

    @Test
    fun uiState_whenInitialised_getFirstHabitAndSetSelectedHabitId() = runTest {
        habitRepository.upsertHabit(habit2)
        entryRepository.toggleEntry(habit2.id, date)
        viewModel = LogbookViewModel(getVirtualEntries, habitRepository, entryRepository, clock)
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        val uiState = viewModel.uiState.map { (it as LogbookUiState.SelectedHabit) }
        assertEquals(habit2.id, uiState.first().habitId)
        collectJob.cancel()
    }

    @Test
    fun uiState_whenInitialised_getFirstHabitAndLoadEntries() = runTest {
        habitRepository.upsertHabit(habit2)
        entryRepository.toggleEntry(habit2.id, date)
        entryRepository.toggleEntry(habit2.id, date.minusDays(1))
        viewModel = LogbookViewModel(getVirtualEntries, habitRepository, entryRepository, clock)
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        val uiState = viewModel.uiState.map { it as LogbookUiState.SelectedHabit }
        assertTrue(uiState.first().completed.contains(date))
        assertTrue(uiState.first().completed.contains(date.minusDays(1)))
        collectJob.cancel()
    }

    @Test
    fun uiState_whenHabitSelected_thenLoadHabits() = runTest {
        habitRepository.upsertHabit(habit2)
        habitRepository.upsertHabit(habit3)
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        viewModel.setSelectedHabit(habit2.id)
        val uiState = viewModel.uiState.map { it as LogbookUiState.SelectedHabit }
        assertNotNull(uiState.first().habits.find { it.name == habit2.name })
        assertNotNull(uiState.first().habits.find { it.name == habit3.name })
        collectJob.cancel()
    }

    @Test
    fun uiState_whenCompletedByWeek_thenLoadCompletedByWeekDates() = runTest {
        habitRepository.upsertHabit(habit4)
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        viewModel.setSelectedHabit(habit4.id)
        entryListF.forEach { entryRepository.toggleEntry(habit4.id, it.date) }
        val uiState = viewModel.uiState.map { it as LogbookUiState.SelectedHabit }.first()
        assertTrue(uiState.completedByWeek.contains(LocalDate.parse("2023-02-27")))
        assertTrue(uiState.completedByWeek.contains(LocalDate.parse("2023-03-03")))
        assertTrue(uiState.completedByWeek.contains(LocalDate.parse("2023-03-04")))
        assertTrue(uiState.completedByWeek.contains(LocalDate.parse("2023-03-05")))
        collectJob.cancel()
    }

    @Test
    fun uiState_whenNotCompletedByWeek_thenLoadCompletedDates() = runTest {
        habitRepository.upsertHabit(habit4)
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        viewModel.setSelectedHabit(habit4.id)
        val expectedList = entryListF.drop(1)
        expectedList.forEach { entryRepository.toggleEntry(habit4.id, it.date) }
        val uiState = viewModel.uiState.map { it as LogbookUiState.SelectedHabit }.first()
        expectedList.forEach { assertTrue(uiState.completed.contains(it.date)) }
        assertTrue(uiState.completedByWeek.isEmpty())
        collectJob.cancel()
    }

    @Test
    fun setSelectedHabit_whenSelectedHabitChanges_thenLoadNewEntries() = runTest {
        habitRepository.upsertHabit(habit1)
        habitRepository.upsertHabit(habit2)
        entryRepository.toggleEntry(habit1.id, date)
        entryRepository.toggleEntry(habit2.id, date.plusDays(1))
        viewModel.setSelectedHabit(habit1.id)
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        val uiState = viewModel.uiState.map { it as LogbookUiState.SelectedHabit }
        assertTrue(uiState.first().completed.contains(date))
        viewModel.setSelectedHabit(habit2.id)
        assertTrue(uiState.first().completed.contains(date.plusDays(1)))
        collectJob.cancel()
    }

    @Test
    fun toggleEntry_whenDateToggled_thenModifyState() = runTest {
        habitRepository.upsertHabit(habit1)
        habitRepository.upsertHabit(habit2)
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        viewModel.setSelectedHabit(habit1.id)
        val uiState = viewModel.uiState.map { it as LogbookUiState.SelectedHabit }
        viewModel.toggleEntry(date)
        assertTrue(uiState.first().completed.contains(date))
        viewModel.setSelectedHabit(habit2.id)
        assertFalse(uiState.first().completed.contains(date))
        viewModel.toggleEntry(date)
        assertTrue(uiState.first().completed.contains(date))
        collectJob.cancel()
    }

}