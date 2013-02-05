package com.ago.guitartrainer.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.ago.guitartrainer.GuitarTrainerApplication;
import com.ago.guitartrainer.SettingsActivity;
import com.ago.guitartrainer.instruments.guitar.GuitarFingeringHelper;
import com.ago.guitartrainer.instruments.guitar.GuitarUtils;
import com.ago.guitartrainer.instruments.guitar.Position;
import com.ago.guitartrainer.notation.Chord;
import com.ago.guitartrainer.notation.Degree;
import com.ago.guitartrainer.notation.Key;
import com.ago.guitartrainer.notation.Note;
import com.ago.guitartrainer.notation.NoteStave;
import com.ago.guitartrainer.notation.Octave;
import com.ago.guitartrainer.scalegrids.ScaleGrid;
import com.ago.guitartrainer.scalegrids.ScaleGrid.Type;

public class LessonsUtils {

    private static Random random = new Random();

    public static Position randomPosition(ScaleGrid gridShape) {
        List<Position> strongPositions = gridShape.strongPositions();

        int randomMin = 0;
        int randomMax = strongPositions.size();

        int randomNum = randomMin + (int) (Math.random() * randomMax);

        return (strongPositions.size() > 0) ? strongPositions.get(randomNum) : null;
    }

    private static List<Degree> randomDegrees = new ArrayList<Degree>();

    /**
     * Return a random {@link Degree} from those which are I, II...
     * 
     * @return degree of the scale grid
     */
    public static Degree randomDegree() {

        if (randomDegrees.size() == 0) {
            randomDegrees.addAll(Arrays.asList(Degree.STRONG_DEGREES));
        }

        int randomIndex = LessonsUtils.random(0, randomDegrees.size() - 1);
        Degree randomDegree = randomDegrees.remove(randomIndex);
        return randomDegree;
    }

    private static List<Integer> fretPositions = new ArrayList<Integer>();

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
        if (fretPositions.size() == 0) {
            fretPositions.addAll(Arrays.asList(new Integer[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }));
        }
        int indexFretPosition = LessonsUtils.random(0, fretPositions.size() - 1);
        int fretPosition = fretPositions.remove(indexFretPosition);

        int fretPositionEnd = fretPosition + gst.numOfFrets() - 1;
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

    private static List<ScaleGrid.Type> randomScalegridType = new ArrayList<ScaleGrid.Type>();

    public static ScaleGrid.Type randomScalegridType() {
        if (randomScalegridType.size() == 0) {
            randomScalegridType.addAll(Arrays.asList(ScaleGrid.Type.values()));
        }

        int indexScalegridType = LessonsUtils.random(0, randomScalegridType.size() - 1);
        Type scalegridTyp = randomScalegridType.remove(indexScalegridType);

        return scalegridTyp;
    }

    // used for randomNote() calls
    private static List<Key> randomKey = new ArrayList<Key>();

    /**
     * Return random note in one of the main keys {C, D, ..}.
     * 
     * @return
     */
    public static Note randomNote() {

        boolean isDebugMode = GuitarTrainerApplication.getPrefs().getBoolean(SettingsActivity.KEY_DEBUG_MODE, true);

        if (randomKey.size() == 0) {
            randomKey.addAll(Arrays.asList(Key.mainKeys));
        }

        int indexKey = LessonsUtils.random(0, randomKey.size() - 1);
        Key key = randomKey.remove(indexKey);

        Octave octave = Octave.IV;
        if (isDebugMode) {
            int indexOctave = LessonsUtils.random(0, Octave.values().length - 1);
            octave = Octave.values()[indexOctave];
        }

        Note note = NoteStave.getInstance().resolveNote(key, octave);

        return note;
    }

    public static Position randomPosition() {
        boolean isDebugMode = GuitarTrainerApplication.getPrefs().getBoolean(SettingsActivity.KEY_DEBUG_MODE, true);

        Position pos;
        boolean isMainKey = false;
        do {

            int str = LessonsUtils.random(1, 6);
            int fret = LessonsUtils.random(0, (isDebugMode) ? 5 : GuitarUtils.FRETS_ON_GUITAR);
            pos = new Position(str, fret);
            Note note = GuitarFingeringHelper.getInstance().resolveNote(pos);
            isMainKey = Arrays.binarySearch(Key.mainKeys, note.getKey()) >= 0;
        } while (!isMainKey);

        return pos;
    }

    private static List<Degree[]> randomChords = new ArrayList<Degree[]>();

    public static Degree[] randomChord() {
        if (randomChords.size() == 0) {
            randomChords.addAll(Arrays.asList(Chord.CHORDS));
        }

        int indexChord = LessonsUtils.random(0, randomChords.size() - 1);
        Degree[] chord = randomChords.remove(indexChord);
        return chord;
    }
}
