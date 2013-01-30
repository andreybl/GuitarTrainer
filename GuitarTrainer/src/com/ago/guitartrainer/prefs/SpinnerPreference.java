package com.ago.guitartrainer.prefs;

import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.ago.guitartrainer.R;

/**
 * Preference which use a {@link Spinner} to set its value.
 * 
 * 
 * @author Andrej Golovko - jambit GmbH
 * 
 */
public class SpinnerPreference extends DialogPreference {

    private Spinner spinner;

    public SpinnerPreference(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }

    public SpinnerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.preference_spinner);
    }

    @Override
    protected View onCreateDialogView() {
        View root = super.onCreateDialogView();

        spinner = (Spinner) root.findViewById(R.id.spinner_question_duration);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.seconds_titles,
                android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        int indexOfValue = getPersistedInt(0);
        spinner.setSelection(indexOfValue);
        return root;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
        case DialogInterface.BUTTON_POSITIVE: // User clicked OK!
            int indexOfValue = spinner.getSelectedItemPosition();
            persistInt(indexOfValue);
            break;
        }
        super.onClick(dialog, which);
    }
}