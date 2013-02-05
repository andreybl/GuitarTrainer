package com.ago.guitartrainer.scalegrids;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.ago.guitartrainer.R;
import com.ago.guitartrainer.instruments.guitar.GuitarFingeringHelper;
import com.ago.guitartrainer.instruments.guitar.GuitarUtils;
import com.ago.guitartrainer.instruments.guitar.Position;
import com.ago.guitartrainer.notation.Chord;
import com.ago.guitartrainer.notation.Degree;
import com.ago.guitartrainer.notation.Note;
import com.ago.guitartrainer.utils.ArrayUtils;

/**
 * Holds the information for the grid shape projection:
 * <ul>
 * <li>fret at which the projection starts
 * <li>layout of degrees inside of the shape
 * </ul>
 * 
 * @author Andrej Golovko - jambit GmbH
 * 
 */
public abstract class ScaleGrid {

    /**
     * For forms:
     * 
     * @see http://www.guitarlessonworld.com/lessons/chord-to-chord-relationships.htm
     */
    private static Map<Degree[], Integer[]> mapAlphaChord2Form = new HashMap<Degree[], Integer[]>();

    private static Map<Degree[], Integer[]> mapBetaChord2Form = new HashMap<Degree[], Integer[]>();

    private static Map<Degree[], Integer[]> mapGammaChord2Form = new HashMap<Degree[], Integer[]>();

    private static Map<Degree[], Integer[]> mapDeltaChord2Form = new HashMap<Degree[], Integer[]>();

    private static Map<Degree[], Integer[]> mapEpsilonChord2Form = new HashMap<Degree[], Integer[]>();

    static {
        mapAlphaChord2Form.put(Chord.major, new Integer[] { 0, 1, 0, 2, 3, Chord.NOTPLAYED });
        mapAlphaChord2Form.put(Chord.minor, new Integer[] { 3, 1, 0, 1, 3, Chord.NOTPLAYED });
        // mapAlphaChord2Form.put(Chord.dim, new Integer[] { XXX });
        // mapAlphaChord2Form.put(Chord.aug, new Integer[] { XXX });
        mapAlphaChord2Form.put(Chord.major7thChord, new Integer[] { 0, 0, 0, 2, 3, Chord.NOTPLAYED });
        mapAlphaChord2Form.put(Chord.minor7thChord, new Integer[] { Chord.NOTPLAYED, 1, 3, 1, 3, Chord.NOTPLAYED });
        mapAlphaChord2Form.put(Chord.dominantSeptChord, new Integer[] { 0, 1, 3, 2, 3, Chord.NOTPLAYED });
        // mapAlphaChord2Form.put(Chord.dim7thChord, new Integer[] { XXX });
    }

    static {
        mapBetaChord2Form.put(Chord.major, new Integer[] { 1, 3, 3, 3, 1, -1 });
        mapBetaChord2Form.put(Chord.minor, new Integer[] { 1, 2, 3, 3, 1, -1 });
        // mapBetaChord2Form.put(Chord.dim, new Integer[] { XXX });
        // mapBetaChord2Form.put(Chord.aug, new Integer[] { XXX });
        mapBetaChord2Form.put(Chord.major7thChord, new Integer[] { 1, 3, 2, 3, 1, -1 });
        mapBetaChord2Form.put(Chord.minor7thChord, new Integer[] { 1, 2, 1, 3, 1, -1 });
        mapBetaChord2Form.put(Chord.dominantSeptChord, new Integer[] { 1, 3, 1, 3, 1, Chord.NOTPLAYED }); // also: 131314
        // mapBetaChord2Form.put(Chord.dim7thChord, new Integer[] { XXX });
    }

    static {
        mapGammaChord2Form.put(Chord.major, new Integer[] { 4, 1, 1, 1, 3, 4 });
        mapGammaChord2Form.put(Chord.minor, new Integer[] { 4, 4, 1, 1, 2, 4 });
        // mapGammaChord2Form.put(Chord.dim, new Integer[] { XXX });
        // mapGammaChord2Form.put(Chord.aug, new Integer[] { XXX });
        mapGammaChord2Form.put(Chord.major7thChord, new Integer[] { 3, 1, 1, 1, 3, 4 });
        mapGammaChord2Form.put(Chord.minor7thChord, new Integer[] { 2, -1, 1, 1, 2, 4 });
        mapGammaChord2Form.put(Chord.dominantSeptChord, new Integer[] { 2, 1, 1, 1, 3, 4 });
        // mapGammaChord2Form.put(Chord.dim7thChord, new Integer[] { XXX });
    }

    static {
        mapDeltaChord2Form.put(Chord.major, new Integer[] { 1, 1, 2, 3, 3, 1 });
        mapDeltaChord2Form.put(Chord.minor, new Integer[] { 1, 1, 1, 3, 3, 1 });
        // mapDeltaChord2Form.put(Chord.dim, new Integer[] { XXX });
        // mapDeltaChord2Form.put(Chord.aug, new Integer[] { XXX });
        mapDeltaChord2Form.put(Chord.major7thChord, new Integer[] { 1, 1, 2, 2, 3, 1 });
        mapDeltaChord2Form.put(Chord.minor7thChord, new Integer[] { 1, 1, 1, 1, 3, 1 });
        mapDeltaChord2Form.put(Chord.dominantSeptChord, new Integer[] { 1, 1, 2, 1, 3, 1 });
        // mapDeltaChord2Form.put(Chord.dim7thChord, new Integer[] { XXX });
    }

    static {
        mapEpsilonChord2Form.put(Chord.major, new Integer[] { 3, 4, 3, 1, -1, -1 });
        mapEpsilonChord2Form.put(Chord.minor, new Integer[] { 2, 4, 3, 1, Chord.NOTPLAYED, Chord.NOTPLAYED });
        // mapEpsilonChord2Form.put(Chord.dim, new Integer[] { XXX });
        // mapEpsilonChord2Form.put(Chord.aug, new Integer[] { XXX });
        mapEpsilonChord2Form.put(Chord.major7thChord, new Integer[] { 3, 3, 3, 1, -1, -1 });
        mapEpsilonChord2Form.put(Chord.minor7thChord, new Integer[] { 2, 2, 3, 1, -1, -1 });
        mapEpsilonChord2Form.put(Chord.dominantSeptChord, new Integer[] { 3, 2, 3, 1, -1, -1 });
        // mapEpsilonChord2Form.put(Chord.dim7thChord, new Integer[] { XXX });
    }

    public enum Type {

        ALPHA('C', 4, mapAlphaChord2Form),

        BETA('A', 5, mapBetaChord2Form),

        GAMMA('G', 5, mapGammaChord2Form),

        DELTA('E', 4, mapDeltaChord2Form),

        EPSILON('D', 5, mapEpsilonChord2Form);

        /* number of frets, which are taken by this grid shape */
        private int numOfFrets;

        private char cagedCode;

        private Map<Degree[], Integer[]> mapChord2Form = new HashMap<Degree[], Integer[]>();

        Type(char cagedCode, int numOfFrets, Map<Degree[], Integer[]> mapChord2Form) {
            this.cagedCode = cagedCode;
            this.numOfFrets = numOfFrets;
            this.mapChord2Form = mapChord2Form;
        }

        public int numOfFrets() {
            return numOfFrets;
        }

        public String cagedCode() {
            return String.valueOf(cagedCode);
        }

        public Integer[] getChorForm(Degree[] chord) {
            if (mapChord2Form == null)
                return null;

            if (mapChord2Form.containsKey(chord))
                return mapChord2Form.get(chord);
            else
                return null;
        }

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
    protected ScaleGrid(Degree[] zeroFretDegrees, int numOfFrets, int[] rootStrings, int suggestedStartingFret) {
        this.numOfFrets = numOfFrets;
        initByStartingFret(zeroFretDegrees, suggestedStartingFret);

    }

    protected ScaleGrid(Degree[] zeroFretDegrees, int numOfFrets, int[] rootStrings, Note note) {
        this.numOfFrets = numOfFrets;

    }

    public Type getType() {
        if (this instanceof AlphaScaleGrid) {
            return Type.ALPHA;
        } else if (this instanceof BetaScaleGrid) {
            return Type.BETA;
        } else if (this instanceof GammaScaleGrid) {
            return Type.GAMMA;
        } else if (this instanceof DeltaScaleGrid) {
            return Type.DELTA;
        } else if (this instanceof EpsilonScaleGrid) {
            return Type.EPSILON;
        } else {
            // TODO: actually, not possible. I must throw exception here.
            return null;
        }

    }

    /*
     * TODO: calculate the rootStrings from the shape itself. It can be done after degreeToPosition is calculated
     */
    private void initByStartingFret(Degree[] zeroFretDegrees, int suggestedStartingFret) {

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
                int string = iString + 1;
                Position position = new Position(string, iFret + startingFret);
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
        int overestimate = (suggestedStartFret + numOfFrets) - GuitarUtils.FRETS_ON_GUITAR;
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
        int noteFret = 0;
        outerloop: for (int i = 0; i <= GuitarUtils.FRETS_ON_GUITAR; i++) {
            for (int j = 0; j < rootStrings.length; j++) {
                Note n = GuitarFingeringHelper.getInstance().resolveNote(new Position(rootStrings[j] + 1, i));
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

        List<Position> positions = degreeToPosition.get(degree);

        List<Note> results = new ArrayList<Note>();
        for (Position pos : positions) {
            Note n = GuitarFingeringHelper.getInstance().resolveNote(pos); // +fretShifts
            results.add(n);
        }

        return results;
    }

    /**
     * Calculates positions of the notes for the specific grid shape projection.
     * 
     * Several positions with the same degree are possible for specific grid shape. But also keep in mind, that the
     * notes for those positions are different.
     * 
     * @param degree
     *            which positions must be returned
     */
    public List<Position> degree2Positions(Degree degree) {
        List<Position> positions = new ArrayList<Position>();

        for (Position position : degreeToPosition.get(degree)) {
            positions.add(new Position(position.getStringIndex() + 1, position.getFret()));
        }

        return positions;
    }

    public List<Position> strongPositions() {
        List<Position> positions = new ArrayList<Position>();

        for (Degree d : degreeToPosition.keySet()) {
            if (ArrayUtils.inArray(d, degreesStrong)) {
                List<Position> origPositions = degreeToPosition.get(d);
                for (Position origPosition : origPositions) {
                    positions.add(new Position(origPosition.getStringIndex() + 1, origPosition.getFret()));
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
     * @deprecated the method seems to be useless, will be removed
     */
    public List<Position> applyShape(List<Position> positions) {
        List<Position> projected = new ArrayList<Position>();

        /*
         * we assume, the grid shapes are always defined starting from the zero fret. So the 0 in the equation. The
         * start/end frets for range checking are inclusive
         */
        // int startInclFret = 0 + fretShifts;

        for (Position position : positions) {
            if (position.getFret() >= startingFret && position.getFret() <= endingFret) {
                Degree d = positionToDegree.get(position);
                if (d.isStrong()) {
                    projected.add(position);
                }
            }
        }

        return projected;
    }

    public int getStartingFret() {
        return startingFret;
    }

    public static ScaleGrid create(ScaleGrid.Type gridShapeType, int fretPosition) {
        ScaleGrid gs = null;

        switch (gridShapeType) {
        case ALPHA: {
            gs = new AlphaScaleGrid(fretPosition);
            break;
        }
        case BETA: {
            gs = new BetaScaleGrid(fretPosition);
            break;
        }
        case GAMMA: {
            gs = new GammaScaleGrid(fretPosition);
            break;
        }
        case DELTA: {
            gs = new DeltaScaleGrid(fretPosition);
            break;
        }
        case EPSILON: {
            gs = new EpsilonScaleGrid(fretPosition);
            break;
        }
        default:
            break;
        }

        return gs;
    }

    public static ScaleGrid create(int checkboxResourceId, int progress) {
        ScaleGrid gs = null;
        switch (checkboxResourceId) {
        case R.id.gridshape_alpha:
            gs = new AlphaScaleGrid(progress);
            break;
        case R.id.gridshape_beta:
            gs = new BetaScaleGrid(progress);
            break;
        case R.id.gridshape_gamma:
            gs = new GammaScaleGrid(progress);
            break;
        case R.id.gridshape_delta:
            gs = new DeltaScaleGrid(progress);
            break;
        case R.id.gridshape_epsilon:
            gs = new EpsilonScaleGrid(progress);
            break;

        default:
            break;
        }

        return gs;
    }

    public List<Position> chord2Positions(Degree[] chord) {

        List<Position> positions = new ArrayList<Position>();

        Integer[] fretOffsets = getType().getChorForm(chord);

        if (fretOffsets != null) {

            for (int string = 1; string <= 6; string++) {
                int stringIndex = string - 1;
                int offsetValue = fretOffsets[stringIndex];
                if (offsetValue != Chord.NOTPLAYED) {
                    Position chordPosition = new Position(string, offsetValue + getStartingFret());
                    positions.add(chordPosition);
                }
            }

        } else {
            // no unique chord form was registered for the scale grid
            for (Degree degree : chord) {
                List<Position> pos = degree2Positions(degree);
                positions.addAll(pos);
            }

        }

        return positions;
    }

    /**
     * Returns the root - e.g. the Ist degree position - of the scale grid.
     * 
     * Among all Ist degree positions the one is returned, which is located on the lower fret.
     * 
     * @return
     */
    public Position getRootPosition() {
        List<Position> firstPositions = degree2Positions(Degree.ONE);
        Position root = firstPositions.get(0);
        for (Position position : firstPositions) {
            if (position.getFret() < root.getFret()) {
                root = position;
            }
        }
        return root;
    }

}
