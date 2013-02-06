package com.ago.guitartrainer.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ago.guitartrainer.R;
import com.ago.guitartrainer.lessons.custom.LessonModeDegree2Parent;
import com.ago.guitartrainer.lessons.custom.LessonParentDegree2Mode;
import com.ago.guitartrainer.ui.ChordsView;
import com.ago.guitartrainer.ui.DegreesView;
import com.ago.guitartrainer.ui.KeysView;
import com.ago.guitartrainer.ui.LearningStatusView;

public class FragmentParentDegree2Mode extends Fragment {

    private String TAG = "GT-" + this.getClass().getSimpleName();

    private KeysView modesView;

    private KeysView parentKeysView;

    private DegreesView degreesView;

    private LearningStatusView learningStatusView;

    private LessonParentDegree2Mode lesson;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        LinearLayout mainLayout = (LinearLayout) inflater
                .inflate(R.layout.fragment_parentdegree2mode, container, false);

        modesView = (KeysView) mainLayout.findViewById(R.id.view_modesview);
        parentKeysView = (KeysView) mainLayout.findViewById(R.id.view_parentkeysview);
        degreesView = (DegreesView) mainLayout.findViewById(R.id.view_degreesview);

        learningStatusView = (LearningStatusView) mainLayout.findViewById(R.id.learning_status);

        if (lesson != null)
            lesson.onFragmentInitializationCompleted(this);

        return mainLayout;
    }

    public void onInitializationCompletedListener(LessonParentDegree2Mode lesson) {
        this.lesson = lesson;
    }

    public KeysView getModesView() {
        return modesView;
    }

    public KeysView getParentsView() {
        return parentKeysView;
    }

    public DegreesView getDegreesView() {
        return degreesView;
    }

    public LearningStatusView getLearningStatusView() {
        return learningStatusView;
    }

}
