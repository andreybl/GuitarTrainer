package com.ago.guitartrainer.lessons.custom;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.util.Log;

import com.ago.guitartrainer.GuitarTrainerApplication;
import com.ago.guitartrainer.SettingsActivity;
import com.ago.guitartrainer.db.DatabaseHelper;
import com.ago.guitartrainer.lessons.AQuestion;
import com.ago.guitartrainer.lessons.AQuestion.QuestionStatus;
import com.ago.guitartrainer.lessons.ILesson;
import com.ago.guitartrainer.lessons.LessonMetrics;
import com.ago.guitartrainer.lessons.QuestionMetrics;
import com.ago.guitartrainer.lessons.helpers.PauseTimer;
import com.ago.guitartrainer.lessons.helpers.QuestionTimer;
import com.ago.guitartrainer.ui.LearningStatusView;
import com.ago.guitartrainer.ui.MainFragment;
import com.j256.ormlite.dao.RuntimeExceptionDao;

public abstract class ALesson implements ILesson {

    private static final int UNDEFINED = 0;

    private static final boolean IS_SUCCESS = true;

    /** UI view to represent the lesson status to the user. Is a widget, common for all lessons. */
    private LearningStatusView learningStatusView;

    /**
     * Countdown during the question is asked.
     * 
     * Upon expire (finish() riched) the answer is considered to be "FAILED" and we skip to the next question.
     * 
     * */
    private CountDownTimer questionTimer;

    /**
     * Current question, which is present to the user.
     * 
     * To have the question be available here, the subclass must register it with
     * {@link #registerQuestion(RuntimeExceptionDao, AQuestion, QuestionMetrics)} call.
     * */
    private AQuestion currentQuestion;

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

    // TOODO: remove from class var?
    protected RuntimeExceptionDao<QuestionMetrics, Integer> qmDao;

    @Override
    public void prepareUi() {
        MainFragment uiControls = MainFragment.getInstance();
        learningStatusView = uiControls.getLearningStatusView();

        qmDao = DatabaseHelper.getInstance().getRuntimeExceptionDao(QuestionMetrics.class);

        doPrepareUi();
    }

    protected abstract void doPrepareUi();

    public String getTag() {
        return "GT-" + getClass().getSimpleName();
    }

    /**
     * Set free format message from lesson to be shown to the user in learning status view.
     * 
     * This is a way for lesson to give the user feedback about how he is doing. For instance, the question can inform
     * user that some more input is required from him.
     * 
     * @param msg
     *            to be shown in the learning status view
     */
    protected void messageInLearningStatus(String msg) {
        learningStatusView.updateMessageToUser(msg);
    }

    public void next() {

        /*-
         * 1. start the question countdown 
         *    and cancel the pause countdown which may have been started
         */
        if (questionTimer != null)
            questionTimer.cancel();

        if (pauseTimer != null)
            pauseTimer.cancel();

        /* 2. start lesson related metrics */
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

        /* 3. update the learning status view */
        learningStatusView.updateCurrentLessonDuration(lessonMetrics.currentDuration());
        learningStatusView.updateAnswerStatus(QuestionStatus.UNDEFINED);
        learningStatusView.updateNextQuestionIndication(UNDEFINED);
        int qCounter = lessonMetrics.increaseQuestionsCounter();
        learningStatusView.updateQuestionsCounter(qCounter);

        /* 4. highly custom: decide on question, show it to the user */
        doNext();

        /*
         * 5. resolve the question metrics object, and start the measurement with its help. Be sure it is done _after_
         * the question was asked
         */
        AQuestion currentQuestion = getCurrentQuestion();
        currentQuestionMetrics = resolveOrCreateQuestionMetrics(currentQuestion.getId());
        currentQuestionMetrics.start();

        /* 6. start the question timer */
        int questionMaxDurationSec = GuitarTrainerApplication.getPrefs().getInt(
                SettingsActivity.KEY_QUESTION_DURATION_MAX, 10);
        questionTimer = new QuestionTimer(this, learningStatusView, questionMaxDurationSec * 1000, 300);
        questionTimer.start();

        Log.d(getTag(), getCurrentQuestion().toString());
    }

    /**
     * Resolve question metrics or create a new one. The question metrics is not persisted to the dB.
     * 
     * @param question
     * @return
     */
    protected QuestionMetrics resolveOrCreateQuestionMetrics(int questionId) {
        QuestionMetrics qm = null;
        try {
            // resolve metrics for the question
            List<QuestionMetrics> qMetrics = qmDao.queryBuilder().where().idEq(questionId).query();
            if (qMetrics.size() == 0) {
                qm = new QuestionMetrics();
            } else if (qMetrics.size() == 1) {
                qm = qMetrics.get(0);
            } else {
                throw new RuntimeException("The question metrics object is not unique.");
            }

        } catch (SQLException e) {
            Log.e(getTag(), e.getMessage(), e);
        }

        return qm;
    }

    protected void registerQuestion(RuntimeExceptionDao qDao, AQuestion currentQuestion, QuestionMetrics qm) {
        if (qm.getId() == 0) {
            qmDao.create(qm);
        }
        if (currentQuestion.getId() == 0) {
            currentQuestion.setMetrics(qm);
            qDao.create(currentQuestion);
        }

        this.currentQuestion = currentQuestion;

    }

    public abstract void doNext();

    @Override
    public void stop() {

        lessonMetrics.stopTime();

        if (pauseTimer != null)
            pauseTimer.cancel();

        questionTimer.cancel();

        currentQuestionMetrics.submitAnswer(false);

        learningStatusView.updateMessageToUser(null);

        doStop();

        qmDao.update(currentQuestionMetrics);
    }

    /**
     * Must be called by the sub-class, when the question is answered <code>successfully</code>.
     * 
     * */
    protected void onSuccess() {
        /*
         * TODO: update metrics for question, persist it to db; update lesson metrics and persist. go to next question
         */
        currentQuestionMetrics.submitAnswer(IS_SUCCESS);

        vibrateYesAndCompleted();

        questionTimer.cancel();

        qmDao.update(currentQuestionMetrics);

        learningStatusView.updateTimestampOfLastSuccessfulAnswer(currentQuestionMetrics.lastSuccessfulAnswer());
        learningStatusView.updateAnswerStatus(QuestionStatus.SUCCESS);
        learningStatusView.updateCurrentQuestionTrials(currentQuestionMetrics.numOfTrialsLastLoop());

        int pauseDuration = GuitarTrainerApplication.getPrefs().getInt(
                SettingsActivity.KEY_POST_QUESTION_PAUSE_DURATION, 5);
        if (pauseDuration > 0) {
            pauseTimer = new PauseTimer(this, learningStatusView, pauseDuration * 1000, 1000);
            pauseTimer.start();
        } else {
            next();
        }

    }

    protected boolean isLessonRunning() {
        boolean isRunning = lessonMetrics == null || !lessonMetrics.isFinished();

        return isRunning;
    }

    /**
     * Must be called by the sub-class, when the question is answered <code>incorrectly</code>.
     */
    protected void onFailure() {
        currentQuestionMetrics.submitAnswer(!IS_SUCCESS);

        qmDao.update(currentQuestionMetrics);

        learningStatusView.updateAnswerStatus(QuestionStatus.FAILURE);
        learningStatusView.updateCurrentQuestionTrials(currentQuestionMetrics.numOfTrialsLastLoop());
    }

    public abstract void doStop();

    protected AQuestion getCurrentQuestion() {

        return this.currentQuestion;
    }

    @Override
    public LessonMetrics getLessonMetrics() {
        return lessonMetrics;
    }

    /**
     * Vibrates shortly, when the answer from user is correct, but not complete. E.g. additional user inputs are
     * expected.
     */
    protected void vibrateYesButUncompleted() {
        Vibrator vibratorService = (Vibrator) MainFragment.getInstance().getActivity()
                .getSystemService(Context.VIBRATOR_SERVICE);
        vibratorService.vibrate(200);

    }

    /**
     * Vibrate in pattern, when the answer of user is completed. E.g. the question was answered successfully and we can
     * go to the next question.
     */
    protected void vibrateYesAndCompleted() {
        Vibrator vibratorService = (Vibrator) MainFragment.getInstance().getActivity()
                .getSystemService(Context.VIBRATOR_SERVICE);
        vibratorService.vibrate(300);

        // This example will cause the phone to vibrate in Morse Code
        int dot = 200; // Length of a Morse Code "dot" in milliseconds
        int dash = 500; // Length of a Morse Code "dash" in milliseconds
        int short_gap = 200; // Length of Gap Between dots/dashes
        int medium_gap = 500; // Length of Gap Between Letters
        int long_gap = 1000; // Length of Gap Between Words
        long[] pattern = { 0, // Start immediately
                dot, short_gap, dot // s
        };

        // Only perform this pattern one time (-1 means "do not repeat")
        vibratorService.vibrate(pattern, -1);

    }
}
