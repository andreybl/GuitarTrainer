package com.ago.guitartrainer.lessons;

/**
 * The interface is to be implemented by a lesson.
 * 
 * The lesson can be considered as a controller, which use the UI views according to the logic of the lesson.
 * 
 * */
public interface ILesson {

    public String getTitle();

    public long getDuration();

    public void prepareUi();
    
    public void next();
    
    public void stop();

}
