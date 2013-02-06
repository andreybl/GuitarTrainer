package com.ago.guitartrainer.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ago.guitartrainer.R;
import com.ago.guitartrainer.lessons.custom.LessonKeychordDegree2Parentkey;
import com.ago.guitartrainer.lessons.custom.LessonNote2Position;
import com.ago.guitartrainer.ui.ChordsView;
import com.ago.guitartrainer.ui.DegreesView;
import com.ago.guitartrainer.ui.FretView;
import com.ago.guitartrainer.ui.KeysView;
import com.ago.guitartrainer.ui.LearningStatusView;
import com.ago.guitartrainer.ui.NotesView;

public class FragmentKeychordDegree2Parentkey extends Fragment {

    private String TAG = "GT-" + this.getClass().getSimpleName();

    private KeysView keysView;

    private KeysView parentKeysView;

    private ChordsView chordsView;

    private DegreesView degreesView;

    private LearningStatusView learningStatusView;

    private LessonKeychordDegree2Parentkey lesson;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        LinearLayout mainLayout = (LinearLayout) inflater.inflate(R.layout.fragment_keychorddegree2parentkey,
                container, false);

        keysView = (KeysView) mainLayout.findViewById(R.id.view_keysview);
        parentKeysView = (KeysView) mainLayout.findViewById(R.id.view_parentkeysview);
        chordsView = (ChordsView) mainLayout.findViewById(R.id.view_chordsview);
        degreesView = (DegreesView) mainLayout.findViewById(R.id.view_degreesview);
        learningStatusView = (LearningStatusView) mainLayout.findViewById(R.id.learning_status);

        // lesson.setViewFragment(this);
        if (lesson != null)
            lesson.onFragmentInitializationCompleted(this);

        return mainLayout;
    }

    public void onInitializationCompletedListener(LessonKeychordDegree2Parentkey lesson) {
        this.lesson = lesson;
    }

    public KeysView getKeysView() {
        return keysView;
    }

    public KeysView getParentKeysView() {
        return parentKeysView;
    }

    public ChordsView getChordsView() {
        return chordsView;
    }

    public DegreesView getDegreesView() {
        return degreesView;
    }

    public LearningStatusView getLearningStatusView() {
        return learningStatusView;
    }

}
