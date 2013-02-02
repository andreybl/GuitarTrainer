package com.ago.guitartrainer.scalegrids;

import com.ago.guitartrainer.notation.Degree;
import com.ago.guitartrainer.notation.Note;

public class AlphaScaleGrid extends ScaleGrid {

    //@formatter:off
    private static Degree[] zeroFretDegrees = new Degree[] { 
        Degree.THREE, 
        Degree.SEVEN, 
        Degree.FIVE, 
        Degree.TWO,
        Degree.SIX, 
        Degree.THREE };
    //@formatter:on

    private static ScaleGrid.Type scaleGridType = Type.ALPHA;

    private static int[] rootStrings = new int[] { 1, 4 };

    AlphaScaleGrid(Note key) {
        super(zeroFretDegrees, scaleGridType.numOfFrets(), rootStrings, key);
    }

    AlphaScaleGrid(int suggestedFret) {
        super(zeroFretDegrees, scaleGridType.numOfFrets(), rootStrings, suggestedFret);
    }

}
