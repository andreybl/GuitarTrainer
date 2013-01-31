package com.ago.guitartrainer;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.ago.guitartrainer.db.DatabaseHelper;
import com.ago.guitartrainer.ui.MainFragment;
import com.ago.guitartrainer.ui.dialogs.AboutDialog;

public class MasterActivity extends FragmentActivity {

    private static String TAG = "GT-MasterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        // getActionBar().hide();
        // }

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
        switch (item.getItemId()) {
        case R.id.menu_settings: {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            break;
        }case R.id.menu_reset_database: {
//            Intent intent = new Intent(this, SettingsActivity.class);
//            startActivity(intent);
            
            DatabaseHelper.getInstance().resetData();
            
            break;
        }
        case R.id.about: {
            AboutDialog about = new AboutDialog(this);
            about.setTitle("about this app");

            about.show();

            break;
        }
        }

        return false;
    }

    public void replaceFragment(Fragment frg) {
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, frg);
        ft.addToBackStack(null);
        ft.commit();
    }

}
