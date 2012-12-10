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
import java.util.Arrays;
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
import com.ago.guitartrainer.notation.Note;
import com.ago.guitartrainer.notation.NoteStave;

/**
 * The thread, in pich the pitch detection takes place
 * 
 * @author Andrej Golovko - jambit GmbH
 * 
 */
public class PitchDetector implements Runnable {

    private final String TAG = "GuitarTrainer";

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
    private final static int MAX_FREQUENCY = 600; // HZ - it's for guitar,
                                                  // should be enough
    private final static int DRAW_FREQUENCY_STEP = 5;

    private AudioRecord recorder_;

//    private List<INoteEventListener> fingerboardListeners = new ArrayList<INoteEventListener>();

    private List<INoteEventListener> listeners = new ArrayList<INoteEventListener>();
    
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

    public PitchDetector() {
        System.loadLibrary("jni");
    }

    private Note prevNote = null;
    
    public void run() {
        NoteStave noteScale = NoteStave.getInstance();
        
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
        recorder_ = new AudioRecord(AudioSource.MIC, RATE, CHANNEL_MODE, ENCODING, 6144);
        if (recorder_.getState() != AudioRecord.STATE_INITIALIZED) {
            Log.w(TAG, "Can't initialize AudioRecord");
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

            /*
             * 
             * TODO: the problem here: the sound played outside of the start()/stop() is not catched.
             * 
             * The solution could be to implement the capturing of audion in the separate thread as making the FFT.
             */

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

            
            double[][] slotsFrequency2NormalizedAmplitude = new double[data.length][2];

            final double freqStep = 1.0 * RATE / CHUNK_SIZE_IN_SAMPLES;

            for (int i = min_frequency_fft; i <= max_frequency_fft; i++) {
                final double slotFrequency = i * freqStep;

                final double draw_frequency = Math.round((slotFrequency - MIN_FREQUENCY) / DRAW_FREQUENCY_STEP)
                        * DRAW_FREQUENCY_STEP + MIN_FREQUENCY;

                final double slotAmplitude = Math.pow(data[i * 2], 2) + Math.pow(data[i * 2 + 1], 2);

                final double normalized_amplitude = slotAmplitude * Math.pow(MIN_FREQUENCY * MAX_FREQUENCY, 0.5)
                        / slotFrequency;

                Double sumForSlot = slotsFrequency2Amplitude.get(draw_frequency);

                if (sumForSlot == null)
                    sumForSlot = 0.0;

                slotsFrequency2Amplitude.put(draw_frequency, Math.pow(slotAmplitude, 0.5) / freqStep + sumForSlot);

                slotsFrequency2NormalizedAmplitude[i][0] = slotFrequency;
                slotsFrequency2NormalizedAmplitude[i][1] = normalized_amplitude;

                
                if (normalized_amplitude > best_amplitude) {
                    best_frequency = slotFrequency;
                    best_amplitude = normalized_amplitude;
                }
            }
            
            Arrays.sort(slotsFrequency2NormalizedAmplitude, new Comparator<double[]>() {
                @Override
                public int compare(final double[] entry1, final double[] entry2) {
                    final Double time1 = entry1[1];
                    final Double time2 = entry2[1];
                        
                    return time1.compareTo(time2)*-1;
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
            int numOfStrings = 6;
            double[][] freqsMatched = new double[numOfStrings][2];
            for (int i = 0; i < numOfStrings; i++) {
                freqsMatched[i][0] = slotsFrequency2NormalizedAmplitude[i][0];
                freqsMatched[i][1] = slotsFrequency2NormalizedAmplitude[i][1];
            }

            double pitch = freqsMatched[0][0];
            double normalAmpli = freqsMatched[0][1];
            
            Note note = noteScale.resolveNote(pitch);
            

            if (prevNote==null) {
                prevNote = note;
                Log.d(TAG, note+": " + normalAmpli);
            } else {
                if (prevNote.equals(note) || note.equals("D2di") || note.equals("F5")) {
                } else {
                    Log.d(TAG, note+": " + normalAmpli);
                }
            }
            
            NotePlayingEvent e = new NotePlayingEvent(note, pitch, normalAmpli, NoteEventType.NOTE_PLAY_STARTED, System.currentTimeMillis());

            notifyListener(e);
        }
    }
    
    private void notifyListener(NotePlayingEvent e) {
        for (INoteEventListener listener : listeners) {
            listener.noteStateChanged(e);
        }
    }

    public void addNoteStateChangedListener(INoteEventListener listener) {
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
