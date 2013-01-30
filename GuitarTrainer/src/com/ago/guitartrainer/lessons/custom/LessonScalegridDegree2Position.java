package com.ago.guitartrainer.lessons.custom;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;

import com.ago.guitartrainer.R;
import com.ago.guitartrainer.SettingsActivity;
import com.ago.guitartrainer.db.DatabaseHelper;
import com.ago.guitartrainer.events.NotePlayingEvent;
import com.ago.guitartrainer.events.OnViewSelectionListener;
import com.ago.guitartrainer.lessons.AQuestion.QuestionStatus;
import com.ago.guitartrainer.lessons.LessonMetrics;
import com.ago.guitartrainer.lessons.QuestionMetrics;
import com.ago.guitartrainer.notation.Degree;
import com.ago.guitartrainer.notation.Position;
import com.ago.guitartrainer.scalegrids.ScaleGrid;
import com.ago.guitartrainer.scalegrids.ScaleGrid.Type;
import com.ago.guitartrainer.ui.DegreesView;
import com.ago.guitartrainer.ui.FretView;
import com.ago.guitartrainer.ui.FretView.Layer;
import com.ago.guitartrainer.ui.LearningStatusView;
import com.ago.guitartrainer.ui.MainFragment;
import com.ago.guitartrainer.ui.ScalegridsView;
import com.ago.guitartrainer.utils.LessonsUtils;
import com.j256.ormlite.dao.RuntimeExceptionDao;

public class LessonScalegridDegree2Position extends ALesson {

    private static final int UNDEFINED = 0;

    /* START: views to visualize questions */
    private FretView fretView;

    private ScalegridsView shapesView;

    private DegreesView degreesView;

    private LearningStatusView learningStatusView;

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

    /*-
     * TODO:
     * 
     * * introduce AQuestion<T>, where T would be the QuestionScalegridDegree2Position here
     * * implement getQuestion():QuestionScalegridDegree2Position and use it in this subclass
     * * all save/restor can be done in AQuestion with such design (?) 
     */
    private QuestionScalegridDegree2Position currentQuestion;

    /**
     * Countdown during the question is asked.
     * 
     * Upon expire (finish() riched) the answer is considered to be "FAILED" and we skip to the next question.
     * 
     * */
    private CountDownTimer questionTimer;

    /**
     * 
     * Countdown started after answer is "SUCCESS" before we go to the next question.
     * 
     * User is allowed to be prepared for the next question in such way.
     * 
     * */
    private PauseTimer pauseTimer;

    /**
     * Metrics for the lesson.
     * 
     * Must be found from dB before the first question in lesson's loop starts. Must be updated and persisted before
     * goid next question.
     */
    private LessonMetrics lessonMetrics;

    /**
     * metrics for the current question.
     * 
     * Before the question is shown to user, the object must be found in dB. After going to the next question the
     * metrics must be updated and persisted.
     * 
     * */
    private QuestionMetrics currentQuestionMetrics;

    private DatabaseHelper dbHelper = new DatabaseHelper(MainFragment.getInstance().getActivity());

    private RuntimeExceptionDao<QuestionScalegridDegree2Position, Integer> qDao;

    private RuntimeExceptionDao<QuestionMetrics, Integer> qmDao;

    @Override
    public String getTitle() {
        return "ScalegridDegree2Position";
    }

    @Override
    public void prepareUi() {

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

        learningStatusView = uiControls.getLearningStatusView();

        OnViewSelectionListener<NotePlayingEvent> onSelectionListener = new InnerOnSelectionListener();
        fretView.registerListener(onSelectionListener);

        // and also prepare the dB
        qDao = dbHelper.getRuntimeExceptionDao(QuestionScalegridDegree2Position.class);
        qmDao = dbHelper.getRuntimeExceptionDao(QuestionMetrics.class);

        // try {
        // DatabaseConnection rwCon = qmDao.getConnectionSource().getReadWriteConnection();
        // qmDao.setAutoCommit(rwCon, true);
        // } catch (SQLException e) {
        // e.printStackTrace();
        // }
    }

    @Override
    public void stop() {
        lessonMetrics.stopTime();
        fretView.clearLayer(layerLesson);

        pauseTimer.cancel();
        questionTimer.cancel();

        currentQuestionMetrics.submitAnswer(false);

        qmDao.update(currentQuestionMetrics);

    }

    /**
     * Skip to the next lesson. Or start the first question in the lesson loop.
     * 
     * The answer results are not important.
     * 
     **/
    @Override
    public void next() {
        /*-
         *  
         * During starting new or skipping to new question inside of 
         * the current lesson loop we must either create or pick from dB:
         *   - LessonMetrics, this is done once in the loop
         *   - Question (appropriate for this lesson type) 
         *   - QuestionMetrics for the question
         *  
         *  
         * 
         **/

        /* start question countdown */
        if (questionTimer != null)
            questionTimer.cancel();

        /* cancel if the user clicked the "Next" button */
        if (pauseTimer != null)
            pauseTimer.cancel();

        // TODO: restore the question from the database
        // currentQuestion = new QuestionScalegridDegree2Position();

        if (lessonMetrics == null) {
            /*
             * TODO: we must read the lesson metrics from db here.
             */
            lessonMetrics = new LessonMetrics();

            /*
             * the operations on metrics instance is done only once at this place - at the very beginning of the lesson,
             * just before the first question is asked
             */
            lessonMetrics.startTime();
            int currentLoop = lessonMetrics.increaseLoop();

            learningStatusView.updateLessonLoop(currentLoop);
        }

        learningStatusView.updateCurrentLessonDuration(lessonMetrics.currentDuration());
        learningStatusView.updateAnswerStatus(QuestionStatus.UNDEFINED);

        learningStatusView.updateNextQuestionIndication(UNDEFINED);

        fretView.clearLayer(layerLesson);

        int qCounter = lessonMetrics.increaseQuestionsCounter();
        learningStatusView.updateQuestionsCounter(qCounter);

        /*
         * the original idea was to keep the question params completely in an appropriate AQuestion subclass. But the
         * problems is, that we must decided on the params before we can pick the question from the dB. And the
         * AQuestion instance is required to get appropriate QuestionMetrics object.
         * 
         * So we use temporal vars, which type actually corresponds to the AQuestion vars types.
         */
        Type scaleGridType = Type.ALPHA;
        int fretPosition = 0;
        Degree degree = Degree.ONE;

        {
            /*
             * 1.
             * 
             * In this block we decide on the parameters of the learning function. There are three params here, and each
             * of them can be either user selected or randomly picked.
             */
            if (!isShapeInputAllowed) {
                // param1: grid shape type must be random
                scaleGridType = randomGridShapeType();
            }

            if (!isAreaStartInputAllowed) {
                fretPosition = randomFretPositionForGridShapeType(currentQuestion.scaleGridType);
            }

            if (!isDegreeInputAllowed) {
                degree = randomDegree();
            }
        }

        {
            /*
             * 2.
             * 
             * now we ready either to pick AQuestion from dB, or create a new one. And also the same for related
             * QuestionMetrics.
             */
            try {
                // resolve question
                List<QuestionScalegridDegree2Position> quests = qDao.queryBuilder().where()
                        .eq("scaleGridType", scaleGridType).and().eq("fretPosition", fretPosition).and()
                        .eq("degree", degree).query();
                if (quests.size() == 0) {
                    currentQuestion = new QuestionScalegridDegree2Position();
                    currentQuestion.scaleGridType = scaleGridType;
                    currentQuestion.fretPosition = fretPosition;
                    currentQuestion.degree = degree;
                    // in the next step the metrics object will be added

                } else if (quests.size() == 1) {
                    currentQuestion = quests.get(0);
                } else {
                    throw new RuntimeException("The question object is not unique.");
                }

                // resolve metrics for the question
                List<QuestionMetrics> qMetrics = qmDao.queryBuilder().where().idEq(currentQuestion.getId()).query();
                if (qMetrics.size() == 0) {
                    currentQuestionMetrics = new QuestionMetrics();
                } else if (qMetrics.size() == 1) {
                    currentQuestionMetrics = qMetrics.get(0);
                } else {
                    throw new RuntimeException("The question metrics object is not unique.");
                }

                // save both object to dB, if did not exist before
                if (currentQuestionMetrics.getId() == 0) {
                    qmDao.create(currentQuestionMetrics);
                }
                if (currentQuestion.getId() == 0) {
                    currentQuestion.setMetrics(currentQuestionMetrics);
                    qDao.create(currentQuestion);

                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        currentQuestionMetrics.start();

        /* 3. visualize the question to the user */
        ScaleGrid gridShape = ScaleGrid.create(currentQuestion.scaleGridType, currentQuestion.fretPosition);

        /* both positions must be played for the answer to be accepted */
        acceptedPositions = gridShape.degree2Positions(currentQuestion.degree);

        shapesView.show(gridShape.getType());
        degreesView.show(currentQuestion.degree);
        fretView.show(layerLesson, gridShape);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainFragment.getInstance()
                .getActivity());
        int questionMaxDurationSec = sharedPref.getInt(SettingsActivity.KEY_QUESTION_DURATION_MAX, 10);

        questionTimer = new QuestionTimer(questionMaxDurationSec * 1000, 300);

        questionTimer.start();

        Log.d(getTag(), currentQuestion.toString());
    }

    @Override
    public void showMetrics() {
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

                    if (avg > 10000) {
                        color = R.color.red;
                    } else if (avg > 5000) {
                        color = R.color.orange;
                    } else if (avg > 0) {
                        color = R.color.green;
                    } else {
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

    private ScaleGrid.Type randomGridShapeType() {
        int indexOfGridShape = LessonsUtils.random(0, ScaleGrid.Type.values().length - 1);
        ScaleGrid.Type gridShapeType = ScaleGrid.Type.values()[indexOfGridShape];

        return gridShapeType;
    }

    /**
     * Calculates a random but still valid start of the area in which the scale grid of a given type may reside.
     * 
     * The start area is considered to be valid, if it lays somewhere inside of the 0..
     * {@value ScaleGrid#FRETS_ON_GUITAR} frets of the guitar.
     * 
     * @param gst
     *            type of the scale grid
     * @return valid start of the area
     */
    private int randomFretPositionForGridShapeType(ScaleGrid.Type gst) {
        int fretPosition = LessonsUtils.random(0, ScaleGrid.FRETS_ON_GUITAR);
        int fretPositionEnd = fretPosition + gst.numOfFrets();
        if (fretPositionEnd > ScaleGrid.FRETS_ON_GUITAR) {
            fretPosition = ScaleGrid.FRETS_ON_GUITAR - (fretPositionEnd - fretPosition);
        }
        return fretPosition;
    }

    /**
     * Return a random {@link Degree} from those which are I, II...
     * 
     * @return degree of the scale grid
     */
    private Degree randomDegree() {
        Degree[] mainDegrees = new Degree[] { Degree.ONE, Degree.TWO, Degree.THREE, Degree.FOUR, Degree.FIVE,
                Degree.SIX, Degree.SEVEN };

        boolean isMainDegree = false;
        Degree degree;
        do {
            int indexOfDegree = LessonsUtils.random(0, Degree.values().length - 1);
            degree = Degree.values()[indexOfDegree];
            isMainDegree = Arrays.binarySearch(mainDegrees, degree) >= 0;
        } while (!isMainDegree);

        return degree;
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

            MainFragment.getInstance().getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    /* the lesson has not started. So we ignore all events. */
                    if (acceptedPositions == null)
                        return;

                    /*
                     * no lesson is currently running.
                     * 
                     * Maybe we are in phase of going to the next question.
                     */
                    if (currentQuestionMetrics.isClosed())
                        return;

                    boolean isSuccessful = isAnswerAccepted(acceptedPositions, npEvent.position,
                            npEvent.possiblePositions);

                    if (isSuccessful) {
                        /*
                         * TODO: update metrics for question, persist it to db; update lesson metrics and persist. go to
                         * next question
                         */
                        currentQuestionMetrics.submitAnswer(isSuccessful);
                        learningStatusView.updateTimestampOfLastSuccessfulAnswer(currentQuestionMetrics
                                .lastSuccessfulAnswer());
                        fretView.show(layerLesson, acceptedPositions);

                        learningStatusView.updateAnswerStatus(QuestionStatus.SUCCESS);

                        questionTimer.cancel();

                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainFragment
                                .getInstance().getActivity());
                        int pauseDuration = sharedPref.getInt(SettingsActivity.KEY_POST_QUESTION_PAUSE_DURATION, 5);
                        if (pauseDuration > 0) {
                            pauseTimer = new PauseTimer(pauseDuration * 1000, 1000);
                            pauseTimer.start();
                        }

                    } else {
                        currentQuestionMetrics.submitAnswer(false);

                        learningStatusView.updateAnswerStatus(QuestionStatus.FAILURE);

                    }

                    qmDao.update(currentQuestionMetrics);

                    learningStatusView.updateCurrentQuestionTrials(currentQuestionMetrics.numOfTrialsLastLoop());

                }

            });

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

            if (currentQuestion != null)
                currentQuestion.scaleGridType = element;

        }

    }

    /**
     * Countdown which runs during the user tries to answer the question.
     * 
     * The timer is responsible for:
     * <ul>
     * <li>update the {@link LearningStatusView} with duration of the question
     * <li>skip to next question, if expires
     * </ul>
     * 
     * @author Andrej Golovko - jambit GmbH
     * 
     */
    private class QuestionTimer extends CountDownTimer {

        public QuestionTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            next();

        }

        @Override
        public void onTick(long millisUntilFinished) {
            learningStatusView.updateCurrentQuestionDuration(millisUntilFinished);
        }

    }

    /**
     * Coundown used to pause after successful answer to the current question before we go to the next question.
     * 
     * @author Andrej Golovko - jambit GmbH
     * 
     */
    private class PauseTimer extends CountDownTimer {

        public PauseTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            next();
        }

        @Override
        public void onTick(long millisUntilFinished) {
            learningStatusView.updateNextQuestionIndication(millisUntilFinished);
        }
    }
}
