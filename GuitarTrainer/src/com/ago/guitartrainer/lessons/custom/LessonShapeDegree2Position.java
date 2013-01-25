package com.ago.guitartrainer.lessons.custom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.graphics.Color;
import android.graphics.drawable.shapes.Shape;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;

import com.ago.guitartrainer.R;
import com.ago.guitartrainer.events.NotePlayingEvent;
import com.ago.guitartrainer.events.OnViewSelectionListener;
import com.ago.guitartrainer.gridshapes.GridShape;
import com.ago.guitartrainer.gridshapes.GridShape.Type;
import com.ago.guitartrainer.notation.Degree;
import com.ago.guitartrainer.notation.Position;
import com.ago.guitartrainer.ui.DegreesView;
import com.ago.guitartrainer.ui.FretView;
import com.ago.guitartrainer.ui.FretView.Layer;
import com.ago.guitartrainer.ui.MainFragment;
import com.ago.guitartrainer.ui.ShapesView;
import com.ago.guitartrainer.utils.LessonsUtils;

public class LessonShapeDegree2Position extends ALesson {

    private FretView fretView;

    private ShapesView shapesView;

    private DegreesView degreesView;

    private List<Position> acceptedPositions;

    private TextView tvLessonStatus;

    private Layer layerLesson = new Layer(FretView.LAYER_Z_LESSON, MainFragment.getInstance().getResources()
            .getColor(R.color.blue));

    /**
     * if true, the grid shape used as lesson parameter is allowed to be entered by the user. Otherwise, the parameter
     * is selected randomly.
     */
    private boolean isShapeInputAllowed = true;

    /** shape type, as selected by the user */
    private GridShape.Type gridShapeType = Type.ALPHA;

    @Override
    public String getTitle() {
        return "ShapeDegree2Position";
    }

    @Override
    public void prepareUi() {

        // initialize views required for the current type of lesson
        MainFragment uiControls = MainFragment.getInstance();

        fretView = uiControls.getFretView();
        fretView.setEnabled(true);
        fretView.setEnabledInput(true);

        uiControls.getNotesView().setEnabled(false);

        shapesView = uiControls.getShapestView();
        shapesView.setEnabled(true);
        shapesView.setEnabledInput(isShapeInputAllowed);

        if (isShapeInputAllowed) {
            InnerOnShapeSelectionListener onShapeSelection = new InnerOnShapeSelectionListener();
            shapesView.registerListener(onShapeSelection);
        }

        degreesView = uiControls.getDegreesView();
        degreesView.setEnabled(true);
        degreesView.setEnabledInput(false);

        uiControls.getShapestView().setEnabled(true);

        tvLessonStatus = uiControls.getLessonStatusView();

        OnViewSelectionListener<NotePlayingEvent> onSelectionListener = new InnerOnSelectionListener();
        fretView.registerListener(onSelectionListener);

    }

    @Override
    public void stop() {
        fretView.clearLayer(layerLesson);
    }

    /**
     * Skip to the next lesson.
     * 
     * The answer results are not important.
     * 
     **/
    @Override
    public void next() {

        final int newCounter = increaseCounter();

        fretView.clearLayer(layerLesson);

        MainFragment.getInstance().getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                tvLessonStatus.setText(String.valueOf(newCounter));

            }
        });

        // random shape + position
        GridShape gridShape;
        if (isShapeInputAllowed) {
            gridShape = GridShape.create(gridShapeType, 0);
        } else {
            gridShape = randomGridShape();
        }

        // random degree
        Degree degree = randomDegree();

        /*
         * TODO: if several positions returned, we must make difference between "higher" and "lower" position.
         * 
         * I assume, not more than two positions can be returned.
         */
        acceptedPositions = gridShape.degree2Positions(degree);

        // visualize it
        shapesView.show(gridShape.getType());
        degreesView.show(degree);
        fretView.show(layerLesson, gridShape);

        Log.d(getTag(), "Shape: " + gridShape + ", Degree: " + degree + ", Expect positions: " + acceptedPositions);
    }

    private GridShape randomGridShape() {
        int indexOfGridShape = LessonsUtils.random(0, GridShape.Type.values().length - 1);
        GridShape.Type gridShapeType = GridShape.Type.values()[indexOfGridShape];

        int posStart = LessonsUtils.random(0, GridShape.FRETS_ON_GUITAR);
        int posEnd = posStart + gridShapeType.numOfFrets();
        if (posEnd > GridShape.FRETS_ON_GUITAR) {
            posStart = GridShape.FRETS_ON_GUITAR - (posEnd - posStart);
        }

        GridShape gridShape = GridShape.create(gridShapeType, posStart);

        return gridShape;
    }

    /**
     * Return a random {@link Degree} from those which are I, II...
     * 
     * @return degree of the scale grid
     */
    private Degree randomDegree() {
        Degree[] mainDegrees = new Degree[] { Degree.ONE, Degree.TWO, Degree.THREE, Degree.FOUR, Degree.FIVE,
                Degree.SIX, Degree.SEVEN };

        boolean isMainDegree = false;
        Degree degree;
        do {
            int indexOfDegree = LessonsUtils.random(0, Degree.values().length - 1);
            degree = Degree.values()[indexOfDegree];
            isMainDegree = Arrays.binarySearch(mainDegrees, degree) >= 0;
        } while (!isMainDegree);

        return degree;
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
                        tvLessonStatus.setBackgroundColor(Color.GREEN);
                        fretView.show(layerLesson, acceptedPositions);

                        CountDownTimer cdt = new CountDownTimer(5000, 1000) {

                            @Override
                            public void onTick(long millisUntilFinished) {
                                // DO NOTHING

                            }

                            @Override
                            public void onFinish() {
                                LessonShapeDegree2Position.this.next();

                            }
                        };
                        cdt.start();

                        // fretView.clearFret();
                    } else {
                        tvLessonStatus.setBackgroundColor(Color.RED);
                    }

                    tvLessonStatus.setText(String.valueOf(counter()));

                }
            });

        }
    }

    /**
     * Listens on user selection of the shape.
     * 
     * This listener does not participate in evaluation of answer. It just configure one of the lesson parameters - the
     * grid shape to use in questions.
     * 
     * @author Andrej Golovko - jambit GmbH
     * 
     */
    private class InnerOnShapeSelectionListener implements OnViewSelectionListener<GridShape.Type> {

        @Override
        public void onViewElementSelected(GridShape.Type element) {
            gridShapeType = element;

        }

    }
}
