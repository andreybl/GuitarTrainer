package com.ago.guitartrainer.lessons.custom;

import java.sql.SQLException;
import java.util.List;

import com.ago.guitartrainer.MasterActivity;
import com.ago.guitartrainer.R;
import com.ago.guitartrainer.events.NotePlayingEvent;
import com.ago.guitartrainer.events.OnViewSelectionListener;
import com.ago.guitartrainer.fragments.FragmentScalegridChord2Positions;
import com.ago.guitartrainer.instruments.guitar.Position;
import com.ago.guitartrainer.lessons.AQuestion;
import com.ago.guitartrainer.notation.Chord;
import com.ago.guitartrainer.notation.Degree;
import com.ago.guitartrainer.scalegrids.ScaleGrid;
import com.ago.guitartrainer.utils.LessonsUtils;

public class LessonScalegridChord2Positions extends LessonScalegridDegree2Position {

    private Degree[] chord = Chord.major;

    public void onFragmentInitializationCompleted(FragmentScalegridChord2Positions fragment) {
        this.fragment = fragment;

        // initialize views required for the current type of lesson
        fragment.getFretView().setEnabled(true);
        fragment.getFretView().setEnabledInput(true);

        fragment.getScalegridView().setEnabled(true);

        OnViewSelectionListener<NotePlayingEvent> onSelectionListener = new InnerOnSelectionListener();
        fragment.getFretView().registerListener(onSelectionListener);
    }

    @Override
    protected void showQuestionToUser(AQuestion q) {
        QuestionScalegridChord2Position quest = (QuestionScalegridChord2Position) q;
        FragmentScalegridChord2Positions f = (FragmentScalegridChord2Positions) fragment;
        f.getScalegridView().show(quest.scaleGridType);

        ScaleGrid sg = ScaleGrid.create(quest.scaleGridType, quest.fretPosition);

        f.getChordsView().show(chord);

        if (!fragment.getScalegridView().isRootOnlyShown()) {
            fragment.getFretView().show(layerLesson, sg);
        } else {
            fragment.getFretView().show(layerLesson, sg.getRootPosition());
        }        
    }
    
    @Override
    protected AQuestion resolveNextQuestion() throws SQLException {
        FragmentScalegridChord2Positions f = (FragmentScalegridChord2Positions) fragment;
        
        ScaleGrid sg = ScaleGrid.create(quest.scaleGridType, quest.fretPosition);
        
        if (!f.getChordsView().isRandomInput()) {
            LessonsUtils.randomChord();
            fragment.getFretView().show(layerLesson, sg);
        } else {
            fragment.getFretView().show(layerLesson, sg.getRootPosition());
        }
    }

    @Override
    protected List<Position> prepareExpectedPositions(QuestionScalegridChord2Position quest) {
        ScaleGrid sg = ScaleGrid.create(quest.scaleGridType, quest.fretPosition);
        return sg.chord2Positions(quest.chord);
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
