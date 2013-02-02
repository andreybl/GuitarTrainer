package com.ago.guitartrainer.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.ago.guitartrainer.GuitarTrainerApplication;
import com.ago.guitartrainer.PitchDetector;
import com.ago.guitartrainer.R;
import com.ago.guitartrainer.SettingsActivity;
import com.ago.guitartrainer.events.INoteEventListener;
import com.ago.guitartrainer.events.NotePlayingEvent;
import com.ago.guitartrainer.midi.FFTPitchDetectorListener;
import com.ago.guitartrainer.midi.PitchDetectorPhase;
import com.ago.guitartrainer.notation.Note;
import com.ago.guitartrainer.notation.NoteStave;
import com.ago.guitartrainer.notation.Position;
import com.ago.guitartrainer.scalegrids.ScaleGrid;

/**
 * The view with a fret of a guitar. Consists of the fret image and some control widgets arround it.
 * 
 * One of the controls is a switch between mode of input for the view:
 * <ul>
 * <li>manual input - the positions are input'ed by touching the image of the fret
 * <li>sound input - the app listens for sound, detect the frequency, transform it to position(-s)
 * <li>
 * </ul>
 * 
 * The listeners of the class are notified when a note is selected. The note can be selected in two modes: either in
 * manual (touch on screen) or in sound-sampling mode (playing on guitar).
 * 
 * @author Andrej Golovko - jambit GmbH
 * 
 */
public class FretView extends AInoutView<NotePlayingEvent> {

    /** z-index of the layer, where the lesson can draw/visualize its questions */
    public final static int LAYER_Z_LESSON = 2;

    /** z-index of the layer, which is used to draw user touches */
    public final static int LAYER_Z_TOUCHES = 3;

    /** z-index of the layer, which is used to draw notes detected with FFT */
    public final static int LAYER_Z_FFT = 4;

    private FretImageView fretImageView;

    /** contains the name of the last note played */
    private TextView tvNoteName;

    /** radio group for selection between two input modes: either manual or by playing guitar */
    private RadioGroup rgInputMode;

    /** ImageView's used to represent the status of the FFT */
    private ImageView imgStatusRunning;
    private ImageView imgStatusSampling;
    private ImageView imgStatusDoFFT;
    private ImageView imgStatusFFTAnalyze;

    private LinearLayout mainLayout;

    private Activity currentContext;

    /**
     * the thread in which pitch detector runs.
     * 
     * the thread is used as a ultimate indicator for the input mode: it is not null and active.
     * */
    private Thread pitchDetectorThread;

    /**
     * mapping from pitch detection phase to the image representing this phase.
     * 
     * The image has two colors: gray color (phase is not active) and any other color (phase is active). For instance,
     * the active {@link PitchDetectorPhase#PITCH_DETECTION} phase is presented by the red square.
     * 
     * */
    private Map<PitchDetectorPhase, ImageView> phase2Image = new HashMap<PitchDetectorPhase, ImageView>();

    public FretView(Context context) {
        super(context);

        init();
    }

    public FretView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public FretView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    private void init() {
        currentContext = (Activity) getContext();
        LayoutInflater inflater = (currentContext).getLayoutInflater();
        mainLayout = (LinearLayout) inflater.inflate(R.layout.fret_view, this, true);

        fretImageView = (FretImageView) mainLayout.findViewById(R.id.img_fretimageview);
        fretImageView.registerFretView(this);

        rgInputMode = (RadioGroup) mainLayout.findViewById(R.id.rb_group_lessons);

        tvNoteName = (TextView) mainLayout.findViewById(R.id.tv_note);

        /* decide on input mode: either manual or sound */
        boolean isManualInput = GuitarTrainerApplication.getPrefs().getBoolean(
                SettingsActivity.KEY_INPUT_METHOD_MANUAL, true);
        RadioButton rbInputMode = (isManualInput) ? (RadioButton) mainLayout.findViewById(R.id.rb_input_manual)
                : (RadioButton) mainLayout.findViewById(R.id.rb_input_sound);
        rbInputMode.setChecked(true);
        if (isManualInput) {
            startManualInput();
        } else {
            startSoundInput();
        }

        imgStatusRunning = (ImageView) mainLayout.findViewById(R.id.pitchdetector_status_running);
        imgStatusSampling = (ImageView) mainLayout.findViewById(R.id.pitchdetector_status_sampling);
        imgStatusDoFFT = (ImageView) mainLayout.findViewById(R.id.pitchdetector_status_dofft);
        imgStatusFFTAnalyze = (ImageView) mainLayout.findViewById(R.id.pitchdetector_status_fftanalyze);

        // register images for each phase
        phase2Image.put(PitchDetectorPhase.PITCH_DETECTION, imgStatusRunning);
        phase2Image.put(PitchDetectorPhase.AUDIO_SAMPLING, imgStatusSampling);
        phase2Image.put(PitchDetectorPhase.DO_FFT, imgStatusDoFFT);
        phase2Image.put(PitchDetectorPhase.ANALYZE_FFT, imgStatusFFTAnalyze);

        InnerOnInputModeChangedListener innerOnRadioListener = new InnerOnInputModeChangedListener();
        rgInputMode.setOnCheckedChangeListener(innerOnRadioListener);

        // TODO: register listeners for the fretImageView or for the current view

    }

    private void startSoundInput() {
        /*-
         * sound input mode
         * 
         * The notes are inputed with help of the guitar itself. Two listeners are registered on FFT
         * detector: 
         *     - one listens for notes detected, 
         *     - another listens for FFT detector internals phases
         *     
         * Both events are used for visualization:
         *     - notes are transformed to positions and are shown
         *     - FFT detector phases are visualized with dots
         */
        FFTPitchDetectorListener innerFFTListener = new InnerFFTPitchDetectorListener();
        INoteEventListener innerNotesListener = new InnerNotesListener();
        PitchDetector pd = new PitchDetector(innerFFTListener);
        pd.registerNotesListener(innerNotesListener);
        pitchDetectorThread = new Thread(pd);

        // NOTE: it would be possible also to clear only touch layer
        clearAllLayers();

        pitchDetectorThread.start();
    }

    private void startManualInput() {
        if (pitchDetectorThread != null && pitchDetectorThread.isAlive())
            pitchDetectorThread.interrupt();

        // NOTE: it would be possible also to clear only FFT layer
        clearAllLayers();

    }

    public void clearLayer(Layer layer) {
        fretImageView.clearLayer(layer);
    }

    public void clearLayerByZIndex(int zIndex) {
        fretImageView.clearLayer(zIndex);
    }

    public void clearAllLayers() {
        fretImageView.clearAllLayers();
    }

    /**
     * Indicate the input mode for the view.
     * 
     * We have currently only two input modes for the fret: manual and sound-based. If true, the input mode is manual:
     * e.g. user must touch the fret image to input the played position. If false, the input mode is sound-based: e.g.
     * the user must play something on real guitar, so that the sound is sampled and FFT-processed to detect the played
     * position.
     * 
     * @return
     */
    private boolean isManualInput() {
        boolean isSound = pitchDetectorThread != null && pitchDetectorThread.isAlive();
        return !isSound;
    }

    @Override
    public void setEnabled(boolean enabled) {
        rgInputMode.setEnabled(enabled);
        rgInputMode.getChildAt(0).setEnabled(enabled);
        rgInputMode.getChildAt(1).setEnabled(enabled);

        fretImageView.setEnabled(enabled);

        if (!enabled) {
            if (pitchDetectorThread != null && pitchDetectorThread.isAlive())
                pitchDetectorThread.interrupt();
        }

        super.setEnabled(enabled);

    }

    public void show(Layer layer, ScaleGrid gridShape) {
        fretImageView.show(layer, gridShape);
    }

    public void show(Layer layer, Position... positions) {
        fretImageView.show(layer, positions);
    }

    public void show(Layer layer, List<Position> positions) {
        Position[] arrPositions = positions.toArray(new Position[positions.size()]);
        fretImageView.show(layer, arrPositions);
    }

    public void show(Layer layer, Map<Position, Integer> map) {
        List<Position> coloredPositions = new ArrayList<Position>();
        for (Position position : map.keySet()) {
            int colorResourceId = map.get(position);
            Position coloredPosition = new PositionColored(position, colorResourceId);
            coloredPositions.add(coloredPosition);
        }
        show(layer, coloredPositions);
    }

    /*
     * **** INNER CLASSES
     */

    /**
     * Layer which are used to draw {@link Position}.
     * 
     * The layer does not contains any {@link Position} itself but is associated with a set of them somewhere else. The
     * concept of layer orchestrate the drawing on the same fret image by different drawing participants: user touching
     * the screen, FFT detector, active lesson etc.
     * 
     * The drawing participant create own layer to draw in it and is responsible for cleaning it up as soon as the layer
     * is not required any more.
     * 
     * @author Andrej Golovko - jambit GmbH
     * 
     */
    public static class Layer implements Comparable<Layer> {

        private UUID uuid;

        /**
         * the z-index of the layer, which control its order compared to other layers.
         * */
        private int zIndex;

        private int colorId;

        public Layer(int zIndex, int colorId) {
            uuid = UUID.randomUUID();
            this.zIndex = zIndex;
            this.colorId = colorId;
        }

        @Override
        public boolean equals(Object o) {

            if (o instanceof Layer) {
                Layer l = (Layer) o;
                return this.uuid == l.uuid;
            }

            return false;
        }

        @Override
        public int compareTo(Layer l) {
            if (this.zIndex < l.zIndex)
                return -1;
            else if (this.zIndex == l.zIndex)
                return 0;
            else
                return 1;
        }
    }

    private class InnerFFTPitchDetectorListener implements FFTPitchDetectorListener {

        // long tstStartReading = 0;
        // long tstEndReading = 0;
        // long tstStartAnalyzis = 0;
        // long tstEndAnalyzis = 0;
        // Log.d(TAG, "Timing: " + (tstEndReading - tstStartReading) + ", " + (tstEndAnalyzis - tstStartAnalyzis));

        @Override
        public void startedPhase(final PitchDetectorPhase phase) {

            Activity activity = (Activity) getContext();
            activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    ImageView imageView = phase2Image.get(phase);
                    imageView.setImageResource(R.drawable.icon_square_red);
                }
            });

        }

        @Override
        public void finishedPhase(final PitchDetectorPhase phase) {
            Activity activity = (Activity) getContext();
            activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    ImageView imageView = phase2Image.get(phase);
                    imageView.setImageResource(R.drawable.icon_square_gray);
                }
            });

        }
    }

    /**
     * Listens for notes which were detected in FFT pitch detector.
     * 
     * The listener is used only when the pitch detector is running. With help of this listener the note received from
     * FFT pitch detector is:
     * <ul>
     * <li>visualized on the fret as a (several) dots
     * <li>re-fired to listeners registered on FretView
     * </ul>
     * 
     * @author Andrej Golovko - jambit GmbH
     * 
     */
    private class InnerNotesListener implements INoteEventListener {
        /** layer in which we draw dots (notes), which were recognized with FFT */
        Layer layerFFT = new Layer(LAYER_Z_FFT, getResources().getColor(R.color.green));

        @Override
        public void noteStateChanged(final NotePlayingEvent e) {
            NoteStave noteStave = NoteStave.getInstance();
            List<Position> positions = noteStave.resolvePositions(e.note);
            fretImageView.clearLayer(layerFFT);
            show(layerFFT, positions);

            if (e.position == null) {
                e.possiblePositions = positions;
            }

            Activity ctx = (Activity) getContext();
            ctx.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    tvNoteName.setText(e.note.toString());
                }
            });

            notifyListeners(e);
        }
    }

    /**
     * Listens on clicks on radio buttons for input selection mode.
     * 
     * @author Andrej Golovko - jambit GmbH
     * 
     */
    public class InnerOnInputModeChangedListener implements OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (group == rgInputMode) {
                switch (checkedId) {
                case R.id.rb_input_manual:
                    // manual input mode
                    startManualInput();
                    break;
                case R.id.rb_input_sound:
                    startSoundInput();
                    break;

                default:
                    break;
                }
            }
        }
    }

    /**
     * The central class behind of the {@link FretView}, which represent the fret to the user.
     * 
     * The {@link FretImageView} is NOT expected to be used outside of the {@link FretView} instance enclosing it. But
     * it is not possible to make it private, because otherwise it would not possible to instantiate the class with XML
     * layout file.
     * 
     * 
     * The FretImageView has a guitar fret as its image. The purpose of the image is two-fold:
     * <ul>
     * <li>take user input
     * <li>visualize lesson question
     * </ul>
     * 
     * An example for user input: From the place where user touches the screen a valid position as presented by
     * {@link Position} instance is derived. An example for output: Any valid {@link Position} can be represented as a
     * dot on the image.
     * 
     * Note that the fret is intended to be used for visualizing any kind of data, which require the fret. For instance,
     * you may also visualize the grid shapes with the image.
     * 
     * @author Andrej Golovko - jambit GmbH
     * 
     */
    // TODO: introduce the Visitor patter, where the logic of drawing is encapsulated in a class
    public static class FretImageView extends ImageView {

        /*
         * Note: the coordinates for the @drawable/guitar_12_frets image
         * 
         * private int[] midlesOfFrets = new int[] { 40, 116, 229, 343, 453, 553, 650, 743, 831, 912, 991, 1074, 1151 };
         * private int[] midlesOfStrings = new int[] { 22, 52, 82, 109, 139, 169 };
         */

        /*
         * 
         * the coordinates for @drawable/guitar_12_frets_50percent image
         * 
         * Note that the 0-position of midlesOfFrest must be the middle of the 0-fret
         */
        // @formatter:off
        private int[] midlesOfFrets = new int[] { 
                38  /* 0 */, 
                100, 
                200, 
                300, 
                400, 
                484 /* 5 */, 
                573 /* 6 */, 
                650 /* 7 */,
                729 /* 8 */, 
                800 /* 9 */, 
                870 /* 10 */, 
                942, 
                1011 /* 12 */};
        // @formatter:on
        private int[] midlesOfStrings = new int[] { 22, 42, 69, 96, 122, 149 };

        /** paint object used to draw on the canvas */
        Paint paint = new Paint();

        // associated parent fretView
        private FretView fretView;

        /* x-coordinate of the touch on the screen */
        private int x;

        /* y-coordinate of the touch on the screen */
        private int y;

        // private Map<Position, Integer> positionsAndColor = new HashMap<Position, Integer>();

        private Map<Layer, Set<Position>> mapLayer2Positions = new Hashtable<Layer, Set<Position>>();

        /** layer where the touches on the image itself will be drawn. */

        private Layer layerTouches = new Layer(LAYER_Z_TOUCHES, getResources().getColor(R.color.red));

        public FretImageView(Context context) {
            super(context);
        }

        public void clearLayer(int zIndex) {
            Set<Layer> layersToClear = new HashSet<FretView.Layer>();
            for (Layer layer : mapLayer2Positions.keySet()) {
                if (layer.zIndex == zIndex) {
                    layersToClear.add(layer);
                }
            }

            for (Layer layer : layersToClear) {
                clearLayer(layer);
            }
        }

        public FretImageView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public FretImageView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        private void show(Layer layer, ScaleGrid gridShape) {
            List<Position> list = gridShape.strongPositions();
            Position[] positions = list.toArray(new Position[list.size()]);
            show(layer, positions);
        }

        private void show(Layer layer, Position... newPositions) {

            /* Position's which already exist in the layer */
            Set<Position> existingPositions;

            if (mapLayer2Positions.containsKey(layer)) {
                existingPositions = mapLayer2Positions.get(layer);
            } else {
                existingPositions = new HashSet<Position>();
                mapLayer2Positions.put(layer, existingPositions);
            }

            for (Position p : newPositions) {
                existingPositions.add(p);
            }

            postInvalidate();
        }

        private void clearLayer(Layer layer) {
            mapLayer2Positions.remove(layer);
            postInvalidate();

        }

        private void clearAllLayers() {
            mapLayer2Positions.clear();
            postInvalidate();
        }

        public void registerFretView(FretView fv) {
            this.fretView = fv;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            /*
             * The onDraw() operates on layers, each layer consits of positions (dots) in it.
             * 
             * First, we sort the Layers according to its z-index. Second, we draw the positions from each layer.
             */

            // order layer. The layer with z-index "0" is the lowest one
            List<Layer> layers = new ArrayList<Layer>(mapLayer2Positions.keySet());
            Collections.sort(layers);

            for (Layer layer : layers) {

                Set<Position> positions = mapLayer2Positions.get(layer);
                for (Position position : positions) {
                    int pxFret = midlesOfFrets[position.getFret()];
                    int pxStr = midlesOfStrings[position.getStringIndex()];

                    /*
                     * support for the differently colored positions.
                     * 
                     * if the position is not PositionColored and does not have own color - use color of the layer to
                     * paint it.
                     */
                    if (position instanceof PositionColored) {
                        PositionColored colored = (PositionColored) position;
                        int colorId = getResources().getColor(colored.colorResourceId);
                        paint.setColor(colorId);
                    } else {
                        paint.setColor(layer.colorId);
                    }

                    canvas.drawCircle(pxFret, pxStr, 10, paint);
                }
            }
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent event) {

            if (event.getAction() != MotionEvent.ACTION_UP)
                return true;

            /* ignore user touching the screen, if the input mode is sound-based */
            if (!fretView.isManualInput())
                return true;

            // TODO: also ignore event, if isEnabledInput==false

            if (!fretView.isEnabled()) {
                /*
                 * ignore all touch events, if the view is disabled. We assume the view is disabled exactly when the
                 * fretView is disabled.
                 */
                return true;
            }

            // x - for frets
            x = (int) event.getX();

            // y - for strings
            y = (int) event.getY();

            /*
             * resolve the best match of fret and string for the current touch, use it to resolve position.
             * 
             * These steps are required to draw the user touches snapped to the fret/string.
             */
            int xClosest = indexOfClosest(x, midlesOfFrets);
            int yClosest = indexOfClosest(y, midlesOfStrings);

            int strClosest = yClosest + 1;
            int fretClosest = xClosest;
            Position pos = new Position(strClosest, fretClosest);

            Log.d("GT-FretViewImage", "X:" + x + ", Y:" + y + "; str:" + strClosest + ", fret:" + fretClosest);

            clearLayer(layerTouches);
            show(layerTouches, pos);

            Note note = NoteStave.getInstance().resolveNote(pos);
            NotePlayingEvent npe = new NotePlayingEvent(note, pos);
            if (fretView != null)
                fretView.notifyListeners(npe);
            return true;
        }

        /**
         * Calculated the index of the closest match for <code>values</code> inside of the <code>values</code> array.
         * 
         * For example, for find="34" and values=(12, 33, 51) the returned values must equal 1 (index of 33).
         * 
         * @param find
         *            is the value to be matched
         * @param values
         *            is the array to look search through
         * @return index of best match
         */
        private int indexOfClosest(int find, int... values) {
            int closest = values[0];
            int indexOfClosest = 0;

            int distance = Math.abs(closest - find);
            for (int index = 0; index < values.length; index++) {
                int val = values[index];
                int distanceI = Math.abs(val - find);
                if (distance > distanceI) {
                    closest = val;
                    indexOfClosest = index;
                    distance = distanceI;
                }
            }
            return indexOfClosest;
        }

        @Override
        public void setEnabled(boolean enabled) {
            if (!enabled)
                setImageResource(R.drawable.guitar_12_frets_50percent_gray);
            else
                setImageResource(R.drawable.guitar_12_frets_50percent);

            super.setEnabled(enabled);
        }

    }

    private class PositionColored extends Position {

        /**
         * color of the position.
         * 
         * The call like getResources().getColor(colorResourceId) is required for this color to take effect.
         * 
         */
        private int colorResourceId;

        public PositionColored(Position position, int colorResourceId) {
            super(position.getStringIndex() + 1, position.getFret());
            this.colorResourceId = colorResourceId;
        }
    }
}
