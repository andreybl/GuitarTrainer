package com.ago.guitartrainer;

import com.ago.guitartrainer.Notes.Note;

/**
 * A fretboard projection means some notes, scales, chords etc. displayed directly on the fretboard. Fretboard
 * projetions are extremely useful because they help you visualize what you want to play on the fretboard.
 * 
 * A fretboard projection has three important qualities: shape, position and scale degrees
 * 
 * @author Andrej Golovko - jambit GmbH
 * 
 */
public class FretboardProjection {

    private GridShape gShape;

    /**
     * The position moves on the fretboard depending on the key.
     */
    private Note key;

    
    public FretboardProjection (GridShape gShape, Note key) {
        this.gShape = gShape;
        this.key = key;
    }
    
    /**
     * the "grid" and "grid shape" are used interchangeably
     */
    public enum GridShape {
        /** from fragments: alpha and beta */
        ALPHA,

        /** from fragments: beta and gamma */
        BETA,

        /** from fragments: gamma and delta */
        GAMMA,

        /** from fragments: delta and epsilon */
        DELTA,

        /** from fragments: epsilon and alpha */
        EPSILON
    }

}
