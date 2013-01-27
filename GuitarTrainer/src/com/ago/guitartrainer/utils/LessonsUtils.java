package com.ago.guitartrainer.utils;

import java.util.List;
import java.util.Random;

import com.ago.guitartrainer.notation.Note;
import com.ago.guitartrainer.notation.NoteStave;
import com.ago.guitartrainer.notation.Position;
import com.ago.guitartrainer.scalegrids.ScaleGrid;

public class LessonsUtils {

    private static Random random = new Random();
    
    public static Position pickPosition(ScaleGrid gridShape) {
        List<Position> strongPositions = gridShape.strongPositions();

        int randomMin = 0;
        int randomMax = strongPositions.size();
        
        int randomNum = randomMin + (int) (Math.random() * randomMax);
        
        return (strongPositions.size()>0) ? strongPositions.get(randomNum) : null;
    }

    /**
     * Returns random integer in the range specified.
     * 
     * @param min value allowed for random integer
     * @param max value allowed for the random integer
     * @return random integer
     */
    public static int random(int min, int max) {
        
        int i1 = random.nextInt(max - min + 1) + min;
        return i1;
    }

}
