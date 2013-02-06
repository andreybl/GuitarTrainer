package com.ago.guitartrainer.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.ago.guitartrainer.R;
import com.ago.guitartrainer.scalegrids.ScaleGrid;
import com.ago.guitartrainer.scalegrids.ScaleGrid.Type;

public class ScalegridsView extends AbstractNotationView<ScaleGrid.Type> {

    private CheckBox cbIsRootOnlyShown;

    private CheckBox cbIsRandomPosition;

    public ScalegridsView(Context context) {
        super(context);
    }

    public ScalegridsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScalegridsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void doInit(View mainLayout) {
        registerElement((Button) mainLayout.findViewById(R.id.gridshape_alpha), ScaleGrid.Type.ALPHA);
        registerElement((Button) mainLayout.findViewById(R.id.gridshape_beta), ScaleGrid.Type.BETA);
        registerElement((Button) mainLayout.findViewById(R.id.gridshape_gamma), ScaleGrid.Type.GAMMA);
        registerElement((Button) mainLayout.findViewById(R.id.gridshape_delta), ScaleGrid.Type.DELTA);
        registerElement((Button) mainLayout.findViewById(R.id.gridshape_epsilon), ScaleGrid.Type.EPSILON);

        cbIsRandomPosition = (CheckBox) mainLayout.findViewById(R.id.cb_random_position);
        cbIsRootOnlyShown = (CheckBox) mainLayout.findViewById(R.id.cb_root_only);

        // cbIsRootOnlyShown.setOnClickListener(onClickListener);
    }

    @Override
    protected int defaultLayoutResource() {
        return R.layout.scalegrids_view;
    }

    @Override
    protected Type defaultElement() {
        return ScaleGrid.Type.ALPHA;
    }

    /**
     * Returns true, if the user request that only Ist degree position (root) is shown on the currently shown scale
     * grid.
     * 
     * @return
     */
    public boolean isRootOnlyShown() {
        return cbIsRootOnlyShown.isChecked();
    }

    public boolean isRandomPosition() {
        return cbIsRandomPosition.isChecked();
    }

}