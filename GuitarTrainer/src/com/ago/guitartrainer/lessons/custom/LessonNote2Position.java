package com.ago.guitartrainer.lessons.custom;

import java.util.List;

import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;

import com.ago.guitartrainer.events.NotePlayingEvent;
import com.ago.guitartrainer.events.OnViewSelectionListener;
import com.ago.guitartrainer.lessons.ILesson;
import com.ago.guitartrainer.notation.Note;
import com.ago.guitartrainer.notation.NoteStave;
import com.ago.guitartrainer.notation.Position;
import com.ago.guitartrainer.ui.FretView;
import com.ago.guitartrainer.ui.MainFragment;
import com.ago.guitartrainer.ui.NotesView;
import com.ago.guitartrainer.utils.LessonsUtils;

/**
 * The lesson implements the learning function:
 * 
 * <pre>
 *      function(position):note
 * </pre>
 * 
 * In other words, the random position is selected on the fret and demonstrated to the user. The user is suggested to
 * select the correct note on the {@link NotesView}.
 * 
 * @author Andrej Golovko - jambit GmbH
 * 
 */

public class LessonNote2Position implements ILesson {

    private String TAG = "GT-SimpleLesson";

    private FretView fretView;

    private NotesView notesView;

    private TextView tvLessonStatus;

    /** counts the lessons */
    private int counter = 0;

    private Note questionedNote;
    private List<Position> acceptedPositions;

    @Override
    public String getTitle() {
        return "Note 2 Position";
    }

    @Override
    public long getDuration() {
        // TODO Auto-generated method stub
        return 123;
    }

    @Override
    public void prepareUi() {

        // initialize views required for the current type of lesson
        MainFragment uiControls = MainFragment.getInstance();
        fretView = uiControls.getFretView();
        notesView = uiControls.getNotesView();
        tvLessonStatus = uiControls.getLessonStatusView();

        fretView.setEnabled(true);
        notesView.setEnabled(true);

        uiControls.getDegreesView().setEnabled(false);
        uiControls.getShapestView().setEnabled(false);

        OnViewSelectionListener<NotePlayingEvent> onSelectionListener = new InnerOnSelectionListener();
        fretView.registerListener(onSelectionListener);

    }

    @Override
    public void stop() {
        fretView.clearFret();
    }

    /**
     * Skip to the next lesson.
     * 
     * The answer results are not important.
     * 
     **/
    @Override
    public void next() {

        counter++;

        fretView.clearFret();

        MainFragment.getInstance().getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                tvLessonStatus.setText(String.valueOf(counter));

            }
        });

        /*
         * in the Note2Position lesson the user is presented with the (random?) note, With the answer the user must find
         * the note's position on the fret, by inputing it either manually or by playing guitar.
         * 
         * The problem is, that the note may be played in different positions. But the user is expected a unique answer.
         * A workaround is to restrict the area on the fret, from which the answer is expected. At least for the manual
         * input it will be possible to decided, if the answer is as expected.
         */

        // select random note
        Note[] notes = Note.values();
        
        // TODO: use this line, to train on the whole fret
//        int index = LessonsUtils.random(0, notes.length - 1);
        
        int index = LessonsUtils.random(0, 5);
        questionedNote = notes[index];

        // visualize it
        notesView.showNote(questionedNote);

        acceptedPositions = NoteStave.getInstance().resolvePositions(questionedNote);

        Log.d(TAG, "Note: " + questionedNote + ", Allowed positions: " + acceptedPositions);
    }

    /*
     * *** INNER CLASSES
     */
    /**
     * The only input expected from the user is note selection. And we listen to this input here.
     * 
     * @author Andrej Golovko - jambit GmbH
     * 
     */
    public class InnerOnSelectionListener implements OnViewSelectionListener<NotePlayingEvent> {

        @Override
        public void onViewElementSelected(final NotePlayingEvent npe) {
            // TODO: user UI widget to inform about answer correctness

            MainFragment.getInstance().getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    if (npe.position!=null && acceptedPositions.contains(npe.position)) {
                        LessonNote2Position.this.next();
                        tvLessonStatus.setBackgroundColor(Color.GREEN);
                        fretView.clearFret();
                    } else {
                        tvLessonStatus.setBackgroundColor(Color.RED);
                    }

                    tvLessonStatus.setText(String.valueOf(counter));

                }
            });

        }
    }

}
