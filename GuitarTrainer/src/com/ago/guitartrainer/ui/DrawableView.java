/** Copyright (C) 2009 by Aleksey Surkov.
 **
 ** Permission to use, copy, modify, and distribute this software and its
 ** documentation for any purpose and without fee is hereby granted, provided
 ** that the above copyright notice appear in all copies and that both that
 ** copyright notice and this permission notice appear in supporting
 ** documentation.  This software is provided "as is" without express or
 ** implied warranty.
 */

package com.ago.guitartrainer.ui;

import java.util.HashMap;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.view.View;

import com.ago.guitartrainer.PitchDetectionRepresentation;

/**
 * 
 * The view represents the guitar fretboard with six strings and first 5 frets on it.
 * 
 * Each position is associated with frequency for the note.
 * 
 * 
 */
public class DrawableView extends View {

    // NotePitches[i][j] is the pitch of i-th string on j-th fret. 0th fret means an open fret.
    private double[][] notePitches = new double[6][6];

    private TreeMap<Double, Integer> notePitchesMap = new TreeMap<Double, Integer>();

    private final static int MIN_AMPLITUDE = 40000;
    private final static int maxAmplitude = 200000;
    private final static double MAX_PITCH_DIFF = 20; // in Hz
    private final static int UI_UPDATE_MS = 100;

    // padding arround the current view
    private final int FINGERBOARD_PADDING = 10;

    final static int HEADSTOCK_HEIGHT = 10;
    final static int HEADSTOCK_WIDTH = 50;

    /**
     * Contains the results of FFT, which can be interpreted as frequencies with respective amplitude values assigned to
     * them.
     */
    private HashMap<Double, Double> frequency2Amplitude;

    /**
     * represents the pitch (frequency) which was recognized by FFT as the dominating frequency of the sample.
     */
    private double bestPitchMatch;

    private PitchDetectionRepresentation pitchValue;
    private Handler handler_;
    private Timer timer_;

    /**
     * Initialize the array of pitches. Starts timer for the view invalidation after predefined period of time.
     * Invalidation occurs on periodic basis.
     * 
     * @param context
     */
    public DrawableView(Context context) {
        super(context);

        /*-
         * the format of the array is:
         * [string][fret] 
         */

        // E - open 6th string
        notePitches[0][0] = 82.41;
        notePitches[0][1] = 87.31;
        notePitches[0][2] = 92.5;
        notePitches[0][3] = 98;
        notePitches[0][4] = 103.8;

        // A - open 5th string
        notePitches[1][0] = 110;
        notePitches[1][1] = 116.54;
        notePitches[1][2] = 123.48;
        notePitches[1][3] = 130.82;
        notePitches[1][4] = 138.59;

        // D - open 4th string
        notePitches[2][0] = 147.83; // 146.8?
        notePitches[2][1] = 155.56;
        notePitches[2][2] = 164.81;
        notePitches[2][3] = 174.62;
        notePitches[2][4] = 185;

        // G - open 3rd string
        notePitches[3][0] = 196;
        notePitches[3][1] = 207;
        notePitches[3][2] = 220;
        notePitches[3][3] = 233.08;

        // B - open 2nd string
        notePitches[4][0] = 246.96;
        notePitches[4][1] = 261.63;
        notePitches[4][2] = 277.18;
        notePitches[4][3] = 293.66;
        notePitches[4][4] = 311.13;

        // E - open 1st string
        notePitches[5][0] = 329.63;
        notePitches[5][1] = 349.23;
        notePitches[5][2] = 369.99;
        notePitches[5][3] = 392;
        notePitches[5][4] = 415.3;
        notePitches[5][5] = 440;

        for (int string_no = 0; string_no < 6; string_no++) {
            for (int fret = 0; fret < 6; fret++) {
                if (notePitches[string_no][fret] > 0) {
                    /*-
                     * 0   1   2   3   4   5
                     * 100 101 102 103 104 105
                     * 200 201 202 203 204 205
                     * ...
                     * 
                     */
                    notePitchesMap.put(notePitches[string_no][fret], string_no * 100 + fret); // encode coordinates
                }
            }
        }

        // UI update cycle.
        handler_ = new Handler();
        timer_ = new Timer();
        timer_.schedule(new TimerTask() {
            public void run() {
                handler_.post(new Runnable() {
                    public void run() {
                        invalidate();
                    }
                });
            }
        }, UI_UPDATE_MS, UI_UPDATE_MS);
    }

    /**
     * Resolves the fret/string combination which could be used to play the <code>pitch</code>. The integer returned is
     * the result of applying a function to the fret/string. Basically, the "int" returned identifies uniquely the
     * fret/string combination.
     * 
     * The challenge of the method is to find correct fret/string based on the pitch as detected by FFT.
     * 
     * @param pitch
     *            is the dominating frequency as detected by FFT
     * @return integer identifying unique combination of the fret/string
     */
    private int getFingerboardCoord(double pitch) {
        final SortedMap<Double, Integer> tail_map = notePitchesMap.tailMap(pitch);
        final SortedMap<Double, Integer> head_map = notePitchesMap.headMap(pitch);
        final double closest_right = tail_map == null || tail_map.isEmpty() ? notePitchesMap.lastKey() : tail_map
                .firstKey();
        final double closest_left = head_map == null || head_map.isEmpty() ? notePitchesMap.firstKey() : head_map
                .lastKey();
        if (closest_right - pitch < pitch - closest_left) {
            return notePitchesMap.get(closest_right);
        } else {
            return notePitchesMap.get(closest_left);
        }
    }

    /**
     * Draws the fretboard.
     * 
     * @param canvas
     * @param rect
     */
    private void drawFingerboard(Canvas canvas, Rect rect) {
        /*-
         * Screen coordinates:
         * 
         * ------> X
         * |
         * |
         * |
         * v Y
         * 
         */

        // color used for strings
        Paint paint = new Paint();
        paint.setARGB(255, 100, 200, 100);

        // Draw strings
        for (int i = 0; i < 6; i++) {
            /*-
             * Drawing of strings are done over Y-axis from top to down. The offset 
             * is applied to the Y-coordinate. 
             * 
             * string 1 ----------------
             * string 2 ----------------
             * string 3 ----------------
             * string 4 ----------------
             * string 5 ----------------
             * string 6 ----------------
             */
            final int offset = Math.round((rect.height() - FINGERBOARD_PADDING * 2) / 5 * i) + FINGERBOARD_PADDING;
            canvas.drawLine(rect.left, rect.top + offset, rect.right, rect.top + offset, paint);
        }

        // Draw fingerboard's end.
        canvas.drawRect(rect.right - FINGERBOARD_PADDING, rect.top, rect.right, rect.bottom, paint);

        /*-
         * Draw frets
         * 
         * Drawing frets for the fingerboard, which corresponds to the vertical bars.
         */
        for (int i = 1; i < 6; i++) {
            final int offset = Math.round((rect.width() - FINGERBOARD_PADDING * 2) / 5 * i) + FINGERBOARD_PADDING;
            canvas.drawLine(rect.right - offset, rect.top, rect.right - offset, rect.bottom, paint);
        }

        // set color for guitar fingerboard and draw ...
        paint.setARGB(255, 195, 118, 27);
        // ... upper line, marking the boarders (along frets + headstock)
        canvas.drawLine(rect.left, rect.top, rect.right, rect.top, paint);
        canvas.drawLine(rect.right + HEADSTOCK_WIDTH, rect.top - HEADSTOCK_HEIGHT, rect.right, rect.top, paint);
        // ... bottom line, marking the boarders (along frets + headstock)
        canvas.drawLine(rect.left, rect.bottom, rect.right, rect.bottom, paint);
        canvas.drawLine(rect.right + HEADSTOCK_WIDTH, rect.bottom + HEADSTOCK_HEIGHT, rect.right, rect.bottom, paint);

        // Marks on the 3rd and 5th frets.
        final long offset_3_mark = Math.round((rect.width() - FINGERBOARD_PADDING * 2) / 5 * 2.5) + FINGERBOARD_PADDING;
        final long offset_5_mark = Math.round((rect.width() - FINGERBOARD_PADDING * 2) / 5 * 4.5) + FINGERBOARD_PADDING;
        canvas.drawCircle(rect.right - offset_3_mark, rect.top, 3, paint);
        canvas.drawCircle(rect.right - offset_5_mark, rect.top, 3, paint);

        // Draw strings on the headstock
        paint.setARGB(255, 100, 200, 100);
        for (int i = 1; i <= 6; i++) {
            int startY = rect.top - HEADSTOCK_HEIGHT
                    + Math.round((rect.height() + 2 * HEADSTOCK_HEIGHT - FINGERBOARD_PADDING * 2) / 5 * (i - 1))
                    + FINGERBOARD_PADDING;
            int stopX = rect.top + Math.round((rect.height() - FINGERBOARD_PADDING * 2) / 5 * (i - 1))
                    + FINGERBOARD_PADDING;

            canvas.drawLine(rect.right + HEADSTOCK_WIDTH, startY, rect.right, stopX, paint);
        }
    }

    private long getAmplitudeScreenHeight(Canvas canvas, double amplitude, Rect histogram_rect) {
        return Math.round(amplitude / maxAmplitude * histogram_rect.height());
    }

    private void drawPitchOnFingerboard(Canvas canvas, Rect rect, Point text_point) {
        final int MARK_RADIUS = 5;
        if (pitchValue == null || !pitchValue.string_detected)
            return;
        final int alpha = pitchValue.getAlpha();
        if (alpha == 0)
            return;
        int string_no = pitchValue.string_no;
        int fret = pitchValue.fret;

        Paint paint = new Paint();
        paint.setARGB(alpha, 200, 210, 210);
        if (fret == 0) {
            // Highlight the string.
            final int offset = Math.round((rect.height() - FINGERBOARD_PADDING * 2) / 5 * string_no)
                    + FINGERBOARD_PADDING;
            canvas.drawLine(rect.left, rect.top + offset, rect.right, rect.top + offset, paint);
            // Actually use the corresponding coordinate on the previous string.
            if (string_no > 0) {
                if (string_no == 4) {
                    fret = 4;
                } else {
                    fret = 5;
                }
                string_no--;
            }
        }

        // Draw the needed position on the fingerboard.
        final long offset_y = Math.round((rect.height() - FINGERBOARD_PADDING * 2) / 5 * string_no)
                + FINGERBOARD_PADDING;
        final long offset_x = Math.round((rect.width() - FINGERBOARD_PADDING * 2) / 5 * (fret - 0.5))
                + FINGERBOARD_PADDING;
        final long circle_x = rect.right - offset_x;
        final long circle_y = rect.top + offset_y;
        canvas.drawCircle(circle_x, circle_y, MARK_RADIUS, paint);

        // Draw the position's pitch and the delta.
        paint.setARGB(alpha, 180, 180, 180);
        canvas.drawLine(text_point.x, text_point.y, text_point.x + 20, text_point.y, paint);
        canvas.drawLine(text_point.x, text_point.y, circle_x, circle_y, paint);
        paint.setTextSize(25);
        final double position_pitch = notePitches[pitchValue.string_no][pitchValue.fret];
        final double delta = pitchValue.pitch - position_pitch;
        String message = position_pitch + " Hz (";
        message += delta > 0 ? "-" : "+";
        message += Math.round(Math.abs(delta) * 100) / 100.0 + "Hz)";
        canvas.drawText(message, text_point.x + 30, text_point.y + 10, paint);
    }

//    /**
//     * Draws the histogram of the frequencies in the lower part of the screen.
//     * 
//     * The {@link #frequency2Amplitude} is used to make a draw. The false is returned, if the
//     * {@link #frequency2Amplitude} is <code>null</code>.
//     * 
//     * The call can be safly removed as far as it is not influencing the detection.
//     * 
//     * @param canvas
//     * @param rect
//     * @return
//     */
//    private boolean drawHistogram(Canvas canvas, Rect rect) {
//        if (frequency2Amplitude == null)
//            return false;
//        Paint paint = new Paint();
//        // Draw border.
//        paint.setARGB(80, 200, 200, 200);
//        paint.setStyle(Paint.Style.STROKE);
//        canvas.drawRect(rect, paint);
//
//        // Draw threshold.
//        paint.setARGB(180, 200, 0, 0);
//        final long threshold_screen_height = getAmplitudeScreenHeight(canvas, MIN_AMPLITUDE, rect);
//        canvas.drawLine(rect.left, rect.bottom - threshold_screen_height, rect.right, rect.bottom
//                - threshold_screen_height, paint);
//
//        // Draw histogram.
//        paint.setARGB(255, 140, 140, 140);
//
//        boolean above_threshold = false;
//        int column_no = 0;
//        Iterator<Entry<Double, Double>> it = frequency2Amplitude.entrySet().iterator();
//        while (it.hasNext()) {
//            Entry<Double, Double> entry = it.next();
//            // double frequency = entry.getKey();
//            final double amplitude = Math.min(entry.getValue(), maxAmplitude);
//            final long height = getAmplitudeScreenHeight(canvas, amplitude, rect);
//            if (height > threshold_screen_height)
//                above_threshold = true;
//            canvas.drawRect(rect.left + rect.width() * column_no / frequency2Amplitude.size(), rect.bottom - height,
//                    rect.left + rect.width() * (column_no + 1) / frequency2Amplitude.size(), rect.bottom, paint);
//            column_no++;
//        }
//        return above_threshold;
//    }

    private void drawCurrentFrequency(Canvas canvas, int x, int y) {
        if (pitchValue == null) {
            Paint paint = new Paint();
            paint.setARGB(255, 200, 200, 200);
            paint.setTextSize(18);
            canvas.drawText("Pull a string on your guitar.", 20, 40, paint);
            return;
        }
        final int alpha = pitchValue.getAlpha();
        if (alpha == 0)
            return;
        Paint paint = new Paint();
        paint.setARGB(alpha, 200, 0, 0);
        paint.setTextSize(35);
        canvas.drawText(Math.round(pitchValue.pitch * 10) / 10.0 + " Hz", 20, 40, paint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final int MARGIN = 20;
        final int effective_height = canvas.getHeight() - 4 * MARGIN;
        final int effective_width = canvas.getWidth() - 2 * MARGIN;
        final Rect fingerboard = new Rect(MARGIN, effective_height * 20 / 100 + MARGIN + HEADSTOCK_HEIGHT,
                effective_width + MARGIN - HEADSTOCK_WIDTH, effective_height * 60 / 100 + MARGIN - HEADSTOCK_HEIGHT);
        final Rect histogram = new Rect(MARGIN, effective_height * 60 / 100 + 2 * MARGIN, effective_width + MARGIN,
                effective_height + MARGIN);

        // drawHistogram(canvas, histogram);

        /*
         * prepare everything required to draw the best pitch
         */
        final int coord = getFingerboardCoord(bestPitchMatch);
        final int string_no = coord / 100;
        final int fret = coord % 100;
        final double found_pitch = notePitches[string_no][fret];
        final double diff = Math.abs(found_pitch - bestPitchMatch);
        if (diff < MAX_PITCH_DIFF) {
            pitchValue = new PitchDetectionRepresentation(bestPitchMatch, string_no, fret);
        } else {
            pitchValue = new PitchDetectionRepresentation(bestPitchMatch);
        }

        // draw the best matched frequency (pitch) on the fingerboard as a small white circle.
        drawCurrentFrequency(canvas, 20, 50);
        drawFingerboard(canvas, fingerboard);
        drawPitchOnFingerboard(canvas, fingerboard, new Point(20, 80));
    }

    public void setDetectionResults(final HashMap<Double, Double> frequencies, double pitch) {
        frequency2Amplitude = frequencies;
        bestPitchMatch = pitch;
    }

}