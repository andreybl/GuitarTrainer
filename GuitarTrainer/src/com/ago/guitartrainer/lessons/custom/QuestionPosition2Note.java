package com.ago.guitartrainer.lessons.custom;

import com.ago.guitartrainer.lessons.AQuestion;
import com.ago.guitartrainer.lessons.QuestionMetrics;
import com.j256.ormlite.field.DatabaseField;

public class QuestionPosition2Note extends AQuestion {

    @DatabaseField(generatedId = true)
    private int id;

    /* TODO: what if we move it to AQuesion? together with get-method */
    @DatabaseField(canBeNull = false, foreign = true)
    private QuestionMetrics qMetric;
    
    @DatabaseField
    public int fret;
    
    @DatabaseField
    public int string;

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
