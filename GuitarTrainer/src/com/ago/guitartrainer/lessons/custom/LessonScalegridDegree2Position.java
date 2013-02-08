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
import com.ago.guitartrainer.MasterActivity;
import com.ago.guitartrainer.R;
import com.ago.guitartrainer.SettingsActivity;
import com.ago.guitartrainer.db.DatabaseHelper;
import com.ago.guitartrainer.events.NotePlayingEvent;
import com.ago.guitartrainer.events.OnViewSelectionListener;
import com.ago.guitartrainer.fragments.FragmentScalegridDegree2Position;
import com.ago.guitartrainer.instruments.guitar.Position;
import com.ago.guitartrainer.lessons.AQuestion;
import com.ago.guitartrainer.lessons.QuestionMetrics;
import com.ago.guitartrainer.notation.Degree;
import com.ago.guitartrainer.notation.Note;
import com.ago.guitartrainer.scalegrids.ScaleGrid;
import com.ago.guitartrainer.scalegrids.ScaleGrid.Type;
import com.ago.guitartrainer.ui.FretView;
import com.ago.guitartrainer.ui.FretView.Layer;
import com.ago.guitartrainer.ui.LearningStatusView;
import com.ago.guitartrainer.utils.ArrayUtils;
import com.ago.guitartrainer.utils.LessonsUtils;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.Where;

public class LessonScalegridDegree2Position extends ALesson {

    protected FragmentScalegridDegree2Position fragment;

    /** the layer for the fret view image, used to visualize the question on the fret */
    protected Layer layerLesson = new Layer(FretView.LAYER_Z_LESSON, MasterActivity.getInstance().getResources()
            .getColor(R.color.blue));

    private Layer layerLessonSubmited = new Layer(FretView.LAYER_Z_LESSON + 100, MasterActivity.getInstance()
            .getResources().getColor(R.color.green));

    /**
     * Positions, which are accepted by the lesson as successful answer to the current question.
     * 
     * In general, there are several positions available for the same degree inside of the scale grid. Each such
     * position correspond to a different {@link Note}. In this lesson we assume the user submit all of the positions
     * step-by-step.
     * */
    private List<Position> expectedPositions;

    public LessonScalegridDegree2Position() {
        if (qDao.countOf() == 0) {
            populateDatabase();
        }
    }

    /**
     * _Correctly_ submitted by the user positions, as mentioned in javadoc for {@link #expectedPositions}.
     * 
     * The question of the lesson is considered to be accomplished, when the {@link #submittedPositions} contains the
     * same objects as {@link #expectedPositions}
     * */
    private Set<Position> submittedPositions = new HashSet<Position>();

    // TODO: remove from class var? it is cached in DatabaseHelper anyway
    private RuntimeExceptionDao<QuestionScalegridDegree2Position, Integer> qDao = DatabaseHelper.getInstance()
            .getRuntimeExceptionDao(QuestionScalegridDegree2Position.class);

    @Override
    public String getTitle() {
        String str = MasterActivity.getInstance().getResources()
                .getString(R.string.lesson_scalegriddegree2position_title);
        return str;
    }

    @Override
    public String getDescription() {
        String str = MasterActivity.getInstance().getResources()
                .getString(R.string.lesson_scalegriddegree2position_description);
        return str;
    }

    @Override
    public void doStop() {
        fragment.getFretView().clearLayer(layerLesson);
    }

    /**
     * Skip to the next lesson. Or start the first question in the lesson loop.
     * 
     * The answer results are not important.
     * 
     **/
    @Override
    public void doNext() {
        fragment.getFretView().clearLayer(layerLesson);
        fragment.getFretView().clearLayer(layerLessonSubmited);
        fragment.getFretView().clearLayerByZIndex(FretView.LAYER_Z_TOUCHES);
        fragment.getFretView().clearLayerByZIndex(FretView.LAYER_Z_FFT);

        /*
         * 2. resolve question and metrics by params from dB
         * 
         * now we ready either to pick AQuestion from dB, or create a new one. And also the same for related
         * QuestionMetrics.
         */
        QuestionScalegridDegree2Position quest = null;
        try {
            // TODO: user it!
             quest = (QuestionScalegridDegree2Position) resolveNextQuestionByLearningAlgo(qDao);

            if (quest == null)
                quest = (QuestionScalegridDegree2Position) resolveNextQuestion();

            QuestionMetrics qm = resolveOrCreateQuestionMetrics(quest.getId());

            registerQuestion(qDao, quest, qm);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (quest != null) {
            ScaleGrid sg = ScaleGrid.create(quest.scaleGridType, quest.fretPosition);
            expectedPositions = sg.degree2Positions(quest.degree);

            submittedPositions.clear();
            messageInLearningStatus(null);

            showQuestionToUser(quest);

        }
    }

    private void populateDatabase() {

        int fretPosition = 1;

        for (Type scalegridType : Type.values()) {
            for (Degree degree : Degree.NATURAL_DEGREES) {
                QuestionScalegridDegree2Position quest = new QuestionScalegridDegree2Position();
                quest.scaleGridType = scalegridType;
                quest.fretPosition = fretPosition;
                quest.degree = degree;

                QuestionMetrics qm = new QuestionMetrics();
                qmDao.create(qm);

                quest.setMetrics(qm);
                qDao.create(quest);

            }
        }

    }

    /**
     * Resolve the next question to be asked every time the method is called.
     * 
     * The question is resolved based on parameters, which are:
     * <ul>
     * <li>either user input'ed
     * <li>or randomly selected
     * </ul>
     * 
     * The mathod does not apply any learning algorithms to decide which question to return.
     * 
     * @return question to be present to user
     * @throws SQLException
     */
    protected AQuestion resolveNextQuestion() throws SQLException {
        QuestionScalegridDegree2Position quest = null;

        Where<QuestionScalegridDegree2Position, Integer> where = qDao.queryBuilder().where();

        Type scalegridType = Type.ALPHA;
        Degree degree = Degree.TWO;
        int fretPosition = 1;

        /*
         * 1. Decided on parameterst of the question: scale grid, fret position, degree
         * 
         * Each param can either be random, or defined by the user.
         */

        if (fragment.getScalegridView().isRandomInput()) {
            scalegridType = LessonsUtils.randomScalegridType();
        } else {
            // user selected choice
            scalegridType = fragment.getScalegridView().element();
        }
        where = where.eq("scaleGridType", scalegridType);

        if (fragment.getScalegridView().isRandomPosition()) {
            fretPosition = LessonsUtils.randomFretPositionForGridShapeType(scalegridType);
        } else {
            // user selected choice
            fretPosition = 1;
        }
        where = where.and().eq("fretPosition", fretPosition);

        if (fragment.getDegreesView().isRandomInput()) {
            degree = LessonsUtils.randomDegree();
        } else {
            // user selected choice
            degree = fragment.getDegreesView().element();
        }
        where = where.and().eq("degree", degree);

        /*
         * 2. use the learning algo to decide on unique question to ask in current lap
         * 
         * We ignore the fact, that some params can be either random or user selected. If all params are either random
         * or user selected, the result of the "quests" is a single question. And the application of the learning algo
         * does not harm.
         */
        List<QuestionScalegridDegree2Position> quests = where.query();

        if (quests.size() == 0) {
            quest = new QuestionScalegridDegree2Position();
            quest.scaleGridType = scalegridType;
            quest.fretPosition = fretPosition;
            quest.degree = degree;
        } else {
            quest = quests.get(0);
        }

        return quest;
    }

    protected void showQuestionToUser(AQuestion q) {
        QuestionScalegridDegree2Position quest = (QuestionScalegridDegree2Position) q;
        fragment.getScalegridView().show(quest.scaleGridType);
        fragment.getDegreesView().show(quest.degree);

        ScaleGrid sg = ScaleGrid.create(quest.scaleGridType, quest.fretPosition);

        if (!fragment.getScalegridView().isRootOnlyShown()) {
            fragment.getFretView().show(layerLesson, sg);
        } else {
            fragment.getFretView().show(layerLesson, sg.getRootPosition());
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

        mediaPlayer = MediaPlayer.create(MasterActivity.getInstance(), mp3Id);
        mediaPlayer.start();

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
            fragment.getFretView().clearLayer(layerLesson);

            /*
             * TODO: implementation is sub-optimal. The query could actually be done with single left-join query. But
             * currently, I donn't know how to do it with ORMLite
             */
            List<QuestionScalegridDegree2Position> quests = qDao.queryBuilder().where()
                    .eq("scaleGridType", fragment.getScalegridView().element()).query();

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
            ScaleGrid scaleGrid = ScaleGrid.create(fragment.getScalegridView().element(), 0);
            Map<Position, Integer> mapPosition2Color = new HashMap<Position, Integer>();

            for (Degree degree : Degree.NATURAL_DEGREES) {
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
            fragment.getFretView().show(layerLesson, mapPosition2Color);

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

    @Override
    protected LearningStatusView getLearningStatusView() {
        return fragment.getLearningStatusView();
    }

    public void onFragmentInitializationCompleted(FragmentScalegridDegree2Position fragment) {

        this.fragment = fragment;

        // initialize views required for the current type of lesson
        fragment.getFretView().setEnabled(true);
        fragment.getFretView().setEnabledInput(true);

        fragment.getScalegridView().setEnabled(true);

        fragment.getDegreesView().setEnabled(true);
        fragment.getDegreesView().setEnabledInput(false);

        fragment.getScalegridView().setEnabled(true);

        OnViewSelectionListener<NotePlayingEvent> onSelectionListener = new InnerOnSelectionListener();
        fragment.getFretView().registerListener(onSelectionListener);
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

                    /* clean either touch of FFT drawn layer, show what was correctly submitted by the user till now */
                    List<Position> tmp = new ArrayList<Position>();
                    tmp.addAll(submittedPositions);
                    fragment.getFretView().show(layerLessonSubmited, tmp);
                    fragment.getFretView().clearLayerByZIndex(FretView.LAYER_Z_TOUCHES);
                    fragment.getFretView().clearLayerByZIndex(FretView.LAYER_Z_FFT);

                    /* all positions from users are new */
                    if (ArrayUtils.isEqual(expectedPositions, submittedPositions)) {
                        onSuccess(npEvent.userInputMethod, expectedPositions.size());
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

}
