/** Copyright (C) 2009 by Aleksey Surkov.
 **
 ** Permission to use, copy, modify, and distribute this software and its
 ** documentation for any purpose and without fee is hereby granted, provided
 ** that the above copyright notice appear in all copies and that both that
 ** copyright notice and this permission notice appear in supporting
 ** documentation.  This software is provided "as is" without express or
 ** implied warranty.
 */

package com.ago.guitartrainer;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.ago.guitartrainer.events.INoteEventListener;
import com.ago.guitartrainer.events.NotePlayingEvent;
import com.ago.guitartrainer.gridshapes.GridShape;
import com.ago.guitartrainer.lessons.Lesson;
import com.ago.guitartrainer.notation.NoteStave;
import com.ago.guitartrainer.notation.Position;
import com.ago.guitartrainer.ui.DrawableView;
import com.ago.guitartrainer.ui.FretImageView;

/**
 * An activity which controls the pitch detection continuously and presents the results of detection.
 * 
 * The pitch detection runs in a separate thread and stops only when the activity stops.
 * 
 * @author Andrej Golovko - jambit GmbH
 * 
 */
public class GuitarTrainerActivity extends Activity {

    public static String TAG = "GuitarTrainer";

    /** view with fretboard */
    private DrawableView fingerboardView;

    /** a thread in which the internal audio source is used to detect the pitch */
    private Thread pitch_detector_thread_;

    private Button btnStartLesson;

    public TextView txtPlayNote;
    public TextView txtLessonResults;
    
    public FretImageView fretImageView;

    private SeekBar sbarFretSelection;

    private CompoundButton cbGridAlpha;
    private CompoundButton cbGridBeta;
    private CompoundButton cbGridGamma;
    private CompoundButton cbGridDelta;
    private CompoundButton cbGridEpsilon;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        txtPlayNote = (TextView) findViewById(R.id.txt_playnote);
        txtLessonResults = (TextView) findViewById(R.id.txt_lessonresults);
        btnStartLesson = (Button) findViewById(R.id.btn_start_lesson);
        fretImageView = (FretImageView) findViewById(R.id.img_fretimageview);
        sbarFretSelection = (SeekBar) findViewById(R.id.sbar_fretselection);

        cbGridAlpha = (CompoundButton) findViewById(R.id.cb_gridselection_alpha);
        cbGridBeta = (CompoundButton) findViewById(R.id.cb_gridselection_beta);
        cbGridGamma = (CompoundButton) findViewById(R.id.cb_gridselection_gamma);
        cbGridDelta = (CompoundButton) findViewById(R.id.cb_gridselection_delta);
        cbGridEpsilon = (CompoundButton) findViewById(R.id.cb_gridselection_epsilon);

        sbarFretSelection.setMax(12);

        currentLesson = new Lesson(this);
        
        OnSeekBarChangeListener seekBarListener = currentLesson.new InnerOnSeekBarChangeListener();
        sbarFretSelection.setOnSeekBarChangeListener(seekBarListener);
        OnCheckedChangeListener onCheckedListener = currentLesson.new InnerOnCheckedChangeListener();
        cbGridAlpha.setOnCheckedChangeListener(onCheckedListener);
        cbGridBeta.setOnCheckedChangeListener(onCheckedListener);
        cbGridGamma.setOnCheckedChangeListener(onCheckedListener);
        cbGridDelta.setOnCheckedChangeListener(onCheckedListener);
        cbGridEpsilon.setOnCheckedChangeListener(onCheckedListener);

        btnStartLesson.setOnClickListener(new InnerOnClickListener());

    }

    @Override
    public void onStart() {
        super.onStart();
        PitchDetector pitchDetector = new PitchDetector();

        pitchDetector.addNoteStateChangedListener(new InnerNotesListener());

        pitch_detector_thread_ = new Thread(pitchDetector);
        pitch_detector_thread_.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        pitch_detector_thread_.interrupt();
    }

    /**
     * Shows on the guitar the frequency as detected by FFT.
     * 
     * The <code>pitch</code> is the frequency with the highest amplitude which was recognized by FFT as dominating the
     * analyzed wave (ASSUMPTION).
     * 
     * 
     * @param frequencies
     * @param pitch
     */
    public void showPitchDetectionResult(final HashMap<Double, Double> frequencies, final double pitch) {
        fingerboardView.setDetectionResults(frequencies, pitch);
    }

    /*
     * ***************** Inner Classes
     */
    
    private Lesson currentLesson;

    private class InnerOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            currentLesson.startLesson();
        }
    }

    private class InnerNotesListener implements INoteEventListener {

        @Override
        public void noteStateChanged(NotePlayingEvent e) {

            /*
             * notes are only interested after the lesson have started
             */
            if (!currentLesson.isActive())
                return;

            NoteStave stave = NoteStave.getInstance();

            boolean isCorrect = currentLesson.suggestAnswer(e.note);
            
            if (isCorrect) {
                currentLesson.startLesson();
            }
        }
    }



}