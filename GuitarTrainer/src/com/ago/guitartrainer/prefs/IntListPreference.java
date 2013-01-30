package com.ago.guitartrainer.prefs;

import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;

/**
 * A hack to original ListPreference, so that we can store the integer values into preferences.
 * 
 * The origianl {@link ListPreference} save selected values as strings.
 * 
 * @author Andrej Golovko - jambit GmbH
 *
 */
public class IntListPreference extends ListPreference {
    public IntListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IntListPreference(Context context) {
        super(context);
    }

    @Override
    protected boolean persistString(String value) {
        if (value == null) {
            return false;
        } else {
            return persistInt(Integer.valueOf(value));
        }
    }

    @Override
    protected String getPersistedString(String defaultReturnValue) {
        if (getSharedPreferences().contains(getKey())) {
            int intValue = getPersistedInt(0);
            return String.valueOf(intValue);
        } else {
            return defaultReturnValue;
        }
    }
}