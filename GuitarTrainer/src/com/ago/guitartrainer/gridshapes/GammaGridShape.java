package com.ago.guitartrainer.gridshapes;

import com.ago.guitartrainer.notation.Degree;
import com.ago.guitartrainer.notation.Note;

public class GammaGridShape extends GridShape {

    //@formatter:off
    private static Degree[] zeroFretDegrees = new Degree[] { 
        Degree.FIVE_HALF, 
        Degree.TWO_HALF, 
        Degree.SEVEN, 
        Degree.FOUR_HALF,
        Degree.ONE_HALF, 
        Degree.FIVE_HALF };
    //@formatter:on

    private static int frets = 5;

    private static int[] rootStrings = new int[] { 2, 5 };

    GammaGridShape(Note key) {
        super(zeroFretDegrees, frets, rootStrings, key);
    }

    GammaGridShape(int suggestedFret) {
        super(zeroFretDegrees, frets, rootStrings, suggestedFret);
    }
}
