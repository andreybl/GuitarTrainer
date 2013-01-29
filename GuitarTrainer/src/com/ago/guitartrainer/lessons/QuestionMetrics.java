package com.ago.guitartrainer.lessons;

import com.j256.ormlite.field.DatabaseField;

/**
 * Metrics associated with specific IQuestion instance.
 * 
 * We make difference between:
 * <ul>
 * <li>metrics for answering the question in the current loop
 * <li>total metrics of answering the question
 * </ul>
 * 
 * @author Andrej Golovko - jambit GmbH
 * 
 */
public class QuestionMetrics {

    @DatabaseField(generatedId = true)
    private int id;

    /** timestamp when the question was started */
    private long startedAt = 0;

    /** duration of the current question, eg. how much it took to answer the question successfully, in ms */
    private long duration = 0;

    /** avg time required by the user to successfully answer the question, in ms */
    @DatabaseField
    public long avgSuccessfulAnswerTime;

    /**
     * counter for successful answers.
     * 
     * Note that the difference (askedCounter-numOfSuccessfulAnswers) informs about number of times when the question
     * was skipped (e.g. user failed to answer the question).
     * */
    @DatabaseField
    private int numOfSuccessfulAnswers;

    @DatabaseField
    private int numOfFailedAnswers;

    /**
     * the timestamp of the latest successful answer to the question
     * */
    @DatabaseField
    private long tstOfLatestSuccessfulAnswer;

    /**
     * counter for successful answers in last loop
     * 
     * Note: the field is not persisted
     */
    private int numOfSuccessfulAnswersLastLoop = 0;

    
    /**
     * counter for failed answers in last loop
     * 
     * Note: the field is not persisted
     * */
    private int numOfFailedAnswersLastLoop = 0;

    /** avg time of successful answers in the last loop */
    private long avgSuccessfulAnswerTimeLastLoop;

    public int getId(){
        return id;
    }
    
    /**
     * Submit the time required by the user to answer the question.
     * 
     * Not only correct, but also incorrect answers are accounted. We also account for incorrect answer, when the user
     * skips the question without trying to answer it, like it is the case when clicking on the "Next" button.
     * 
     * The avg answer time for the question is calculated accumulatively with formula:
     * <p>
     * <img src="doc-files/accumulated-avg.png"/>
     * 
     * @param duration
     *            of the answer as took for the user, in ms
     * @param isSuccess
     *            indicates whether the answer was successful
     */
    public void submitAnswer(boolean isSuccess) {

        if (isClosed())
            return;

        if (isSuccess) {
            /*
             * the question is done, we update metrics for it and prohibit any submission to the question
             */

            long currentTime = System.currentTimeMillis();

            duration = currentTime - startedAt;

            avgSuccessfulAnswerTimeLastLoop = (avgSuccessfulAnswerTimeLastLoop * numOfSuccessfulAnswersLastLoop + duration)
                    / (numOfSuccessfulAnswersLastLoop + 1);
            
            numOfSuccessfulAnswersLastLoop++;
            numOfSuccessfulAnswers++;

            tstOfLatestSuccessfulAnswer = currentTime;

            // TODO: do something with loop and non-loop metrics
            avgSuccessfulAnswerTime = avgSuccessfulAnswerTimeLastLoop;
            
            // TODO: calc the avg value for total
        } else {

            numOfFailedAnswersLastLoop++;
            numOfFailedAnswers++;
        }

    }

    /**
     * How often the question was already asked in the current round of asking the question.
     * 
     * Count both failed and successful answers. The number of successful answers is accounted at most once in the
     * returned value.
     * 
     * @return number of times the question was asked
     */
    public int numOfTrials() {
        return numOfSuccessfulAnswers + numOfFailedAnswers;
    }

    public int numOfTrialsLastLoop() {
        return numOfSuccessfulAnswersLastLoop + numOfFailedAnswersLastLoop;
    }

    /**
     * Is called to indicate that the question is started.
     * 
     * In other words, after the call to start() the user is shown the question and is expected to provide answer.
     */
    public void start() {

        if (startedAt != 0)
            throw new RuntimeException("The question was already strted, the " + getClass().getName()
                    + "#startedAt != 0");

        startedAt = System.currentTimeMillis();
    }

    /**
     * Returns the duration of the question.
     * 
     * @return duration of question, in ms
     */
    public long duration() {
        if (duration != 0)
            return duration; // the question was accomplished already
        else if (startedAt == 0)
            return 0; // the question was not started yet
        else
            return System.currentTimeMillis() - startedAt; // the question is running
    }

    /*
     * TODO: tst is good, but must be from last loop
     */
    public long lastSuccessfulAnswer() {
        return tstOfLatestSuccessfulAnswer;
    }

    public boolean isClosed() {
        boolean isClosed = startedAt != 0 && duration != 0;
        return isClosed;
    }
}