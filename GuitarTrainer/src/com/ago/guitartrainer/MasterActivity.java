package com.ago.guitartrainer;

import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.ago.guitartrainer.ui.MainFragment;

public class MasterActivity extends FragmentActivity {

    private static String TAG = "GT-MasterActivity";

    // private SlidingMenu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().hide();
        }

        setContentView(R.layout.main2);

        // set the Above View
        setContentView(R.layout.content_frame);

        /*
         * Calling this [setDefauulValues()] during onCreate() ensures that your application is properly initialized
         * with default settings, which your application might need to read in order to determine some behaviors (such
         * as whether to download data while on a cellular network).
         */
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, new MainFragment());
        ft.addToBackStack(null);
        ft.commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*
         * The "..." will not be shown, as far as it is in Action Bar, but it is hidden with getActionBar().hide() call
         */
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // respond to menu item selection
        return true;
    }

    public void replaceFragment(Fragment frg) {
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, frg);
        ft.addToBackStack(null);
        ft.commit();
    }

    // @Override
    // public void onBackPressed() {
    // FragmentTransaction ft = getFragmentManager().beginTransaction();
    // ft.replace(android.R.id.content, new MainFragment());
    // ft.addToBackStack(null);
    // ft.commit();
    //
    // }

}
