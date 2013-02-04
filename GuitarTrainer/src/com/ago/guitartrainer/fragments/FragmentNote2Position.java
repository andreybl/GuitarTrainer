package com.ago.guitartrainer.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ago.guitartrainer.R;
import com.ago.guitartrainer.lessons.custom.LessonNote2Position;
import com.ago.guitartrainer.ui.FretView;
import com.ago.guitartrainer.ui.LearningStatusView;
import com.ago.guitartrainer.ui.NotesView;

public class FragmentNote2Position extends Fragment {

    private String TAG = "GT-" + this.getClass().getSimpleName();

    private FretView fretView;

    private NotesView notesView;

    private LearningStatusView learningStatusView;

     private LessonNote2Position lesson;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        LinearLayout mainLayout = (LinearLayout) inflater.inflate(R.layout.fragment_note2position, container, false);

        fretView = (FretView) mainLayout.findViewById(R.id.view_fretview);
        notesView = (NotesView) mainLayout.findViewById(R.id.view_notesview);
        learningStatusView = (LearningStatusView) mainLayout.findViewById(R.id.learning_status);

        // lesson.setViewFragment(this);
        if (lesson != null)
            lesson.onFragmentInitializationCompleted(this);

        return mainLayout;
    }

    public void onInitializationCompletedListener(LessonNote2Position lesson) {
        this.lesson = lesson;
    }

    public FretView getFretView() {
        return fretView;
    }

    public NotesView getNotesView() {
        return notesView;
    }

    public LearningStatusView getLearningStatusView() {
        return learningStatusView;
    }

}
