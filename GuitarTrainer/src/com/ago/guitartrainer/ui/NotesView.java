package com.ago.guitartrainer.ui;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
 * 
 * @author Andrej Golovko - jambit GmbH
 * 
 */
public class NotesView extends AInoutView<Note> {

    private Key selectedKey = Key.E;

    /** octave selected according to scientific pitch notation */
    private Octave selectedOctave = Octave.II;

    private ImageView imgNote;

    private GestureDetector gestures;

    /** the note selected, must be in sync with {@link #selectedKey} and {@link #selectedOctave}. */
    private Note selectedNote = Note.E2;

    private int lastSelectedNoteDrawable = -1;

    private TextView tvNoteText;

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

        tvNoteText = (TextView) mainLayout.findViewById(R.id.note_text);

        gestures = new GestureDetector(getContext(), new GestureListener());

        showNote(selectedKey, selectedOctave);
    }

    public void showNote(Note note) {
        showNote(note.getKey(), note.getOctave());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        /**
         * make the GestureDetector the final word in all touch events for this custom View. However, the
         * GestureDetector doesnÕt actually do anything with motion events, it simply recognizes them and makes a call
         * to the registered GestureListener class.
         */

        /*
         * TODO: if I touch the screen, hold it a second and only after that make a drawing - such kind of drawing seems
         * NOT to be recognized as a gesture.
         */
        return gestures.onTouchEvent(event);
    }

    /**
     * Show the note image in the view.
     * 
     * @param selectedKey2
     * @param selectedOctave2
     */
    /*
     * TODO: the buttons must be colored correctly, for the case the showNote() is called outside of the view
     */
    private void showNote(final Key key, final Octave octave) {

        Activity activity = (Activity) getContext();
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Note selectedNote = NoteStave.getInstance().resolveNote(key, octave);
                if (selectedNote != null) {
                    if (note2DrawableId.containsKey(selectedNote)) {
                        int idOfNoteDrawable = note2DrawableId.get(selectedNote);
                        imgNote.setImageResource(idOfNoteDrawable);
                        // String txtNote = selectedNote.toString() + " (Key: " + selectedNote.getKey() + "; Octave: "
                        // + selectedNote.getOctave() + "; Pitch=" + selectedNote.getPitch() + ")";
                        String txtNote = selectedNote.toString() + " (" + selectedNote.getPitch() + " Hz)";
                        tvNoteText.setText(txtNote);
                    }
                } else {
                    Log.w(getTagLogging(), "Failed to resolve image for: " + key + "/" + octave);
                }

            }
        });

    }

    /**
     * {@inheritDoc}
     * 
     * In the isEnabled state the view widgets - e.g. buttons, images etc. - are enabled. It means they are not gray'ed
     * out or so. But it does not mean that input to the view is possible. Like, it is still possible that no buttons
     * are allowed to be pressed in the isEnabled view.
     */
    @Override
    public void setEnabled(boolean enabled) {

        if (!enabled) {
            /*
             * when disabling, cache the ID of the drawable for last note.
             * 
             * for some reason we can not get it from ImageView - eg. not imgNote.getImageResource(), so we use a
             * workaround.
             */
            Note selectedNote = NoteStave.getInstance().resolveNote(selectedKey, selectedOctave);
            lastSelectedNoteDrawable = note2DrawableId.get(selectedNote);

            imgNote.setImageResource(R.drawable.note_disabled);
        } else {
            if (lastSelectedNoteDrawable > 0) {
                imgNote.setImageResource(lastSelectedNoteDrawable);
            }
        }

        if (!enabled)
            setEnabledInput(enabled);

        super.setEnabled(enabled);
    }

    private class GestureListener implements OnGestureListener, OnDoubleTapListener {

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            // TODO Auto-generated method stub
            /*
             * With double-tap the user confirm its selection.
             * 
             * The note visible as image is considered as being selected.
             */
            notifyListeners(selectedNote);

            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            // DO NOTHING
            return false;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            // DO NOTHING
            return false;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            /**
             * the false from the isEnabledInput() results in no touch event is processed by the current GestureLiseter.
             * 
             * In other words, the touch on the screen take no effect.
             **/
            return isEnabledInput();
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            /**
             * the method is called every time the user move the finger.
             * 
             * The MotionEvent.getY() returns the different to the touch position, which was the start of the gesture.
             * The distanceY specify the direction of the gestures on the Y-axis.
             */

            if (e2.getY() > 25) {
                if (distanceY > 0) {
                    selectedNote = NoteStave.getInstance().nextHasNoSharpsFlats(selectedNote);
                } else {
                    selectedNote = NoteStave.getInstance().previousHasNoSharpsFlats(selectedNote);
                }

                NotesView.this.showNote(selectedNote.getKey(), selectedNote.getOctave());

            }

            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            // DO NOTHING

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            // TODO Auto-generated method stub
            return false;
        }
    }
}
