package com.ago.guitartrainer.lessons;

/**
 * The interface is to be implemented by a lesson.
 * 
 * The lesson can be considered as a controller, which use the UI views according to the logic of the lesson.
 * 
 * */
public interface ILesson {

    /**
     * Returns title of the lesson class, used for instance in the lessons selection dialog.
     * 
     * The title are expected to be unique for each class of the lesson and should reflect in short, which kind of
     * questions are asked in the lesson.
     * 
     * The title is also supposed to be short enough to be easily placed even into small widgets.
     * 
     * @return
     */
    public String getTitle();

    /**
     * Returns total duration of the lesson as accumulated through all loops of the lesson.
     * 
     * @return
     */
    public long getDuration();

    /**
     * Initializes UI elements, which are required by the lesson.
     * 
     * There different kinds of widgets required for the lesson. But at least widgets for presenting the question on one
     * side and the widgets for inputing the answers by the user on the other side are the must.
     */
    public void prepareUi();

    /**
     * Skip to the next question of the lesson.
     * 
     * The method is also used to start the lesson.
     */
    public void next();

    /**
     * Stops the lesson.
     * 
     */
    public void stop();

    /**
     * Show metrics associated with the lesson which represent the user progress.
     * 
     */
    public void showMetrics();
}
