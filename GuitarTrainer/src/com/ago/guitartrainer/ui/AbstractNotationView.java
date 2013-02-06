package com.ago.guitartrainer.ui;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ago.guitartrainer.R;
import com.ago.guitartrainer.events.OnViewSelectionListener;
import com.ago.guitartrainer.notation.Note;

public abstract class AbstractNotationView<T> extends LinearLayout {

    private Map<Button, T> btn2Element = new Hashtable<Button, T>();

    /** title of the view */
    private TextView tvViewTitle;

    private View mainLayout;

    protected CheckBox cbIsRandomInput;

    private T currentElement;

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

    /**
     * Listeners for the selections in current view.
     * 
     * Only valid selections made by the user are fired. If the selection - here, a {@link Note} - was randomly set by
     * the program, the event is not fired.
     */
    private List<OnViewSelectionListener<T>> listeners = new ArrayList<OnViewSelectionListener<T>>();

    public AbstractNotationView(Context context) {
        super(context);

        init();
    }

    public AbstractNotationView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public AbstractNotationView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    private void init() {
        mainLayout = ((Activity) getContext()).getLayoutInflater().inflate(defaultLayoutResource(), this, true);

        doInit(mainLayout);

        tvViewTitle = (TextView) mainLayout.findViewById(R.id.txt_view_title);
        cbIsRandomInput = (CheckBox) mainLayout.findViewById(R.id.cb_random_input);

        /* default element selected */
        currentElement = defaultElement();
        show(currentElement);

        InnerOnClickListener onClickListener = new InnerOnClickListener();
        for (Button btnGrid : btn2Element.keySet()) {
            btnGrid.setOnClickListener(onClickListener);
        }

        if (cbIsRandomInput != null)
            cbIsRandomInput.setOnClickListener(onClickListener);

    }

    /**
     * Custom initialization. Here the buttons can be inflated and also mapped (registered) to elements.
     * 
     * @param mainLayout
     */
    protected abstract void doInit(View mainLayout);

    /**
     * Default layout, like "R.layout.elements_view"
     * 
     * @return
     */
    protected abstract int defaultLayoutResource();

    /**
     * Returns default element which must be set in view before anything is select - either by user or randomly.
     * 
     * @return
     */
    protected abstract T defaultElement();

    protected final void registerElement(Button btn, T element) {
        btn2Element.put(btn, element);
    }

    @Override
    public void setEnabled(boolean enabled) {
        tvViewTitle.setEnabled(enabled);

        /*- 
         * 
         * Note: no following calls here, they lead to recursion:
         *        mainLayout.setEnabled(enabled);
         *        setEnabled(enabled);
         * 
         */
        Set<Button> btns = btn2Element.keySet();
        for (Button button : btns) {
            button.setEnabled(enabled);
        }

        super.setEnabled(enabled);
    }

    public T element() {
        return currentElement;
    }

    /**
     * Returns true, if the user request the App itself to decide on the element to be shown in lessons.
     * 
     * @return
     */
    public boolean isRandomInput() {
        if (cbIsRandomInput != null)
            return cbIsRandomInput.isChecked();
        else
            return false;
    }

    public void show(T element) {
        Set<Button> btns = btn2Element.keySet();

        Button selectedBtn = resolveButton(btns, element);

        selectButton(btns, selectedBtn);

    }

    private Button resolveButton(Set<Button> btns, T element) {
        Button selectedBtn = null;
        for (Button button : btns) {
            T d = btn2Element.get(button);
            if (element == d) {
                selectedBtn = button;
                break;
            }
        }
        return selectedBtn;
    }
    
    protected Button resolveButton(T element) {
        Button selectedBtn = null;
        for (Button button : btn2Element.keySet()) {
            T d = btn2Element.get(button);
            if (element == d) {
                selectedBtn = button;
                break;
            }
        }
        return selectedBtn;
    }

    /**
     * Set the single button (<code>selectedBtn</code>) to be selected among the other buttons (<code>btns</code>)
     * passed as parameter.
     * 
     * @param btns
     * @param selectedBtn
     */
    private void selectButton(Set<Button> btns, Button selectedBtn) {
        // make selected another button
        for (Button btn : btns) {
            btn.setTextColor(Color.WHITE);
        }

        selectedBtn.setTextColor(Color.GREEN);
    }

    protected String getTagLogging() {
        return "GT-" + getClass().getSimpleName();
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

    /*
     * ***** INNER CLASSES
     */
    private class InnerOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            if (btn2Element.containsKey(v)) {
                T element = btn2Element.get(v);

                currentElement = element;

                Set<Button> btns = btn2Element.keySet();

                Button selectedBtn = resolveButton(btns, element);

                selectButton(btns, selectedBtn);

                notifyListeners(element);

            } else if (v == cbIsRandomInput) {
                setEnabled(!cbIsRandomInput.isChecked());
            }
        }
    }
}
