package com.ago.guitartrainer.notation;

/**
 * Represents a single note position on the fretboard.
 * 
 * The same note can be played in different positions. But the position itself is unique in terms of the notes: you can
 * play only one note in some specific position.
 * 
 * The counting is from 0. So the open fret is equal to "0". The first string is referenced as "0". The last sixth
 * string is referenced as "5".
 * 
 * @author Andrej Golovko - jambit GmbH
 * 
 */
public class Position {
    public int fret;
    public int string;

    public Position(int string, int fret) {
        this.string = string;
        this.fret = fret;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Position) {
            Position p = (Position) o;
            if (p.fret == this.fret && p.string == this.string)
                return true;
        }

        return false;
    }
    
    @Override
    public int hashCode() {
        return 37*fret + string;
    }
    
    @Override
    public String toString() {
        return "["+string+"]["+fret+"]";
    }
}
