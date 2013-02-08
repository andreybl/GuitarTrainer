package com.ago.guitartrainer.lessons;

// @formatter:off
/**
 * 
 * The quality of answer of the user. In original SM2 algo its levels are defined as:
 * 
 * 5 - perfect response
 * 4 - correct response after a hesitation
 * 3 - correct response recalled with serious difficulty
 * 2 - incorrect response; where the correct one seemed easy to recall
 * 1 - incorrect response; the correct one remembered
 * 0 - complete blackout.
 * 
 * @author Andrej Golovko - jambit GmbH
 *
 */
// @formatter:on
public enum QualityOfAnswer {
    UNDEFINED(-1), BLACKOUT(0), INSUFFICIENT(1), SUFFICIENT(2), SATISFACTORY(3), GOOD(4), EXCELLENT(5);

    public int value = -1;

    QualityOfAnswer(int val) {
        this.value = val;
    }

    /**
     * The constant used to calculate the intervals between single {@link QualityOfAnswer} values.
     * 
     */
    private static double interQualityConstant = 1.5;

    public static QualityOfAnswer mapToQuality(long bestMotionTime, int numberOfInputs, long dur) {
        /*
         * TODO: a and b constants which depend on task and subject conditions.
         * 
         * @see: http://www.etl-lab.eng.wayne.edu/adrc/Human_Factors/Hicks/Hicks_law.htm
         */
        int a = 4;
        int b = 1;

        /*-
         * we must map the user answer time to the QualityOfAnswer scale
         * 
         * bestMotionTime - is for case when single item must be selected. We must Hick's Law to it, to receive
         * calculated best time for the numberOfItems which must have been submitted.
         * 
         * RT = a + b*log2(n+1)
         * 
         * Further, we assume the resulting bestMotionTime to correspond to the EXCELLENT. 
         * The value bestMotionTime*1.5 corresponds to value GOOD and so on. 
         * The constant 1.5 could be changed. 
         * 
         * The answer time must fall into GOOD..EXCELLENT to be evaluated as EXCELLENT.
         */
        // TODO: not working!
//        double log2 = Math.log(numberOfInputs + 1) / Math.log(2);
//        double bestReactionTimePossible = bestMotionTime + b * log2 * 1000;
        double bestReactionTimePossible = bestMotionTime;

        // TODO: is it OK? visualize on paper!
        QualityOfAnswer qoa;
        if (dur < bestReactionTimePossible * interQualityConstant) {
            qoa = EXCELLENT;
        } else if (dur < bestReactionTimePossible * interQualityConstant * 2) {
            qoa = GOOD;
        } else if (dur < bestReactionTimePossible * interQualityConstant * 3) {
            qoa = SATISFACTORY;
        } else if (dur < bestReactionTimePossible * interQualityConstant * 4) {
            qoa = SUFFICIENT;
        } else if (dur < bestReactionTimePossible * interQualityConstant * 5) {
            qoa = INSUFFICIENT;
        } else {
            qoa = BLACKOUT;
        }

        return qoa;

    }
}
