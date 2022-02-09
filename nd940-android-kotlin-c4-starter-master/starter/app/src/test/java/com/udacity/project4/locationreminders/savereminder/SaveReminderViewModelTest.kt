package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import java.util.*

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {
    //TODO: provide testing to the SaveReminderView and its live data objects

    private lateinit var saveReminderViewModel: SaveReminderViewModel
    private lateinit var fakeDataSource: FakeDataSource
    private lateinit var reminders: MutableList<ReminderDTO>

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setupViewModel() {
        stopKoin()
        reminders = createRemindersTest()
        fakeDataSource = FakeDataSource(reminders)
        saveReminderViewModel =
            SaveReminderViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
    }

    private fun createRemindersTest(): MutableList<ReminderDTO> {
        val whiteHouse = ReminderDTO(
            "White house",
            "President lives here",
            "Washington DC",
            38.8976763,
            -77.0365298)
        val pennsylvania = ReminderDTO(
            "Pennsylvania",
            "Vacation",
            "United States",
            42.2033216,
            -77.1945247
        )
        val newYork = ReminderDTO(
            "New York",
            "The Big Apple",
            "United States",
            40.7127753,
            -74.0059728
        )
        return mutableListOf<ReminderDTO>(whiteHouse, pennsylvania, newYork)
    }

    @Test
    fun onClear_allValuesAreNull() = runBlockingTest {
        // given: Since the local variables in SaveReminderViewModel is updated by data binding
        // instead use reminders[0] or white house to save update the local variables
        saveReminderViewModel.reminderTitle.value = reminders[0].title
        saveReminderViewModel.reminderDescription.value = reminders[0].description
        saveReminderViewModel.reminderSelectedLocationStr.value = reminders[0].location
        saveReminderViewModel.latitude.value = reminders[0].latitude
        saveReminderViewModel.longitude.value = reminders[0].longitude

        // when: onClear is called
        saveReminderViewModel.onClear()

        // then: all the variables in saveReminderViewModel are null values
        assertThat(saveReminderViewModel.reminderTitle.getOrAwaitValue(), Matchers.nullValue())
        assertThat(saveReminderViewModel.reminderDescription.getOrAwaitValue(), Matchers.nullValue())
        assertThat(saveReminderViewModel.reminderSelectedLocationStr.getOrAwaitValue(), Matchers.nullValue())
        assertThat(saveReminderViewModel.latitude.getOrAwaitValue(), Matchers.nullValue())
        assertThat(saveReminderViewModel.longitude.getOrAwaitValue(), Matchers.nullValue())
    }

    @Test
    fun saveReminder_showLoading() = runBlockingTest {
        // given: Pause dispatcher to verify initial values
        mainCoroutineRule.pauseDispatcher()
        // when: call saveReminder()
        val data = ReminderDataItem(
        "White house",
        "President lives here",
        "Washington DC",
        38.8976763,
        -77.0365298, UUID.randomUUID().toString())

        saveReminderViewModel.saveReminder(data)
        // then: showLoading = true
        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), `is`(true))
        // given: execute pending coroutines actions
        mainCoroutineRule.resumeDispatcher()
        // then: showLoading = false
        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), `is`(false))
        // then: showToast
        assertThat(saveReminderViewModel.showToast.getOrAwaitValue(), `is`("Reminder Saved !"))
    }

}