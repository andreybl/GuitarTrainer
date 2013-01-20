package com.ago.guitartrainer.midi;

import android.view.View;

/**
 * 
 * Listener for FFT detection events, is usualy implemented by {@link View} subclasses, which can visualize the phases
 * passed during FFT-based pitch detection.
 * 
 * @author Andrej Golovko - jambit GmbH
 * 
 */
public interface FFTPitchDetectorListener {



    void startedPhase(PitchDetectorPhase phase);

    void finishedPhase(PitchDetectorPhase phase);
}
