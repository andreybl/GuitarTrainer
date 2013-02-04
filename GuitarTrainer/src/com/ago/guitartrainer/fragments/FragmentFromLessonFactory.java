package com.ago.guitartrainer.fragments;

import android.support.v4.app.Fragment;

import com.ago.guitartrainer.lessons.ILesson;
import com.ago.guitartrainer.lessons.custom.LessonNote2Position;
import com.ago.guitartrainer.lessons.custom.LessonPosition2Note;
import com.ago.guitartrainer.lessons.custom.LessonScalegridDegree2Position;

public class FragmentFromLessonFactory {

    public static Fragment fragmentForLesson(ILesson lesson) {
        Fragment fragment = null;
        if (lesson instanceof LessonPosition2Note) {
            FragmentPosition2Note tmpFragment = new FragmentPosition2Note();
            tmpFragment.onInitializationCompletedListener((LessonPosition2Note) lesson);
            fragment = tmpFragment;
        } else if (lesson instanceof LessonNote2Position) {
            FragmentNote2Position tmpFragment = new FragmentNote2Position();
            tmpFragment.onInitializationCompletedListener((LessonNote2Position) lesson);
            fragment = tmpFragment;
            // ((LessonNote2Position) lesson).setViewFragment((FragmentNote2Position) fragment);
        } else if (lesson instanceof LessonScalegridDegree2Position) {
            FragmentScalegridDegree2Position tmpFragment = new FragmentScalegridDegree2Position();
            tmpFragment.onInitializationCompletedListener((LessonScalegridDegree2Position) lesson);
            fragment = tmpFragment;
        }
        

        if (fragment == null)
            throw new RuntimeException("Failed ot resolve fragment for lesson: " + lesson.getClass().getSimpleName());

        return fragment;

    }
}
