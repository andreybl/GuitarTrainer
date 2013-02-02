package com.ago.guitartrainer.scalegrids;

import com.ago.guitartrainer.notation.Degree;
import com.ago.guitartrainer.notation.Note;
import com.ago.guitartrainer.scalegrids.ScaleGrid.Type;

public class GammaScaleGrid extends ScaleGrid {

    //@formatter:off
    private static Degree[] zeroFretDegrees = new Degree[] { 
        Degree.FIVE_HALF, 
        Degree.TWO_HALF, 
        Degree.SEVEN, 
        Degree.FOUR_HALF,
        Degree.ONE_HALF, 
        Degree.FIVE_HALF };
    //@formatter:on

    private static ScaleGrid.Type scaleGridType = Type.GAMMA;

    private static int[] rootStrings = new int[] { 2, 5 };

    GammaScaleGrid(Note key) {
        super(zeroFretDegrees, scaleGridType.numOfFrets(), rootStrings, key);
    }

    GammaScaleGrid(int suggestedFret) {
        super(zeroFretDegrees, scaleGridType.numOfFrets(), rootStrings, suggestedFret);
    }
}
