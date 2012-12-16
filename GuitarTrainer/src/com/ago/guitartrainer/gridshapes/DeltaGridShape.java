package com.ago.guitartrainer.gridshapes;

import com.ago.guitartrainer.notation.Degree;
import com.ago.guitartrainer.notation.Note;

public class DeltaGridShape extends GridShape {

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

    DeltaGridShape(Note key) {
        super(zeroFretDegrees, frets, rootStrings, key);
    }

    DeltaGridShape(int suggestedFret) {
        super(zeroFretDegrees, frets, rootStrings, suggestedFret);
    }
}
