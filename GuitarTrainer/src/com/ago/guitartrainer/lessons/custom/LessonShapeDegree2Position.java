package com.ago.guitartrainer.lessons.custom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.CountDownTimer;
import android.util.Log;

import com.ago.guitartrainer.R;
import com.ago.guitartrainer.events.NotePlayingEvent;
import com.ago.guitartrainer.events.OnViewSelectionListener;
import com.ago.guitartrainer.gridshapes.GridShape;
import com.ago.guitartrainer.gridshapes.GridShape.Type;
import com.ago.guitartrainer.lessons.AQuestion;
import com.ago.guitartrainer.lessons.AQuestion.QuestionStatus;
import com.ago.guitartrainer.lessons.LessonMetrics;
import com.ago.guitartrainer.lessons.QuestionMetrics;
import com.ago.guitartrainer.notation.Degree;
import com.ago.guitartrainer.notation.Position;
import com.ago.guitartrainer.ui.DegreesView;
import com.ago.guitartrainer.ui.FretView;
import com.ago.guitartrainer.ui.FretView.Layer;
import com.ago.guitartrainer.ui.LearningStatusView;
import com.ago.guitartrainer.ui.MainFragment;
import com.ago.guitartrainer.ui.ShapesView;
import com.ago.guitartrainer.utils.LessonsUtils;

public class LessonShapeDegree2Position extends ALesson {

    /* START: views to visualize questions */
    private FretView fretView;

    private ShapesView shapesView;

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

    /** shape type, as selected by the user */
    private GridShape.Type gridShapeType = Type.ALPHA;

    /** start area for the scale grid, as selected by the user */
    private int areaStart = 0;

    /** degree, as selected by the user */
    private Degree degree;

    private CountDownTimer questionTimer;

    /**
     * metrics for the lesson.
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

    private class QuestionShapeDegree2Position extends AQuestion {
        private GridShape.Type gridShapeType = Type.ALPHA;

        private int position = 0;

        private Degree degree = Degree.ONE;
    }

    @Override
    public String getTitle() {
        return "ShapeDegree2Position";
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
            gridShapeType = randomGridShapeType();
        }

        if (!isAreaStartInputAllowed) {
            areaStart = randomAreaPositionForGridShapeType(gridShapeType);
        }

        Degree degree = Degree.ONE;
        if (!isDegreeInputAllowed) {
            degree = randomDegree();
        }

        /* 2. try to resolve the Question with the given params from dB. Or create a new one */
        {
            /*
             * TODO: the question must be taken from dB
             * 
             * actually, we require the metrics here. But question can be used to resolve the metrics.
             * 
             * The QuestionShapeDegree2Position instance is actually not required for presenting the question to the
             * user.
             */
            // QuestionShapeDegree2Position currentQuestion = DataFacade.findQuestionByParams(
            // QuestionShapeDegree2Position.class, gridShapeType, areaStart, degree);
            //
            // // TODO: the questionMetrics must be created, if null
            // currentQuestionMetrics = DataFacade.findMetricsByQuestion(currentQuestion);

            currentQuestionMetrics = new QuestionMetrics();

            currentQuestionMetrics.start();

        }

        /* 3. visualize the question to the user */

        GridShape gridShape = GridShape.create(gridShapeType, areaStart);

        /* both positions must be played for the answer to be accepted */
        acceptedPositions = gridShape.degree2Positions(degree);

        shapesView.show(gridShape.getType());
        degreesView.show(degree);
        fretView.show(layerLesson, gridShape);

        /* start question countdown */
        if (questionTimer != null)
            questionTimer.cancel();

        questionTimer = new QuestionTimer(10000, 300);
        questionTimer.start();

        Log.d(getTag(), "Shape: " + gridShape + ", Degree: " + degree + ", Expect positions: " + acceptedPositions);
    }

    private GridShape.Type randomGridShapeType() {
        int indexOfGridShape = LessonsUtils.random(0, GridShape.Type.values().length - 1);
        GridShape.Type gridShapeType = GridShape.Type.values()[indexOfGridShape];

        return gridShapeType;
    }

    /**
     * Calculates a random but still valid start of the area in which the scale grid of a given type may reside.
     * 
     * The start area is considered to be valid, if it lays somewhere inside of the 0..
     * {@value GridShape#FRETS_ON_GUITAR} frets of the guitar.
     * 
     * @param gst
     *            type of the scale grid
     * @return valid start of the area
     */
    private int randomAreaPositionForGridShapeType(GridShape.Type gst) {
        int posStart = LessonsUtils.random(0, GridShape.FRETS_ON_GUITAR);
        int posEnd = posStart + gst.numOfFrets();
        if (posEnd > GridShape.FRETS_ON_GUITAR) {
            posStart = GridShape.FRETS_ON_GUITAR - (posEnd - posStart);
        }
        return posStart;
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

                        PauseTimer pauseTimer = new PauseTimer(15000, 1000);
                        pauseTimer.start();

                    } else {
                        currentQuestionMetrics.submitAnswer(false);

                        learningStatusView.updateAnswerStatus(QuestionStatus.FAILURE);

                    }

                    learningStatusView.updateCurrentQuestionTrials(currentQuestionMetrics.numOfTrialsLastLoop());

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
                        possibleAcceptedInterception.addAll(npEvent.possiblePositions);

                    possibleAcceptedInterception.retainAll(acceptedPositions);

                    boolean isAnswerAccepted = false;
                    if (exactPosition != null && acceptedPositions.contains(exactPosition)) {
                        isAnswerAccepted = true;
                    } else if (possibleAcceptedInterception.size() > 0) {
                        isAnswerAccepted = true;
                    }

                    return isAnswerAccepted;
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
    private class InnerOnShapeSelectionListener implements OnViewSelectionListener<GridShape.Type> {

        @Override
        public void onViewElementSelected(GridShape.Type element) {
            gridShapeType = element;

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
