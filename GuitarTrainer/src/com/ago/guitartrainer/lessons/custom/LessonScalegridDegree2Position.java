package com.ago.guitartrainer.lessons.custom;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.ago.guitartrainer.GuitarTrainerApplication;
import com.ago.guitartrainer.R;
import com.ago.guitartrainer.SettingsActivity;
import com.ago.guitartrainer.db.DatabaseHelper;
import com.ago.guitartrainer.events.NotePlayingEvent;
import com.ago.guitartrainer.events.OnViewSelectionListener;
import com.ago.guitartrainer.lessons.AQuestion;
import com.ago.guitartrainer.lessons.QuestionMetrics;
import com.ago.guitartrainer.notation.Degree;
import com.ago.guitartrainer.notation.Position;
import com.ago.guitartrainer.scalegrids.ScaleGrid;
import com.ago.guitartrainer.scalegrids.ScaleGrid.Type;
import com.ago.guitartrainer.ui.DegreesView;
import com.ago.guitartrainer.ui.FretView;
import com.ago.guitartrainer.ui.FretView.Layer;
import com.ago.guitartrainer.ui.MainFragment;
import com.ago.guitartrainer.ui.ScalegridsView;
import com.ago.guitartrainer.utils.LessonsUtils;
import com.j256.ormlite.dao.RuntimeExceptionDao;

public class LessonScalegridDegree2Position extends ALesson {

    /* START: views to visualize questions */
    private FretView fretView;

    private ScalegridsView shapesView;

    private DegreesView degreesView;

    /** the layer for the fret view image, used to visualize the question on the fret */
    private Layer layerLesson = new Layer(FretView.LAYER_Z_LESSON, MainFragment.getInstance().getResources()
            .getColor(R.color.blue));

    /* END: views to visualize questions */

    /** positions, which are accepted by the lesson as successful answer to the current question */
    private List<Position> acceptedPositions;

    /**
     * if true, the grid shape used as lesson parameter is allowed to be entered by the user. Otherwise, the parameter
     * is selected randomly.
     */
    private boolean isShapeInputAllowed = true;
    private ScaleGrid.Type userScalegridType = Type.ALPHA;

    /**
     * if true, the user is allowed decided on the degree parameter by himself. If false, the degree is selected
     * randomly.
     */
    private boolean isDegreeInputAllowed = false;

    /**
     * if true, the user is allowed to decide on the start position of the scale grid. If false, the valid starting
     * position is selected randomly.
     */
    private boolean isAreaStartInputAllowed = true;

    // TODO: remove from class var? it is cached in DatabaseHelper anyway
    private RuntimeExceptionDao<QuestionScalegridDegree2Position, Integer> qDao;

    @Override
    public String getTitle() {
        String str = MainFragment.getInstance().getResources()
                .getString(R.string.lesson_scalegriddegree2position_title);
        return str;
    }

    @Override
    public String getDescription() {
        String str = MainFragment.getInstance().getResources()
                .getString(R.string.lesson_scalegriddegree2position_description);
        return str;
    }

    @Override
    public void doPrepareUi() {

        qDao = DatabaseHelper.getInstance().getRuntimeExceptionDao(QuestionScalegridDegree2Position.class);

        // initialize views required for the current type of lesson
        MainFragment uiControls = MainFragment.getInstance();

        fretView = uiControls.getFretView();
        fretView.setEnabled(true);
        fretView.setEnabledInput(true);

        uiControls.getNotesView().setEnabled(false);

        shapesView = uiControls.getShapestView();
        shapesView.setEnabled(true);
        shapesView.setEnabledInput(isShapeInputAllowed);

        if (isShapeInputAllowed) {
            InnerOnShapeSelectionListener onShapeSelection = new InnerOnShapeSelectionListener();
            shapesView.registerListener(onShapeSelection);
        }

        degreesView = uiControls.getDegreesView();
        degreesView.setEnabled(true);
        degreesView.setEnabledInput(false);

        uiControls.getShapestView().setEnabled(true);

        OnViewSelectionListener<NotePlayingEvent> onSelectionListener = new InnerOnSelectionListener();
        fretView.registerListener(onSelectionListener);

    }

    @Override
    public void doStop() {
        fretView.clearLayer(layerLesson);
    }

    /**
     * Skip to the next lesson. Or start the first question in the lesson loop.
     * 
     * The answer results are not important.
     * 
     **/
    @Override
    public void doNext() {
        fretView.clearLayer(layerLesson);
        /*
         * the original idea was to keep the question params completely in an appropriate AQuestion subclass. But the
         * problems is, that we must decided on the params before we can pick the question from the dB. And the
         * AQuestion instance is required to get appropriate QuestionMetrics object.
         * 
         * So we use temporal vars, which type actually corresponds to the AQuestion vars types.
         */
        // Type scaleGridType = Type.ALPHA;
        int fretPosition = 0;
        Degree degree = Degree.ONE;

        /*
         * 1.
         * 
         * In this block we decide on the parameters of the learning function. There are three params here, and each of
         * them can be either user selected or randomly picked.
         */
        if (!isShapeInputAllowed) {
            // param1: grid shape type must be random
            userScalegridType = LessonsUtils.randomGridShapeType();
        }

        if (!isAreaStartInputAllowed) {
            fretPosition = LessonsUtils.randomFretPositionForGridShapeType(userScalegridType);
        }

        if (!isDegreeInputAllowed) {
            degree = LessonsUtils.randomDegree();
        }

        /*
         * 2.
         * 
         * now we ready either to pick AQuestion from dB, or create a new one. And also the same for related
         * QuestionMetrics.
         */
        QuestionScalegridDegree2Position quest = resolveOrCreateQuestion(userScalegridType, fretPosition, degree);
        QuestionMetrics qm = resolveOrCreateQuestionMetrics(quest.getId());
        registerQuestion(qDao, quest, qm);

        /* 3. visualize the question to the user */
        ScaleGrid gridShape = ScaleGrid.create(quest.scaleGridType, quest.fretPosition);

        /* both positions must be played for the answer to be accepted */
        acceptedPositions = gridShape.degree2Positions(quest.degree);

        shapesView.show(gridShape.getType());
        degreesView.show(quest.degree);
        fretView.show(layerLesson, gridShape);

    }

    /**
     * Find question in dB or create a new one. The question crated is not persisted in the method.
     * 
     * @param scalegridType
     * @param fretPosition
     * @param degree
     * @return
     */
    private QuestionScalegridDegree2Position resolveOrCreateQuestion(Type scalegridType, int fretPosition, Degree degree) {
        QuestionScalegridDegree2Position question = null;
        try {
            // resolve question
            List<QuestionScalegridDegree2Position> quests = qDao.queryBuilder().where()
                    .eq("scaleGridType", scalegridType).and().eq("fretPosition", fretPosition).and()
                    .eq("degree", degree).query();
            if (quests.size() == 0) {
                question = new QuestionScalegridDegree2Position();
                question.scaleGridType = scalegridType;
                question.fretPosition = fretPosition;
                question.degree = degree;
                // in the next step the metrics object will be added

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

    @Override
    public void showMetrics() {

        int shortestReactionTimeMs = GuitarTrainerApplication.getPrefs().getInt(
                SettingsActivity.KEY_QUESTION_SHORTEST_REACTION_TIME, 1000);

        /*
         * TODO:
         * 
         * the current implementation of the metrics is for demo purposes only. For instance, it is just easier for me
         * to reuse existing views - FretView - as to write a separate one.
         */

        /*-
         * With the visualization used here we project all scale grids of specific type onto the single scale grid
         * located at position 0. For example, the degree II of Alpha scale grid will contain the metrics for all
         * degree-II positions which are on the fret.
         * 
         * Basically, the algo is as following
         *  - find questions and metrics for specific scale grid type
         *  - create Map<Degree, Double> position-agnostic map from Degree to avg response time
         *  - use this map to create another Map<Position, Integer> map, which contains the positions 
         *    of specific scale grid starting from fretPosition=0. (the Integer specifies the color)
         */
        try {
            fretView.clearLayer(layerLesson);

            ScaleGrid.Type scaleGridType = ScaleGrid.Type.ALPHA;
            /*
             * TODO: implementation is sub-optimal. The query could actually be done with single left-join query. But
             * currently, I donn't know how to do it with ORMLite
             */
            List<QuestionScalegridDegree2Position> quests = qDao.queryBuilder().where()
                    .eq("scaleGridType", scaleGridType).query();

            /* collection metrics about degrees */
            Map<Degree, Double> mapDegree2Avg = new HashMap<Degree, Double>();
            Map<Degree, Integer> mapDegree2Counter = new HashMap<Degree, Integer>(); // helper map, to calculate the
                                                                                     // average
            for (QuestionScalegridDegree2Position quest : quests) {
                if (quest.getMetrics() != null) {
                    Integer qmId = quest.getMetrics().getId();
                    List<QuestionMetrics> results = qmDao.queryBuilder().where().idEq(qmId).query();
                    QuestionMetrics qm = results.get(0);

                    if (!mapDegree2Avg.containsKey(quest.degree)) {
                        mapDegree2Avg.put(quest.degree, 0d);
                        mapDegree2Counter.put(quest.degree, 0);
                    }

                    int nMinus1 = mapDegree2Counter.get(quest.degree);
                    int n = nMinus1 + 1;
                    mapDegree2Counter.put(quest.degree, n);

                    double oldVal = mapDegree2Avg.get(quest.degree);
                    double newVal = (nMinus1 * oldVal + (double) qm.avgSuccessfulAnswerTime) / n;
                    mapDegree2Avg.put(quest.degree, newVal);
                }
            }

            /* Preparing the position to color map, Map<Position, Integer> */
            ScaleGrid scaleGrid = ScaleGrid.create(ScaleGrid.Type.ALPHA, 0);
            Map<Position, Integer> mapPosition2Color = new HashMap<Position, Integer>();

            for (Degree degree : Degree.STRONG_DEGREES) {
                /*
                 * TODO: problem: positions with the same degree will always have the same color.
                 * 
                 * To differentiate the both positions with the same degree we need to be more precise in question
                 * itself: like, use "Higher" or "Lower" keywords when asking the user to identify position in scale
                 * grid.
                 */
                List<Position> positions = scaleGrid.degree2Positions(degree);

                for (Position position : positions) {
                    int color = R.color.black;

                    if (!mapDegree2Avg.containsKey(degree)) {
                        mapPosition2Color.put(position, color);
                        continue;
                    }

                    Double avg = mapDegree2Avg.get(degree);

                    if (avg > shortestReactionTimeMs * 2) {
                        color = R.color.red;
                    } else if (avg > shortestReactionTimeMs) {
                        color = R.color.orange;
                    } else if (avg > 0 && avg <= shortestReactionTimeMs) {
                        color = R.color.green;
                    } else {
                        // avg == 0
                        color = R.color.gray;
                    }
                    mapPosition2Color.put(position, color);
                }
            }

            // visualize
            fretView.show(layerLesson, mapPosition2Color);

        } catch (SQLException e) {
            Log.d(getTag(), e.getMessage(), e);
        }
    }

    /**
     * Calculates whether the answer provided by the user - reflected with <code>exactPosition</code> and
     * <code>possiblePositions</code> - can be considered as a successful answer.
     * 
     * @param acceptedPositions
     *            positions on fret, which are expected by the question
     * @param exactPosition
     *            which was pressed on fret, if unambiguous detection was possible
     * @param possiblePositions
     *            which could have been pressed, usually due to note detection with help of sound
     * @return
     */
    private boolean isAnswerAccepted(List<Position> acceptedPositions, Position exactPosition,
            List<Position> possiblePositions) {
        // TODO: the npe.position is not set, when detected with FFT. It is not possible to
        // resolve unique position.

        List<Position> possibleAcceptedInterception = new ArrayList<Position>();

        if (possiblePositions != null)
            possibleAcceptedInterception.addAll(possiblePositions);

        possibleAcceptedInterception.retainAll(acceptedPositions);

        boolean isAnswerAccepted = false;
        if (exactPosition != null && acceptedPositions.contains(exactPosition)) {
            isAnswerAccepted = true;
        } else if (possibleAcceptedInterception.size() > 0) {
            isAnswerAccepted = true;
        }

        return isAnswerAccepted;
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
        public void onViewElementSelected(final NotePlayingEvent npEvent) {

            /* the lesson has not started. So we ignore all events. */
            if (acceptedPositions == null)
                return;

            if (!isLessonRunning())
                return;

            boolean isSuccessful = isAnswerAccepted(acceptedPositions, npEvent.position, npEvent.possiblePositions);

            if (isSuccessful) {

                onSuccess();
                fretView.show(layerLesson, acceptedPositions);

            } else {

                onFailure();
            }

        }
    }

    /**
     * Listens on user selection of the shape.
     * 
     * This listener does not participate in evaluation of answer. It just configure one of the lesson parameters - the
     * grid shape to use in questions.
     * 
     * @author Andrej Golovko - jambit GmbH
     * 
     */
    private class InnerOnShapeSelectionListener implements OnViewSelectionListener<ScaleGrid.Type> {

        @Override
        public void onViewElementSelected(ScaleGrid.Type element) {

            // if (currentQuestion != null)
            userScalegridType = element;
            if (isLessonRunning())
                next();
        }

    }

}
