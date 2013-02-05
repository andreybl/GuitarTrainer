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
import com.ago.guitartrainer.notation.Chord;
import com.ago.guitartrainer.notation.Degree;

public class ChordsView extends AInoutView<Degree> {

    private Map<Button, Degree[]> btn2Chord = new Hashtable<Button, Degree[]>();

    /** title of the view */
    private TextView tvViewTitle;

    private View mainLayout;

    private CheckBox cbIsRandomInput;

    private Degree[] currentChord;

    public ChordsView(Context context) {
        super(context);

        init();
    }

    public ChordsView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public ChordsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    private void init() {
        mainLayout = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.chords_view, this, true);

        tvViewTitle = (TextView) mainLayout.findViewById(R.id.txt_view_title);

        btn2Chord.put((Button) mainLayout.findViewById(R.id.chord_maj), Chord.major);
        btn2Chord.put((Button) mainLayout.findViewById(R.id.chord_min), Chord.minor);
        btn2Chord.put((Button) mainLayout.findViewById(R.id.chord_dim), Chord.dim);
        btn2Chord.put((Button) mainLayout.findViewById(R.id.chord_aug), Chord.aug);
        btn2Chord.put((Button) mainLayout.findViewById(R.id.chord_maj7), Chord.major7thChord);
        btn2Chord.put((Button) mainLayout.findViewById(R.id.chord_min7), Chord.minor7thChord);
        btn2Chord.put((Button) mainLayout.findViewById(R.id.chord_domsept7), Chord.dominantSeptChord);
        btn2Chord.put((Button) mainLayout.findViewById(R.id.chord_dim7), Chord.dim7thChord);

        cbIsRandomInput = (CheckBox) mainLayout.findViewById(R.id.cb_random_input);

        /* default degree selected */
        currentChord = Chord.major;
        show(currentChord);

        InnerOnClickListener onClickListener = new InnerOnClickListener();
        for (Button btnGrid : btn2Chord.keySet()) {
            btnGrid.setOnClickListener(onClickListener);
        }

        cbIsRandomInput.setOnClickListener(onClickListener);

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
        Set<Button> btns = btn2Chord.keySet();
        for (Button button : btns) {
            button.setEnabled(enabled);
        }

        super.setEnabled(enabled);
    }

    public Degree[] chord() {
        return currentChord;
    }

    /**
     * Returns true, if the user request the App itself to decide on the degree to be shown in lessons.
     * 
     * @return
     */
    public boolean isRandomInput() {
        return cbIsRandomInput.isChecked();
    }

    public void show(Degree[] degree) {
        Set<Button> btns = btn2Chord.keySet();

        Button selectedBtn = resolveDegree(btns, degree);

        selectButton(btns, selectedBtn);

    }

    private Button resolveDegree(Set<Button> btns, Degree[] chord) {
        Button selectedBtn = null;
        for (Button button : btns) {
            Degree[] c = btn2Chord.get(button);
            if (chord == c) {
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

        @Override
        public void onClick(View v) {
            if (btn2Chord.containsKey(v)) {
                Degree[] chord = btn2Chord.get(v);

                currentChord = chord;

                Set<Button> btns = btn2Chord.keySet();

                Button selectedBtn = resolveDegree(btns, chord);

                selectButton(btns, selectedBtn);

            } else if (v == cbIsRandomInput) {
                setEnabled(!cbIsRandomInput.isChecked());
            }
        }
    }

}
