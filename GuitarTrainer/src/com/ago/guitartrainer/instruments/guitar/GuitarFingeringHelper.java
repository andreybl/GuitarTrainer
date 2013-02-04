package com.ago.guitartrainer.instruments.guitar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.ago.guitartrainer.notation.Note;

/**
 * For the guitar, the following are the notes in this notation on the strings:
 * <ul>
 * <li>The first string is E4
 * <li>The second string is B3
 * <li>The third string is G3
 * <li>The fourth string D3
 * <li>The fifth string is A2
 * <li>The sixth string is E2
 * </ul>
 * 
 * @author Andrej Golovko - jambit GmbH
 * @link http://guitar-trivia.blogspot.de/2011_06_01_archive.html
 * 
 */
public class GuitarFingeringHelper {

    /*-
     * the format of the array is:
     * [string][fret] 
     * 
     * The fret==0 has the meaning of "open fret".
     */
    private Note[][] notesOnFret = new Note[6][13];

    private Map<Note, List<Position>> mapNote2Positions = new Hashtable<Note, List<Position>>();

    private static GuitarFingeringHelper INSTANCE;

    private void initNotesOnFret() {

        // E - open 1st string
        notesOnFret[0][0] = Note.E4;
        notesOnFret[0][1] = Note.F4;
        notesOnFret[0][2] = Note.F4di;
        notesOnFret[0][3] = Note.G4;
        notesOnFret[0][4] = Note.G4di;
        notesOnFret[0][5] = Note.A4;
        notesOnFret[0][6] = Note.A4di;
        notesOnFret[0][7] = Note.B4;
        notesOnFret[0][8] = Note.C5;
        notesOnFret[0][9] = Note.C5di;
        notesOnFret[0][10] = Note.D5;
        notesOnFret[0][11] = Note.D5di;
        notesOnFret[0][12] = Note.E5;

        // B - open 2nd string
        notesOnFret[1][0] = Note.B3;
        notesOnFret[1][1] = Note.C4;
        notesOnFret[1][2] = Note.C4di;
        notesOnFret[1][3] = Note.D4;
        notesOnFret[1][4] = Note.D4di;
        notesOnFret[1][5] = Note.E4;
        notesOnFret[1][6] = Note.F4;
        notesOnFret[1][7] = Note.F4di;
        notesOnFret[1][8] = Note.G4;
        notesOnFret[1][9] = Note.G4di;
        notesOnFret[1][10] = Note.A4;
        notesOnFret[1][11] = Note.A4di;
        notesOnFret[1][12] = Note.B4;

        // G - open 3rd string
        notesOnFret[2][0] = Note.G3;
        notesOnFret[2][1] = Note.G3di;
        notesOnFret[2][2] = Note.A3;
        notesOnFret[2][3] = Note.A3di;
        notesOnFret[2][4] = Note.B3;
        notesOnFret[2][5] = Note.C4;
        notesOnFret[2][6] = Note.C4di;
        notesOnFret[2][7] = Note.D4;
        notesOnFret[2][8] = Note.D4di;
        notesOnFret[2][9] = Note.E4;
        notesOnFret[2][10] = Note.F4;
        notesOnFret[2][11] = Note.F4di;
        notesOnFret[2][12] = Note.G4;

        // D - open 4th string
        notesOnFret[3][0] = Note.D3;
        notesOnFret[3][1] = Note.D3di;
        notesOnFret[3][2] = Note.E3;
        notesOnFret[3][3] = Note.F3;
        notesOnFret[3][4] = Note.F3di;
        notesOnFret[3][5] = Note.G3;
        notesOnFret[3][6] = Note.G3di;
        notesOnFret[3][7] = Note.A3;
        notesOnFret[3][8] = Note.A3di;
        notesOnFret[3][9] = Note.B3;
        notesOnFret[3][10] = Note.C4;
        notesOnFret[3][11] = Note.C4di;
        notesOnFret[3][12] = Note.D4;

        // A - open 5th string
        notesOnFret[4][0] = Note.A2;
        notesOnFret[4][1] = Note.A2di;
        notesOnFret[4][2] = Note.B2;
        notesOnFret[4][3] = Note.C3;
        notesOnFret[4][4] = Note.C3di;
        notesOnFret[4][5] = Note.D3;
        notesOnFret[4][6] = Note.D3di;
        notesOnFret[4][7] = Note.E3;
        notesOnFret[4][8] = Note.F3;
        notesOnFret[4][9] = Note.F3di;
        notesOnFret[4][10] = Note.G3;
        notesOnFret[4][11] = Note.G3di;
        notesOnFret[4][12] = Note.A3;

        // E - open 6th string
        notesOnFret[5][0] = Note.E2;
        notesOnFret[5][1] = Note.F2;
        notesOnFret[5][2] = Note.F2di;
        notesOnFret[5][3] = Note.G2;
        notesOnFret[5][4] = Note.G2di;
        notesOnFret[5][5] = Note.A2;
        notesOnFret[5][6] = Note.A2di;
        notesOnFret[5][7] = Note.B2;
        notesOnFret[5][8] = Note.C3;
        notesOnFret[5][9] = Note.C3di;
        notesOnFret[5][10] = Note.D3;
        notesOnFret[5][11] = Note.D3di;
        notesOnFret[5][12] = Note.E3;

        //
        for (int iString = 0; iString < notesOnFret.length; iString++) {
            for (int iFret = 0; iFret < notesOnFret[iString].length; iFret++) {
                Note note = notesOnFret[iString][iFret];

                List<Position> list;
                if (!mapNote2Positions.containsKey(note)) {
                    list = new ArrayList<Position>();
                    mapNote2Positions.put(note, list);
                } else {
                    list = mapNote2Positions.get(note);
                }

                int string = (iString + 1);
                list.add(new Position(string, iFret));
            }
        }

    }

    private GuitarFingeringHelper() {
        initNotesOnFret();
    }

    public static GuitarFingeringHelper getInstance() {
        if (INSTANCE == null)
            INSTANCE = new GuitarFingeringHelper();
        return INSTANCE;
    }

    /**
     * Resolve note on the guitar fret by indexing it with string and fret.
     * 
     * @return
     */
    public Note resolveNote(Position p) {
        return notesOnFret[p.getStringIndex()][p.getFret()];
    }

    public List<Position> resolvePositions(Note n) {
        List<Position> list = new ArrayList<Position>();
        if (mapNote2Positions.containsKey(n)) {
            list.addAll(mapNote2Positions.get(n));
        }

        return list;
    }
}
