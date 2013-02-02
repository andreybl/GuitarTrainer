package com.ago.guitartrainer.scalegrids;

import com.ago.guitartrainer.notation.Degree;
import com.ago.guitartrainer.notation.Note;

public class BetaScaleGrid extends ScaleGrid {

    //@formatter:off
    private static Degree[] zeroFretDegrees = new Degree[] { 
        Degree.FOUR_HALF, 
        Degree.ONE_HALF, 
        Degree.SIX, 
        Degree.THREE,
        Degree.SEVEN, 
        Degree.FOUR_HALF };
    //@formatter:on

    private static ScaleGrid.Type scaleGridType = Type.BETA;

    private static int[] rootStrings = new int[] { 2, 4 };

    BetaScaleGrid(Note key) {
        super(zeroFretDegrees, scaleGridType.numOfFrets(), rootStrings, key);
    }

    BetaScaleGrid(int suggestedFret) {
        super(zeroFretDegrees, scaleGridType.numOfFrets(), rootStrings, suggestedFret);
    }
}
