package com.example.concerttrack


import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import com.example.concerttrack.adapters.ArtistsAdapter
import com.example.concerttrack.adapters.FavArtistsAdapter
import com.example.concerttrack.ui.FanMainPageActivity
import com.example.concerttrack.util.EspressoIdlingResource
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.favourites_fragment.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test


class AddArtistToFavouritesTest {


    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After()
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    companion object {
        const val FAN_EMAIL = "ania123@gmail.com"
        const val FAN_PASSWORD = "ania123"
        const val SEARCH_WORD = "Kwiat Jabłoni"
    }

    @Test
    fun searchAndAddArtistToFavourites() {
        runBlocking {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(FAN_EMAIL, FAN_PASSWORD).await()
        }
        ActivityScenario.launch(FanMainPageActivity::class.java)
        onView(withId(R.id.searchFragment)).perform(click()).check(matches(isDisplayed()))
        onView(withId(R.id.searchEventArtistET)).perform(click())
        onView(withId(R.id.searchEventArtistET)).perform(replaceText(SEARCH_WORD))
        onView(withId(R.id.searchEventArtistET)).perform(pressImeActionButton())

        onView(withId(R.id.artistsInSearchRV)).perform(
            actionOnItemAtPosition<ArtistsAdapter.ArtistViewHolder>(0, click())
        )

        onView(withId(R.id.addToFavBtn)).perform(click())
        onView(withId(R.id.favouritesFragment2)).perform(click()).check(matches(isDisplayed()))
        onView(withId(R.id.favouritesArtistsBtn)).perform(click()).check(matches(isDisplayed()))

        onView(withId(R.id.favouritesArtistsRV)).perform(
            actionOnItemAtPosition<FavArtistsAdapter.FavArtistViewHolder>(0, click())
        )

        onView(withId(R.id.artistNameTV)).check(matches(withText(SEARCH_WORD)))

    }
}