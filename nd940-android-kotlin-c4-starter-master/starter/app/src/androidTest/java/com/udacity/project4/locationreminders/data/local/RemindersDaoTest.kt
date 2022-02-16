package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest(){

    //    TODO: Add testing implementation to the RemindersDao.kt

    // execute each tasks synchronously using architecture components
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun cleanUp() {
        database.close()
    }

    @Test
    fun saveReminderAndGetReminderById() = runBlockingTest {
        // given: List<ReminderDTO>
        val reminder = ReminderDTO(
            "title1",
            "desc1",
            "loc1",
            0.0,
            0.0)
        database.reminderDao().saveReminder(reminder)

        // when: calling getting reminder by getReminderById
        val data = database.reminderDao().getReminderById(reminder.id)

        //then: the GET data contains expected values
        assertThat(data, notNullValue())
        assertThat(data?.title, `is`(reminder.title))
        assertThat(data?.description, `is`(reminder.description))
        assertThat(data?.location, `is`(reminder.location))
        assertThat(data?.latitude, `is`(reminder.latitude))
        assertThat(data?.longitude, `is`(reminder.longitude))
        assertThat(data?.id, `is`(reminder.id))
    }

    @Test
    fun getRemindersAndEmptyList() = runBlockingTest{
        // given: no saved data
        // when: getReminders is called
        val res = database.reminderDao().getReminders()
        // then: receive an error or expect null value
        assertThat(res, `is`(emptyList<ReminderDTO>()))
    }

    @Test
    fun saveReminderAndDeleteAllReminders() = runBlockingTest {
        // given: list of ReminderDTO
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
        // when: save List of ReminderDTO
        database.reminderDao().saveReminder(reminder1)
        database.reminderDao().saveReminder(reminder2)
        // when: call DeleteAllReminders
        database.reminderDao().deleteAllReminders()
        // when: call getReminders
        val res = database.reminderDao().getReminders()
        // then: all data should be erased and result is an Empty List
        assertThat(res, `is`(emptyList()))
    }
}