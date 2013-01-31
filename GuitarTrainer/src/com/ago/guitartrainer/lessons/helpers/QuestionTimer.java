package com.ago.guitartrainer.lessons.helpers;

import android.os.CountDownTimer;

import com.ago.guitartrainer.lessons.ILesson;
import com.ago.guitartrainer.ui.LearningStatusView;

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
public class QuestionTimer extends CountDownTimer {

    private ILesson lesson;

    private LearningStatusView learningStatusView;

    public QuestionTimer(ILesson lesson, LearningStatusView lsv, long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        this.lesson = lesson;
        this.learningStatusView = lsv;

    }

    @Override
    public void onFinish() {
        lesson.next();

    }

    @Override
    public void onTick(long millisUntilFinished) {
        learningStatusView.updateCurrentQuestionDuration(millisUntilFinished);
    }

}