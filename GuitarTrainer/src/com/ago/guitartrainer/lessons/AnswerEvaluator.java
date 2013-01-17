package com.ago.guitartrainer.lessons;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import android.graphics.Color;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.ago.guitartrainer.GuitarTrainerActivity;
import com.ago.guitartrainer.gridshapes.GridShape;
import com.ago.guitartrainer.notation.Degree;
import com.ago.guitartrainer.notation.Note;
import com.ago.guitartrainer.notation.NoteStave;
import com.ago.guitartrainer.notation.Position;
import com.ago.guitartrainer.utils.LessonsUtils;

/**
 * Encapsulate the logic used to which evaluates correctness user answers.
 * 
 * The logic behind the quest, which can tell whether the user answered the question correctly.
 * 
 * @author Andrej Golovko - jambit GmbH
 * @deprecated use one of the {@link ILesson} implementations insteas. Maybe intro kind of AbstractLesson for common
 *             behaviour.
 */
public class AnswerEvaluator {

    private final String TAG = "GuTr-Lesson";

    private GuitarTrainerActivity activity;

    private long tstLessonStart;

    private long tstLessonEnd;

    private GridShape currentGridShape;

    private Position lessonPosition;

    private boolean isActive;

    // if true, the lesson finished by countdown interrupt
    private boolean lessonFailed = false;

//    private final FretImageView fretImageView;

    private Degree degree;

    private static int MS_IN_SECOND = 1000;

    private int lessonsCounter = 0;

    public Map<Degree, Long> statistics = new HashMap<Degree, Long>();

    private int progress;

    public AnswerEvaluator(GuitarTrainerActivity activity) {
        this.activity = activity;
//        fretImageView = activity.fretImageView;
    }

    public boolean isActive() {
        return isActive;
    }

    public void startLesson() {

        lessonsCounter++;

        // 2. pick at random the degree to play in the projection
        lessonPosition = LessonsUtils.pickPosition(currentGridShape);
        degree = currentGridShape.position2Degree(lessonPosition);

        isActive = true;
        // 3. request the user to play degree
        // Note: "Tip" is shown for debug purposes only
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                activity.txtPlayNote.setText("Find degree " + degree + ". Tip: " + lessonPosition);

            }
        });

        // 4. countdown runs ...
        tstLessonStart = System.currentTimeMillis();

        // TODO: call count down thread here, which will restart lesson after no-right-response timeout
    }

    public boolean suggestAnswer(Note note) {

        NoteStave stave = NoteStave.getInstance();

        /*
         * user have played some note on the guitar, several positions could be responsible for it
         */
        final List<Position> positionsAll = stave.resolvePositions(note);
        final List<Position> positionsInShape = currentGridShape.applyShape(positionsAll);

        Log.d(TAG, note.toString() + ", Positions origianl: " + positionsAll + ", Positions projected: "
                + positionsInShape);

        boolean isCorrect = false;
        if (positionsInShape.size() == 1 && positionsInShape.get(0).equals(lessonPosition)) {
            tstLessonEnd = System.currentTimeMillis();

            // how long it took to recognize the note
            long diff = tstLessonEnd - tstLessonStart;

            if (statistics.containsKey(degree)) {
                Long val = statistics.get(degree);
                Long statValue = Math.abs((val + diff) / 2);
                statistics.put(degree, statValue);
            } else {
                statistics.put(degree, diff);
            }

            isCorrect = true;
        }

        // TextView txtPlayNote = (TextView)activity.findViewById(R.id.txt_playnote);

        activity.runOnUiThread(new Runnable() {
            public void run() {
//                fretImageView.clear();
//                fretImageView.showOnFret(Color.BLUE, currentGridShape);
//                fretImageView.showOnFret(Color.RED, positionsAll);
//                fretImageView.showOnFret(Color.GREEN, positionsInShape);
//                fretImageView.draw();

                String str = prepareResultsString();
                activity.txtLessonResults.setText(str);
            }
        });

        return isCorrect;

    }

    private String prepareResultsString() {
        String result = lessonsCounter + ": ";
        TreeSet<Degree> degrees = new TreeSet<Degree>(statistics.keySet());
        for (Degree d : degrees) {
            result += d.name() + ":" + Math.abs(statistics.get(d) / MS_IN_SECOND) + "; ";
        }

        return result;
    }

    /**
     * On seekbar, with which the fret can be selected
     * 
     * @author Andrej Golovko - jambit GmbH
     * 
     */
    public class InnerOnSeekBarChangeListener implements OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
            AnswerEvaluator.this.progress = progress;

            if (currentGridShape != null) {
                activity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        currentGridShape = GridShape.create(currentGridShape.getClass(), progress);
//                        fretImageView.clear();
//                        fretImageView.showOnFret(Color.BLUE, currentGridShape);
//                        fretImageView.draw();
                    }
                });
            }

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub

        }

    }

    /**
     * On checkboxes for grids
     * 
     * @author Andrej Golovko - jambit GmbH
     * 
     */
    public class InnerOnCheckedChangeListener implements OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            if (isChecked) {
                currentGridShape = GridShape.create(buttonView.getId(), AnswerEvaluator.this.progress);

                activity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
//                        fretImageView.clear();
//                        fretImageView.showOnFret(Color.BLUE, currentGridShape);
//                        fretImageView.draw();
                    }
                });
            }

        }

    }
}
