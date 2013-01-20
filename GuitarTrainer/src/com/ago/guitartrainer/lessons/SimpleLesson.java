package com.ago.guitartrainer.lessons;

import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;

import com.ago.guitartrainer.events.OnViewSelectionListener;
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

public class SimpleLesson implements ILesson {

    private String TAG = "GT-SimpleLesson";

    private FretView fretView;

    private NotesView notesView;

    private Note expectedNote;

    private TextView tvLessonStatus;

    /** counts the lessons */
    private int counter = 0;

    @Override
    public String getTitle() {
        return getClass().getSimpleName();
    }

    @Override
    public long getDuration() {
        // TODO Auto-generated method stub
        return 123;
    }

    @Override
    public void start() {
        /*-
         * TODO:
         *   * highlight the fret as input (with red background)
         *   * disable input for the fret
         *   * enable input for the NotesView (btw, add "Submit" button or similar to NotesView)
         *     the submission on the NotesView must lead to answer evaluation.
         *     possible scenario: evaluate on every NoteView change (?)
         *     possible scenario: gestures to select note, instead of pressing buttons
         *   * disable view - degree, shape - no participating in lesson 
         *   * randomly select position and draw it on the fret
         * 
         */

        // fretView.isParameter(true);
        // fretView.setOnSelectionListener();

        // Note: not required, we use it randomly in the lesson itself
        // fretView.isRandom(true);

        /*
         * It is not clear in advance, which UI controls the lesson may require. The lessons are quite different. So we
         * just pick required controls from MainFragment.
         * 
         * The assumption is, that the MainFragment exists and is visible. We also may wish to set it per injection
         * later and check if it is visible every time when using its widgets.
         * 
         * The viable alternative could also be provide necessary calls - like, registerForNoteSelection() - on the
         * MainFragment itself. But it could result in to much methods on it. Basically, every view would require
         * several methods associated with it.
         */

        // initialize views required for the current type of lesson
        MainFragment uiControls = MainFragment.getInstance();
        fretView = uiControls.getFretView();
        notesView = uiControls.getNotesView();
        tvLessonStatus = uiControls.getLessonStatusView();

        OnViewSelectionListener<Note> onSelectionListener = new InnerOnSelectionListener();
        notesView.registerElementSelectedListener(onSelectionListener);

        next();
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
    private void next() {

        counter++;

        fretView.clearFret();
        
        MainFragment.getInstance().getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                tvLessonStatus.setText(String.valueOf(counter));

            }
        });

        int str = LessonsUtils.random(1, 6);
        int fret = LessonsUtils.random(0, 5);

        Position pos = new Position(str, fret);
        fretView.showOnFret(pos);

        expectedNote = NoteStave.getInstance().resolveNote(pos);

        Log.d(TAG, "Position: " + pos + ", Note: " + expectedNote);
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
    public class InnerOnSelectionListener implements OnViewSelectionListener<Note> {

        @Override
        public void onViewElementSelected(final Note note) {
            // TODO: user UI widget to inform about answer correctness

            MainFragment.getInstance().getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Log.d(TAG, "Notes soll/ist: " + expectedNote + "/" + note);
                    if (note.equals(expectedNote)) {
                        SimpleLesson.this.next();
                        tvLessonStatus.setBackgroundColor(Color.GREEN);
                    } else {
                        tvLessonStatus.setBackgroundColor(Color.RED);
                    }

                    tvLessonStatus.setText(String.valueOf(counter));

                }
            });

        }
    }

}
