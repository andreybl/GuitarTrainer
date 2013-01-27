package com.ago.guitartrainer.lessons.custom;

import com.ago.guitartrainer.lessons.AQuestion;
import com.ago.guitartrainer.notation.Degree;
import com.ago.guitartrainer.scalegrids.ScaleGrid;
import com.ago.guitartrainer.scalegrids.ScaleGrid.Type;
import com.j256.ormlite.field.DatabaseField;

public class QuestionScalegridDegree2Position extends AQuestion {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    public ScaleGrid.Type scaleGridType = Type.ALPHA;

    /** the starting position, at which the scale grid is shown. Fret position */
    @DatabaseField
    public int fretPosition = 0;

    @DatabaseField
    public Degree degree = Degree.ONE;

    @Override
    public String toString() {
        String str = this.getClass().getSimpleName() + "[" + id + "]";
        str += ": Scale Grid" + scaleGridType;
        str += ", Degree: " + degree;
        str += ", Fret Position: " + fretPosition;

        return str;
    }
}
