package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersDao
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.get
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import javax.sql.DataSource

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
    private val testModule = module {
        viewModel { RemindersListViewModel(appContext, get() as ReminderDataSource) }
        single { LocalDB.createRemindersDao(appContext) }
        single { RemindersLocalRepository(get() as RemindersDao )}
    }

    @Before
    fun init(){
        startKoin { modules(testModule) }
        repository = get() as RemindersLocalRepository

    }

    @After
    fun cleanUp() {

    }

}