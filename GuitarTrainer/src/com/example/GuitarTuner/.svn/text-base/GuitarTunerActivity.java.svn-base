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

import java.lang.Thread;
import java.util.HashMap;

import com.example.GuitarTuner.DrawableView;
import com.example.GuitarTuner.PitchDetector;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

public class GuitarTunerActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tv_ = new DrawableView(this);
		setContentView(tv_);
	}

	@Override
	public void onStart() {
		super.onStart();
		pitch_detector_thread_ = new Thread(new PitchDetector(this, new Handler()));
		pitch_detector_thread_.start();
	}

	@Override
	public void onStop() {
		super.onStop();
		pitch_detector_thread_.interrupt();
	}

	
	public void ShowPitchDetectionResult(
			final HashMap<Double, Double> frequencies,
			final double pitch) {
		tv_.setDetectionResults(frequencies, pitch);
	}

	DrawableView tv_;
	Thread pitch_detector_thread_;
}