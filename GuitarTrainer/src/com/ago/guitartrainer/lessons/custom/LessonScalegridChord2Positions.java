package com.ago.guitartrainer.lessons.custom;

import java.util.List;

import com.ago.guitartrainer.GuitarTrainerApplication;
import com.ago.guitartrainer.MasterActivity;
import com.ago.guitartrainer.R;
import com.ago.guitartrainer.SettingsActivity;
import com.ago.guitartrainer.events.NotePlayingEvent;
import com.ago.guitartrainer.events.OnViewSelectionListener;
import com.ago.guitartrainer.fragments.FragmentScalegridChord2Positions;
import com.ago.guitartrainer.instruments.guitar.Position;
import com.ago.guitartrainer.notation.Chord;
import com.ago.guitartrainer.notation.Degree;
import com.ago.guitartrainer.utils.LessonsUtils;

public class LessonScalegridChord2Positions extends LessonScalegridDegree2Position {

    public void onFragmentInitializationCompleted(FragmentScalegridChord2Positions fragment) {
        this.fragment = fragment;

        // initialize views required for the current type of lesson
        fragment.getFretView().setEnabled(true);
        fragment.getFretView().setEnabledInput(true);

        fragment.getScalegridView().setEnabled(true);

        // fragment.getChordsView().setEnabled(false);

        fragment.getScalegridView().setEnabled(true);

        OnViewSelectionListener<NotePlayingEvent> onSelectionListener = new InnerOnSelectionListener();
        fragment.getFretView().registerListener(onSelectionListener);
    }

    @Override
    protected void askQuestionToUser() {

        fragment.getScalegridView().show(gridShape.getType());
        // fragment.getDegreesView().show(quest.degree);

        FragmentScalegridChord2Positions f = (FragmentScalegridChord2Positions) fragment;
        f.getChordsView().show(chord);

        if (!fragment.getScalegridView().isRootOnlyShown()) {
            fragment.getFretView().show(layerLesson, gridShape);
        } else {
            fragment.getFretView().show(layerLesson, gridShape.getRootPosition());
        }

        // boolean playSound = GuitarTrainerApplication.getPrefs().getBoolean(SettingsActivity.KEY_PLAY_SOUNDS, false);
        // if (playSound) {
        // playDegree(quest.degree);
        // }
    }

    private Degree[] chord = Chord.major;

    @Override
    protected List<Position> generateExpectedPositions() {
        FragmentScalegridChord2Positions f = (FragmentScalegridChord2Positions) fragment;
        if (f.getChordsView().isRandomInput()) {
            chord = LessonsUtils.randomChord();
        } else {
            chord = f.getChordsView().chord();
        }

        /* all positions must be played for the answer to be accepted */
        List<Position> positions = gridShape.chord2Positions(chord);
        return positions;
    }

    @Override
    public String getTitle() {
        String str = MasterActivity.getInstance().getResources()
                .getString(R.string.lesson_scalegridchord2positions_title);
        return str;
    }

    @Override
    public String getDescription() {
        String str = MasterActivity.getInstance().getResources()
                .getString(R.string.lesson_scalegridchord2positions_description);
        return str;
    }

}
