package com.example.calltest;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.detector.services.LocalPhoneNumber;
import com.google.gson.Gson;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.calltest", appContext.getPackageName());
    }
    @Test
    public void parsingTest() {
        final Gson gson = new Gson();
        final String jsonArray = "[{\"number\":\"13213213\"},{\"number\":\"77777\"}]";
        final LocalPhoneNumber[] array = gson.fromJson(jsonArray, LocalPhoneNumber[].class);
        assertEquals(array[0].getNumber(), "13213213");
    }
}