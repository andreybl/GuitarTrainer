package com.ago.guitartrainer.ui;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import com.ago.guitartrainer.R;
import com.ago.guitartrainer.notation.Degree;

public class DegreesView extends AInoutView<Degree> {

    private Map<Button, Degree> btn2Degree = new Hashtable<Button, Degree>();

    /** title of the view */
    private TextView tvViewTitle;

    /** grid layout, where buttons are located */
    private GridLayout gridForButtons;

    private View mainLayout;

    public DegreesView(Context context) {
        super(context);

        init();
    }

    public DegreesView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public DegreesView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    private void init() {
        mainLayout = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.degrees_view, this, true);

        tvViewTitle = (TextView) mainLayout.findViewById(R.id.txt_view_title);

        gridForButtons = (GridLayout) mainLayout.findViewById(R.id.grid_for_buttons);

        btn2Degree.put((Button) mainLayout.findViewById(R.id.degree_1), Degree.ONE);
        btn2Degree.put((Button) mainLayout.findViewById(R.id.degree_2), Degree.TWO);
        btn2Degree.put((Button) mainLayout.findViewById(R.id.degree_3), Degree.THREE);
        btn2Degree.put((Button) mainLayout.findViewById(R.id.degree_4), Degree.FOUR);
        btn2Degree.put((Button) mainLayout.findViewById(R.id.degree_5), Degree.FIVE);
        btn2Degree.put((Button) mainLayout.findViewById(R.id.degree_6), Degree.SIX);
        btn2Degree.put((Button) mainLayout.findViewById(R.id.degree_7), Degree.SEVEN);

        InnerOnClickListener onClickListener = new InnerOnClickListener();
        for (Button btnGrid : btn2Degree.keySet()) {
            btnGrid.setOnClickListener(onClickListener);
        }

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
        Set<Button> btns = btn2Degree.keySet();
        for (Button button : btns) {
            button.setEnabled(enabled);
        }

        // TODO: check compatibility problem. compiled fine once
        // gridForButtons.setEnabled(enabled);

        super.setEnabled(enabled);
    }

    public void show(Degree degree) {
        Set<Button> btns = btn2Degree.keySet();

        Button selectedBtn = resolveDegree(btns, degree);

        selectButton(btns, selectedBtn);

    }

    private Button resolveDegree(Set<Button> btns, Degree degree) {
        Button selectedBtn = null;
        for (Button button : btns) {
            Degree d = btn2Degree.get(button);
            if (degree == d) {
                selectedBtn = button;
                break;
            }
        }
        return selectedBtn;
    }

    /*
     * ***** INNER CLASSES
     */
    private class InnerOnClickListener implements OnClickListener {

        Degree selected = null;

        @Override
        public void onClick(View v) {
            selected = btn2Degree.get(v);

            System.out.println("Clicked: " + selected);
        }
    }

}
