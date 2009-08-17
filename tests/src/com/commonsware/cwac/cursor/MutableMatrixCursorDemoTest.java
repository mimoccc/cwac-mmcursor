package com.commonsware.cwac.cursor;

import android.test.ActivityInstrumentationTestCase;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class com.commonsware.cwac.cursor.MutableMatrixCursorDemoTest \
 * com.commonsware.cwac.cursor.tests/android.test.InstrumentationTestRunner
 */
public class MutableMatrixCursorDemoTest extends ActivityInstrumentationTestCase<MutableMatrixCursorDemo> {

    public MutableMatrixCursorDemoTest() {
        super("com.commonsware.cwac.cursor", MutableMatrixCursorDemo.class);
    }

}
