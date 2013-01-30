package com.ago.guitartrainer;

import android.app.Activity;
import android.os.Bundle;

/**
 * Settings screen as they must be implemented in Android 3.0 and later. Works together with {@link SettingsFragment}.
 * 
 * From Android Guide:
 * 
 * <pre>
 * On Android 3.0 and later, you should instead use a traditional Activity that hosts a PreferenceFragment that 
 * displays your app settings. However, you can also use PreferenceActivity to create a two-pane layout 
 * for large screens when you have multiple groups of settings.
 * </pre>
 * 
 * @author Andrej Golovko - jambit GmbH
 * 
 */
public class SettingsActivity extends Activity {

    public static final String KEY_QUESTION_DURATION_MAX = "question_duration_max";
    
    public static final String KEY_POST_QUESTION_PAUSE_DURATION = "post_question_pause_duration";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }
}