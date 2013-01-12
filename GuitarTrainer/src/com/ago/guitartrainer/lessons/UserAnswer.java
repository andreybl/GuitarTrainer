package com.ago.guitartrainer.lessons;

import com.ago.guitartrainer.notation.Position;

/**
 * An answer proposed by the user as response to the Question and its parameters.
 * 
 * The answer is expressed in the way, that it can be compared to the expected answer. For instance, if we expect single
 * fret position as user input, the UserAnswer must contain only single position. The UserAnswer is prepared by the
 * AnswerEvaluator and is used to compare it with ExpectedAnswer
 * 
 * @author Andrej Golovko - jambit GmbH
 * 
 */
public class UserAnswer {

    /** original question */
    private QuestionParams question;

    /**
     * the answer of the user, must be compared with Question#expectedPosition to decide on its position
     */
    private Position position;

    /**
     * delay with which the question was answered correctly. {@link Long#MAX_VALUE} means the question was not answered
     * correctly at all.
     * 
     * */
    private long answerDelay = Long.MAX_VALUE;
}
