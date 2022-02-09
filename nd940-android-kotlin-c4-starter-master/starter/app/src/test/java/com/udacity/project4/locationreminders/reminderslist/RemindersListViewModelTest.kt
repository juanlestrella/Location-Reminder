package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    // TODO: provide testing to the RemindersListViewModel and its live data objects
    // How to name test functions: fun given_when_then

    private lateinit var remindersListViewModel: RemindersListViewModel
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
        remindersListViewModel =
            RemindersListViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
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
    fun loadReminders_loadData_remindersListNotEmpty() = runBlockingTest {
        // given: the list of ReminderDTO from createRemindersTest function

        // when: call the loadReminders
        remindersListViewModel.loadReminders()

        // then: the value of remindersList should not be null
        val value = remindersListViewModel.remindersList.getOrAwaitValue()
        assertThat(value, not(emptyList()))

    }

    @Test
    fun loadReminders_showLoading() = runBlockingTest {
        // given: Pause dispatcher to verify initial values
        mainCoroutineRule.pauseDispatcher()
        // when: load reminders
        remindersListViewModel.loadReminders()
        // then: showLoading is true
        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is` (true))
        // given execute pending coroutines actions
        mainCoroutineRule.resumeDispatcher()
        // then: showloading is false
        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(false))
    }

    @Test
    fun loadReminders_noData_emptyList() = runBlockingTest {
        // given: no data saved
        fakeDataSource.deleteAllReminders()

        // when: call the loadReminders
        remindersListViewModel.loadReminders()

        // then: showNoData is true
        assertThat(remindersListViewModel.showNoData.value, `is`(true))
    }

    @Test
    fun loadReminders_showSnackbar_errorMessage() = runBlockingTest {
        // given: shouldReturnError = false
        fakeDataSource.setReturnError(true)
        // when: load reminders
        remindersListViewModel.loadReminders()
        // then: error message should show
        assertThat(remindersListViewModel.showSnackBar.getOrAwaitValue(),
            `is`("Reminders not found"))
    }
}