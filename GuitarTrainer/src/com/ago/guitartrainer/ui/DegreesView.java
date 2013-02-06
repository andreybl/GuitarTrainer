package com.ago.guitartrainer.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.ago.guitartrainer.R;
import com.ago.guitartrainer.notation.Degree;

public class DegreesView extends AbstractNotationView<Degree> {

    public DegreesView(Context context) {
        super(context);
    }

    public DegreesView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DegreesView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void doInit(View mainLayout) {
        registerElement((Button) mainLayout.findViewById(R.id.degree_1), Degree.ONE);
        registerElement((Button) mainLayout.findViewById(R.id.degree_2), Degree.TWO);
        registerElement((Button) mainLayout.findViewById(R.id.degree_3), Degree.THREE);
        registerElement((Button) mainLayout.findViewById(R.id.degree_4), Degree.FOUR);
        registerElement((Button) mainLayout.findViewById(R.id.degree_5), Degree.FIVE);
        registerElement((Button) mainLayout.findViewById(R.id.degree_6), Degree.SIX);
        registerElement((Button) mainLayout.findViewById(R.id.degree_7), Degree.SEVEN);

    }

    @Override
    protected int defaultLayoutResource() {
        return R.layout.degrees_view;
    }

    @Override
    protected Degree defaultElement() {
        return Degree.ONE;
    }

}
