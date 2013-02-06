package com.ago.guitartrainer.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.ago.guitartrainer.R;
import com.ago.guitartrainer.notation.Chord;
import com.ago.guitartrainer.notation.Degree;

public class ChordsView extends AbstractNotationView<Degree[]> {
    public ChordsView(Context context) {
        super(context);
    }

    public ChordsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChordsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void doInit(View mainLayout) {
        registerElement((Button) mainLayout.findViewById(R.id.chord_maj), Chord.major);
        registerElement((Button) mainLayout.findViewById(R.id.chord_min), Chord.minor);
        registerElement((Button) mainLayout.findViewById(R.id.chord_aug), Chord.aug);
        registerElement((Button) mainLayout.findViewById(R.id.chord_dim), Chord.dim);
        registerElement((Button) mainLayout.findViewById(R.id.chord_maj7), Chord.major7thChord);
        registerElement((Button) mainLayout.findViewById(R.id.chord_min7), Chord.minor7thChord);
        registerElement((Button) mainLayout.findViewById(R.id.chord_domsept7), Chord.dominantSeptChord);
        registerElement((Button) mainLayout.findViewById(R.id.chord_dim7), Chord.dim7thChord);
        registerElement((Button) mainLayout.findViewById(R.id.chord_min7b5), Chord.minor7b5Chord);

    }

    @Override
    protected int defaultLayoutResource() {
        return R.layout.chords_view;
    }

    @Override
    protected Degree[] defaultElement() {
        return Chord.major;
    }
}
