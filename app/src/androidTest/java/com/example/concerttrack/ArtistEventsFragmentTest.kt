package com.example.concerttrack

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import com.example.concerttrack.adapters.ArtistEventsAdapter
import com.example.concerttrack.ui.ArtistMainPageActivity
import com.example.concerttrack.util.EspressoIdlingResource
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.hamcrest.core.IsNot.not
import org.junit.After
import org.junit.Before
import org.junit.Test

class ArtistEventsFragmentTest {

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After()
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    companion object {
        const val ARTIST_EMAIL = "artystaTest@gmail.com"
        const val ARTIST_PASSWORD = "test123"
        const val EVENT_NAME = "Wydarzenie testowe"
        const val EVENT_DATE = "02-12-2025"
        const val EVENT_TIME = "18:30"
        const val EVENT_DESCRIPTION = "To jest wydarzenie dodawane w ramach testu!"
        const val PLACE_NAME = "Stary Klasztor"
    }

    @Test
    fun addNewEventAndCheck() {
        runBlocking {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(ARTIST_EMAIL,
                ARTIST_PASSWORD).await()
        }
        ActivityScenario.launch(ArtistMainPageActivity::class.java)

        onView(withId(R.id.addEventActivity)).perform(click())
        onView(withId(R.id.eventHeaderET)).perform(replaceText(EVENT_NAME))
        onView(withId(R.id.dateET)).perform(replaceText(EVENT_DATE))
        onView(withId(R.id.timeET)).perform(replaceText(EVENT_TIME))
        onView(withId(R.id.eventDescET)).perform(replaceText(EVENT_DESCRIPTION))
        onView(withId(R.id.nextBtn)).perform(click())

        onView(withId(R.id.placeNameET)).perform(replaceText(PLACE_NAME))
        onView(withId(R.id.searchPlaceET)).perform(replaceText(PLACE_NAME))
        onView(withId(R.id.searchPlaceET)).perform(pressImeActionButton())
        onView(withId(R.id.addEventBtn)).perform(click())

        onView(withId(R.id.comingArtistEventsRV)).check(matches(isDisplayed()))

        onView(withId(R.id.comingArtistEventsRV)).perform(
            actionOnItemAtPosition<ArtistEventsAdapter.ArtistEventViewHolder>(0, click()))

        onView(withId(R.id.eventNameInFragment)).check(matches(withText(EVENT_NAME)))

    }

    @Test
    fun addNewEventAndDelete() {
        runBlocking {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(ARTIST_EMAIL,
                ARTIST_PASSWORD).await()
        }
        ActivityScenario.launch(ArtistMainPageActivity::class.java)

        onView(withId(R.id.addEventActivity)).perform(click())
        onView(withId(R.id.eventHeaderET)).perform(replaceText(EVENT_NAME))
        onView(withId(R.id.dateET)).perform(replaceText(EVENT_DATE))
        onView(withId(R.id.timeET)).perform(replaceText(EVENT_TIME))
        onView(withId(R.id.eventDescET)).perform(replaceText(EVENT_DESCRIPTION))
        onView(withId(R.id.nextBtn)).perform(click())

        onView(withId(R.id.placeNameET)).perform(replaceText(PLACE_NAME))
        onView(withId(R.id.searchPlaceET)).perform(replaceText(PLACE_NAME))
        onView(withId(R.id.searchPlaceET)).perform(pressImeActionButton())
        onView(withId(R.id.addEventBtn)).perform(click())

        onView(withId(R.id.comingArtistEventsRV)).check(matches(isDisplayed()))

        onView(withId(R.id.comingArtistEventsRV)).perform(
            actionOnItemAtPosition<ArtistEventsAdapter.ArtistEventViewHolder>(0,
                GeneralSwipeAction(Swipe.SLOW,GeneralLocation.BOTTOM_RIGHT,GeneralLocation.BOTTOM_LEFT,
                    Press.FINGER))
        )

        onView(withId(R.id.comingArtistEventsRV)).check(matches(not(isDisplayed())))
    }
}