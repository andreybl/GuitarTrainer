package com.ago.guitartrainer.lessons;

import com.ago.guitartrainer.gridshapes.GridShape;
import com.ago.guitartrainer.notation.Position;

/**
 * The instance of this object holds the data associated with the questioning some facts by the user:
 * <ul>
 * <li>question itself
 * <li>expected answer
 * <li>basic metrics, like time required to answer the question
 * </ul>
 * 
 * 
 * @author Andrej Golovko - jambit GmbH
 * 
 */
public class QuestionParams {

    enum GridShapeKey {
        A, B, G, D, E
    }
    
    /**
     * shape of the grid, where the degree must be recognized.
     * */
    private GridShapeKey shape;

    /**
     * start position of the shape on the fret.
     * 
     * */
    private int startPosition;

    /** degree which must be found by the user */
    private int degree;

}
