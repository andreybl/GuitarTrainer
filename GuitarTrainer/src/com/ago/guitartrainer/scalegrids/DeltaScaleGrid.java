package com.ago.guitartrainer.scalegrids;

import com.ago.guitartrainer.notation.Degree;
import com.ago.guitartrainer.notation.Note;
import com.ago.guitartrainer.scalegrids.ScaleGrid.Type;

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

    private static ScaleGrid.Type scaleGridType = Type.DELTA;

    private static int[] rootStrings = new int[] { 0, 3 };

    DeltaScaleGrid(Note key) {
        super(zeroFretDegrees, scaleGridType.numOfFrets(), rootStrings, key);
    }

    DeltaScaleGrid(int suggestedFret) {
        super(zeroFretDegrees, scaleGridType.numOfFrets(), rootStrings, suggestedFret);
    }
}
