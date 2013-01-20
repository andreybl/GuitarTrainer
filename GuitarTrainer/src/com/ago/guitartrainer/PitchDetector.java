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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.util.Log;

import com.ago.guitartrainer.events.INoteEventListener;
import com.ago.guitartrainer.events.NoteEventType;
import com.ago.guitartrainer.events.NotePlayingEvent;
import com.ago.guitartrainer.midi.FFTPitchDetectorListener;
import com.ago.guitartrainer.midi.PitchDetectorPhase;
import com.ago.guitartrainer.notation.Note;
import com.ago.guitartrainer.notation.NoteStave;

/**
 * The thread, where the pitch detection takes place.
 * 
 * IMPORTANT: leave the class in the current packages. The name of pack and name of JNI function must correspond to each
 * other.
 * 
 * @author Andrej Golovko - jambit GmbH
 * 
 */
public class PitchDetector implements Runnable {

    private final String TAG = "GuitarTrainer";

    /**
     * listener for internal phases of the pitch detection. It is usually implemented by the view, which can visualize
     * the phases of the {@link PitchDetector}.
     */
    private FFTPitchDetectorListener phasesListener;

    /**
     * 
     * listeners for the results of pitch detector.
     * 
     * The listeners are notified, only if some note was detected. The {@link #phasesListener} on the other hand is
     * notified all time during the pitch detector runs.
     * 
     * */
    private List<INoteEventListener> listeners = new ArrayList<INoteEventListener>();

    // Currently, only this combination of rate, encoding and channel mode
    // actually works.
    private final static int RATE = 8000;
    private final static int CHANNEL_MODE = AudioFormat.CHANNEL_IN_MONO;
    private final static int ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    private final static int BUFFER_SIZE_IN_MS = 4000;
    private final static int CHUNK_SIZE_IN_SAMPLES = 4096;

    private final static int CHUNK_SIZE_IN_MS = 1000 * CHUNK_SIZE_IN_SAMPLES / RATE;
    private final static int BUFFER_SIZE_IN_BYTES = RATE * BUFFER_SIZE_IN_MS / 1000 * 2;
    private final static int CHUNK_SIZE_IN_BYTES = RATE * CHUNK_SIZE_IN_MS / 1000 * 2;
    private final static int MIN_FREQUENCY = 50; // HZ
    private final static int MAX_FREQUENCY = 700; // HZ - it's for guitar,
                                                  // should be enough
    private final static int DRAW_FREQUENCY_STEP = 5;

    private AudioRecord recorder_;

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

    /**
     * @deprecated
     * */
    public PitchDetector() {
        System.loadLibrary("jni");
    }

    /**
     * Pitch detector initialized with a view, which can be used to visualize the pitch detector activities.
     * 
     * @param view
     *            to be used by the pitch detector, can be null
     */
    public PitchDetector(FFTPitchDetectorListener view) {
        System.loadLibrary("jni");

        this.phasesListener = view;
    }

    private Note prevNote = null;

    public void run() {

        final int min_frequency_fft = Math.round(MIN_FREQUENCY * CHUNK_SIZE_IN_SAMPLES / RATE);

        final int max_frequency_fft = Math.round(MAX_FREQUENCY * CHUNK_SIZE_IN_SAMPLES / RATE);

        NoteStave noteScale = NoteStave.getInstance();

        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
        recorder_ = new AudioRecord(AudioSource.MIC, RATE, CHANNEL_MODE, ENCODING, 6144);
        if (recorder_.getState() != AudioRecord.STATE_INITIALIZED) {
            Log.w(TAG, "Can't initialize AudioRecord");
            return;
        }

        /*
         * The "audio_data" contains a buffer to put the raw audio output into it. It is also an input to FFT algorithm.
         */
        short[] audio_data = new short[BUFFER_SIZE_IN_BYTES / 2];

        /*-
         * The "data" is a in/out variable.  
         * 
         * The output of FFT is saved in "data". In this case the "data" must be considered
         * as the slots. Each slot is holding the amplitude of certain 
         * frequency. The frequency can be calculated from the index of the slot.
         * 
         * The number of slots is huge - like, 8192. We analyze only part of the slots, 
         * which corresponds to frequencies interesting for us.
         */
        double[] data = new double[CHUNK_SIZE_IN_SAMPLES * 2];

        phasesListener.startedPhase(PitchDetectorPhase.PITCH_DETECTION);
        while (!Thread.interrupted()) {

            phasesListener.startedPhase(PitchDetectorPhase.AUDIO_SAMPLING);

            // make a sample of audio stream
            recorder_.startRecording();
            recorder_.read(audio_data, 0, CHUNK_SIZE_IN_BYTES / 2);
            recorder_.stop();

            for (int i = 0; i < CHUNK_SIZE_IN_SAMPLES; i++) {
                data[i * 2] = audio_data[i];
                data[i * 2 + 1] = 0;
            }
            phasesListener.finishedPhase(PitchDetectorPhase.AUDIO_SAMPLING);

            // do FFT, put results into "data"
            phasesListener.startedPhase(PitchDetectorPhase.DO_FFT);
            DoFFT(data, CHUNK_SIZE_IN_SAMPLES);
            phasesListener.finishedPhase(PitchDetectorPhase.DO_FFT);

            phasesListener.startedPhase(PitchDetectorPhase.ANALYZE_FFT);
            // cache the raw amplitude for each frequency
            HashMap<Double, Double> freqToAmpli = new HashMap<Double, Double>();

            // cache the normalized amplitude for each frequency
            HashMap<Double, Double> freqToNormAmpli = new HashMap<Double, Double>();

            final double freqStep = 1.0 * RATE / CHUNK_SIZE_IN_SAMPLES;

            for (int i = min_frequency_fft; i <= max_frequency_fft; i++) {
                // calculate freq. Take its amli and calcualte the normal ampli
                final double freqOfSlot = i * freqStep;

                final double indexOfSlot = Math.round((freqOfSlot - MIN_FREQUENCY) / DRAW_FREQUENCY_STEP)
                        * DRAW_FREQUENCY_STEP + MIN_FREQUENCY;

                final double ampliOfSlot = Math.pow(data[i * 2], 2) + Math.pow(data[i * 2 + 1], 2);

                final double normAmpliOfSlot = ampliOfSlot * Math.pow(MIN_FREQUENCY * MAX_FREQUENCY, 0.5) / freqOfSlot;

                Double sumForSlot = freqToNormAmpli.get(indexOfSlot);

                if (sumForSlot == null)
                    sumForSlot = 0.0;

                // register for frequency: ampli and normal ampi
                freqToAmpli.put(freqOfSlot, ampliOfSlot);
                freqToNormAmpli.put(freqOfSlot, Math.pow(ampliOfSlot, 0.5) / freqStep + sumForSlot);

            } // for, min..max frequency fft

            List<Double> amplitudesNormalized = new ArrayList<Double>();
            for (Double v : freqToNormAmpli.values()) {
                amplitudesNormalized.add(v);
            }

            Collections.sort(amplitudesNormalized, new Comparator<Double>() {

                @Override
                public int compare(Double lhs, Double rhs) {
                    return rhs.compareTo(lhs);
                }
            });

            /*-
             * TODO:
             * 
             * The FFT is ready, but the result may differ depending on the instrument which is played:
             *   - the 6 strings guitar can have at most 6 frequencies, constituting the sound
             *   - for guitar, the press on one string means on some cases unpress of the other one
             *   - the FFT can not detect pressing/unpressing, but only sounding
             *   - for guitar, the listeners are not only interested in frequencies, but also in string/fret position
             *    
             * The listeners must be notified taking all above into account. Also note: 
             * the frequencies aboce can be sorted for easier handling.
             */

            List<Double> subAmplitudesNormalized = amplitudesNormalized.subList(0, 5);

            double pitch = 0;
            double ampli = 0;
            double ampliNorm = 0;
            for (Double freq : freqToNormAmpli.keySet()) {
                Double a = freqToNormAmpli.get(freq);
                if ((subAmplitudesNormalized.get(0) - a) == 0) {
                    pitch = freq;
                    ampli = freqToAmpli.get(freq);
                    ampliNorm = freqToNormAmpli.get(freq);
                    break;
                }
            }

            Log.d(TAG, "pitch=" + pitch + ", norm.ampli=" + ampliNorm + ", ampli=" + freqToAmpli.get(pitch));

            Note note = noteScale.resolveNote(pitch);

            if (ampliNorm > 200000) {
                if (note != null) {
                    if (note.equals("D2di") || note.equals("F5")) {
                        // DO NOTHING: pitches outside of the guitar playing range
                    } else {
                        if (prevNote == null || !(prevNote.equals(note))) {
                            NotePlayingEvent e = new NotePlayingEvent(note, pitch, ampliNorm,
                                    NoteEventType.NOTE_PLAY_STARTED, System.currentTimeMillis());

                            notifyListener(e);
                        }
                    }
                }
            }

            phasesListener.finishedPhase(PitchDetectorPhase.ANALYZE_FFT);

        } // while, !Thread.interrupted()

        phasesListener.finishedPhase(PitchDetectorPhase.PITCH_DETECTION);
    }

    private void notifyListener(NotePlayingEvent e) {
        if (e.note == Note.D2di || e.note == Note.F5)
            return;

        for (INoteEventListener listener : listeners) {
            listener.noteStateChanged(e);
        }
    }

    public void registerNotesListener(INoteEventListener listener) {
        this.listeners.add(listener);
    }

    public void removeNoteStateChangedListener(INoteEventListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        listeners.clear();
    }
}
