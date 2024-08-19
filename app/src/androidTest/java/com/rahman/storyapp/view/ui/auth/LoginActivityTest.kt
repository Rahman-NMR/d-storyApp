package com.rahman.storyapp.view.ui.auth

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rahman.storyapp.R
import com.rahman.storyapp.utils.EspressoIdlingResource
import com.rahman.storyapp.view.ui.WelcomeActivity
import com.rahman.storyapp.view.ui.stories.MainActivity
import org.junit.After
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class LoginActivityTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        Intents.init()
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        Intents.release()
    }

    @Test
    fun testA_loginTest() {
        onView(withId(R.id.ed_login_email)).perform(click())
        onView(withId(R.id.ed_login_email)).perform(replaceText("earth@mail.us"), closeSoftKeyboard())

        onView(withId(R.id.ed_login_password)).perform(click())
        onView(withId(R.id.ed_login_password)).perform(replaceText("12qwaszx"), closeSoftKeyboard())

        onView(withId(R.id.cb_show_pass_login)).perform(click())
        onView(withId(R.id.action_login)).perform(click())

        intended(hasComponent(MainActivity::class.java.name))
    }

    @Test
    fun testB_logoutTest() {
        ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.action_logout)).perform(click())
        onView(withText(R.string.logout)).inRoot(isDialog()).perform(click())

        intended(hasComponent(WelcomeActivity::class.java.name))
    }
}