package com.ago.guitartrainer.lessons.custom;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;

import com.ago.guitartrainer.R;
import com.ago.guitartrainer.events.NotePlayingEvent;
import com.ago.guitartrainer.events.OnViewSelectionListener;
import com.ago.guitartrainer.lessons.AQuestion;
import com.ago.guitartrainer.lessons.ILesson;
import com.ago.guitartrainer.lessons.LessonMetrics;
import com.ago.guitartrainer.notation.Key;
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

public class LessonNote2Position extends ALesson {

    private String TAG = "GT-SimpleLesson";

    private FretView fretView;

    private NotesView notesView;

    private LearningStatusView learningStatusView;


    /** the note for which the fret position must be found */
    private Note questionedNote;

    private Layer layerLesson = new Layer(FretView.LAYER_Z_LESSON, MainFragment.getInstance().getResources()
            .getColor(R.color.blue));

    /**
     * positions which are accepted as correct answer?
     * 
     * for simplicity, we currently allow several acceptable positions as input, because one note on guitar can usually
     * be taken at several (maximally at three) positions.
     * 
     * */
    private List<Position> acceptedPositions;

    /**
     * specify the keys, in which the notes proposed as questions must be.
     * 
     * The keys corresponds to the main degrees of the C-major scale: C, D, E etc. The main reason to exclude keys with
     * sharps/flats: the appropriate images are not currently not available in the {@link NotesView}. But on the other
     * side it could be enough just to no the position of the main keys.
     * */
    private List<Key> mainKeys = new ArrayList<Key>();
    {
        mainKeys.add(Key.C);
        mainKeys.add(Key.D);
        mainKeys.add(Key.E);
        mainKeys.add(Key.F);
        mainKeys.add(Key.G);
        mainKeys.add(Key.A);
        mainKeys.add(Key.B);
    }


    @Override
    public String getTitle() {
        String str = MainFragment.getInstance().getResources().getString(R.string.lesson_note2position_title);
        return str;
    }

    @Override
    public String getDescription() {
        String str = MainFragment.getInstance().getResources().getString(R.string.lesson_note2position_description);
        return str;
    }

    @Override
    public long getDuration() {
        // TODO Auto-generated method stub
        return 123;
    }

    @Override
    public void doPrepareUi() {

        // initialize views required for the current type of lesson
        MainFragment uiControls = MainFragment.getInstance();
        fretView = uiControls.getFretView();
        notesView = uiControls.getNotesView();
        learningStatusView = uiControls.getLearningStatusView();

        fretView.setEnabled(true);
        notesView.setEnabled(true);
        notesView.setEnabledInput(false);

        uiControls.getDegreesView().setEnabled(false);
        uiControls.getShapestView().setEnabled(false);

        OnViewSelectionListener<NotePlayingEvent> onSelectionListener = new InnerOnSelectionListener();
        fretView.registerListener(onSelectionListener);

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

        do {

            /*
             * TODO: for debug purposes
             * 
             * for debugging the notes from index range 21..32 are taken, these are notes also available on the
             * electronic tuner.
             * 
             * For real life use: int index = LessonsUtils.random(0, notes.length - 1);
             */
            int index = LessonsUtils.random(21, 32);
            questionedNote = notes[index];

        } while (!mainKeys.contains(questionedNote.getKey()));

        // visualize it
        notesView.showNote(questionedNote);

        acceptedPositions = NoteStave.getInstance().resolvePositions(questionedNote);

        Log.d(TAG, "Note: " + questionedNote + ", Allowed positions: " + acceptedPositions);
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
    public class InnerOnSelectionListener implements OnViewSelectionListener<NotePlayingEvent> {

        @Override
        public void onViewElementSelected(final NotePlayingEvent npe) {
            // TODO: user UI widget to inform about answer correctness

            /* the lesson has not started. So we ignore all events. */
            if (acceptedPositions == null)
                return;

            MainFragment.getInstance().getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    // TODO: the npe.position is not set, when detected with FFT. It is not possible to
                    // resolve unique position.

                    List<Position> possibleAcceptedInterception = new ArrayList<Position>();
                    if (npe.possiblePositions != null)
                        possibleAcceptedInterception.addAll(npe.possiblePositions);
                    possibleAcceptedInterception.retainAll(acceptedPositions);

                    boolean isAnswerAccepted = false;
                    if (npe.position != null && acceptedPositions.contains(npe.position)) {
                        isAnswerAccepted = true;
                    } else if (possibleAcceptedInterception.size() > 0) {
                        isAnswerAccepted = true;
                    }

                    if (isAnswerAccepted) {
                        // TODO: learning status
                        // tvLessonStatus.setBackgroundColor(Color.GREEN);

                        fretView.show(layerLesson, acceptedPositions);

                        CountDownTimer cdt = new CountDownTimer(5000, 1000) {

                            @Override
                            public void onTick(long millisUntilFinished) {
                                // DO NOTHING

                            }

                            @Override
                            public void onFinish() {
                                LessonNote2Position.this.next();

                            }
                        };
                        cdt.start();

                        // fretView.clearFret();
                    } else {
                        // TODO: learning status
                        // tvLessonStatus.setBackgroundColor(Color.RED);
                    }

                    // TODO: learning status
                    // tvLessonStatus.setText(String.valueOf(counter));

                }
            });

        }
    }

}
