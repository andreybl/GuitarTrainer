package com.ago.guitartrainer.instruments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ago.guitartrainer.notation.Position;

public class GuitarUtils {

    public static int FRETS_ON_GUITAR = 12;

    /**
     * Calculate the area, in which the <code>position</code> is unique, e.g. all other positions from
     * <code>positions</code> are not in this area.
     * 
     * @param positions
     * @param position
     * @return
     */
    public static int[] calculateUniqueAreaForPosition(List<Position> positions, Position position) {
        List<Integer> frets = new ArrayList<Integer>();
        for (Position pos : positions) {
            frets.add(pos.getFret());
        }
        Collections.sort(frets);

        int startArea = 0;
        int endArea = 12;

        for (Integer fret : frets) {
            if (position.getFret() > fret && fret >= startArea) {
                startArea = fret + 1;
            } else if (position.getFret() < fret && fret <= endArea) {
                endArea = fret - 1;
            }
        }

        int[] startEnd = new int[] { startArea, endArea };
        return startEnd;
    }
}
