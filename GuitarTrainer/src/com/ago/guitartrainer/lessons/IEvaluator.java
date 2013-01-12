package com.ago.guitartrainer.lessons;

/**
 * An interface which must be evaluated by the engine generating questions.
 * 
 *  The learning session consists of the following element:
 *  <ul>
 *  <li> the evaluator pick the next question as {@link QuestionParams} to be presented to the user  
 *  <li> the question is presented to the user in some way
 *  <li> the user suggest an answer to the question either by manipulating UI controls or by playing on the guitar
 *  <li> the 
 *  </ul>
 * 
 * @author Andrej Golovko - jambit GmbH
 *
 */
public interface IEvaluator {

    public QuestionParams nextQuestion();

    public QuestionParams startQuestion(QuestionParams qp);

    /**
     * Suggest answer to the question as proposed by the user. Returns whether the question was accepted by the
     * evaluator. 
     * 
     * @param qp question which must be answered
     * @param ua answer proposed by the user
     * @return
     */
    public boolean suggestAnswer(QuestionParams qp, UserAnswer ua);
}
