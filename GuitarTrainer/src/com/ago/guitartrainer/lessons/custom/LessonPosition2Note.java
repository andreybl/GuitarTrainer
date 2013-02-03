package com.ago.guitartrainer.lessons.custom;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.ago.guitartrainer.GuitarTrainerApplication;
import com.ago.guitartrainer.R;
import com.ago.guitartrainer.SettingsActivity;
import com.ago.guitartrainer.db.DatabaseHelper;
import com.ago.guitartrainer.events.OnViewSelectionListener;
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

public class LessonPosition2Note extends ALesson {

    private FretView fretView;

    private NotesView notesView;

    private Note expectedNote;

    private Layer layerLesson = new Layer(FretView.LAYER_Z_LESSON, MainFragment.getInstance().getResources()
            .getColor(R.color.blue));

    @Override
    public String getTitle() {
        String str = MainFragment.getInstance().getResources().getString(R.string.lesson_position2note_title);
        return str;
    }

    @Override
    public String getDescription() {
        String str = MainFragment.getInstance().getResources().getString(R.string.lesson_position2note_description);
        return str;
    }

    @Override
    public void doPrepareUi() {

        /*
         * It is not clear in advance, which UI controls the lesson may require. The lessons are quite different. So we
         * just pick required controls from MainFragment.
         * 
         * The assumption is, that the MainFragment exists and is visible. We also may wish to set it per injection
         * later and check if it is visible every time when using its widgets.
         * 
         * The viable alternative could also be provide necessary calls - like, registerForNoteSelection() - on the
         * MainFragment itself. But it could result in to much methods on it. Basically, every view would require
         * several methods associated with it.
         */

        // initialize views required for the current type of lesson
        MainFragment uiControls = MainFragment.getInstance();
        fretView = uiControls.getFretView();
        notesView = uiControls.getNotesView();

        fretView.setEnabled(true);
        notesView.setEnabled(true);
        notesView.setEnabledInput(true);

        uiControls.getDegreesView().setEnabled(false);
        uiControls.getShapestView().setEnabled(false);

        OnViewSelectionListener<Note> onSelectionListener = new InnerOnSelectionListener();
        notesView.registerListener(onSelectionListener);

    }

    @Override
    public void doStop() {
        fretView.clearLayer(layerLesson);
    }

    @Override
    public void showMetrics() {
        /*
         * we multiply the setting by "3", because the note view is not too much user friendly. We compensate it by
         * tolerating longer response times.
         */
        int shortestReactionTimeMs = GuitarTrainerApplication.getPrefs().getInt(
                SettingsActivity.KEY_QUESTION_SHORTEST_REACTION_TIME, 1000) * 3;

        fretView.clearLayer(layerLesson);

        RuntimeExceptionDao<QuestionPosition2Note, Integer> qDao = DatabaseHelper.getInstance().getRuntimeExceptionDao(
                QuestionPosition2Note.class);

        List<QuestionPosition2Note> quests = Collections.emptyList();

        try {
            quests = qDao.queryBuilder().query();
        } catch (SQLException e) {
            Log.e(getTag(), e.getMessage(), e);
        }

        Map<Position, Integer> mapPosition2Color = new HashMap<Position, Integer>();
        for (QuestionPosition2Note quest : quests) {
            if (quest.getMetrics() != null) {
                Integer qmId = quest.getMetrics().getId();
                List<QuestionMetrics> qMetricsList = Collections.emptyList();

                try {
                    qMetricsList = qmDao.queryBuilder().where().idEq(qmId).query();
                } catch (SQLException e) {
                    Log.e(getTag(), e.getMessage(), e);
                }

                QuestionMetrics qm = qMetricsList.get(0);

                int color = R.color.black;

                if (qm.avgSuccessfulAnswerTime > shortestReactionTimeMs * 2) {
                    color = R.color.red;
                } else if (qm.avgSuccessfulAnswerTime > shortestReactionTimeMs) {
                    color = R.color.orange;
                } else if (qm.avgSuccessfulAnswerTime > 0 && qm.avgSuccessfulAnswerTime <= shortestReactionTimeMs) {
                    color = R.color.green;
                } else {
                    // avg == 0
                    color = R.color.gray;
                }

                Position position = new Position(quest.string, quest.fret);

                mapPosition2Color.put(position, color);

            }

        }

        // visualize
        fretView.show(layerLesson, mapPosition2Color);
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

        Position pos = LessonsUtils.randomPosition();
        
        fretView.show(layerLesson, pos);

        RuntimeExceptionDao<QuestionPosition2Note, Integer> qDao = DatabaseHelper.getInstance().getRuntimeExceptionDao(
                QuestionPosition2Note.class);
        QuestionPosition2Note quest = resolveOrCreateQuestion(pos);
        QuestionMetrics qm = resolveOrCreateQuestionMetrics(quest.getId());
        registerQuestion(qDao, quest, qm);

        expectedNote = NoteStave.getInstance().resolveNote(pos);

        Log.d(getTag(), "Position: " + pos + ", Note: " + expectedNote);
    }

    private QuestionPosition2Note resolveOrCreateQuestion(Position pos) {
        RuntimeExceptionDao<QuestionPosition2Note, Integer> qDao = DatabaseHelper.getInstance().getRuntimeExceptionDao(
                QuestionPosition2Note.class);

        QuestionPosition2Note question = null;
        try {
            // resolve question
            List<QuestionPosition2Note> quests = qDao.queryBuilder().where().eq("fret", pos.getFret()).and()
                    .eq("string", pos.getString()).query();
            if (quests.size() == 0) {
                question = new QuestionPosition2Note();
                question.fret = pos.getFret();
                question.string = pos.getString();

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
    public class InnerOnSelectionListener implements OnViewSelectionListener<Note> {

        @Override
        public void onViewElementSelected(final Note note) {
            
            if (!isLessonRunning())
                return;
            
            Log.d(getTag(), "Notes soll/ist: " + expectedNote + "/" + note);
            if (note.equals(expectedNote)) {
                onSuccess();
            } else {
                onFailure();
            }

        }
    }

}
