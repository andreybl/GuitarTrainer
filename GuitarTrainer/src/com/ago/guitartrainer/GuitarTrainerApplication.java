package com.ago.guitartrainer;

import android.app.Application;

import com.ago.guitartrainer.db.DatabaseHelper;

public class GuitarTrainerApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        
        
        DatabaseHelper.initDatabaseHelperInstance(getApplicationContext());
    }
}
