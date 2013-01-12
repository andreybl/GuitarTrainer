package com.ago.guitartrainer.ui;

import java.util.Hashtable;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.ago.guitartrainer.R;
import com.ago.guitartrainer.notation.Degree;

public class DegreesView extends LinearLayout {

    private Map<Button, Degree> btn2Degree = new Hashtable<Button, Degree>();

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
        View mainLayout = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.degrees_view, this, true);

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

    private class InnerOnClickListener implements OnClickListener {

        Degree selected = null;

        @Override
        public void onClick(View v) {
            selected = btn2Degree.get(v);

            System.out.println("Clicked: " + selected);
        }
    }

}
