package com.learnit.learnit.types;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.learnit.learnit.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.File;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@Config(emulateSdk = 21, reportSdk = 21, constants = BuildConfig.class)
@RunWith(RobolectricTestRunner.class)
public class DbHelperTest {
    SQLiteDatabase database;

    @Test
    public void testReadFromDb() {
        assertThat(database == null, is(false));
    }

    @Before
    public void setUp() throws Exception {
        Context context = RuntimeEnvironment.application;
        assertThat(context == null, is(false));
        if (context == null) {
            return;
        }
        String filePath = context.getPackageResourcePath() + "/res/sample.db";

        database = SQLiteDatabase.openDatabase(
                (new File(filePath)).getAbsolutePath(),
                null,
                SQLiteDatabase.OPEN_READONLY);
    }
}