package com.ago.guitartrainer.lessons.custom;

import java.sql.SQLException;
import java.util.List;

import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;

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
        
        qmDao = 
                DatabaseHelper.getInstance().getRuntimeExceptionDao(QuestionMetrics.class);
        
        doPrepareUi();
    }

    protected abstract void doPrepareUi();

    public String getTag() {
        return "GT-" + getClass().getSimpleName();
    }

    @Override
    public long getDuration() {
        // TODO Auto-generated method stub
        return 123;
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
        currentQuestionMetrics = resolveQuestionMetrics(currentQuestion);
        currentQuestionMetrics.start();

        /* 6. start the question timer */
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainFragment.getInstance()
                .getActivity());
        int questionMaxDurationSec = sharedPref.getInt(SettingsActivity.KEY_QUESTION_DURATION_MAX, 10);
        questionTimer = new QuestionTimer(this, learningStatusView, questionMaxDurationSec * 1000, 300);
        questionTimer.start();

        Log.d(getTag(), getCurrentQuestion().toString());
    }

    protected QuestionMetrics resolveQuestionMetrics(AQuestion question) {
        QuestionMetrics qm = null;
        try {
            // resolve metrics for the question
            List<QuestionMetrics> qMetrics = qmDao.queryBuilder().where().idEq(question.getId()).query();
            if (qMetrics.size() == 0) {
                qm = new QuestionMetrics();
            } else if (qMetrics.size() == 1) {
                qm = qMetrics.get(0);
            } else {
                throw new RuntimeException("The question metrics object is not unique.");
            }

            // save both object to dB, if they did not exist before
            if (qm.getId() == 0) {
                qmDao.create(qm);
            }
        } catch (SQLException e) {
            Log.e(getTag(), e.getMessage(), e);
        }

        return qm;
    }

    public abstract void doNext();

    @Override
    public void stop() {

        lessonMetrics.stopTime();

        if (pauseTimer != null)
            pauseTimer.cancel();

        questionTimer.cancel();

        currentQuestionMetrics.submitAnswer(false);

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

        questionTimer.cancel();

        qmDao.update(currentQuestionMetrics);

        learningStatusView.updateTimestampOfLastSuccessfulAnswer(currentQuestionMetrics.lastSuccessfulAnswer());
        learningStatusView.updateAnswerStatus(QuestionStatus.SUCCESS);
        learningStatusView.updateCurrentQuestionTrials(currentQuestionMetrics.numOfTrialsLastLoop());

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainFragment.getInstance()
                .getActivity());
        int pauseDuration = sharedPref.getInt(SettingsActivity.KEY_POST_QUESTION_PAUSE_DURATION, 5);
        if (pauseDuration > 0) {
            pauseTimer = new PauseTimer(this, learningStatusView, pauseDuration * 1000, 1000);
            pauseTimer.start();
        } else {
            next();
        }

    }

    protected boolean isLessonRunning() {
        boolean isRunning = !currentQuestionMetrics.isClosed();

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

    protected abstract AQuestion getCurrentQuestion();

}
