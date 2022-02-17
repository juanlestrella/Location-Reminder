package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragment
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.MainCoroutineRule
import com.udacity.project4.locationreminders.data.local.RemindersDao
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.get
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest : AutoCloseKoinTest() {
    // ESPRESSO: watch lesson 4: 13
    // MOCKS: watch lesson 4: 16
    // KOIN Tutorial: https://insert-koin.io/docs/quickstart/kotlin

    //    TODO: test the navigation of the fragments.
    // Use MOCK to test navigation (see TasksFragmentTest.kt in android-testing-starter-code)
    //    TODO: test the displayed data on the UI.
    // Use  ESPRESSO to test displayed data (see TasksDetailFragmentTest.kt in android-testing-starter-code)
    //    TODO: add testing for the error messages.
    // use ESPRESSO


    // USE KOIN TO CREATE THE DATABASE

    private val appContext:Application = ApplicationProvider.getApplicationContext()
    private lateinit var repository: ReminderDataSource

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun init(){
        stopKoin()
        val testModule = module {
            viewModel { RemindersListViewModel(appContext, get() as ReminderDataSource) }
            single { SaveReminderViewModel(appContext, get() as ReminderDataSource)}
            single { LocalDB.createRemindersDao(appContext) }
            single { RemindersLocalRepository(get()) as ReminderDataSource }
        }
        startKoin { modules(listOf(testModule)) }
        repository = get()
    }

    @Test
    fun reminderSaved_displayedInUI() = mainCoroutineRule.runBlockingTest {

            // given: a saved reminder to the database
            val whiteHouse = ReminderDTO(
                "White house",
                "President lives here",
                "Washington DC",
                38.8976763,
                -77.0365298
            )
            runBlocking{
                repository.deleteAllReminders()
                repository.saveReminder(whiteHouse)
            }
            // when: ReminderList Fragment launched to display reminders
            launchFragmentInContainer<ReminderListFragment>(null, R.style.AppTheme)
            // then: Reminder's properties are displayed to the screen
            onView(withText(whiteHouse.title)).check(matches(isDisplayed()))
            onView(withText(whiteHouse.description)).check(matches(isDisplayed()))
            onView(withText(whiteHouse.location)).check(matches(isDisplayed()))
    }

    @Test
    fun clickAddReminderFAB_navigateToSaveReminder() = runBlockingTest {
        // given: on the RemindersListFragment
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }
        // when: the AddReminderFAB is clicked
        onView(withId(R.id.addReminderFAB)).perform(click())

        // then: verify that we navigate to the SaveReminderFragment
        verify(navController).navigate(
            ReminderListFragmentDirections.toSaveReminder()
        )
    }

}