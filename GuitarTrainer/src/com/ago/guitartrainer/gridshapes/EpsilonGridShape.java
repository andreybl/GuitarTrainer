package com.ago.guitartrainer.gridshapes;

import com.ago.guitartrainer.notation.Degree;
import com.ago.guitartrainer.notation.Note;

public class EpsilonGridShape extends GridShape {

    //@formatter:off
    private static Degree[] zeroFretDegrees = new Degree[] { 
        Degree.ONE_HALF, 
        Degree.FIVE_HALF, 
        Degree.THREE, 
        Degree.SEVEN,
        Degree.FOUR_HALF, 
        Degree.ONE_HALF };
    //@formatter:on

    private static int frets = 5;

    private static int[] rootStrings = new int[] { 1, 3 };

    EpsilonGridShape(Note key) {
        super(zeroFretDegrees, frets, rootStrings, key);
    }
    
    EpsilonGridShape(int suggestedFret) {
        super(zeroFretDegrees, frets, rootStrings, suggestedFret);
    }

}
