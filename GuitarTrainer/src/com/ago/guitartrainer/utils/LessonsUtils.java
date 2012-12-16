package com.ago.guitartrainer.utils;

import java.util.List;

import com.ago.guitartrainer.gridshapes.GridShape;
import com.ago.guitartrainer.notation.Position;

public class LessonsUtils {


    public static Position pickPosition(GridShape gridShape) {
        List<Position> strongPositions = gridShape.strongPositions();

        int randomMin = 0;
        int randomMax = strongPositions.size();
        
        int randomNum = randomMin + (int) (Math.random() * randomMax);
        
        return (strongPositions.size()>0) ? strongPositions.get(randomNum) : null;
    }

}
