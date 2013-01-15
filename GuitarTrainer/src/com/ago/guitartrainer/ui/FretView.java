package com.ago.guitartrainer.ui;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ago.guitartrainer.R;

public class FretView extends LinearLayout {


    public FretView(Context context) {
        super(context);

        init();
    }

    public FretView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public FretView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    private void init() {
        View mainLayout = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.fret_view, this, true);
        
        ImageView imageView = (ImageView)mainLayout.findViewById(R.id.img_fretimageview);
        
        System.out.println("bla");
        
        // TODO: register listeners for the fretImageView or for the current view

//        btn2Degree.put((Button) mainLayout.findViewById(R.id.degree_1), Degree.ONE);
//        btn2Degree.put((Button) mainLayout.findViewById(R.id.degree_2), Degree.TWO);
//        btn2Degree.put((Button) mainLayout.findViewById(R.id.degree_3), Degree.THREE);
//        btn2Degree.put((Button) mainLayout.findViewById(R.id.degree_4), Degree.FOUR);
//        btn2Degree.put((Button) mainLayout.findViewById(R.id.degree_5), Degree.FIVE);
//        btn2Degree.put((Button) mainLayout.findViewById(R.id.degree_6), Degree.SIX);
//        btn2Degree.put((Button) mainLayout.findViewById(R.id.degree_7), Degree.SEVEN);
//
//        InnerOnClickListener onClickListener = new InnerOnClickListener();
//        for (Button btnGrid : btn2Degree.keySet()) {
//            btnGrid.setOnClickListener(onClickListener);
//        }

    }

    private class InnerOnClickListener implements OnClickListener {

//        Degree selected = null;

        @Override
        public void onClick(View v) {
//            selected = btn2Degree.get(v);

//            System.out.println("Clicked: " + selected);
        }
    }

}
