package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Error

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

//    TODO: Add testing implementation to the RemindersLocalRepository.kt

    // execute each task synchronously using architecture components
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var repository: RemindersLocalRepository
    private lateinit var database: RemindersDatabase

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()

        repository = RemindersLocalRepository(
            database.reminderDao(),
            Dispatchers.Main
        )
    }

    @After
    fun cleanUp() = database.close()

    @Test
    fun getReminders_Success_savedLocalReminder() = mainCoroutineRule.runBlockingTest {
        // given: saved local reminder
        val reminder1 = ReminderDTO(
            "title1",
            "desc1",
            "loc1",
            0.0,
            0.0)
        val reminder2 = ReminderDTO(
            "title2",
            "desc2",
            "loc2",
            0.0,
            0.0)
        repository.saveReminder(reminder1)
        repository.saveReminder(reminder2)
        // when: getReminders is called
        val result = repository.getReminders() as Result.Success
        // then: should contain data
        assertThat(result.data, not(nullValue()))
    }

    @Test
    fun getReminderById_Failed_CannotFindId() = mainCoroutineRule.runBlockingTest {
        // given: unsaved reminder
        val unsavedReminder1 = ReminderDTO(
            "title1",
            "desc1",
            "loc1",
            0.0,
            0.0)
        // when: looking for the unsaved Id
        val result = repository.getReminder(unsavedReminder1.id) as Result.Error
        // then: should return error
        assertThat(result.message, `is`("Reminder not found!"))
    }

    fun deleteAllReminders_Error_getReminderById() = mainCoroutineRule.runBlockingTest {
        // given: two saved reminders
        val reminder1 = ReminderDTO(
            "title1",
            "desc1",
            "loc1",
            0.0,
            0.0)
        val reminder2 = ReminderDTO(
            "title2",
            "desc2",
            "loc2",
            0.0,
            0.0)
        repository.saveReminder(reminder1)
        repository.saveReminder(reminder2)
        // when: deleting all reminders
        repository.deleteAllReminders()
        // then: getReminder should return Error Message
        val result1 = repository.getReminder(reminder1.id)
        //val result2 = repository.getReminder(reminder2.id) as Result.Error
        assertThat(result1 is Result.Error, `is`(true))
        result1 as Result.Error
        assertThat(result1.message, `is`("Reminder not found!"))
        //assertThat(result2.message, `is`("Reminder not found!"))
    }

}