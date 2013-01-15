package com.ago.guitartrainer.ui;

import java.util.Hashtable;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ago.guitartrainer.R;
import com.ago.guitartrainer.notation.Key;
import com.ago.guitartrainer.notation.Note;
import com.ago.guitartrainer.notation.NoteStave;
import com.ago.guitartrainer.notation.Octave;

/**
 * The {@link NotesView} is used to represent a note on the note stave.
 * 
 * The view can be used both for output and for user input. If used for output, the listeners may be registered on the
 * view to receive notifications about note selected by user.
 * 
 * The view consists basically from:
 * <ul>
 * <li>picture of the note on a stave
 * <li>possible keys: C, D, E, F, G, A, B
 * <li>possible octaves: II, III, IV, V
 * </ul>
 * 
 * The scientific notation is used to represent notes.
 * 
 * @author Andrej Golovko - jambit GmbH
 * 
 */
public class NotesView extends LinearLayout {

    private static String TAG = "GT-NotesView";

    private Key selectedKey = Key.E;

    /** octave selected according to scientific pitch notation */
    private Octave selectedOctave = Octave.II;

    private ImageView imgNote;

    private Map<Button, Key> btn2Key = new Hashtable<Button, Key>();

    private Map<Button, Octave> btn2Octave = new Hashtable<Button, Octave>();

    private static Map<Note, Integer> note2DrawableId = new Hashtable<Note, Integer>();
    {
        note2DrawableId.put(Note.E2, R.drawable.note_e2);
        note2DrawableId.put(Note.F2, R.drawable.note_f2);
        note2DrawableId.put(Note.G2, R.drawable.note_g2);
        note2DrawableId.put(Note.A2, R.drawable.note_a2);
        note2DrawableId.put(Note.B2, R.drawable.note_b2);
        note2DrawableId.put(Note.C3, R.drawable.note_c3);
        note2DrawableId.put(Note.D3, R.drawable.note_d3);
        note2DrawableId.put(Note.E3, R.drawable.note_e3);
        note2DrawableId.put(Note.F3, R.drawable.note_f3);
        note2DrawableId.put(Note.G3, R.drawable.note_g3);
        note2DrawableId.put(Note.A3, R.drawable.note_a3);
        note2DrawableId.put(Note.B3, R.drawable.note_b3);
        note2DrawableId.put(Note.C4, R.drawable.note_c4);
        note2DrawableId.put(Note.D4, R.drawable.note_d4);
        note2DrawableId.put(Note.E4, R.drawable.note_e4);
        note2DrawableId.put(Note.F4, R.drawable.note_f4);
        note2DrawableId.put(Note.G4, R.drawable.note_g4);
        note2DrawableId.put(Note.A4, R.drawable.note_a4);
        note2DrawableId.put(Note.B4, R.drawable.note_b4);
        note2DrawableId.put(Note.C5, R.drawable.note_c5);
        note2DrawableId.put(Note.D5, R.drawable.note_d5);
        note2DrawableId.put(Note.E5, R.drawable.note_e5);
        note2DrawableId.put(Note.F5, R.drawable.note_f5);

    }

    public NotesView(Context context) {
        super(context);

        init();
    }

    public NotesView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public NotesView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    private void init() {
        View mainLayout = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.notes_view, this, true);

        imgNote = (ImageView) mainLayout.findViewById(R.id.note_image);

        btn2Key.put((Button) mainLayout.findViewById(R.id.key_c), Key.C);
        btn2Key.put((Button) mainLayout.findViewById(R.id.key_d), Key.D);
        btn2Key.put((Button) mainLayout.findViewById(R.id.key_e), Key.E);
        btn2Key.put((Button) mainLayout.findViewById(R.id.key_f), Key.F);
        btn2Key.put((Button) mainLayout.findViewById(R.id.key_g), Key.G);
        btn2Key.put((Button) mainLayout.findViewById(R.id.key_a), Key.A);
        btn2Key.put((Button) mainLayout.findViewById(R.id.key_b), Key.B);

        btn2Octave.put((Button) mainLayout.findViewById(R.id.octave_2), Octave.II);
        btn2Octave.put((Button) mainLayout.findViewById(R.id.octave_3), Octave.III);
        btn2Octave.put((Button) mainLayout.findViewById(R.id.octave_4), Octave.IV);
        btn2Octave.put((Button) mainLayout.findViewById(R.id.octave_5), Octave.V);

        OnClickListener onClickListener1 = new KeyOnClickListener();
        for (Button btn : btn2Key.keySet()) {
            btn.setOnClickListener(onClickListener1);

            if (btn2Key.get(btn) == selectedKey)
                btn.setTextColor(Color.GREEN);
        }

        OnClickListener onClickListener2 = new OctaveOnClickListener();
        for (Button btn : btn2Octave.keySet()) {
            btn.setOnClickListener(onClickListener2);
            if (btn2Octave.get(btn) == selectedOctave)
                btn.setTextColor(Color.GREEN);
        }

        showNote(selectedKey, selectedOctave);
    }

    private void showNote(Key selectedKey2, Octave selectedOctave2) {

        Activity activity = (Activity) getContext();
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Note selectedNote = NoteStave.getInstance().resolveNote(selectedKey, selectedOctave);
                if (selectedNote != null) {
                    int idOfNoteDrawable = note2DrawableId.get(selectedNote);
                    imgNote.setImageResource(idOfNoteDrawable);
                } else {
                    Log.w(TAG, "Failed to resolve image for: " + selectedKey + "/" + selectedOctave);
                }

            }
        });

    }

    private class KeyOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {

            // make selected another button
            for (Button btn : btn2Key.keySet()) {
                btn.setTextColor(Color.WHITE);
            }

            Button b = (Button) v;
            b.setTextColor(Color.GREEN);

            selectedKey = btn2Key.get(v);

            showNote(selectedKey, selectedOctave);

            // TODO: maybe disable Octave buttons, which are not possible with selected Key
        }
    }

    private class OctaveOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {

            // make selected another button
            for (Button btn : btn2Octave.keySet()) {
                btn.setTextColor(Color.WHITE);
            }

            Button b = (Button) v;
            b.setTextColor(Color.GREEN);

            selectedOctave = btn2Octave.get(v);

            showNote(selectedKey, selectedOctave);

            // TODO: maybe disable Key buttons, which are not possible with selected Octave
        }
    }

}
