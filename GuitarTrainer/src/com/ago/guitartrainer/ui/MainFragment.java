package com.ago.guitartrainer.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ago.guitartrainer.R;

public class MainFragment extends Fragment {

    private static String TAG = "GT-MainFragment";

    private FretView fretView;

    private ScalegridsView scalegridView;

    private DegreesView degreesView;

    private NotesView notesView;

    private LearningStatusView learningStatusView;

    /*
     * TODO: rework the concept. The fragment - and basically its views - are required in lessons. But getting the
     * fragment in such way as singleton is not nice.
     */
    private static MainFragment instance;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        LinearLayout mainLayout = (LinearLayout) inflater.inflate(R.layout.fragment_main, container, false);
        /* Start: custom views: fret/shape/notes etc. */
        fretView = (FretView) mainLayout.findViewById(R.id.view_fretview);
        scalegridView = (ScalegridsView) mainLayout.findViewById(R.id.view_scalegridview);
        degreesView = (DegreesView) mainLayout.findViewById(R.id.view_degreesview);
        notesView = (NotesView) mainLayout.findViewById(R.id.view_notesview);
        learningStatusView = (LearningStatusView) mainLayout.findViewById(R.id.learning_status);
        /* End: custom views: fret/shape/notes etc. */

        // Note: the assignment must be done before the ILesson is instantiated.
        instance = this;

        return mainLayout;
    }

    public static MainFragment getInstance() {
        return instance;
    }

    public FretView getFretView() {
        return fretView;
    }

    // TODO: rename
    public ScalegridsView getScalegridView() {
        return scalegridView;
    }

    public DegreesView getDegreesView() {
        return degreesView;
    }

    public NotesView getNotesView() {
        return notesView;
    }

    public LearningStatusView getLearningStatusView() {
        return learningStatusView;
    }

    /*
     * INNER CLASSES ******
     */
    /**
     * Listener for clicks on lesson control buttons and on slide-out menu button.
     * 
     * The listener does not take into account any events on custom view.
     * 
     * @author Andrej Golovko - jambit GmbH
     * 
     */
    private class InnerOnClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
            default:
                break;
            }

        }
    }
}
