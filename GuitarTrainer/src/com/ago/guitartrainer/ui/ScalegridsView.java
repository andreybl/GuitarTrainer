package com.ago.guitartrainer.ui;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ago.guitartrainer.R;
import com.ago.guitartrainer.scalegrids.ScaleGrid;
import com.ago.guitartrainer.scalegrids.ScaleGrid.Type;

public class ScalegridsView extends AInoutView<ScaleGrid.Type> {

    private Map<Button, ScaleGrid.Type> btn2Shape = new Hashtable<Button, ScaleGrid.Type>();

    /** title of the view */
    private TextView tvViewTitle;

    private View mainLayout;

    private ScaleGrid.Type currentScalegridType;

    private CheckBox cbIsRootOnlyShown;

    private CheckBox cbIsRandomInput;
    
    private CheckBox cbIsRandomPosition;

    
    public ScalegridsView(Context context) {
        super(context);

        init();
    }

    public ScalegridsView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public ScalegridsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    private void init() {
        mainLayout = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.scalegrids_view, this, true);

        tvViewTitle = (TextView) mainLayout.findViewById(R.id.txt_view_title);

        btn2Shape.put((Button) mainLayout.findViewById(R.id.gridshape_alpha), ScaleGrid.Type.ALPHA);
        btn2Shape.put((Button) mainLayout.findViewById(R.id.gridshape_beta), ScaleGrid.Type.BETA);
        btn2Shape.put((Button) mainLayout.findViewById(R.id.gridshape_gamma), ScaleGrid.Type.GAMMA);
        btn2Shape.put((Button) mainLayout.findViewById(R.id.gridshape_delta), ScaleGrid.Type.DELTA);
        btn2Shape.put((Button) mainLayout.findViewById(R.id.gridshape_epsilon), ScaleGrid.Type.EPSILON);

        cbIsRandomInput = (CheckBox) mainLayout.findViewById(R.id.cb_random_input);
        cbIsRandomPosition = (CheckBox) mainLayout.findViewById(R.id.cb_random_position);
        cbIsRootOnlyShown = (CheckBox) mainLayout.findViewById(R.id.cb_root_only);

        /* defaults for selected scalegrid type */
        currentScalegridType = Type.ALPHA;
        show(currentScalegridType);

        InnerOnClickListener onClickListener = new InnerOnClickListener();
        for (Button btnGrid : btn2Shape.keySet()) {
            btnGrid.setOnClickListener(onClickListener);
        }

        cbIsRandomInput.setOnClickListener(onClickListener);
        cbIsRootOnlyShown.setOnClickListener(onClickListener);
    }

    @Override
    public void setEnabled(boolean enabled) {
        tvViewTitle.setEnabled(enabled);

        Set<Button> btns = btn2Shape.keySet();
        for (Button button : btns) {
            button.setEnabled(enabled);
        }

        super.setEnabled(enabled);
    }

    public void show(ScaleGrid.Type gridShape) {
        Set<Button> btns = btn2Shape.keySet();

        Button selectedBtn = resolveDegree(btns, gridShape);

        selectButton(btns, selectedBtn);

    }

    public ScaleGrid.Type scalegridType() {
        return currentScalegridType;
    }

    private Button resolveDegree(Set<Button> btns, ScaleGrid.Type shape) {
        Button selectedBtn = null;
        for (Button button : btns) {
            ScaleGrid.Type d = btn2Shape.get(button);
            if (shape == d) {
                selectedBtn = button;
                break;
            }
        }
        return selectedBtn;
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

    /**
     * Returns true, if the user request the App itself to decide on the scale grid to be shown in lessons.
     * 
     * @return
     */
    public boolean isRandomInput() {
        return cbIsRandomInput.isChecked();
    }

    public boolean isRandomPosition() {
        return cbIsRandomPosition.isChecked();
    }
    
    /*
     * **** INNER CLASSES
     */
    private class InnerOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {

            if (btn2Shape.containsKey(v)) {
                ScaleGrid.Type gridShape = btn2Shape.get(v);

                currentScalegridType = gridShape;

                Set<Button> btns = btn2Shape.keySet();

                Button selectedBtn = resolveDegree(btns, gridShape);

                selectButton(btns, selectedBtn);

                /*
                 * TODO: remove listeners from **View classes at all?
                 * 
                 * The selection in view causes an event. But usually, i want to parameterize the lesson by selecting in
                 * multiple views. So don't notify, but let the user to decided when to next().
                 */
                // notifyListeners(gridShape);
            } else if (v == cbIsRandomInput) {
                setEnabled(!cbIsRandomInput.isChecked());
            }
        }
    }

}