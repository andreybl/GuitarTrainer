package com.ago.guitartrainer.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ago.guitartrainer.MasterActivity;
import com.ago.guitartrainer.R;
import com.ago.guitartrainer.events.NotePlayingEvent;
import com.ago.guitartrainer.events.OnViewSelectionListener;
import com.ago.guitartrainer.instruments.guitar.Position;
import com.ago.guitartrainer.ui.FretView;
import com.ago.guitartrainer.ui.FretView.Layer;
import com.ago.guitartrainer.utils.Avg;
import com.ago.guitartrainer.utils.LessonsUtils;

public class FragmentBenchmarkFretview extends Fragment {

    private String TAG = "GT-" + this.getClass().getSimpleName();

    private FretView fretView;

    private Button btnStart;

    private Button btnEnd;

    private TextView txtTask;

    private TextView txtResults;

    private Position position;

    private Layer layer = new Layer(100, MasterActivity.getInstance().getResources().getColor(R.color.blue));

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        LinearLayout mainLayout = (LinearLayout) inflater.inflate(R.layout.fragment_benchmark_fretview, container,
                false);

        fretView = (FretView) mainLayout.findViewById(R.id.view_fretview);

        btnStart = (Button) mainLayout.findViewById(R.id.btn_start);
        btnStart.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                next();

            }
        });
        btnEnd = (Button) mainLayout.findViewById(R.id.btn_end);
        btnEnd.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                // TODO: ask user to register the motion time for the input mode tested.
                // fretView.getInputMode();

                movementTime = new Avg();
            }
        });

        txtTask = (TextView) mainLayout.findViewById(R.id.benchmark_task);
        txtResults = (TextView) mainLayout.findViewById(R.id.benchmark_results);

        InnerListener iListener = new InnerListener();
        fretView.registerListener(iListener);

        // next();

        return mainLayout;
    }

    private long startTime;

    private Avg movementTime = new Avg();

    private void next() {

        position = LessonsUtils.randomPosition();
        fretView.clearLayer(layer);
        fretView.clearLayerByZIndex(FretView.LAYER_Z_TOUCHES);
        fretView.clearLayerByZIndex(FretView.LAYER_Z_FFT);
        fretView.show(layer, position);

        startTime = System.currentTimeMillis();
    }

    private class InnerListener implements OnViewSelectionListener<NotePlayingEvent> {

        @Override
        public void onViewElementSelected(NotePlayingEvent element) {
            boolean isPositionFound = false;
            if (element.position != null) {
                if (position.equals(element.position)) {
                    isPositionFound = true;
                }
            } else if (element.possiblePositions != null) {
                if (element.possiblePositions.contains(position)) {
                    isPositionFound = true;
                }
            }

            if (isPositionFound) {
                long diff = System.currentTimeMillis() - startTime;
                double avgVal = movementTime.addValue(diff);

                // String avgValStr = NumberFormat.getNumberInstance().format(avgVal);
                final String avgVString = String.valueOf(Math.round(avgVal));
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        next();
                        txtResults.setText("Avg: " + avgVString + "ms; measured in " + movementTime.getLaps()
                                + " trials");

                    }
                });

            }

        }
    }
}
