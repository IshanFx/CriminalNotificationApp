package com.protectme;


import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.regex.Matcher;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.doubleClick;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.action.ViewActions.swipeDown;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.core.deps.guava.base.Preconditions.checkNotNull;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.EasyMock2Matchers.equalTo;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsAnything.anything;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.hamcrest.object.HasToString.hasToString;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityInstrumentationTest {


    private static final String STRING_USEREMAIL = "ishan@gmail.com";
    private static final String STRING_USERPASSWORD = "12345";
    private static final String STRING_MENU_HISTORY = "History";
    private static final String STRING_MENU_FAMILY = "Family";
    private static final String STRING_MENU_LOGOUT = "Logout";

    @Rule
    public ActivityTestRule<LoginActivity> mActivityRule = new ActivityTestRule<>(
            LoginActivity.class);

    @Test
    public void sayHello(){
        onView(withId(R.id.email)).perform(typeText(STRING_USEREMAIL), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText(STRING_USERPASSWORD), closeSoftKeyboard());
        onView(withId(R.id.email_sign_in_button)).perform(click());

        onView(withId(R.id.btnRobbery)).perform(click()); //line 1
        onView(withId(R.id.btnKidnap)).perform(click());
        onView(withId(R.id.btnKidnap)).perform(click());
        onView(withId(R.id.btnEvidence)).perform(click());
        Espresso.pressBack();

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());


        onView(withText("History")).perform(click());
        onView(withId(R.id.swipeContainer)).perform(swipeDown());
        onData(anything()).inAdapterView(withId(R.id.historylist)).atPosition(0).perform(click());;
        onView(withId(R.id.btnCloseCase)).perform(click());
        Espresso.pressBack();
        Espresso.pressBack();

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Family")).perform(click());
        Espresso.pressBack();
        onView(withId(R.id.logout_setting)).perform(click());

       /* onView(withId(R.id.fab)).perform(click(), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.txtfamilyname)).perform(typeText("Ishan Fernando"),closeSoftKeyboard());
        onView(withId(R.id.txtfamilynumber)).perform(typeText("0772331421"),closeSoftKeyboard());
        onView(withText("Add")).perform(click());
        Espresso.pressBack();

        onView(withId(R.id.logout_setting)).perform(click());*/
        /*onView(withText("Say hello!")).perform(click()); //line 2

        String expectedText = "Hello, " + STRING_TO_BE_TYPED + "!";
        onView(withId(R.id.textView)).check(matches(withText(expectedText))); //line 3

        onView(withId(R.id.button)).perform(click());

        onView(withId(R.id.button2)).perform(click());*/
    }

    public static Matcher withItemContent(String expectedText) {
        checkNotNull(expectedText);
        return withItemContent(equalTo(expectedText));
    }

}