package com.ago.guitartrainer.lessons.custom;

import com.ago.guitartrainer.lessons.ILesson;

public abstract class ALesson implements ILesson {

    /** counts the lessons */
    private int counter = 0;
    
    public String getTag() {
        return "GT-" + getClass().getSimpleName();
    }
    
    @Override
    public long getDuration() {
        // TODO Auto-generated method stub
        return 123;
    }
    
    public int increaseCounter() {
        counter++;
        return counter;
    }
    
    public int counter(){
        return counter;
    }
}
