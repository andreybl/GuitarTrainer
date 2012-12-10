package com.ago.guitartrainer.events;

import com.ago.guitartrainer.notation.Note;
import com.ago.guitartrainer.notation.Position;

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
     * Position of the note
     * 
     * The non-wired algorithm (e.g. FFT) can not in its essence tell, which position is responsible for the note
     * sounding.
     */
    private Position position;

    public NoteEventType type;
    
    public double pitch;
    
    public double normalAmpli;

    /**
     * Timestamp of the event. 
     * 
     * Listeners may decide to ignore the event based on the timestamp.
     */
    public long timestamp;

    public NotePlayingEvent(Note note, double pitch, double normalAmpli, NoteEventType type, long tst) {
        this.note = note;
        this.type = type;
        this.timestamp = tst;
        this.pitch = pitch;
        this.normalAmpli = normalAmpli;
    }

    @Override
    public String toString() {
        String str = "Event ["+timestamp+"]: " + note + ", " +type;
        return str;
    }

}
