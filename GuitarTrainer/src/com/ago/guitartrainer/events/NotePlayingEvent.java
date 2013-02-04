package com.ago.guitartrainer.events;

import java.util.List;

import com.ago.guitartrainer.instruments.guitar.Position;
import com.ago.guitartrainer.notation.Note;

/**
 * The event is generated for the note.
 * 
 * @author Andrej Golovko - jambit GmbH
 * 
 */
public class NotePlayingEvent {

    /** The note itself */
    public Note note;

    /**
     * Position of the note.
     * 
     * The problem especially with a guitar is, that the same pitch can be played in different positions. Maybe with
     * other musical instruments the direct pitch-to-position is direct.
     * 
     * 
     * The position of the note is not always available and depends on the event source:
     * <ul>
     * <li>no position, if detected with FFT
     * <li>there is position, if the note selected by user touching the fret image
     * <li>there is position, when note is detected with some wired approach (e.g. non-FFT based approach)
     * </ul>
     * 
     * 
     */
    public Position position;

    public NoteEventType type;

    public double pitch;

    public double normalAmpli;

    /**
     * Timestamp of the event.
     * 
     * Listeners may decide to ignore the event based on the timestamp.
     */
    public long timestamp;

    /**
     * possible positions, which could lead to the note being played.
     * 
     * With FFT it is not possible to detect the played position exactly, so we supply possible positions.
     */
    public List<Position> possiblePositions;

    /**
     * Constructor to be used, when the event is generated as a result of pitch detection - like, with some FFT-based
     * algorithm.
     * 
     * */
    public NotePlayingEvent(Note note, double pitch, double normalAmpli, NoteEventType type, long tst) {
        this.note = note;
        this.type = type;
        this.timestamp = tst;
        this.pitch = pitch;
        this.normalAmpli = normalAmpli;
    }

    public NotePlayingEvent(Note note, Position pos) {
        this.note = note;
        this.position = pos;
        this.timestamp = System.currentTimeMillis();
        this.type = NoteEventType.NOTE_PRESSED_STARTED;
    }

    @Override
    public String toString() {
        String str = "Event [" + timestamp + "]: " + note + ", " + type;
        return str;
    }

}
