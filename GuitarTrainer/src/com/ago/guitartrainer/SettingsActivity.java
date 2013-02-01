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
    
    public static final String KEY_QUESTION_SHORTEST_REACTION_TIME = "question_shortest_reaction_time";

    public static final String KEY_NOTESVIEW_SENSITIVITY = "notesview_sensitivity";

    public static final String KEY_DEBUG_MODE = "debug_mode";
    
    public static final String KEY_PLAY_SOUNDS = "play_sounds";
    
    public static final String KEY_PLAY_VIBRATIONS = "play_vibrations";
    
    public static final String KEY_INPUT_METHOD_MANUAL = "input_method_manual";
    
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }
}