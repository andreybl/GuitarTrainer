package com.ago.guitartrainer.lessons.helpers;

import android.os.CountDownTimer;

import com.ago.guitartrainer.lessons.ILesson;
import com.ago.guitartrainer.ui.LearningStatusView;

/**
 * A count down class, used to pause before skipping to the next question after a successful answer to previous question
 * was provided.
 * 
 * Is usually use for didactical purposes to give the user time on reflections. But also is required so that the user
 * realize that the question was answered successfully and can prepared mentally to the next question.
 * 
 * @author Andrej Golovko - jambit GmbH
 * 
 */
public class PauseTimer extends CountDownTimer {

    private ILesson lesson;

    private LearningStatusView learningStatusView;

    public PauseTimer(ILesson lesson, LearningStatusView lsv, long millisInFuture, long countDownInterval) {
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
        learningStatusView.updateNextQuestionIndication(millisUntilFinished);
    }
}