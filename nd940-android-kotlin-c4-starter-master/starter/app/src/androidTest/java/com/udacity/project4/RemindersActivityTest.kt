package com.udacity.project4

import android.app.Activity
import android.app.Application
import android.app.PendingIntent.getActivity
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.ToastManager
import com.udacity.project4.util.monitorActivity
import com.udacity.project4.utils.EspressoIdlingResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get

@RunWith(AndroidJUnit4::class)
@LargeTest
//END TO END test to black box test the app
class RemindersActivityTest :
    AutoCloseKoinTest() {// Extended Koin Test - embed autoclose @after method to close Koin after every test

    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application
    private val dataBindingIdlingResources = DataBindingIdlingResource()
    private val toastIdlingResource = ToastManager.getIdlingResource()

    // for testing toast
    private fun getActivity(activityScenario: ActivityScenario<RemindersActivity>): Activity? {
        var activity: Activity? = null
        activityScenario.onActivity {
            activity = it
        }
        return activity
    }

    /**
     * As we use Koin as a Service Locator Library to develop our code, we'll also use Koin to test our code.
     * at this step we will initialize Koin related code to be able to use it in out testing.
     */
    @Before
    fun init() {
        stopKoin()//stop the original app koin
        appContext = getApplicationContext()
        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single {
                SaveReminderViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { LocalDB.createRemindersDao(appContext) }
        }
        //declare a new koin module
        startKoin {
            modules(listOf(myModule))
        }
        //Get our real repository
        repository = get()

//        clear the data to start fresh
        runBlocking {
            repository.deleteAllReminders()
        }
    }

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResources)
//        IdlingRegistry.getInstance().register(toastIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResources)
//        IdlingRegistry.getInstance().unregister(toastIdlingResource)
    }

    //    TODO: add End to End testing to the app
    // In this scenario, User already Logged In.
    // User will create a reminder
    @Test
    fun createAndSaveReminder() = runBlocking {
        // start up Authentication screen
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResources.monitorActivity(activityScenario)
        // use onView to test end to end activity
        // click the FAB and check if all editboxes are displayed
        onView(withId(R.id.addReminderFAB)).perform(click())
        onView(withId(R.id.reminderTitle)).check(matches(isDisplayed()))
        onView(withId(R.id.reminderDescription)).check(matches(isDisplayed()))
        onView(withId(R.id.selectLocation)).check(matches(isDisplayed()))
        // Add values to the title,descrption, and location
        onView(withId(R.id.reminderTitle)).perform(replaceText("Title1"))
        onView(withId(R.id.reminderDescription)).perform(replaceText("Description1"))
        onView(withId(R.id.selectLocation)).perform(click())
        onView(withId(R.id.map)).perform(longClick())
        onView(withId(R.id.saveButton)).perform(click())
        // check if added text are displayed
        onView(withText("Title1")).check(matches(isDisplayed()))
        onView(withText("Description1")).check(matches(isDisplayed()))
        onView(withId(R.id.selectedLocation)).check(matches(isDisplayed()))
        // save the reminder
        onView(withId(R.id.saveReminder)).perform(click())
        // check if reminder is added to list of reminders
        onView(withText("Title1")).check(matches(isDisplayed()))
        onView(withText("Description1")).check(matches(isDisplayed()))
        // check that the "no data" string is not visible
        onView(withId(R.id.noDataTextView)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        // check the toast "Geofences added"
        onView(withText(R.string.geofences_added))
            .inRoot(withDecorView(not(`is`(getActivity(activityScenario)?.window?.decorView))))
            .check(matches(isDisplayed()))
//        ToastManager.increment()
        // check the toast "Reminder Saved !"
//        onView(withText(R.string.reminder_saved))
//            .inRoot(withDecorView(not(`is`(getActivity(activityScenario)?.window?.decorView))))
//            .check(matches(isDisplayed()))
        //delay(2000)
        // close activity to reset Database
        activityScenario.close()
    }

    @Test
    fun saveReminderEmptyTitle() = runBlocking {
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResources.monitorActivity(activityScenario)

        onView(withId(R.id.addReminderFAB)).perform(click())
        // try to save without title
        onView(withId(R.id.saveReminder)).perform(click())
        // it's a Snackbar, not toast
        onView(withId(R.id.snackbar_text)).check(matches(withText("Please enter title")))
//        onView(withText(R.string.err_enter_title))
//            .inRoot(withDecorView(not(`is`(getActivity(activityScenario)?.window?.decorView))))
//            .check(matches(isDisplayed()))
        delay(2000)
        activityScenario.close()
    }

    @Test
    fun saveReminderEmptyLocation() = runBlocking {
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResources.monitorActivity(activityScenario)

        onView(withId(R.id.addReminderFAB)).perform(click())
        // try to save with title, but without location
        onView(withId(R.id.reminderTitle)).perform(replaceText("Title1"))
        onView(withId(R.id.saveReminder)).perform(click())
        // it's a Snackbar, not toast
        onView(withId(R.id.snackbar_text)).check(matches(withText("Please select location")))
//        onView(withText(R.string.err_select_location))
//            .inRoot(withDecorView(`is`(getActivity(activityScenario)?.window?.decorView))).check(matches(isDisplayed()))

        delay(2000)
        activityScenario.close()
    }

}
