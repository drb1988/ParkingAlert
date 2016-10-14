package bigcityapps.com.parkingalert;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class MainActivityEspressoTest {


    @Rule
    public ActivityTestRule<Login> mActivityRule = new ActivityTestRule<>(Login.class);

    @Test
    public void ensureTextChangesWork() {
        // Type mText and then press the button.
        onView(withId(R.id.nume_login)).perform(typeText("edname"), closeSoftKeyboard());
        onView(withId(R.id.driver_license_login)).perform(typeText("driverlicense"), closeSoftKeyboard());
        onView(withId(R.id.nick_name_login)).perform(typeText("edNickname"), closeSoftKeyboard());
        onView(withId(R.id.mobile_login)).perform(typeText("0359417305"), closeSoftKeyboard());
        onView(withId(R.id.email_login)).perform(typeText("edemail"), closeSoftKeyboard());
        onView(withId(R.id.city_login)).perform(typeText("Oradea"), closeSoftKeyboard());
        onView(withId(R.id.continuare)).perform(click());

        // Check that the mText was changed.
        onView(withId(R.id.city_login)).check(matches(withText("Oradea")));
    }

//    @Test
//    public void changeText_newActivity() {
//        // Type mText and then press the button.
//        onView(withId(R.id.inputField)).perform(typeText("NewText"),
//                closeSoftKeyboard());
//        onView(withId(R.id.switchActivity)).perform(click());
//
//        // This view is in a different Activity, no need to tell Espresso.
//        onView(withId(R.id.resultView)).check(matches(withText("NewText")));
//    }
}