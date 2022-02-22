package com.udacity.project4.util

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.idling.CountingIdlingResource
import com.udacity.project4.utils.EspressoIdlingResource

object ToastManager {
    private val idlingResource: CountingIdlingResource = CountingIdlingResource("toast")

    // For testing
    fun getIdlingResource(): IdlingResource {
        return idlingResource
    }

    fun increment() {
        idlingResource.increment()
    }

    fun decrement() {
        if (!idlingResource.isIdleNow) {
            idlingResource.decrement()
        }
    }

    inline fun <T> wrapEspressoIdlingResource(function: () -> T): T {
        EspressoIdlingResource.increment()
        return try {
            function()
        } finally {
            EspressoIdlingResource.decrement()
        }
    }

}