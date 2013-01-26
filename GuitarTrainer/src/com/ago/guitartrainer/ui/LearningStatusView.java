package com.ago.guitartrainer.ui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Context;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import com.ago.guitartrainer.R;
import com.ago.guitartrainer.lessons.AQuestion.QuestionStatus;
import com.ago.guitartrainer.utils.TimeUtils;

public class LearningStatusView extends GridLayout {

    private TextView currLessonDuration;

    private TextView currQuestionCounter;

    private TextView currQuestionDuration;

    private TextView currQuestionTrials;

    private TextView avgTotalSuccessAnswerDuration;

    private TextView lastSuccessAnswerTimestamp;

    private TextView lastAnswerStatus;

    private TextView nextQuestionIndicator;
    
    private TextView lessonName;

    /**
     * Formatting used to represent the fields reflecting duration of any kind.
     * */
    private SimpleDateFormat durationFormat = new SimpleDateFormat("mm''ss'\"'SSS");
    

    /** context of the view, used mainly for {@link Activity#runOnUiThread(Runnable)} calls */
    private Activity ctx = (Activity) getContext();

    public LearningStatusView(Context context) {
        super(context);

        init();
    }

    public LearningStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public LearningStatusView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    private void init() {
        View mainLayout = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.learningstatus_view, this,
                true);

        currLessonDuration = (TextView) mainLayout.findViewById(R.id.current_lesson_duration);
        currQuestionCounter = (TextView) mainLayout.findViewById(R.id.current_questions_counter);
        currQuestionDuration = (TextView) mainLayout.findViewById(R.id.current_question_duration);
        currQuestionTrials = (TextView) mainLayout.findViewById(R.id.current_question_trials);
        avgTotalSuccessAnswerDuration = (TextView) mainLayout.findViewById(R.id.total_qsuccess_duration);
        lastSuccessAnswerTimestamp = (TextView) mainLayout.findViewById(R.id.last_qsuccess_timestamp);
        lastAnswerStatus = (TextView) mainLayout.findViewById(R.id.current_question_successfailure);
        nextQuestionIndicator = (TextView) mainLayout.findViewById(R.id.next_question_indicator);
        
        lessonName = (TextView) mainLayout.findViewById(R.id.current_lesson_name);
        

    }

    /**
     * Update field, which shown name of the currently selected lesson. 
     * 
     * @param str name of the currently selected lesson
     */
    public void updateLessonName(String str) {
        ctx.runOnUiThread(new TextViewRunnable(lessonName, str));
    }
    
    /**
     * Update field, informing about time elapsed since the last start of the running lesson.
     * 
     * */
    public void updateCurrentLessonDuration(long lessonDuration) {
        String str = formatTime(lessonDuration);
        ctx.runOnUiThread(new TextViewRunnable(currLessonDuration, str));
    }

    /**
     * 
     * Update field, informing about the current number of question being presented to user.
     * 
     * */
    public void updateQuestionsCounter(int lessonCounter) {
        ctx.runOnUiThread(new TextViewRunnable(currQuestionCounter, String.valueOf(lessonCounter)));
    }

    /**
     * Update field, informing how much time the current question took already.
     * 
     * @param duration
     *            of the current lesson
     */
    public void updateCurrentQuestionDuration(long duration) {
        String str = formatTime(duration);
        ctx.runOnUiThread(new TextViewRunnable(currQuestionDuration, str));
    }

    /**
     * Update field, informing about number of trials user did to answer the running question.
     * 
     * @param trials
     *            counter to answer the question
     */
    public void updateCurrentQuestionTrials(int trials) {
        ctx.runOnUiThread(new TextViewRunnable(currQuestionTrials, String.valueOf(trials)));
    }

    /**
     * Update filed, informing about average time required by the user to answer successfully the running question.
     * 
     * @param avgDuration
     *            time in milliseconds
     */
    public void updateAvgAnswerDuration(long avgDuration) {
        String str = formatTime(avgDuration);
        ctx.runOnUiThread(new TextViewRunnable(avgTotalSuccessAnswerDuration, str));
    }

    /**
     * Update filed, informing about last time outside of the current loop, when the question was answered successfully.
     * 
     * @param avgDuration
     *            time in milliseconds
     */
    public void updateTimestampOfLastSuccessfulAnswer(long timestamp) {
        String str = TimeUtils.formatTimeAgo(timestamp, System.currentTimeMillis());
        ctx.runOnUiThread(new TextViewRunnable(lastSuccessAnswerTimestamp, str));
    }

    public void updateAnswerStatus(QuestionStatus qStatus) {
        if (qStatus == QuestionStatus.SUCCESS) {
            ctx.runOnUiThread(new TextViewRunnable(lastAnswerStatus, "SUCCESS", R.color.green));
        } else if (qStatus == QuestionStatus.FAILURE) {
            ctx.runOnUiThread(new TextViewRunnable(lastAnswerStatus, "FAILED", R.color.red));
        } else {
            String str = getResources().getString(R.string.undefined);
            ctx.runOnUiThread(new TextViewRunnable(lastAnswerStatus, str, R.color.black));
        }
    }

    /**
     * Update field, which shows the current lesson loop
     * 
     * @param loop
     *            current loop of the lesson
     */
    public void updateLessonLoop(int loop) {
        // TODO: implement
    }

    public void updateNextQuestionIndication(long pauseTimeLeft) {
        String str = formatTime(pauseTimeLeft);

        if (pauseTimeLeft <= 0)
            str = getResources().getString(R.string.undefined);

        ctx.runOnUiThread(new TextViewRunnable(nextQuestionIndicator, str));
    }

    /**
     * Format time passed in milliseconds as parameter.
     * 
     * Example:
     * <ul>
     * <li>40 is 040
     * <li>500 is 500
     * <li>2000 is 02"
     * <li>2500 is 02"500
     * <li>60000 is 01'
     * <li>63000 is 01'03"
     * <li>63040 is 01'03"040
     * </ul>
     * 
     * @param lessonDuration
     *            duration of the lesson
     * @return
     */
    private String formatTime(long lessonDuration) {
        String str = durationFormat.format(new Date(lessonDuration));
        
        return str;
    }


    /*
     * ***** INNER CLASSES
     */

    /**
     * 
     * Runnable, which can be used to set the text of a {@link TextView}.
     * 
     * Both TextView and its text must be passed as constructor parameters.
     * 
     * */
    private class TextViewRunnable implements Runnable {

        private TextView tv;
        private String text;
        private int rColorId = 0;

        TextViewRunnable(TextView tv, String text) {
            this.tv = tv;
            this.text = text;
        }

        /**
         * The runnable to update text view, with text and background color passed as constructor parameters.
         * 
         * @param tv
         *            text view to update
         * @param text
         *            to be used to update the text view
         * @param rColorId
         *            color as defined with R.color.xyz
         */
        TextViewRunnable(TextView tv, String text, int rColorId) {
            this.tv = tv;
            this.text = text;
            this.rColorId = rColorId;
        }

        @Override
        public void run() {
            tv.setText(String.valueOf(text));

            if (rColorId != 0) {
                int colorId = getResources().getColor(rColorId);
                tv.setBackgroundColor(colorId);
            }
        }
    }

}
