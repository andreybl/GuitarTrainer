package com.ago.guitartrainer.lessons;

/**
 * The subclasses of this class holds parameters of specific questions as presented to the user.
 * 
 * The question contains:
 * <ul>
 * <li>the data which is presented to user with help of UI widgets
 * <li>the metrics associated with the question, like answer time for the question etc.
 * </ul>
 * 
 * For example, the question may be:
 * "for the given scale grid Alpha, starting at position 3, find the degree II on the fret". The parameters which must
 * be saved in subclass are:
 * <ul>
 * <li>scale grid, which is Alpha here
 * <li>fret position, which is 3 here
 * <li>degree to be shown by the user, which is II here
 * </ul>
 * 
 * Note: the answer is not explicitly saved in the {@link AQuestion}. The metrics associated with the question instance
 * are saved with {@link QuestionMetrics} instance.
 * 
 * Each instance of the IQuestion stays for the specific question like "Locate position for D3 on fret". But this
 * instance is questioned to the user multiple times. The metrics about these questionings are accumulated in the
 * IQustion.
 * 
 * @author Andrej Golovko - jambit GmbH
 * 
 * */
public abstract class AQuestion {
    
    public enum QuestionStatus {
        SUCCESS,
        
        FAILURE,
        
        /* initial status of the question, before the user even tried to answer it */
        UNDEFINED
    }

    
    // TODO use later for persistance, @DatabaseField(generatedId = true)
    // private int id;
    
    public abstract int getId();
    
    public abstract QuestionMetrics getMetrics();
    
    public abstract void setMetrics(QuestionMetrics metrics);

}
