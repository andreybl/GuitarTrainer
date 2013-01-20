package com.ago.guitartrainer.midi;

/** represents states of the pitch detection. The PITCH_DETECTION phases stretches over all other phases. */
public enum PitchDetectorPhase {
    /** designate that the continuous pitch detection is running. */
    PITCH_DETECTION,

    /** during this phase the audio sample is taken, e.g. the sound is recorded */
    AUDIO_SAMPLING,

    /** the sampled audio is transformed with FFT here */
    DO_FFT,

    /** the FFT transformation results are used here to decided about pitch(-s) of sample */
    ANALYZE_FFT

}