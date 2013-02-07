package com.ago.guitartrainer.lessons.custom;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.util.Log;

import com.ago.guitartrainer.GuitarTrainerApplication;
import com.ago.guitartrainer.MasterActivity;
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
import com.j256.ormlite.dao.RuntimeExceptionDao;

public abstract class ALesson implements ILesson {

    private static final int UNDEFINED = 0;

    private static final boolean IS_SUCCESS = true;

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
    protected QuestionMetrics currentQuestionMetrics;

    protected RuntimeExceptionDao<QuestionMetrics, Integer> qmDao = DatabaseHelper.getInstance()
            .getRuntimeExceptionDao(QuestionMetrics.class);

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
        getLearningStatusView().updateMessageToUser(msg);
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

            RuntimeExceptionDao<LessonMetrics, Integer> lmDao = DatabaseHelper.getInstance().getRuntimeExceptionDao(
                    LessonMetrics.class);

            lessonMetrics = resolveOrCreateLessonMetrics(lmDao, this.getClass());
            if (lessonMetrics.getId() == 0)
                lmDao.create(lessonMetrics);
        }

        if (lessonMetrics.isFinished()) {
            lessonMetrics.startTime();

            int currentLoop = lessonMetrics.increaseLoop();
            if (getLearningStatusView() != null)
                getLearningStatusView().updateLessonLoop(currentLoop);

        }

        /* 3. update the learning status view */
        getLearningStatusView().updateCurrentLessonDuration(lessonMetrics.currentDuration());
        getLearningStatusView().updateAnswerStatus(QuestionStatus.UNDEFINED);
        getLearningStatusView().updateNextQuestionIndication(UNDEFINED);
        int qCounter = lessonMetrics.increaseQuestionsCounter();
        getLearningStatusView().updateQuestionsCounter(qCounter);

        /* 4. highly custom: decide on question, show it to the user */
        doNext();

        /*
         * 5. resolve the question metrics object, and start the measurement with its help. Be sure it is done _after_
         * the question was asked
         */
        AQuestion currentQuestion = getCurrentQuestion();
        if (currentQuestion != null) {
            currentQuestionMetrics = resolveOrCreateQuestionMetrics(currentQuestion.getId());
        } else {
            currentQuestionMetrics = new QuestionMetrics();
        }
        currentQuestionMetrics.start();

        /* 6. start the question timer */
        int questionMaxDurationSec = GuitarTrainerApplication.getPrefs().getInt(
                SettingsActivity.KEY_QUESTION_DURATION_MAX, 10);
        questionTimer = new QuestionTimer(this, getLearningStatusView(), questionMaxDurationSec * 1000, 300);
        questionTimer.start();

        if (getCurrentQuestion() != null) {
            Log.d(getTag(), getCurrentQuestion().toString());
        }
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

    private LessonMetrics resolveOrCreateLessonMetrics(RuntimeExceptionDao<LessonMetrics, Integer> dao,
            Class<? extends ALesson> clazz) {
        LessonMetrics obj = null;
        try {
            // resolve question
            List<LessonMetrics> results = dao.queryBuilder().where().eq("lessonClazz", clazz.getSimpleName()).query();

            if (results.size() == 0) {
                obj = new LessonMetrics();
                obj.lessonClazz = clazz.getSimpleName();
            } else if (results.size() == 1) {
                obj = results.get(0);
            } else {
                throw new RuntimeException("The object is not unique: " + LessonMetrics.class.getSimpleName());
            }

        } catch (SQLException e) {
            Log.e(getTag(), e.getMessage(), e);
        }

        return obj;
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

    @Override
    public void stop() {

        // if (lessonMetrics == null) {
        // /* the lessonMetrics is null, only if the lesson was selected but never started. */
        // return;
        // }

        lessonMetrics.stopTime();
        RuntimeExceptionDao<LessonMetrics, Integer> lmDao = DatabaseHelper.getInstance().getRuntimeExceptionDao(
                LessonMetrics.class);
        lmDao.update(lessonMetrics);

        if (pauseTimer != null)
            pauseTimer.cancel();

        questionTimer.cancel();

        currentQuestionMetrics.submitAnswer(false);

        getLearningStatusView().updateMessageToUser(null);

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

        getLearningStatusView().updateTimestampOfLastSuccessfulAnswer(currentQuestionMetrics.lastSuccessfulAnswer());
        getLearningStatusView().updateAnswerStatus(QuestionStatus.SUCCESS);
        getLearningStatusView().updateCurrentQuestionTrials(currentQuestionMetrics.numOfTrialsLastLoop());

        final int pauseDuration = GuitarTrainerApplication.getPrefs().getInt(
                SettingsActivity.KEY_POST_QUESTION_PAUSE_DURATION, 5);
        if (pauseDuration > 0) {
            MasterActivity.getInstance().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    pauseTimer = new PauseTimer(ALesson.this, getLearningStatusView(), pauseDuration * 1000, 1000);
                    pauseTimer.start();
                }
            });

        } else {
            next();
        }

    }

    protected boolean isLessonRunning() {
        boolean isRunning = (lessonMetrics != null) && !lessonMetrics.isFinished();

        return isRunning;
    }

    /**
     * Must be called by the sub-class, when the question is answered <code>incorrectly</code>.
     */
    protected void onFailure() {
        currentQuestionMetrics.submitAnswer(!IS_SUCCESS);

        qmDao.update(currentQuestionMetrics);

        getLearningStatusView().updateAnswerStatus(QuestionStatus.FAILURE);
        getLearningStatusView().updateCurrentQuestionTrials(currentQuestionMetrics.numOfTrialsLastLoop());
    }

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
        boolean doVibrate = GuitarTrainerApplication.getPrefs().getBoolean(SettingsActivity.KEY_PLAY_VIBRATIONS, true);

        if (!doVibrate)
            return;

        Vibrator vibratorService = (Vibrator) MasterActivity.getInstance().getSystemService(Context.VIBRATOR_SERVICE);
        vibratorService.vibrate(200);

    }

    @Override
    public boolean isRunning() {
        if (lessonMetrics == null)
            return false;

        return !lessonMetrics.isFinished();
    }

    /**
     * Vibrate in pattern, when the answer of user is completed. E.g. the question was answered successfully and we can
     * go to the next question.
     */
    protected void vibrateYesAndCompleted() {

        boolean doVibrate = GuitarTrainerApplication.getPrefs().getBoolean(SettingsActivity.KEY_PLAY_VIBRATIONS, true);

        if (!doVibrate)
            return;

        Vibrator vibratorService = (Vibrator) MasterActivity.getInstance().getSystemService(Context.VIBRATOR_SERVICE);
        vibratorService.vibrate(300);

        // This example will cause the phone to vibrate in Morse Code
        int dot = 200; // Length of a Morse Code "dot" in milliseconds
        int dash = 500; // Length of a Morse Code "dash" in milliseconds
        int short_gap = 100; // Length of Gap Between dots/dashes
        int medium_gap = 500; // Length of Gap Between Letters
        int long_gap = 1000; // Length of Gap Between Words
        long[] pattern = { 0, // Start immediately
                dot, short_gap, dot // s
        };

        // Only perform this pattern one time (-1 means "do not repeat")
        vibratorService.vibrate(pattern, -1);

    }

    public abstract void doNext();

    public abstract void doStop();

    protected abstract LearningStatusView getLearningStatusView();
}
