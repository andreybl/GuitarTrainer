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
import com.ago.guitartrainer.gridshapes.GridShape;

public class ShapesView extends LinearLayout {


    private Map<Button, GridShape.Type> btn2Grid = new Hashtable<Button, GridShape.Type>();
    
    public ShapesView(Context context) {
        super(context);

        init();
    }

    public ShapesView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public ShapesView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    private void init() {
        View mainLayout = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.shapes_view, this, true);

        btn2Grid.put((Button) mainLayout.findViewById(R.id.gridshape_alpha), GridShape.Type.ALPHA);
        btn2Grid.put((Button) mainLayout.findViewById(R.id.gridshape_beta), GridShape.Type.BETA);
        btn2Grid.put((Button) mainLayout.findViewById(R.id.gridshape_gamma), GridShape.Type.GAMMA);
        btn2Grid.put((Button) mainLayout.findViewById(R.id.gridshape_delta), GridShape.Type.DELTA);
        btn2Grid.put((Button) mainLayout.findViewById(R.id.gridshape_epsilon), GridShape.Type.EPSILON);
        

        InnerOnClickListener onClickListener = new InnerOnClickListener();
        for (Button btnGrid : btn2Grid.keySet()) {
            btnGrid.setOnClickListener(onClickListener);
        }

    }

    private class InnerOnClickListener implements OnClickListener {

        GridShape.Type selected;
        
        @Override
        public void onClick(View v) {
            selected = btn2Grid.get(v);

        }
    }

}