package com.ago.guitartrainer.notation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.ago.guitartrainer.utils.MultiMap;

/**
 * The notes representation used - like, E2 - is a Scientific Pitch Notation.
 * 
 * @author Andrej Golovko - jambit GmbH
 * 
 */
public class NoteStave {

    private static NoteStave INSTANCE;

    private SortedMap<Note, Double> mapNote2Frequency = new TreeMap<Note, Double>();

    private MultiMap<Key, Octave, Note> mapKeyOctave2Note = new MultiMap<Key, Octave, Note>();

    private NoteStave() {
        initNote2Freq();
    }

    public static NoteStave getInstance() {
        if (INSTANCE == null)
            INSTANCE = new NoteStave();
        return INSTANCE;
    }

    private void initNote2Freq() {

        registerNote(Note.D2di);
        registerNote(Note.E2);
        registerNote(Note.F2);
        registerNote(Note.F2di);
        registerNote(Note.G2);
        registerNote(Note.G2di);
        registerNote(Note.A2);
        registerNote(Note.A2di);
        registerNote(Note.B2);
        registerNote(Note.C3);  //10
        registerNote(Note.C3di);
        registerNote(Note.D3);
        registerNote(Note.D3di);
        registerNote(Note.E3);
        registerNote(Note.F3);
        registerNote(Note.F3di);
        registerNote(Note.G3);
        registerNote(Note.G3di);
        registerNote(Note.A3);
        registerNote(Note.A3di); // 20
        registerNote(Note.B3);
        registerNote(Note.C4);
        registerNote(Note.C4di);
        registerNote(Note.D4);
        registerNote(Note.D4di);
        registerNote(Note.E4);
        registerNote(Note.F4);
        registerNote(Note.F4di);
        registerNote(Note.G4);
        registerNote(Note.G4di); // 30
        registerNote(Note.A4);
        registerNote(Note.A4di);
        registerNote(Note.B4);
        registerNote(Note.C5);
        registerNote(Note.C5di);
        registerNote(Note.D5);
        registerNote(Note.D5di);
        registerNote(Note.E5);
        registerNote(Note.F5);

    }

    private List<Note> notesOrdered = new ArrayList<Note>();

    private void registerNote(Note note) {

        mapNote2Frequency.put(note, note.getPitch());
        mapKeyOctave2Note.put(note.getKey(), note.getOctave(), note);

        /** IMPORTANT: assumption is, that the registerNote() is called on notes in ordered manner. */
        /*
         * TODO: the next() and previouse are using the notesOrdered. Implement them in a way, so that the notesOrdered
         * are not required.
         */
        notesOrdered.add(note);

    }

    public Note resolveNote(double pitch) {

        /*
         * 1. resolve the closest frequency to the pitch
         */
        Note note = null;
        double prevDiff = Double.MAX_VALUE;
        Iterator<Note> i = mapNote2Frequency.keySet().iterator();
        while (i.hasNext()) {
            Note key = i.next();
            Double curr = mapNote2Frequency.get(key);
            double currDiff = curr - pitch;
            if (note == null) {
                note = key;
            } else {

                if (Math.abs(currDiff) < prevDiff) {
                    note = key;
                }
            }

            prevDiff = Math.abs(currDiff);

        }

        return note;
    }

    /** inner class to do soring of the map **/
    private class ValueComparer implements Comparator {
        private Map _data = null;

        public ValueComparer(Map data) {
            super();
            _data = data;
        }

        public int compare(Object o1, Object o2) {
            Double e1 = (Double) _data.get(o1);
            Double e2 = (Double) _data.get(o2);
            return e1.compareTo(e2);
        }
    }

    public Note resolveNote(Key key, Octave octave) {
        Note note = mapKeyOctave2Note.get(key, octave);

        return note;
    }

    /**
     * Return the next note relative to the <code>note</code> passed as parameter.
     * 
     * For instance, the next to the A3 is A3di.
     * 
     * @param note
     *            relative to which the next note must be returned
     * @return next note relative to parameter note
     */
    public Note next(Note note) {

        int index = notesOrdered.indexOf(note);
        int nextIndex = (index + 1);
        Note next = note;
        if (nextIndex < notesOrdered.size()) {
            next = notesOrdered.get(nextIndex);
        }

        return next;
    }

    /**
     * Basically the same as {@link #next(Note)}, but return the {@link Note} with no sharp/flat.
     * 
     * @param note
     * @return
     */
    public Note nextHasNoSharpsFlats(Note note) {
        Key[] mainDegrees = new Key[] { Key.C, Key.D, Key.E, Key.F, Key.G, Key.A, Key.B };
        Note noteHighest = Note.values()[0];
        Note result = note;
        boolean finishSearch = false;
        do {
            result = next(result);
            finishSearch = Arrays.binarySearch(mainDegrees, result.getKey()) >= 0 || result == noteHighest;
        } while (!finishSearch);

        return result;
    }

    /**
     * Return the previous note relative to the <code>note</code> passed as parameter.
     * 
     * For instance, the previous to the A3 is G3di.
     * 
     * @param note
     *            relative to which the previous note must be returned
     * @return next note relative to parameter note
     */
    public Note previous(Note note) {
        int index = notesOrdered.indexOf(note);
        int prevIndex = (index - 1);
        Note next = note;
        if (prevIndex >= 0) {
            next = notesOrdered.get(prevIndex);
        }

        return next;
    }

    /**
     * Basically the same as {@link #previous(Note)}, but return the {@link Note} with no sharp/flat.
     * 
     * @param note
     * @return
     */
    public Note previousHasNoSharpsFlats(Note note) {
        Key[] mainDegrees = new Key[] { Key.C, Key.D, Key.E, Key.F, Key.G, Key.A, Key.B };
        Note result = note;
        Note noteLowest = Note.values()[0];
        boolean finishSearch = false;
        do {
            result = previous(result);
            finishSearch = Arrays.binarySearch(mainDegrees, result.getKey()) >= 0 || result == noteLowest;
        } while (!finishSearch);

        return result;
    }
}
