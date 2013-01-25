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
import com.ago.guitartrainer.gridshapes.GridShape;

public class ShapesView extends AInoutView<GridShape.Type> {

    private Map<Button, GridShape.Type> btn2Shape = new Hashtable<Button, GridShape.Type>();

    /** title of the view */
    private TextView tvViewTitle;

    /** grid layout, where buttons are located */
    private GridLayout gridForButtons;

    private View mainLayout;

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
        mainLayout = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.shapes_view, this, true);

        tvViewTitle = (TextView) mainLayout.findViewById(R.id.txt_view_title);
        gridForButtons = (GridLayout) mainLayout.findViewById(R.id.grid_for_buttons);

        btn2Shape.put((Button) mainLayout.findViewById(R.id.gridshape_alpha), GridShape.Type.ALPHA);
        btn2Shape.put((Button) mainLayout.findViewById(R.id.gridshape_beta), GridShape.Type.BETA);
        btn2Shape.put((Button) mainLayout.findViewById(R.id.gridshape_gamma), GridShape.Type.GAMMA);
        btn2Shape.put((Button) mainLayout.findViewById(R.id.gridshape_delta), GridShape.Type.DELTA);
        btn2Shape.put((Button) mainLayout.findViewById(R.id.gridshape_epsilon), GridShape.Type.EPSILON);

        InnerOnClickListener onClickListener = new InnerOnClickListener();
        for (Button btnGrid : btn2Shape.keySet()) {
            btnGrid.setOnClickListener(onClickListener);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        tvViewTitle.setEnabled(enabled);

        Set<Button> btns = btn2Shape.keySet();
        for (Button button : btns) {
            button.setEnabled(enabled);
        }

        // TODO: check compatibility problem. compiled fine once
        // gridForButtons.setEnabled(enabled);

        super.setEnabled(enabled);
    }

    public void show(GridShape.Type gridShape) {
        Set<Button> btns = btn2Shape.keySet();

        Button selectedBtn = resolveDegree(btns, gridShape);

        selectButton(btns, selectedBtn);

    }

    private Button resolveDegree(Set<Button> btns, GridShape.Type shape) {
        Button selectedBtn = null;
        for (Button button : btns) {
            GridShape.Type d = btn2Shape.get(button);
            if (shape == d) {
                selectedBtn = button;
                break;
            }
        }
        return selectedBtn;
    }

    /*
     * **** INNER CLASSES
     */
    private class InnerOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            GridShape.Type gridShape = btn2Shape.get(v);

            Set<Button> btns = btn2Shape.keySet();

            Button selectedBtn = resolveDegree(btns, gridShape);

            selectButton(btns, selectedBtn);

            notifyListeners(gridShape);
        }
    }

}