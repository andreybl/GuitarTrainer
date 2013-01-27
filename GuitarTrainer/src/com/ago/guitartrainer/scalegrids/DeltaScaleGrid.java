package com.ago.guitartrainer.scalegrids;

import com.ago.guitartrainer.notation.Degree;
import com.ago.guitartrainer.notation.Note;

public class DeltaScaleGrid extends ScaleGrid {

    //@formatter:off
    private static Degree[] zeroFretDegrees = new Degree[] { 
        Degree.SEVEN, 
        Degree.FOUR_HALF, 
        Degree.TWO, 
        Degree.SIX,
        Degree.THREE, 
        Degree.SEVEN };
    //@formatter:on

    private static int frets = 4;

    private static int[] rootStrings = new int[] { 0, 3 };

    DeltaScaleGrid(Note key) {
        super(zeroFretDegrees, frets, rootStrings, key);
    }

    DeltaScaleGrid(int suggestedFret) {
        super(zeroFretDegrees, frets, rootStrings, suggestedFret);
    }
}
