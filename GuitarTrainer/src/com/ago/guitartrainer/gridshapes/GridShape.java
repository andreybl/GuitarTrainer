package com.ago.guitartrainer.gridshapes;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.ago.guitartrainer.R;
import com.ago.guitartrainer.notation.Degree;
import com.ago.guitartrainer.notation.Note;
import com.ago.guitartrainer.notation.NoteStave;
import com.ago.guitartrainer.notation.Position;
import com.ago.guitartrainer.utils.ArrayUtils;

/**
 * Holds the information for the grid shape projection:
 *   <ul>
 *   <li> fret at which the projection starts
 *   <li> layout of degrees inside of the shape
 *   </ul> 
 * 
 * @author Andrej Golovko - jambit GmbH
 *
 */
public abstract class GridShape {

    public enum Type {
        ALPHA, BETA, GAMMA, DELTA, EPSILON
    }
    
    /** original mapping from degree to position, as calculated relative to the zero fret */
    private Map<Degree, List<Position>> degreeToPosition = new Hashtable<Degree, List<Position>>();

    private Map<Position, Degree> positionToDegree = new Hashtable<Position, Degree>();

    private int startingFret = -1;

    private int endingFret = -1;

    /**
     * Define how many frets belong to the shape. Required during decision about actual starting fret.
     */
    private int numOfFrets = 0;

    public static int FRETS_ON_GUITAR = 12;

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
     * @param numOfFrets
     *            number of frets over which the shape expands
     * @param rootStrings
     *            strings on which the root of the shape is located
     */
    protected GridShape(Degree[] zeroFretDegrees, int numOfFrets, int[] rootStrings, int suggestedStartingFret) {
        this.numOfFrets = numOfFrets;
        initByStartingFret(zeroFretDegrees, suggestedStartingFret);

    }

    protected GridShape(Degree[] zeroFretDegrees, int numOfFrets, int[] rootStrings, Note note) {
        this.numOfFrets = numOfFrets; 
        int fretOfRoot = calculateFretForNote(note, rootStrings);
        initByRootFret(zeroFretDegrees, fretOfRoot);

    }
    
    private void initByRootFret(Degree[] zeroFretDegrees, int rootFret) {
        
    }

    /*
     * TODO: calculate the rootStrings from the shape itself. It can be done after degreeToPosition is calculated
     */
    private void initByStartingFret(Degree[] zeroFretDegrees, int suggestedStartingFret) {
        // this.rootStrings = rootStrings;

        startingFret = calculateStartingFret(suggestedStartingFret);
        endingFret = startingFret + numOfFrets - 1;

        // initialize mapping of degrees to positions
        for (int iString = 0; iString < zeroFretDegrees.length; iString++) {
            Degree d = zeroFretDegrees[iString];
            for (int iFret = 0; iFret < numOfFrets; iFret++) {
                // Degree is shift-independent
                Degree calcD = d.addFrets(iFret);
                {
                    calcD = d.addFrets(iFret);
                }

                if (!degreeToPosition.containsKey(calcD)) {
                    degreeToPosition.put(calcD, new ArrayList<Position>());
                }

                // the position must be shifted
                Position position = new Position(iString, iFret + startingFret);
                degreeToPosition.get(calcD).add(position);
                positionToDegree.put(position, calcD);
            }
        }
    }

    /**
     * Construct the grid shape projection, assuming it starts from the fret specified as parameter.
     * 
     * The projection is set in the way, that all its positions are between 0..12 frets inclusive.
     * 
     * @param suggestedStartFret
     *            the from from which the grid start.
     */
    private int calculateStartingFret(int suggestedStartFret) {
        int startFret = suggestedStartFret;
        int overestimate = (suggestedStartFret + numOfFrets) - FRETS_ON_GUITAR;
        if (suggestedStartFret < 0) {
            startFret = 0;
        } else if (overestimate > 0) {
            startFret = suggestedStartFret - overestimate + 1;
        }

        return startFret;
    }

    /**
     * Set the root note of the shape.
     * 
     * @param key
     *            the note which must be the root of the shape
     */
    private int calculateFretForNote(Note key, int[] rootStrings) {
        NoteStave notes = NoteStave.getInstance();
        int noteFret = 0;
        outerloop: for (int i = 0; i <= 12; i++) {
            for (int j = 0; j < rootStrings.length; j++) {
                Note n = notes.resolveNote(rootStrings[j], i);
                if (n == key) {
                    noteFret = i;
                    break outerloop;
                }
            }
        }
        return noteFret;
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
    public List<Note> degree2Notes(Degree degree) {

        NoteStave notes = NoteStave.getInstance();

        List<Position> positions = degreeToPosition.get(degree);

        List<Note> results = new ArrayList<Note>();
        for (Position pos : positions) {
            Note n = notes.resolveNote(pos.string, pos.fret); // +fretShifts
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
    public List<Position> degree2Positions(Degree degree) {
        List<Position> positions = new ArrayList<Position>();

        for (Position position : degreeToPosition.get(degree)) {
            positions.add(new Position(position.string, position.fret)); // +fretShifts
        }

        return positions;
    }

    public List<Position> strongPositions() {
        List<Position> positions = new ArrayList<Position>();

        for (Degree d : degreeToPosition.keySet()) {
            if (ArrayUtils.inArray(d, degreesStrong)) {
                List<Position> origPositions = degreeToPosition.get(d);
                for (Position origPosition : origPositions) {
                    positions.add(new Position(origPosition.string, origPosition.fret)); // +fretShifts
                }

            }
        }

        return positions;
    }

    /**
     * Return the degree of the position in the context of current grid shape projection.
     * 
     * Null is returned, if the position is outside of the current projection.
     * 
     * @param position
     *            for which the degree must be returned
     * @return degree of the position in context of shape projection
     */
    public Degree position2Degree(Position position) {
        return positionToDegree.get(position);
    }

    /**
     * Projects positions passed onto grid shape. Only strong positions are taken into account.
     * 
     * The positions passed as parameter are projected onto the current grid shape projection. They are kind of filtered
     * through the shape projection. Only positions are returned, which belong to the shape.
     * 
     * The weak positions will not be taken into account and are NOT in the returned value
     * 
     * @param positions
     *            to project onto grid shape
     * @return subset of positions from original list, which are strong in current grind shape
     */
    public List<Position> applyShape(List<Position> positions) {
        List<Position> projected = new ArrayList<Position>();

        /*
         * we assume, the grid shapes are always defined starting from the zero fret. So the 0 in the equation. The
         * start/end frets for range checking are inclusive
         */
        // int startInclFret = 0 + fretShifts;

        for (Position position : positions) {
            if (position.fret >= startingFret && position.fret <= endingFret) {
                Degree d = positionToDegree.get(position);
                if (d.isStrong()) {
                    projected.add(position);
                }
            }
        }

        return projected;
    }

    public static GridShape create(Class<? extends GridShape> clazz, int progress) {
        GridShape gs = null;

        if (clazz.equals(AlphaGridShape.class)) {
            gs = new AlphaGridShape(progress);
        } else if (clazz.equals(BetaGridShape.class)) {
            gs = new BetaGridShape(progress);
        } else if (clazz.equals(GammaGridShape.class)) {
            gs = new GammaGridShape(progress);
        } else if (clazz.equals(DeltaGridShape.class)) {
            gs = new DeltaGridShape(progress);
        } else if (clazz.equals(EpsilonGridShape.class)) {
            gs = new EpsilonGridShape(progress);
        }

        return gs;
    }

    public static GridShape create(int checkboxResourceId, int progress) {
        GridShape gs = null;
        switch (checkboxResourceId) {
        case R.id.gridshape_alpha:
            gs = new AlphaGridShape(progress);
            break;
        case R.id.gridshape_beta:
            gs = new BetaGridShape(progress);
            break;
        case R.id.gridshape_gamma:
            gs = new GammaGridShape(progress);
            break;
        case R.id.gridshape_delta:
            gs = new DeltaGridShape(progress);
            break;
        case R.id.gridshape_epsilon:
            gs = new EpsilonGridShape(progress);
            break;

        default:
            break;
        }

        return gs;
    }

    private static GridShape create(Class<AlphaGridShape> clazz, Note note) {
        GridShape gs = null;

        if (clazz.equals(AlphaGridShape.class)) {
            gs = new AlphaGridShape(note);
        } else if (clazz.equals(BetaGridShape.class)) {
            gs = new BetaGridShape(note);
        } else if (clazz.equals(GammaGridShape.class)) {
            gs = new GammaGridShape(note);
        } else if (clazz.equals(DeltaGridShape.class)) {
            gs = new DeltaGridShape(note);
        } else if (clazz.equals(EpsilonGridShape.class)) {
            gs = new EpsilonGridShape(note);
        }

        return gs;
    }

}
