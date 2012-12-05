/** Copyright (C) 2009 by Aleksey Surkov.
 **
 ** Permission to use, copy, modify, and distribute this software and its
 ** documentation for any purpose and without fee is hereby granted, provided
 ** that the above copyright notice appear in all copies and that both that
 ** copyright notice and this permission notice appear in supporting
 ** documentation.  This software is provided "as is" without express or
 ** implied warranty.
 */

package com.example.GuitarTuner;

import java.util.HashMap;

import android.app.AlertDialog;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.os.Handler;

/**
 * The thread, in pich the pitch detection takes place
 * 
 * @author Andrej Golovko - jambit GmbH
 * 
 */
public class PitchDetector implements Runnable {
    // Currently, only this combination of rate, encoding and channel mode
    // actually works.
    private final static int RATE = 8000;
    private final static int CHANNEL_MODE = AudioFormat.CHANNEL_IN_MONO;
    private final static int ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    private final static int BUFFER_SIZE_IN_MS = 3000;
    private final static int CHUNK_SIZE_IN_SAMPLES = 4096; // = 2 ^
                                                           // CHUNK_SIZE_IN_SAMPLES_POW2
    private final static int CHUNK_SIZE_IN_MS = 1000 * CHUNK_SIZE_IN_SAMPLES / RATE;
    private final static int BUFFER_SIZE_IN_BYTES = RATE * BUFFER_SIZE_IN_MS / 1000 * 2;
    private final static int CHUNK_SIZE_IN_BYTES = RATE * CHUNK_SIZE_IN_MS / 1000 * 2;
    private final static int MIN_FREQUENCY = 50; // HZ
    private final static int MAX_FREQUENCY = 600; // HZ - it's for guitar,
                                                  // should be enough
    private final static int DRAW_FREQUENCY_STEP = 5;

    private GuitarTunerActivity parent_;
    private AudioRecord recorder_;

    // handler used to send the calculation results to the UI thread
    private Handler handler_;

    /**
     * 
     * Taken from which took it from Numerical Recipes in C++, p.513
     * 
     * The 'data' should be an array of length 'size' * 2, where each even element corresponds to the real part and each
     * odd element to the imaginary part of a complex number. For an incoming stream, all imaginary parts should be
     * zero.
     * 
     * An NDK library 'fft-jni'
     * 
     * @param data
     *            as specified above
     * @param size
     * @see from http://www.ddj.com/cpp/199500857
     * 
     **/
    public native void DoFFT(double[] data, int size);

    public PitchDetector(GuitarTunerActivity parent, Handler handler) {
        parent_ = parent;
        handler_ = handler;
        System.loadLibrary("jni");
    }

    public void run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
        recorder_ = new AudioRecord(AudioSource.MIC, RATE, CHANNEL_MODE, ENCODING, 6144);
        if (recorder_.getState() != AudioRecord.STATE_INITIALIZED) {
            showError("Can't initialize AudioRecord");
            return;
        }
        short[] audio_data = new short[BUFFER_SIZE_IN_BYTES / 2];
        double[] data = new double[CHUNK_SIZE_IN_SAMPLES * 2];
        final int min_frequency_fft = Math.round(MIN_FREQUENCY * CHUNK_SIZE_IN_SAMPLES / RATE);
        final int max_frequency_fft = Math.round(MAX_FREQUENCY * CHUNK_SIZE_IN_SAMPLES / RATE);
        while (!Thread.interrupted()) {

            recorder_.startRecording();
            recorder_.read(audio_data, 0, CHUNK_SIZE_IN_BYTES / 2);
            recorder_.stop();
            for (int i = 0; i < CHUNK_SIZE_IN_SAMPLES; i++) {
                data[i * 2] = audio_data[i];
                data[i * 2 + 1] = 0;
            }

            // FFT !
            DoFFT(data, CHUNK_SIZE_IN_SAMPLES);

            /*
             * The result of a (forward) Fast Fourier Transform represents the spectrum of the signal at discrete
             * frequencies.
             * 
             * The results of the FFT can be represented as the slots, where each slot is staying for the (discreet)
             * frequency and the value in this slot corresponds to the frequency's amplitude.
             * 
             * As a result, we must iterate over all slots and find out the slot with the highest amplitude assigned to
             * it. Instead of "slot" we use the word "frequency".
             */

            /*
             * the frequency which was recongnized as the most dominating up to now based on the amplitude value.
             */
            double best_frequency = min_frequency_fft;
            double best_amplitude = 0;

            HashMap<Double, Double> slotsFrequency2Amplitude = new HashMap<Double, Double>();
            final double draw_frequency_step = 1.0 * RATE / CHUNK_SIZE_IN_SAMPLES;

            for (int i = min_frequency_fft; i <= max_frequency_fft; i++) {
                final double slotFrequency = i * draw_frequency_step;

                final double draw_frequency = Math.round((slotFrequency - MIN_FREQUENCY) / DRAW_FREQUENCY_STEP)
                        * DRAW_FREQUENCY_STEP + MIN_FREQUENCY;

                final double slotAmplitude = Math.pow(data[i * 2], 2) + Math.pow(data[i * 2 + 1], 2);

                final double normalized_amplitude = slotAmplitude * Math.pow(MIN_FREQUENCY * MAX_FREQUENCY, 0.5)
                        / slotFrequency;

                Double current_sum_for_this_slot = slotsFrequency2Amplitude.get(draw_frequency);

                if (current_sum_for_this_slot == null)
                    current_sum_for_this_slot = 0.0;
                slotsFrequency2Amplitude.put(draw_frequency, Math.pow(slotAmplitude, 0.5) / draw_frequency_step
                        + current_sum_for_this_slot);
                if (normalized_amplitude > best_amplitude) {
                    best_frequency = slotFrequency;
                    best_amplitude = normalized_amplitude;
                }
            }
            postToUI(slotsFrequency2Amplitude, best_frequency);
        }
    }

    /**
     * Sends the FFT calculation results to the UI thread.
     * 
     * @param frequencies
     * @param pitch
     */
    private void postToUI(final HashMap<Double, Double> frequencies, final double pitch) {
        handler_.post(new Runnable() {
            public void run() {
                parent_.showPitchDetectionResult(frequencies, pitch);
            }
        });
    }

    private void showError(final String msg) {
        handler_.post(new Runnable() {
            public void run() {
                new AlertDialog.Builder(parent_).setTitle("GuitarTuner").setMessage(msg).show();
            }
        });
    }

}
