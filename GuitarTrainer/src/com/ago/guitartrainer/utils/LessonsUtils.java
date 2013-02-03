package com.ago.guitartrainer.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.ago.guitartrainer.GuitarTrainerApplication;
import com.ago.guitartrainer.SettingsActivity;
import com.ago.guitartrainer.instruments.GuitarUtils;
import com.ago.guitartrainer.notation.Degree;
import com.ago.guitartrainer.notation.Key;
import com.ago.guitartrainer.notation.Note;
import com.ago.guitartrainer.notation.Position;
import com.ago.guitartrainer.scalegrids.ScaleGrid;

public class LessonsUtils {

    private static Random random = new Random();

    public static Position pickPosition(ScaleGrid gridShape) {
        List<Position> strongPositions = gridShape.strongPositions();

        int randomMin = 0;
        int randomMax = strongPositions.size();

        int randomNum = randomMin + (int) (Math.random() * randomMax);

        return (strongPositions.size() > 0) ? strongPositions.get(randomNum) : null;
    }

    /**
     * Return a random {@link Degree} from those which are I, II...
     * 
     * @return degree of the scale grid
     */
    public static Degree randomDegree() {
        Degree[] mainDegrees = new Degree[] { Degree.ONE, Degree.TWO, Degree.THREE, Degree.FOUR, Degree.FIVE,
                Degree.SIX, Degree.SEVEN };

        boolean isMainDegree = false;
        Degree degree;
        do {
            int indexOfDegree = LessonsUtils.random(0, Degree.values().length - 1);
            degree = Degree.values()[indexOfDegree];
            isMainDegree = Arrays.binarySearch(mainDegrees, degree) >= 0;
        } while (!isMainDegree);

        return degree;
    }

    /**
     * Calculates a random but still valid start of the area in which the scale grid of a given type may reside.
     * 
     * The start area is considered to be valid, if it lays somewhere inside of the 0..
     * {@value ScaleGrid#FRETS_ON_GUITAR} frets of the guitar.
     * 
     * @param gst
     *            type of the scale grid
     * @return valid start of the area
     */
    public static int randomFretPositionForGridShapeType(ScaleGrid.Type gst) {
        int fretPosition = LessonsUtils.random(0, GuitarUtils.FRETS_ON_GUITAR);
        int fretPositionEnd = fretPosition + gst.numOfFrets();
        if (fretPositionEnd > GuitarUtils.FRETS_ON_GUITAR) {
            fretPosition = GuitarUtils.FRETS_ON_GUITAR - (fretPositionEnd - fretPosition);
        }
        return fretPosition;
    }

    /**
     * Returns random integer in the range specified.
     * 
     * @param min
     *            value allowed for random integer
     * @param max
     *            value allowed for the random integer
     * @return random integer
     */
    public static int random(int min, int max) {

        int i1 = random.nextInt(max - min + 1) + min;
        return i1;
    }

    public static ScaleGrid.Type randomGridShapeType() {
        int indexOfGridShape = LessonsUtils.random(0, ScaleGrid.Type.values().length - 1);
        ScaleGrid.Type gridShapeType = ScaleGrid.Type.values()[indexOfGridShape];

        return gridShapeType;
    }

    /**
     * Return random note in one of the main keys {C, D, ..}.
     * 
     * @return
     */
    public static Note randomNote() {
        /**
         * The keys corresponds to the main degrees of the C-major scale: C, D, E etc. The main reason to exclude keys
         * with sharps/flats: the appropriate images are not currently not available in the {@link NotesView}. But on
         * the other side it could be enough just to no the position of the main keys.
         * */
        Key[] mainKeys = new Key[] { Key.C, Key.D, Key.E, Key.F, Key.G, Key.A, Key.B };

        Note note = null;
        boolean isMainKey = false;
        boolean isDebugMode = GuitarTrainerApplication.getPrefs().getBoolean(SettingsActivity.KEY_DEBUG_MODE, true);
        do {
            /* we use indexes of notes 21..32, because they correspond to note playable by electro-tuner which I have */
            int index = (isDebugMode) ? LessonsUtils.random(21, 32) : LessonsUtils.random(0, Note.values().length - 1);
            note = Note.values()[index];
            isMainKey = Arrays.binarySearch(mainKeys, note.getKey()) >= 0;

        } while (!isMainKey);

        return note;
    }

    public static Position randomPosition() {
        boolean isDebugMode = GuitarTrainerApplication.getPrefs().getBoolean(SettingsActivity.KEY_DEBUG_MODE, true);
        int str = LessonsUtils.random(1, 6);
        int fret = LessonsUtils.random(0, (isDebugMode) ? 5 : GuitarUtils.FRETS_ON_GUITAR);

        Position pos = new Position(str, fret);
        return pos;
    }
}
