package com.ago.guitartrainer.gridshapes;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.ago.guitartrainer.notation.Degree;
import com.ago.guitartrainer.notation.Note;
import com.ago.guitartrainer.notation.NoteStave;
import com.ago.guitartrainer.notation.Position;
import com.ago.guitartrainer.utils.ArrayUtils;

public abstract class GridShape {

    /* [string][fret] */

    private int[] rootStrings;

    // private Note key;

    /** original mapping from degree to position, as calculated relative to the zero fret */
    private Map<Degree, List<Position>> degreeToPosition = new Hashtable<Degree, List<Position>>();
    private Map<Position, Degree> positionToDegree = new Hashtable<Position, Degree>();

    /*
     * shifts of the shape required relatively to the fret "0", so that the root key is positioned on the place of note
     * defined in "key" variable.
     */
    private int fretShifts = -1;

    /**
     * define how many frets belong to the shape
     */
    private int frets = 0;

    private Degree[] degreesStrong = new Degree[] { Degree.ONE, Degree.TWO, Degree.THREE, Degree.FOUR, Degree.FIVE,
            Degree.SIX, Degree.SEVEN };

    /**
     * Create grid shape.
     * 
     * The grid shape can be projected on different positions. But to describe the shape it is enough to specify the
     * degrees of the shape on some selected fret (we choose the zero fret) and the number of frets this shape takes.
     * The first two parameters are used for it.
     * 
     * The <code>rootStrings</code> are used to simplify and optimize the implementation of the class.
     * 
     * @param zeroFretDegrees
     *            degrees of the grid for the zero fret
     * @param frets
     *            number of frets over which the shape expands
     * @param rootStrings
     *            strings on which the root of the shape is located
     */
    /*
     * TODO: calculate the rootStrings from the shape itself. It can be done after degreeToPosition is calculated
     */
    protected GridShape(Degree[] zeroFretDegrees, int frets, int[] rootStrings) {
        this.rootStrings = rootStrings;

        // NoteStave notes = NoteStave.getInstance();

        // initialize mapping of degrees to positions
        for (int i = 0; i < zeroFretDegrees.length; i++) {
            Degree d = zeroFretDegrees[i];
            for (int f = 0; f < frets; f++) {
                Degree calcD = d.addFrets(f);
                {
                    calcD = d.addFrets(f);
                }

                if (!degreeToPosition.containsKey(calcD)) {
                    degreeToPosition.put(calcD, new ArrayList<Position>());
                }

                Position position = new Position(i, f);
                degreeToPosition.get(calcD).add(position);
                positionToDegree.put(position, calcD);
            }
        }
    }

    /**
     * Set the root note of the shape.
     * 
     * @param key
     *            the note which must be the root of the shape
     */
    protected void setKey(Note key) {
        // this.key = key;

        NoteStave notes = NoteStave.getInstance();

        outerloop: for (int i = 0; i <= 12; i++) {
            for (int j = 0; j < rootStrings.length; j++) {
                Note n = notes.resolveNote(rootStrings[j], i);
                if (n == key) {
                    fretShifts = (i - 1);
                    break outerloop;
                }
            }
        }
    }

    /**
     * Calculate note/notes from the shape based on the passed degree.
     * 
     * The same degree can be available several times (twice) in the shape. So several notes can have the same degree.
     * The notes of the same degree are from different octaves, like D3 and D4.
     * 
     * @param degree
     * @return
     */
    public List<Note> calculateNotes(Degree degree) {

        NoteStave notes = NoteStave.getInstance();

        List<Position> positions = degreeToPosition.get(degree);

        List<Note> results = new ArrayList<Note>();
        for (Position pos : positions) {
            Note n = notes.resolveNote(pos.string, pos.fret + fretShifts);
            results.add(n);
        }

        return results;
    }

    /**
     * Calculates positions of the notes for the specific grid shape projection.
     * 
     * @param degree
     *            which positions must be returned
     */
    public List<Position> calculatePositions(Degree degree) {
        List<Position> shiftedPositions = new ArrayList<Position>();

        for (Position position : degreeToPosition.get(degree)) {
            shiftedPositions.add(new Position(position.string, position.fret + fretShifts));
        }

        return shiftedPositions;
    }

    public List<Position> getStrongPositions() {
        List<Position> positions = new ArrayList<Position>();

        for (Degree d : degreeToPosition.keySet()) {
            if (ArrayUtils.inArray(d, degreesStrong)) {
                List<Position> origPositions = degreeToPosition.get(d);
                for (Position origPosition : origPositions) {
                    positions.add(new Position(origPosition.string, origPosition.fret + fretShifts));
                }

            }
        }

        return positions;
    }

    public Degree position2Degree(Position lessonPosition) {
        return positionToDegree.get(lessonPosition);
    }

    /**
     * Projects positions passed onto grid shape. Only strong positions are taken into account.
     * 
     * The positions passed as parameter are projected onto the current grid shape projection. They are kind of filtered
     * through the shape projection. Only positions are returned, which belong to the shape.
     * 
     * The weak positions will not be taken into account and are NOT in the returned value
     * 
     * @param positions to project onto grid shape
     * @return subset of positions from original list, which are strong in current grind shape 
     */
    public List<Position> applyShape(List<Position> positions) {
        List<Position> projected = new ArrayList<Position>();

        /*
         * we assume, the grid shapes are always defined starting from the zero fret. So the 0 in the equation. The
         * start/end frets for range checking are inclusive
         */
        int startInclFret = 0 + fretShifts;
        int endInclFret = startInclFret + frets;

        for (Position position : positions) {
            if (position.fret >= startInclFret && position.fret <= endInclFret) {
                Degree d = positionToDegree.get(position);
                if (d.isStrong()) {
                    projected.add(position);
                }
            }
        }

        return projected;
    }
}
