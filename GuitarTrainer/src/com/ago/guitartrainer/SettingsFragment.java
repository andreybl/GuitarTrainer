package com.ago.guitartrainer;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Fragment to show settings. Works together with {@link SettingsActivity}.
 * 
 * @author Andrej Golovko - jambit GmbH
 * @see SettingsActivity
 *
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

    }
}