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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.ago.guitartrainer.events.INoteEventListener;
import com.ago.guitartrainer.events.NotePlayingEvent;
import com.ago.guitartrainer.gridshapes.AlphaGridShape;
import com.ago.guitartrainer.notation.Degree;
import com.ago.guitartrainer.notation.Note;
import com.ago.guitartrainer.notation.NoteStave;
import com.ago.guitartrainer.notation.Position;
import com.ago.guitartrainer.ui.DrawableView;
import com.ago.guitartrainer.ui.FretImageView;
import com.ago.guitartrainer.utils.ArrayUtils;
import com.ago.guitartrainer.utils.LessonsUtils;

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

    private TextView txtPlayNote;

    private FretImageView fretImageView;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        txtPlayNote = (TextView) findViewById(R.id.txt_playnote);

        btnStartLesson = (Button) findViewById(R.id.btn_start_lesson);

        btnStartLesson.setOnClickListener(new InnerOnClickListener());

        fretImageView = (FretImageView) findViewById(R.id.img_fretimageview);

        // fingerboardView = new DrawableView(this);
        // setContentView(fingerboardView);

        /*-
         * TODO: 
         * + use shape, let's say alpha-shape
         * select random note from alpha-shape
         * show it to the user
         * start listening window
         * finish listening window
         * which notes received?
         * filter them throu shape
         * show on the fretboard
         * save statistics for note recognition
         * 
         * Suggestion: should we run in thread?
         */
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
    private long tstLessonStart;
    private long tstLessonEnd;
    private AlphaGridShape lessonGridShape;
    private Position lessonPosition;

    private class InnerOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {

            /*-
             * Lesson: find degree in the grid shape
             * 
             * Idea: In fixed projection of the grid shape pick at random one of the strong degrees.
             *       User is shown: 
             *          * grid shape name
             *          * Note of the root degree
             *       Request user to play the degree on the fretboard.
             */

            // 1. define the projection for the grid:
            Note root = Note.D4;
            lessonGridShape = new AlphaGridShape(root);

            // 2. pick at random the degree to play in the projection
            lessonPosition = LessonsUtils.pickPosition(lessonGridShape);
            Degree d = lessonGridShape.position2Degree(lessonPosition);

            // 3. request the user to play degree
            // Note: "Tip" is shown for debug purposes only
            txtPlayNote.setText("Find degree " + d + " @(alpha, " + root + "). Tip: " + lessonPosition);

            // 4. countdown runs ...
            tstLessonStart = System.currentTimeMillis();
        }

    }

    private class InnerNotesListener implements INoteEventListener {

        @Override
        public void noteStateChanged(NotePlayingEvent e) {

            /*
             * notes are only interested after the lesson have started
             */
            if (tstLessonStart == 0)
                return;

            NoteStave stave = NoteStave.getInstance();

            /*
             * user have played some note on the guitar, several positions could be responsible for it
             */
            List<Position> positions = stave.resolvePositions(e.note);
            final List<Position> positionsProjected = lessonGridShape.applyShape(positions);

            Log.d(TAG, e.toString() + ", Positions origianl: " + positions+", Positions projected: " + positionsProjected);

            runOnUiThread(new Runnable() {
                public void run() {
                    fretImageView.showOnFret(positionsProjected);
                }
            });

            // boolean inArray = ArrayUtils.inArray(lessonPosition, positions);
            // if (inArray) {
            // tstLessonEnd = System.currentTimeMillis();
            //
            // runOnUiThread(new Runnable() {
            // public void run() {
            // fretImageView.showOnFret(lessonPosition);
            // }
            // });
            // }
            // lessonGridShape.calculatePositions(degree);

        }
    }
}