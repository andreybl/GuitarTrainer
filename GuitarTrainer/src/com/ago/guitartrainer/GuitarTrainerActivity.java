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

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.ago.guitartrainer.events.INoteEventListener;
import com.ago.guitartrainer.events.NotePlayingEvent;
import com.ago.guitartrainer.lessons.AnswerEvaluator;
import com.ago.guitartrainer.notation.NoteStave;
import com.ago.guitartrainer.ui.FretImageView;

/**
 * An activity which controls the pitch detection continuously and presents the results of detection.
 * 
 * The pitch detection runs in a separate thread and stops only when the activity stops.
 * 
 * @author Andrej Golovko - jambit GmbH
 * @deprecated
 */
public class GuitarTrainerActivity extends Activity {

    public static String TAG = "GuitarTrainer";

    /** a thread in which the internal audio source is used to detect the pitch */
    private Thread threadPitchDetector;

    private Button btnStartLesson;

    public TextView txtPlayNote;
    public TextView txtLessonResults;

    public FretImageView fretImageView;

    private SeekBar sbarFretSelection;

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

        sbarFretSelection.setMax(12);

        currentLesson = new AnswerEvaluator(this);

        OnSeekBarChangeListener seekBarListener = currentLesson.new InnerOnSeekBarChangeListener();
        sbarFretSelection.setOnSeekBarChangeListener(seekBarListener);

        btnStartLesson.setOnClickListener(new InnerOnClickListener());

    }

    @Override
    public void onStart() {
        super.onStart();
        PitchDetector pitchDetector = new PitchDetector();

        pitchDetector.addNoteStateChangedListener(new InnerNotesListener());

        threadPitchDetector = new Thread(pitchDetector);
        threadPitchDetector.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        threadPitchDetector.interrupt();
    }

    /*
     * ***************** Inner Classes
     */

    private AnswerEvaluator currentLesson;

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