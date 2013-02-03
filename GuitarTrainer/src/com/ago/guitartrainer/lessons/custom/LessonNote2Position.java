package com.ago.guitartrainer.lessons.custom;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.widget.Toast;

import com.ago.guitartrainer.R;
import com.ago.guitartrainer.db.DatabaseHelper;
import com.ago.guitartrainer.events.NotePlayingEvent;
import com.ago.guitartrainer.events.OnViewSelectionListener;
import com.ago.guitartrainer.instruments.GuitarUtils;
import com.ago.guitartrainer.lessons.QuestionMetrics;
import com.ago.guitartrainer.notation.Note;
import com.ago.guitartrainer.notation.NoteStave;
import com.ago.guitartrainer.notation.Position;
import com.ago.guitartrainer.ui.FretView;
import com.ago.guitartrainer.ui.FretView.Layer;
import com.ago.guitartrainer.ui.MainFragment;
import com.ago.guitartrainer.ui.NotesView;
import com.ago.guitartrainer.utils.LessonsUtils;
import com.j256.ormlite.dao.RuntimeExceptionDao;

/**
 * The lesson implements the learning function:
 * 
 * <pre>
 *      function(position):note
 * </pre>
 * 
 * In other words, the random position is selected on the fret and demonstrated to the user. The user is suggested to
 * select the correct note on the {@link NotesView}.
 * 
 * @author Andrej Golovko - jambit GmbH
 * 
 */

public class LessonNote2Position extends ALesson {

    private FretView fretView;

    private NotesView notesView;

    /** the note for which the fret position must be found */
    private Note questionedNote;

    private Layer layerLesson = new Layer(FretView.LAYER_Z_LESSON, MainFragment.getInstance().getResources()
            .getColor(R.color.blue));

    /**
     * positions which are accepted as correct answer?
     * 
     * for simplicity, we currently allow several acceptable positions as input, because one note on guitar can usually
     * be taken at several (maximally at three) positions.
     * 
     * */
    private List<Position> acceptedPositions;

    @Override
    public String getTitle() {
        String str = MainFragment.getInstance().getResources().getString(R.string.lesson_note2position_title);
        return str;
    }

    @Override
    public String getDescription() {
        String str = MainFragment.getInstance().getResources().getString(R.string.lesson_note2position_description);
        return str;
    }

    @Override
    public void doPrepareUi() {

        // initialize views required for the current type of lesson
        MainFragment uiControls = MainFragment.getInstance();
        fretView = uiControls.getFretView();
        notesView = uiControls.getNotesView();

        fretView.setEnabled(true);
        notesView.setEnabled(true);
        notesView.setEnabledInput(false);

        uiControls.getDegreesView().setEnabled(false);
        uiControls.getShapestView().setEnabled(false);

        OnViewSelectionListener<NotePlayingEvent> onSelectionListener = new InnerOnSelectionListener();
        fretView.registerListener(onSelectionListener);

    }

    @Override
    public void doStop() {
        fretView.clearLayer(layerLesson);
    }

    @Override
    public void showMetrics() {
        Toast.makeText(MainFragment.getInstance().getActivity(), "No showMetrics() implemented", 2000).show();
    }

    /**
     * Skip to the next lesson.
     * 
     * The answer results are not important.
     * 
     **/
    @Override
    public void doNext() {

        fretView.clearLayer(layerLesson);

        /*
         * in the Note2Position lesson the user is presented with the (random?) note, With the answer the user must find
         * the note's position on the fret, by inputing it either manually or by playing guitar.
         * 
         * The problem is, that the note may be played in different positions. But the user is expected a unique answer.
         * A workaround is to restrict the area on the fret, from which the answer is expected. At least for the manual
         * input it will be possible to decided, if the answer is as expected.
         */

        questionedNote = LessonsUtils.randomNote();
        do {
            acceptedPositions = NoteStave.getInstance().resolvePositions(questionedNote);
            /* can be for instance the case for F5 (not on the fret at all) */
        } while (acceptedPositions.size() == 0);

        RuntimeExceptionDao<QuestionNote2Position, Integer> qDao = DatabaseHelper.getInstance().getRuntimeExceptionDao(
                QuestionNote2Position.class);
        QuestionNote2Position currentQuestion = resolveOrCreateQuestion(qDao, questionedNote);
        QuestionMetrics qm = resolveOrCreateQuestionMetrics(currentQuestion.getId());
        registerQuestion(qDao, currentQuestion, qm);

        // TODO: tmp solution, the "random" must be set'able by the user
        boolean isRandomArea = true;
        if (isRandomArea) {
            int randomIndex = LessonsUtils.random(0, acceptedPositions.size() - 1);
            Position randomPosition = acceptedPositions.get(randomIndex);
            int[] startEnd = GuitarUtils.calculateUniqueAreaForPosition(acceptedPositions, randomPosition);
            acceptedPositions.clear();
            acceptedPositions.add(randomPosition);
            fretView.showArea(startEnd[0], startEnd[1]);

        }

        // visualize it
        notesView.showNote(questionedNote);

        Log.d(getTag(), "Note: " + questionedNote + ", Allowed positions: " + acceptedPositions);
    }

    private QuestionNote2Position resolveOrCreateQuestion(RuntimeExceptionDao<QuestionNote2Position, Integer> qDao,
            Note note) {
        QuestionNote2Position question = null;
        try {
            // resolve question
            List<QuestionNote2Position> quests = qDao.queryBuilder().where().eq("note", note).query();
            if (quests.size() == 0) {
                question = new QuestionNote2Position();
                question.note = note;
            } else if (quests.size() == 1) {
                question = quests.get(0);
            } else {
                throw new RuntimeException("The question object is not unique.");
            }

        } catch (SQLException e) {
            Log.e(getTag(), e.getMessage(), e);
        }

        return question;
    }

    /*
     * *** INNER CLASSES
     */
    /**
     * The only input expected from the user is note selection. And we listen to this input here.
     * 
     * @author Andrej Golovko - jambit GmbH
     * 
     */
    public class InnerOnSelectionListener implements OnViewSelectionListener<NotePlayingEvent> {

        @Override
        public void onViewElementSelected(final NotePlayingEvent npe) {
            // TODO: user UI widget to inform about answer correctness

            /* the lesson has not started. So we ignore all events. */
            if (acceptedPositions == null)
                return;

            if (!isLessonRunning())
                return;

            // TODO: the npe.position is not set, when detected with FFT. It is not possible to
            // resolve unique position.

            List<Position> possibleAcceptedInterception = new ArrayList<Position>();
            if (npe.possiblePositions != null)
                possibleAcceptedInterception.addAll(npe.possiblePositions);
            possibleAcceptedInterception.retainAll(acceptedPositions);

            boolean isAnswerAccepted = false;
            if (npe.position != null && acceptedPositions.contains(npe.position)) {
                isAnswerAccepted = true;
            } else if (possibleAcceptedInterception.size() > 0) {
                isAnswerAccepted = true;
            }

            if (isAnswerAccepted) {

                onSuccess();
                fretView.clearLayer(layerLesson);
                fretView.show(layerLesson, acceptedPositions);
            } else {
                onFailure();
            }
        }
    }

}
