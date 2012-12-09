package com.ago.guitartrainer.gridshapes;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.ago.guitartrainer.Degree;
import com.ago.guitartrainer.Notes;
import com.ago.guitartrainer.Notes.Note;

public abstract class GridShape {

    /* [string][fret] */

    private int[] rootStrings;

    private Note key;

    private Map<Degree, List<int[]>> degreeToPosition = new Hashtable<Degree, List<int[]>>();

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
     * TODO: calculate the rootStrings from the shape itself. It can be done after degreeToPosition is calcualted
     */
    protected GridShape(Degree[] zeroFretDegrees, int frets, int[] rootStrings) {
        this.rootStrings = rootStrings;

        // initialize mapping of degrees to positions
        for (int i = 0; i < zeroFretDegrees.length; i++) {
            Degree d = zeroFretDegrees[i];
            for (int f = 0; f < frets; f++) {
                Degree calcD = d.addFrets(f);
                {
                    calcD = d.addFrets(f);
                }

                if (!degreeToPosition.containsKey(calcD)) {
                    degreeToPosition.put(calcD, new ArrayList<int[]>());
                }

                degreeToPosition.get(calcD).add(new int[] { i, f });
            }
        }
    }

    /**
     * Set the root note of the shape.
     * 
     * @param key the note which must be the root of the shape
     */
    protected void setKey(Note key) {
        this.key = key;
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
    public List<Note> calcualteNotes(Degree degree) {

        Notes notes = new Notes();

        /*
         * shifts of the shape required relatively to the fret "0", so that the root key is positioned on the place of
         * note defined in "key" variable.
         */
        int fretShifts = -1;

        outerloop: for (int i = 0; i <= 12; i++) {
            for (int j = 0; j < rootStrings.length; j++) {
                Note n = notes.resolveNote(rootStrings[j], i);
                if (n == key) {
                    fretShifts = i;
                    break outerloop;
                }
            }
        }

        List<int[]> positions = degreeToPosition.get(degree);

        List<Note> results = new ArrayList<Notes.Note>();
        for (int[] pos : positions) {
            Note n = notes.resolveNote(pos[0], pos[1] + fretShifts);
            results.add(n);
        }

        System.out.println("key: " + key);
        System.out.println("degree: " + degree);
        System.out.println("shift: " + fretShifts);
        for (Note note : results) {
            System.out.println("note: " + note);
        }

        return results;
    }

}
