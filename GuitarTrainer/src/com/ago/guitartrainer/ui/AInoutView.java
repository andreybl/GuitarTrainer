package com.ago.guitartrainer.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.LinearLayout;

import com.ago.guitartrainer.events.OnViewSelectionListener;
import com.ago.guitartrainer.notation.Note;

public abstract class AInoutView<T> extends LinearLayout {
    /**
     * Listeners for the selections in current view.
     * 
     * Only valid selections made by the user are fired. If the selection - here, a {@link Note} - was randomly set by
     * the program, the event is not fired.
     */
    private List<OnViewSelectionListener<T>> listeners = new ArrayList<OnViewSelectionListener<T>>();

    /**
     * if true, the input to the view is allowed.
     * 
     * E.g. the user may press the buttons in the view, touch on associated image(-s) etc. Note, that the isEnabled and
     * isEnabledInput relation can be expressed with boolean expression:
     * 
     * <pre>
     *      isEnabled or (not isEnabled and not isEnabledInput)
     * </pre>
     * 
     * For instance, if "not isEnabled" applies, must also apply the "not isEnabledInput".
     * */
    private boolean isEnabledInput = true;

    public AInoutView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public AInoutView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AInoutView(Context context) {
        super(context);
    }

    public void setEnabledInput(boolean enabledInput) {
        this.isEnabledInput = enabledInput;

        if (enabledInput)
            setEnabled(enabledInput);
    }

    public boolean isEnabledInput() {
        return isEnabledInput;
    }
    
    public void registerListener(OnViewSelectionListener<T> listener) {
        listeners.add(listener);
    }



    protected void notifyListeners(T obj) {
        if (obj != null) {
            for (OnViewSelectionListener<T> listener : listeners) {
                listener.onViewElementSelected(obj);
            }
        }
    }

    /**
     * Set the single button (<code>selectedBtn</code>) to be selected among the other buttons (<code>btns</code>)
     * passed as parameter.
     * 
     * @param btns
     * @param selectedBtn
     */
    protected void selectButton(Set<Button> btns, Button selectedBtn) {
        // make selected another button
        for (Button btn : btns) {
            btn.setTextColor(Color.WHITE);
        }

        selectedBtn.setTextColor(Color.GREEN);
    }
    
    protected String getTagLogging() {
        return "GT-"+getClass().getSimpleName();
    }
}
