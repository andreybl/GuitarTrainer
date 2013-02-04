package com.ago.guitartrainer.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ago.guitartrainer.R;
import com.ago.guitartrainer.lessons.custom.LessonPosition2Note;
import com.ago.guitartrainer.ui.FretView;
import com.ago.guitartrainer.ui.LearningStatusView;
import com.ago.guitartrainer.ui.NotesView;

public class FragmentPosition2Note extends Fragment {

    private String TAG = "GT-"+this.getClass().getSimpleName();

    private FretView fretView;

    private NotesView notesView;
    
    private LessonPosition2Note lesson;

    private LearningStatusView learningStatusView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        LinearLayout mainLayout = (LinearLayout) inflater.inflate(R.layout.fragment_position2note, container, false);

        fretView = (FretView) mainLayout.findViewById(R.id.view_fretview);
        notesView = (NotesView) mainLayout.findViewById(R.id.view_notesview);
        learningStatusView = (LearningStatusView) mainLayout.findViewById(R.id.learning_status);

        if (lesson!=null)
            lesson.onFragmentInitializationCompleted(this);
        
        return mainLayout;
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

    public void onInitializationCompletedListener(LessonPosition2Note lesson) {
        this.lesson = lesson;
    }

}
