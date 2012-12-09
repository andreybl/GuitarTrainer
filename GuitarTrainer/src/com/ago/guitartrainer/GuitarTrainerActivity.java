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

import com.ago.guitartrainer.ui.DrawableView;

import android.app.Activity;
import android.os.Bundle;

/**
 * An activity which controls the pitch detection continuously and presents the results of detection.
 * 
 * The pitch detection runs in a separate thread and stops only when the activity stops.
 * 
 * @author Andrej Golovko - jambit GmbH
 * 
 */
public class GuitarTrainerActivity extends Activity implements IFingerboardListener {

    /** view with fretboard */
    DrawableView fingerboardView;

    /** a thread in which the internal audio source is used to detect the pitch */
    Thread pitch_detector_thread_;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        
//        fingerboardView = new DrawableView(this);
//        setContentView(fingerboardView);
        
    }

    @Override
    public void onStart() {
        super.onStart();
        PitchDetector pitchDetector = new PitchDetector();
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

}