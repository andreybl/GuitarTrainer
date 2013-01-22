package com.ago.guitartrainer.notation;

/**
 * Represents a single note position on the fretboard.
 * 
 * The same note can be played in different positions. But the position itself is unique in terms of the notes: you can
 * play only one note in some specific position.
 * 
 * The allowed strings range is 1..6, the allow frets range is 0..12.
 * 
 * @author Andrej Golovko - jambit GmbH
 * 
 */
public class Position {

    /** guitar fret in range 0..12 */
    private int fret;

    /** guitar string in range 1..6 */
    private int string;

    /**
     * Constructor for position on guitar
     * 
     * @param string
     *            guitar string in range 1..6
     * @param fret
     *            of guitar in range 0..12
     */
    public Position(int string, int fret) {
        if (string < 1 || string > 6) {
            throw new IndexOutOfBoundsException("The \"string\" parameter of Position is wrong [min/max/current]: 1/6/"
                    + string);
        }
        if (fret < 0 || fret > 12) {
            throw new IndexOutOfBoundsException("The \"string\" parameter of Position is wrong [min/max/current]: 1/6/"
                    + string);
        }

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
        return 37 * fret + string;
    }

    @Override
    public String toString() {
        return "[string=" + (string) + "/fret=" + fret + "]";
    }

    /**
     * Calculate the guitar string index as it is used in array working with the {@link Position} instances.
     * 
     * @return string index in range 0..5
     * */
    public int getStringIndex() {
        return this.string - 1;
    }

    public int getFret() {
        return fret;
    }
}
