package com.ago.guitartrainer.lessons;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;

/**
 * Metrics associated with the question, or speaking more precisely: with QuestionParams.
 * 
 * The metrics reflect _all_ answers of the user associated with the current QuestionParams. Nevertheless some metrics
 * from the last user answer could be collected here as well, like:
 * 
 * <ul>
 * <li>last correct response timestamp
 * <li>last loop, in which the question was considered
 * </ul>
 * 
 * We can also save:
 * <ul>
 * <li>total number of answers
 * <li>total number of correct answers
 * </ul>
 * 
 * @author Andrej Golovko - jambit GmbH
 * 
 */
public class QuestionMetrics {

    @DatabaseField(generatedId = true)
    int id;

    /** id of the question, to which the metrics belongs */
    /*-
     * Note: it would be possible to code the question here, like:
     *          B:3:II:recognizedegree
     * which could mean: shape B, projection from position 3, degree II. The "recognizedegree" define 
     * the logic used to evaluate answer for correctness. For instance, it make difference whether the 
     * user must touch the screen to locate the degree, or play it on the fret.
     * But it would prevent us from searching over questions itself. 
     */
    int questionId;

    /** how often the question was asked */
    int askedCounter;

    /** how long did it take in average to answer to the question _correctly_ */
    long avgAnswerTime;

    /** the time when the question was asked */
    Date lastQuestioned;
}
