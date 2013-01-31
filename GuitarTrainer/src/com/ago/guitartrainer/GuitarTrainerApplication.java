package com.ago.guitartrainer;

import android.app.Application;
import android.util.DisplayMetrics;

import com.ago.guitartrainer.db.DatabaseHelper;

public class GuitarTrainerApplication extends Application {

    /**
     * Maximum allowed difference between the ppi value returned by the Android device and the Density-DPI value
     * assigned to the Android device. If the difference is bigger than this value we just use the Density-DPI value to
     * calculate the border size. This value (and the test it is used in) had to be introduced bacause some Android
     * devices return the wrong ppi value.
     */
    private static final float MAX_PPI_DENSITYDPI_DIFFERENCE = 40.0f;

    @Override
    public void onCreate() {
        super.onCreate();

        DatabaseHelper.initDatabaseHelperInstance(getApplicationContext());
    }
}
