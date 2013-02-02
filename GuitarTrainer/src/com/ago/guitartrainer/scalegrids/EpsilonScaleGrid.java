package com.ago.guitartrainer.scalegrids;

import com.ago.guitartrainer.notation.Degree;
import com.ago.guitartrainer.notation.Note;
import com.ago.guitartrainer.scalegrids.ScaleGrid.Type;

public class EpsilonScaleGrid extends ScaleGrid {

    //@formatter:off
    private static Degree[] zeroFretDegrees = new Degree[] { 
        Degree.ONE_HALF, 
        Degree.FIVE_HALF, 
        Degree.THREE, 
        Degree.SEVEN,
        Degree.FOUR_HALF, 
        Degree.ONE_HALF };
    //@formatter:on

    private static ScaleGrid.Type scaleGridType = Type.EPSILON;

    private static int[] rootStrings = new int[] { 1, 3 };

    EpsilonScaleGrid(Note key) {
        super(zeroFretDegrees, scaleGridType.numOfFrets(), rootStrings, key);
    }
    
    EpsilonScaleGrid(int suggestedFret) {
        super(zeroFretDegrees, scaleGridType.numOfFrets(), rootStrings, suggestedFret);
    }

}
