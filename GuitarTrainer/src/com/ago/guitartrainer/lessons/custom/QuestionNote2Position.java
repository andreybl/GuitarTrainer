package com.ago.guitartrainer.lessons.custom;

import com.ago.guitartrainer.lessons.AQuestion;
import com.ago.guitartrainer.lessons.QuestionMetrics;
import com.ago.guitartrainer.notation.Note;
import com.j256.ormlite.field.DatabaseField;

public class QuestionNote2Position extends AQuestion {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false, foreign = true)
    private QuestionMetrics qMetric;
    
    @DatabaseField
    public Note note;
    
    @Override
    public int getId() {
        return id;
    }

    @Override
    public QuestionMetrics getMetrics() {
        return qMetric;
    }

    @Override
    public void setMetrics(QuestionMetrics metrics) {
        this.qMetric = metrics;
    }

}
