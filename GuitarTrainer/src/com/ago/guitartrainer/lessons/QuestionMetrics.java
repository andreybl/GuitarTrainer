package com.ago.guitartrainer.lessons;

import java.util.Date;

import com.ago.guitartrainer.GuitarTrainerApplication;
import com.ago.guitartrainer.SettingsActivity;
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
 * @see http://www.supermemo.com/english/ol/sm2.htm
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

    @DatabaseField
    private Date nextLapTime;

    /**
     * The E-Factor - easiness factor - as described in SM2 learning algorithm.
     * 
     * 
     * 
     * E-Factors is allowed to vary between 1.1 for the most difficult items and 2.5 for the easiest ones. At the moment
     * of introducing an item into a SuperMemo database, its E-Factor was assumed to equal 2.5. In the course of
     * repetitions this value was gradually decreased in case of recall problems. Thus the greater problems an item
     * caused in recall, the more significant was the decrease of its E-Factor.
     * 
     * The eFactor is calculated with: eFactor':=eFactor+(0.1-(5-q)*(0.08+(5-q)*0.02)), where q is a quality of the
     * response in range 0..5. The q is reflected with {@link QualityOfAnswer} in our case.
     * 
     * The item must be repeated until the eFactor is at least 4.
     */
    @DatabaseField
    private double eFactor = 2.5;

    // @formatter:off
    /**
     * Inter-repetition interval after the n-th repetition (in days).
     *
     * Calculation of the interval is done with formula:
     *      I(1):=1 
     *      I(2):=6 
     *      for n>2 I(n):=I(n-1)*eFactor
     * 
     */
    //@formatter:on
    private int iRepetitionIntervalInDays = 1;

    /**
     * Counter for successful answers.
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

    public int getId() {
        return id;
    }

    /**
     * The true is set, only if the eFactor of the next lap was calcualted already.
     */
    // private boolean isEFactorUpdated = false;

    /**
     * Submit the time required by the user to answer the question.
     * 
     * The parameter {@link QualityOfAnswer} depends strongly on the input method and so must be provided from the
     * fragment which is in charge of input.
     * 
     * Not only correct, but also incorrect answers are taken into account. We also account for incorrect answer, when
     * the user skips the question without trying to answer it, like it is the case when clicking on the "Next" button.
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
    // TODO: add method liek submitAnswer(isSuccess) to also submit failures, e.g. where no numOfInputs is required
    public void submitAnswer(UserInputMethod userInputMethod, int numberOfInputs, boolean isSuccess) {

        if (isClosed())
            return;

        // calculate quality of answer
        String key = SettingsActivity.KEY_USERINPUTMETHOD_prefix + "_" + userInputMethod;
        long bestMotionTime = GuitarTrainerApplication.getPrefs().getLong(key, 4000);
        QualityOfAnswer qualityOfAnswer = null;

        long currentTime = System.currentTimeMillis();

        if (isSuccess) {

            /*
             * the question is done, we update metrics for it and prohibit any submission to the question
             */

            duration = currentTime - startedAt;

            avgSuccessfulAnswerTimeLastLoop = (avgSuccessfulAnswerTimeLastLoop * numOfSuccessfulAnswersLastLoop + duration)
                    / (numOfSuccessfulAnswersLastLoop + 1);

            numOfSuccessfulAnswersLastLoop++;
            numOfSuccessfulAnswers++;

            tstOfLatestSuccessfulAnswer = currentTime;

            // TODO: do something with loop and non-loop metrics
            avgSuccessfulAnswerTime = avgSuccessfulAnswerTimeLastLoop;

            qualityOfAnswer = QualityOfAnswer.mapToQuality(bestMotionTime, numberOfInputs, duration);

        } else {

            numOfFailedAnswersLastLoop++;
            numOfFailedAnswers++;

            // TODO: actually, "blackout" is only when we go to the next question with next()
            // qualityOfAnswer = QualityOfAnswer.BLACKOUT; so we need submitBlackout() method
        }

        if (isSuccess && qualityOfAnswer != null) {
            Date now = new Date(currentTime);
            if (nextLapTime == null || nextLapTime.before(now)) {

                if (qualityOfAnswer != QualityOfAnswer.UNDEFINED) {

                    eFactor = eFactor - 0.8 + 0.28 * qualityOfAnswer.value - 0.02 * qualityOfAnswer.value
                            * qualityOfAnswer.value;

                    if (iRepetitionIntervalInDays == 1)
                        iRepetitionIntervalInDays = 1;
                    else if (iRepetitionIntervalInDays == 2)
                        iRepetitionIntervalInDays = 6;
                    else
                        iRepetitionIntervalInDays = (int) Math.round(iRepetitionIntervalInDays * eFactor);

                    // TODO: temporally disabled
                    // long iRepetitionIntervalInMs = iRepetitionIntervalInDays * 24 * 60 * 60 * 1000;
                    // nextLapTime = new Date(now.getTime() + iRepetitionIntervalInMs);
                }
            }
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

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + getId() + "]" + ":eFactor=" + eFactor + ";nextLapTime=" + nextLapTime
                + ";avgSuccessfulAnswerTime=" + avgSuccessfulAnswerTime;
    }
}