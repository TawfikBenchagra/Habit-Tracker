package com.tawfik.habits.usecases

import com.tawfik.habits.data.model.Habit
import com.tawfik.habits.data.model.HabitFrequency
import com.tawfik.habits.domain.usecase.GetVirtualEntriesUseCase
import com.tawfik.habits.fake.repository.FakeEntryRepository
import com.tawfik.habits.fake.repository.FakeHabitRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class GetVirtualEntriesUseCaseTest {

    private lateinit var entryRepository: FakeEntryRepository
    private lateinit var habitRepository: FakeHabitRepository
    private lateinit var getVirtualEntries: GetVirtualEntriesUseCase

    @Before
    fun setup() {
        entryRepository = FakeEntryRepository()
        habitRepository = FakeHabitRepository()
        getVirtualEntries = GetVirtualEntriesUseCase(habitRepository, entryRepository)
    }

    @Test
    fun getVirtualEntries_whenWeekCompleted_returnsAllEntriesForWeek() = runTest {
        val habit = Habit(id = 3, name = "test", frequency = HabitFrequency.WEEKLY, repeat = 3)
        val dates = listOf("2023-04-01", "2023-03-31", "2023-03-30")
        habitRepository.upsertHabit(habit)
        dates.forEach { date -> entryRepository.toggleEntry(habit.id, LocalDate.parse(date)) }
        val virtualEntries = getVirtualEntries(habit.id).first()
        val actualDates = virtualEntries.map { it.date.toString() }
            .sortedDescending()
        val expectedDates = getDatesInWeek(LocalDate.parse("2023-03-27"))
            .map { it.toString() }
            .sortedDescending()
        assertEquals(expectedDates, actualDates)
    }

    @Test
    fun getVirtualEntries_whenWeekCompleted_virtualEntriesIdIsNull() = runTest {
        val habit = Habit(id = 3, name = "test", frequency = HabitFrequency.WEEKLY, repeat = 3)
        val dates = listOf("2023-04-01", "2023-03-31", "2023-03-30")
        habitRepository.upsertHabit(habit)
        dates.forEach { date -> entryRepository.toggleEntry(habit.id, LocalDate.parse(date)) }
        val virtualEntries = getVirtualEntries(habit.id).first()
        val actualDates = virtualEntries.filter { !dates.contains(it.date.toString()) }
        assertTrue(actualDates.all { it.id == null })
    }

    @Test
    fun getVirtualEntries_whenWeekCompleted_entriesIdsExist() = runTest {
        val habit = Habit(id = 3, name = "test", frequency = HabitFrequency.WEEKLY, repeat = 3)
        val dates = listOf("2023-04-01", "2023-03-31", "2023-03-30")
        habitRepository.upsertHabit(habit)
        dates.forEach { date -> entryRepository.toggleEntry(habit.id, LocalDate.parse(date)) }
        val virtualEntries = getVirtualEntries(habit.id).first()
        val actualDates = virtualEntries.filter { dates.contains(it.date.toString()) }
        assertTrue(actualDates.all { it.id != null })
    }

    @Test
    fun getVirtualEntries_whenWeekUncompleted_returnsOnlyActualEntries() = runTest {
        val habit = Habit(id = 3, name = "test", frequency = HabitFrequency.WEEKLY, repeat = 4)
        val dates = listOf("2023-04-01", "2023-03-31", "2023-03-30")
        habitRepository.upsertHabit(habit)
        dates.forEach { date -> entryRepository.toggleEntry(habit.id, LocalDate.parse(date)) }
        val virtualEntries = getVirtualEntries(habit.id).first()
        val actualDates = virtualEntries.map { it.date.toString() }
        assertEquals(dates, actualDates)
    }


    @Test
    fun getVirtualEntries_whenMultipleWeekUncompleted_returnsOnlyActualEntries() = runTest {
        val habit = Habit(id = 3, name = "test", frequency = HabitFrequency.WEEKLY, repeat = 4)
        val dates = listOf(
            "2023-04-01",
            "2023-03-31",
            "2023-03-30",
            "2023-03-21",
            "2023-03-19",
            "2023-03-10"
        )
        habitRepository.upsertHabit(habit)
        dates.forEach { date -> entryRepository.toggleEntry(habit.id, LocalDate.parse(date)) }
        val virtualEntries = getVirtualEntries(habit.id).first()
        val actualDates = virtualEntries.map { it.date.toString() }
        assertEquals(dates, actualDates)
    }

    @Test
    fun getVirtualEntries_whenMultipleWeekCompleted_returnsAllEntriesForWeeks() = runTest {
        val habit = Habit(id = 3, name = "test", frequency = HabitFrequency.WEEKLY, repeat = 3)
        val dates = listOf(
            "2023-04-01",
            "2023-03-31",
            "2023-03-30",
            "2023-03-21",
            "2023-03-22",
            "2023-03-25"
        )
        habitRepository.upsertHabit(habit)
        dates.forEach { date -> entryRepository.toggleEntry(habit.id, LocalDate.parse(date)) }
        val virtualEntries = getVirtualEntries(habit.id).first()
        val actualDates = virtualEntries.map { it.date.toString() }
            .sortedDescending()
        val expectedDates = listOf(
            getDatesInWeek(LocalDate.parse("2023-03-27")),
            getDatesInWeek(LocalDate.parse("2023-03-20"))
        )
            .flatten()
            .map { it.toString() }
            .sortedDescending()
        assertEquals(expectedDates, actualDates)
    }

    @Test
    fun getVirtualEntries_whenMultipleWeekCompleted_virtualEntriesIdIsNull() = runTest {
        val habit = Habit(id = 3, name = "test", frequency = HabitFrequency.WEEKLY, repeat = 3)
        val dates = listOf(
            "2023-04-01",
            "2023-03-31",
            "2023-03-30",
            "2023-03-21",
            "2023-03-22",
            "2023-03-25"
        )
        habitRepository.upsertHabit(habit)
        dates.forEach { date -> entryRepository.toggleEntry(habit.id, LocalDate.parse(date)) }
        val virtualEntries = getVirtualEntries(habit.id).first()
        val actualDates = virtualEntries.filter { !dates.contains(it.date.toString()) }
        assertTrue(actualDates.all { it.id == null })
    }

    @Test
    fun getVirtualEntries_whenMultipleWeekCompleted_entriesIdsExist() = runTest {
        val habit = Habit(id = 3, name = "test", frequency = HabitFrequency.WEEKLY, repeat = 3)
        val dates = listOf(
            "2023-04-01",
            "2023-03-31",
            "2023-03-30",
            "2023-03-21",
            "2023-03-22",
            "2023-03-25"
        )
        habitRepository.upsertHabit(habit)
        dates.forEach { date -> entryRepository.toggleEntry(habit.id, LocalDate.parse(date)) }
        val virtualEntries = getVirtualEntries(habit.id).first()
        val actualDates = virtualEntries.filter { dates.contains(it.date.toString()) }
        assertTrue(actualDates.all { it.id != null })
    }

    private fun getDatesInWeek(weekStart: LocalDate): List<LocalDate> {
        return (0..6).map {
            weekStart.plusDays(it.toLong())
        }
    }

}