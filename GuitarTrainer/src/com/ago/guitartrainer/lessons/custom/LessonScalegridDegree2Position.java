package com.ago.guitartrainer.lessons.custom;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.media.MediaPlayer;
import android.util.Log;

import com.ago.guitartrainer.GuitarTrainerApplication;
import com.ago.guitartrainer.R;
import com.ago.guitartrainer.SettingsActivity;
import com.ago.guitartrainer.db.DatabaseHelper;
import com.ago.guitartrainer.events.NotePlayingEvent;
import com.ago.guitartrainer.events.OnViewSelectionListener;
import com.ago.guitartrainer.lessons.QuestionMetrics;
import com.ago.guitartrainer.notation.Degree;
import com.ago.guitartrainer.notation.Note;
import com.ago.guitartrainer.notation.Position;
import com.ago.guitartrainer.scalegrids.ScaleGrid;
import com.ago.guitartrainer.scalegrids.ScaleGrid.Type;
import com.ago.guitartrainer.ui.DegreesView;
import com.ago.guitartrainer.ui.FretView;
import com.ago.guitartrainer.ui.FretView.Layer;
import com.ago.guitartrainer.ui.MainFragment;
import com.ago.guitartrainer.ui.ScalegridsView;
import com.ago.guitartrainer.utils.ArrayUtils;
import com.ago.guitartrainer.utils.LessonsUtils;
import com.j256.ormlite.dao.RuntimeExceptionDao;

public class LessonScalegridDegree2Position extends ALesson {

    /* START: views to visualize questions */
    private FretView fretView;

    private ScalegridsView scalegridsView;

    private DegreesView degreesView;

    /** the layer for the fret view image, used to visualize the question on the fret */
    private Layer layerLesson = new Layer(FretView.LAYER_Z_LESSON, MainFragment.getInstance().getResources()
            .getColor(R.color.blue));

    private Layer layerLessonSubmited = new Layer(FretView.LAYER_Z_LESSON + 100, MainFragment.getInstance()
            .getResources().getColor(R.color.green));

    /* END: views to visualize questions */

    /**
     * Positions, which are accepted by the lesson as successful answer to the current question.
     * 
     * In general, there are several positions available for the same degree inside of the scale grid. Each such
     * position correspond to a different {@link Note}. In this lesson we assume the user submit all of the positions
     * step-by-step.
     * */
    private List<Position> expectedPositions;

    /**
     * _Correctly_ submitted by the user positions, as mentioned in javadoc for {@link #expectedPositions}.
     * 
     * The question of the lesson is considered to be accomplished, when the {@link #submittedPositions} contains the
     * same objects as {@link #expectedPositions}
     * */
    private Set<Position> submittedPositions = new HashSet<Position>();

    /**
     * if true, the grid shape used as lesson parameter is allowed to be entered by the user. Otherwise, the parameter
     * is selected randomly.
     */
//    private boolean isShapeInputAllowed = true;
    private ScaleGrid.Type userScalegridType = Type.ALPHA;

    /**
     * if true, the user is allowed decided on the degree parameter by himself. If false, the degree is selected
     * randomly.
     */
//    private boolean isDegreeInputAllowed = false;

    /**
     * if true, the user is allowed to decide on the start position of the scale grid. If false, the valid starting
     * position is selected randomly.
     */
    private boolean isAreaStartInputAllowed = true;

    // TODO: remove from class var? it is cached in DatabaseHelper anyway
    private RuntimeExceptionDao<QuestionScalegridDegree2Position, Integer> qDao;

    /**
     * Defines whether only position in Ist degrees is shown.
     * 
     * If true, only the Ist degree position is shown. If several Ist degree positions are available, only the one with
     * lower fret is selected.
     * 
     * If false, positions for all main degrees are shown - I, II, ... VII.
     * 
     * @deprecated see {@link ScalegridsView#isRootOnlyShown()}
     * */
    private boolean isOnly1stDegreeShown = false;

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

        scalegridsView = uiControls.getShapestView();
        scalegridsView.setEnabled(true);
//        scalegridsView.setEnabledInput(isShapeInputAllowed);

        if (!scalegridsView.isRandomInput()) {
            InnerOnShapeSelectionListener onShapeSelection = new InnerOnShapeSelectionListener();
            scalegridsView.registerListener(onShapeSelection);
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
        fretView.clearLayer(layerLessonSubmited);
        fretView.clearLayerByZIndex(FretView.LAYER_Z_TOUCHES);
        fretView.clearLayerByZIndex(FretView.LAYER_Z_FFT);

        /*
         * the original idea was to keep the question params completely in an appropriate AQuestion subclass. But the
         * problems is, that we must decided on the params before we can pick the question from the dB. And the
         * AQuestion instance is required to get appropriate QuestionMetrics object.
         * 
         * So we use temporal vars, which type actually corresponds to the AQuestion vars types.
         */
        int fretPosition = 1;
        Degree degree = Degree.ONE;

        /*
         * 1.
         * 
         * In this block we decide on the parameters of the learning function. There are three params here, and each of
         * them can be either user selected or randomly picked.
         */
        if (scalegridsView.isRandomInput()) {
            // param1: grid shape type must be random
            userScalegridType = LessonsUtils.randomGridShapeType();
        } else {
            userScalegridType = scalegridsView.scalegridType();
        }

        if (!isAreaStartInputAllowed) {
            fretPosition = LessonsUtils.randomFretPositionForGridShapeType(userScalegridType);
        }

        if (degreesView.isRandomInput()) {
            degree = LessonsUtils.randomDegree();
        } else {
            degree = degreesView.degree();
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

        /* all positions must be played for the answer to be accepted */
        expectedPositions = gridShape.degree2Positions(quest.degree);

        /** TMP:start: the chord is shown */
        // expectedPositions = gridShape.chord2Positions(Chord.major);
        /** TMP:end */

        submittedPositions.clear();
        messageInLearningStatus(null);

        scalegridsView.show(gridShape.getType());
        degreesView.show(quest.degree);
        
        if (!scalegridsView.isRootOnlyShown()) {
            fretView.show(layerLesson, gridShape);
        } else {
            fretView.show(layerLesson, gridShape.getRootPosition());
        }

        boolean playSound = GuitarTrainerApplication.getPrefs().getBoolean(SettingsActivity.KEY_PLAY_SOUNDS, false);
        if (playSound) {
            playDegree(quest.degree);
        }
    }

    private MediaPlayer mediaPlayer;

    private void playDegree(Degree degree) {
        int mp3Id = R.raw.one;
        switch (degree) {
        case ONE:
            mp3Id = R.raw.one;
            break;
        case TWO:
            mp3Id = R.raw.two;
            break;
        case THREE:
            mp3Id = R.raw.three;
            break;
        case FOUR:
            mp3Id = R.raw.four;
            break;
        case FIVE:
            mp3Id = R.raw.five;
            break;
        case SIX:
            mp3Id = R.raw.six;
            break;
        case SEVEN:
            mp3Id = R.raw.seven;
            break;

        default:
            break;
        }
        ;

        mediaPlayer = MediaPlayer.create(MainFragment.getInstance().getActivity(), mp3Id);
        mediaPlayer.start();

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

            /*
             * TODO: implementation is sub-optimal. The query could actually be done with single left-join query. But
             * currently, I donn't know how to do it with ORMLite
             */
            List<QuestionScalegridDegree2Position> quests = qDao.queryBuilder().where()
                    .eq("scaleGridType", userScalegridType).query();

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
            ScaleGrid scaleGrid = ScaleGrid.create(userScalegridType, 0);
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

                /*
                 * we must be more tolerant to response time, when the user is requested to submit more positions rather
                 * than one
                 */
                int tolleranceCoefficient = positions.size();

                for (Position position : positions) {
                    int color = R.color.black;

                    if (!mapDegree2Avg.containsKey(degree)) {
                        mapPosition2Color.put(position, color);
                        continue;
                    }

                    Double avg = mapDegree2Avg.get(degree);

                    if (avg > shortestReactionTimeMs * 2 * tolleranceCoefficient) {
                        color = R.color.red;
                    } else if (avg > shortestReactionTimeMs * tolleranceCoefficient) {
                        color = R.color.orange;
                    } else if (avg > 0 && avg <= shortestReactionTimeMs * tolleranceCoefficient) {
                        color = R.color.green;
                    } else {
                        // avg == 0, e.g. question was shown, but the user even did not try to answer it
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
     * Return those positions, which were played/touched by the user (delivered in <code>npEvent</code>) and are in
     * <code>expectedPosition</code>.
     * 
     * Usually the returned position(-s) are only a subset of those expected. But during the question run the user
     * delivered positions are accumulated to match the whole list of expected positions.
     * 
     * @param expectedPositions
     *            positions on fret, which are expected by the question
     * @param npEvent
     *            contains either exact position played on the fret or a set of plausible positions played
     * @return
     */
    private List<Position> calculatedAcceptedPositions(List<Position> expectedPositions, NotePlayingEvent npEvent) {

        List<Position> correctPositions = new ArrayList<Position>();

        if (npEvent.position != null) {
            if (expectedPositions.contains(npEvent.position)) {
                correctPositions.add(npEvent.position);
            }
        } else if (!ArrayUtils.isEmpty(npEvent.possiblePositions)) {
            /*
             * The "interception" will contain all positions from submitted by the user, which match the expected
             * positions.
             * 
             * Example: assume, we expect position X, Y, Z; npEvent.possiblePositions contains A, Z, C. The interception
             * will contain the Z. In other words, we a happy to see any user input, even if it is partially correct.
             * The reason: the position is detected with FFT from played note, and such detection can not be precise for
             * guitar.
             */
            List<Position> interception = new ArrayList<Position>();
            interception.addAll(npEvent.possiblePositions);
            interception.retainAll(expectedPositions);

            correctPositions.addAll(interception);
        }

        return correctPositions;
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
            if (expectedPositions == null)
                return;

            if (!isLessonRunning())
                return;

            /* accepted positions, also those which were submitted in previously */
            Collection<Position> positions = calculatedAcceptedPositions(expectedPositions, npEvent);

            if (!ArrayUtils.isEmpty(positions)) {

                List<Position> knownPositions = ArrayUtils.intersect(submittedPositions, positions);

                if (knownPositions.size() == 0) {
                    /* all positions are new, unknown */

                    /*
                     * the user answer accepted. But is it complete?
                     * 
                     * We check here if this and previous answers are complete.
                     */
                    submittedPositions.addAll(positions);

                    messageInLearningStatus("You found " + submittedPositions.size() + " of "
                            + expectedPositions.size() + " positions.");

                    /* clean either touch of fft drawn layer, show what was correctly submitted by the user till now */
                    List<Position> tmp = new ArrayList<Position>();
                    tmp.addAll(submittedPositions);
                    fretView.show(layerLessonSubmited, tmp);
                    fretView.clearLayerByZIndex(FretView.LAYER_Z_TOUCHES);
                    fretView.clearLayerByZIndex(FretView.LAYER_Z_FFT);

                    /* all positions from users are new */
                    if (ArrayUtils.isEqual(expectedPositions, submittedPositions)) {
                        onSuccess();
                    } else {

                        vibrateYesButUncompleted();
                        if (submittedPositions.size() > expectedPositions.size()) {
                            Log.e(getTag(), "NOT POSSIBLE");
                        }
                    }
                } else {
                    // Otherwise: just ignore the event
                }

            } else {
                /* no expected position was delivered at all */
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
