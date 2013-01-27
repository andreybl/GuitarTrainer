package com.ago.guitartrainer.lessons.custom;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.CountDownTimer;
import android.util.Log;

import com.ago.guitartrainer.R;
import com.ago.guitartrainer.db.DatabaseHelper;
import com.ago.guitartrainer.events.NotePlayingEvent;
import com.ago.guitartrainer.events.OnViewSelectionListener;
import com.ago.guitartrainer.lessons.AQuestion.QuestionStatus;
import com.ago.guitartrainer.lessons.LessonMetrics;
import com.ago.guitartrainer.lessons.QuestionMetrics;
import com.ago.guitartrainer.notation.Degree;
import com.ago.guitartrainer.notation.Position;
import com.ago.guitartrainer.scalegrids.ScaleGrid;
import com.ago.guitartrainer.ui.DegreesView;
import com.ago.guitartrainer.ui.FretView;
import com.ago.guitartrainer.ui.FretView.Layer;
import com.ago.guitartrainer.ui.LearningStatusView;
import com.ago.guitartrainer.ui.MainFragment;
import com.ago.guitartrainer.ui.ScalegridsView;
import com.ago.guitartrainer.utils.LessonsUtils;
import com.j256.ormlite.dao.Dao;

public class LessonScalegridDegree2Position extends ALesson {

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

    }

    @Override
    public void stop() {
        lessonMetrics.stopTime();
        fretView.clearLayer(layerLesson);
        questionTimer.cancel();
        currentQuestionMetrics.submitAnswer(false);
    }

    /**
     * Skip to the next lesson.
     * 
     * The answer results are not important.
     * 
     **/
    @Override
    public void next() {

        /* start question countdown */
        if (questionTimer != null)
            questionTimer.cancel();

        /* cancel if the user clicked the "Next" button */
        if (pauseTimer != null)
            pauseTimer.cancel();

        // TODO: restore the question from the database
        currentQuestion = new QuestionScalegridDegree2Position();

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

        learningStatusView.updateNextQuestionIndication(0);

        fretView.clearLayer(layerLesson);

        int qCounter = lessonMetrics.increaseQuestionsCounter();
        learningStatusView.updateQuestionsCounter(qCounter);

        /* 1. decide on the parameters for the learning function */
        if (!isShapeInputAllowed) {
            // param1: grid shape type must be random
            currentQuestion.scaleGridType = randomGridShapeType();
        }

        if (!isAreaStartInputAllowed) {
            currentQuestion.fretPosition = randomFretPositionForGridShapeType(currentQuestion.scaleGridType);
        }

        if (!isDegreeInputAllowed) {
            currentQuestion.degree = randomDegree();
        }

        /* 2. try to resolve the Question with the given params from dB. Or create a new one */
        {
            // TODO: resolve already existing metrics from dB

            currentQuestionMetrics = new QuestionMetrics();

            currentQuestionMetrics.start();

        }

        /* 3. visualize the question to the user */

        ScaleGrid gridShape = ScaleGrid.create(currentQuestion.scaleGridType, currentQuestion.fretPosition);

        /* both positions must be played for the answer to be accepted */
        acceptedPositions = gridShape.degree2Positions(currentQuestion.degree);

        tmpTestPersistance(currentQuestion);

        shapesView.show(gridShape.getType());
        degreesView.show(currentQuestion.degree);
        fretView.show(layerLesson, gridShape);

        questionTimer = new QuestionTimer(10000, 300);
        questionTimer.start();

        Log.d(getTag(), currentQuestion.toString());
    }

    DatabaseHelper dbHelper = new DatabaseHelper(MainFragment.getInstance().getActivity());

    private void tmpTestPersistance(QuestionScalegridDegree2Position question) {
        try {
            Dao<QuestionScalegridDegree2Position, ?> dao = dbHelper.getDao(QuestionScalegridDegree2Position.class);

            dao.create(question);
            
            Log.d(getTag(), "Inserted: "+question);
        } catch (SQLException e) {
            Log.e(getTag(), e.getMessage(), e);
        }
    }

    @Override
    public void showMetrics() {
        try {
            Dao<QuestionScalegridDegree2Position, ?> dao = dbHelper.getDao(QuestionScalegridDegree2Position.class);

            for (QuestionScalegridDegree2Position q : dao) {
                Log.d(getTag(), q.toString());
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
                        pauseTimer = new PauseTimer(15000, 1000);
                        pauseTimer.start();

                    } else {
                        currentQuestionMetrics.submitAnswer(false);

                        learningStatusView.updateAnswerStatus(QuestionStatus.FAILURE);

                    }

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
