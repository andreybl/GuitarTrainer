package com.ago.guitartrainer.lessons.custom;

import com.ago.guitartrainer.lessons.ILesson;

public abstract class ALesson implements ILesson {

    
    public String getTag() {
        return "GT-" + getClass().getSimpleName();
    }
    
    @Override
    public long getDuration() {
        // TODO Auto-generated method stub
        return 123;
    }
    

}
