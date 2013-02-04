package com.ago.guitartrainer.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ago.guitartrainer.R;
import com.ago.guitartrainer.lessons.custom.LessonScalegridDegree2Position;
import com.ago.guitartrainer.ui.DegreesView;
import com.ago.guitartrainer.ui.FretView;
import com.ago.guitartrainer.ui.LearningStatusView;
import com.ago.guitartrainer.ui.NotesView;
import com.ago.guitartrainer.ui.ScalegridsView;

public class FragmentScalegridDegree2Position extends Fragment {

    private String TAG = "GT-" + this.getClass().getSimpleName();

    private FretView fretView;

    private ScalegridsView scalegridView;

    private DegreesView degreesView;

    private LessonScalegridDegree2Position lesson;

    private LearningStatusView learningStatusView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        LinearLayout mainLayout = (LinearLayout) inflater.inflate(R.layout.fragment_scalegriddegree2position,
                container, false);

        fretView = (FretView) mainLayout.findViewById(R.id.view_fretview);
        scalegridView = (ScalegridsView) mainLayout.findViewById(R.id.view_scalegridview);
        degreesView = (DegreesView) mainLayout.findViewById(R.id.view_degreesview);
        learningStatusView = (LearningStatusView) mainLayout.findViewById(R.id.learning_status);

        if (lesson != null)
            lesson.onFragmentInitializationCompleted(this);
        
        return mainLayout;
    }

    public FretView getFretView() {
        return fretView;
    }

    public ScalegridsView getScalegridView() {
        return scalegridView;
    }

    public DegreesView getDegreesView() {
        return degreesView;
    }

    //
    public LearningStatusView getLearningStatusView() {
        return learningStatusView;
    }

    public void onInitializationCompletedListener(LessonScalegridDegree2Position lesson) {
        this.lesson = lesson;
    }

}
