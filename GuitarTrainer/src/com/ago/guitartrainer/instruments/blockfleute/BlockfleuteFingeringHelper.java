package com.ago.guitartrainer.instruments.blockfleute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ago.guitartrainer.notation.Note;

// TODO: rename to fingeringChart
public class BlockfleuteFingeringHelper {

    private static BlockfleuteFingeringHelper INSTANCE;

    private Map<Note, List<PositionBlockfleute>> mapNote2Position = new HashMap<Note, List<PositionBlockfleute>>();

    private Map<PositionBlockfleute, Note> mapPosition2Note = new HashMap<PositionBlockfleute, Note>();

    public static BlockfleuteFingeringHelper getInstance() {
        if (INSTANCE == null)
            INSTANCE = new BlockfleuteFingeringHelper();
        return INSTANCE;
    }

    private BlockfleuteFingeringHelper() {
        initNotesFingering();
    }

    private void initNotesFingering() {
        {
            // Note.C3
            Hole[] closedHoles = new Hole[] { Hole.HOLE1, Hole.HOLE2, Hole.HOLE3, Hole.HOLE4, Hole.HOLE5, Hole.HOLE6,
                    Hole.HOLE7, Hole.HOLE8, Hole.HOLE9, Hole.HOLE10 };
            register(Note.C3, closedHoles);
        }
        // ...
        {
            // Note.C4
            Hole[] closedHoles = new Hole[] { Hole.HOLE1, Hole.HOLE3 };
            register(Note.C4, closedHoles);
        }

    }

    private void register(Note note, Hole[] closedHoles) {
        PositionBlockfleute fingering = new PositionBlockfleute(closedHoles);
        if (mapNote2Position.containsKey(note)) {
            List<PositionBlockfleute> list = mapNote2Position.get(note);
            list.add(fingering);
        } else {
            List<PositionBlockfleute> list = new ArrayList<PositionBlockfleute>();
            list.add(fingering);
            mapNote2Position.put(note, list);
        }

        mapPosition2Note.put(fingering, note);
    }

    public Note resolveNote(PositionBlockfleute p) {
        if (mapPosition2Note.containsKey(p))
            return mapPosition2Note.get(p);

        return null;
    }

    public List<PositionBlockfleute> resolvePositions(Note n) {
        if (mapNote2Position.containsKey(n))
            return mapNote2Position.get(n);

        return null;
    }

    public PositionBlockfleute resolvePosition(Note n) {
        if (mapNote2Position.containsKey(n)) {
            List<PositionBlockfleute> list = mapNote2Position.get(n);
            return list.get(0);
        }

        return null;
    }

}
