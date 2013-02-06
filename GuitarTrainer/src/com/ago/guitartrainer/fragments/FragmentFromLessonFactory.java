package com.ago.guitartrainer.fragments;

import android.support.v4.app.Fragment;

import com.ago.guitartrainer.lessons.ILesson;
import com.ago.guitartrainer.lessons.custom.LessonModeDegree2Parent;
import com.ago.guitartrainer.lessons.custom.LessonNote2Position;
import com.ago.guitartrainer.lessons.custom.LessonParentDegree2Mode;
import com.ago.guitartrainer.lessons.custom.LessonPosition2Note;
import com.ago.guitartrainer.lessons.custom.LessonScalegridChord2Positions;
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
        } else if (lesson instanceof LessonScalegridChord2Positions) {
            FragmentScalegridChord2Positions tmpFragment = new FragmentScalegridChord2Positions();
            tmpFragment.onInitializationCompletedListener((LessonScalegridChord2Positions) lesson);
            fragment = tmpFragment;
        } else if (lesson instanceof LessonScalegridDegree2Position) {
            FragmentScalegridDegree2Position tmpFragment = new FragmentScalegridDegree2Position();
            tmpFragment.onInitializationCompletedListener((LessonScalegridDegree2Position) lesson);
            fragment = tmpFragment;
        } else if (lesson instanceof LessonModeDegree2Parent) {
            FragmentModeDegree2Parent tmpFragment = new FragmentModeDegree2Parent();
            tmpFragment.onInitializationCompletedListener((LessonModeDegree2Parent) lesson);
            fragment = tmpFragment;
        } else if (lesson instanceof LessonParentDegree2Mode) {
            FragmentParentDegree2Mode tmpFragment = new FragmentParentDegree2Mode();
            tmpFragment.onInitializationCompletedListener((LessonParentDegree2Mode) lesson);
            fragment = tmpFragment;
        }

        if (fragment == null)
            throw new RuntimeException("Failed ot resolve fragment for lesson: " + lesson.getClass().getSimpleName());

        return fragment;

    }
}
