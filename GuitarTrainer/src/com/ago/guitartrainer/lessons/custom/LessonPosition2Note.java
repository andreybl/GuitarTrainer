package com.ago.guitartrainer.lessons.custom;

import org.apache.http.MethodNotSupportedException;

import android.graphics.Color;
import android.util.Log;

import com.ago.guitartrainer.R;
import com.ago.guitartrainer.events.OnViewSelectionListener;
import com.ago.guitartrainer.lessons.AQuestion;
import com.ago.guitartrainer.notation.Note;
import com.ago.guitartrainer.notation.NoteStave;
import com.ago.guitartrainer.notation.Position;
import com.ago.guitartrainer.ui.FretView;
import com.ago.guitartrainer.ui.FretView.Layer;
import com.ago.guitartrainer.ui.LearningStatusView;
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

public class LessonPosition2Note extends ALesson {

    private String TAG = "GT-SimpleLesson";

    private FretView fretView;

    private NotesView notesView;

    private Note expectedNote;

    private LearningStatusView learningStatusView;

    private Layer layerLesson = new Layer(FretView.LAYER_Z_LESSON, MainFragment.getInstance().getResources()
            .getColor(R.color.blue));

    @Override
    public String getTitle() {
        String str = MainFragment.getInstance().getResources().getString(R.string.lesson_position2note_title);
        return str;
    }

    @Override
    public String getDescription() {
        String str = MainFragment.getInstance().getResources().getString(R.string.lesson_position2note_description);
        return str;
    }

    @Override
    public long getDuration() {
        // TODO Auto-generated method stub
        return 123;
    }

    @Override
    public void doPrepareUi() {
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
        learningStatusView = uiControls.getLearningStatusView();

        fretView.setEnabled(true);
        notesView.setEnabled(true);
        notesView.setEnabledInput(true);

        uiControls.getDegreesView().setEnabled(false);
        uiControls.getShapestView().setEnabled(false);

        OnViewSelectionListener<Note> onSelectionListener = new InnerOnSelectionListener();
        notesView.registerListener(onSelectionListener);

    }

    @Override
    public void doStop() {
        fretView.clearLayer(layerLesson);
    }

    @Override
    public void showMetrics() {
        // TODO Auto-generated method stub

    }

    /**
     * Skip to the next lesson.
     * 
     * The answer results are not important.
     * 
     **/
    @Override
    public void doNext() {

        fretView.clearLayer(layerLesson);

        MainFragment.getInstance().getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // TODO: learning status
                // learningStatusView.setText(String.valueOf(counter));

            }
        });

        int str = LessonsUtils.random(1, 6);
        int fret = LessonsUtils.random(0, 5);

        Position pos = new Position(str, fret);
        fretView.show(layerLesson, pos);

        expectedNote = NoteStave.getInstance().resolveNote(pos);

        Log.d(TAG, "Position: " + pos + ", Note: " + expectedNote);
    }

    @Override
    protected AQuestion getCurrentQuestion() {
        return null;
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
                        LessonPosition2Note.this.next();
                        learningStatusView.setBackgroundColor(Color.GREEN);
                    } else {
                        learningStatusView.setBackgroundColor(Color.RED);
                    }

                    // TODO: learning status
                    // learningStatusView.setText(String.valueOf(counter));

                }
            });

        }
    }

}
