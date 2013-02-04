package com.ago.guitartrainer.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.ago.guitartrainer.R;
import com.ago.guitartrainer.events.NotePlayingEvent;
import com.ago.guitartrainer.instruments.guitar.GuitarFingeringHelper;
import com.ago.guitartrainer.instruments.guitar.Position;
import com.ago.guitartrainer.notation.Note;
import com.ago.guitartrainer.scalegrids.ScaleGrid;
import com.ago.guitartrainer.ui.FretView.Layer;
import com.ago.guitartrainer.ui.FretView.PositionColored;

/**
 * The central class behind of the {@link FretView}, which represent the fret to the user.
 * 
 * The {@link FretImageView} is NOT expected to be used outside of the {@link FretView} instance enclosing it. But it is
 * not possible to make it private, because otherwise it would not possible to instantiate the class with XML layout
 * file.
 * 
 * 
 * The FretImageView has a guitar fret as its image. The purpose of the image is two-fold:
 * <ul>
 * <li>take user input
 * <li>visualize lesson question
 * </ul>
 * 
 * An example for user input: From the place where user touches the screen a valid position as presented by
 * {@link Position} instance is derived. An example for output: Any valid {@link Position} can be represented as a dot
 * on the image.
 * 
 * Note that the fret is intended to be used for visualizing any kind of data, which require the fret. For instance, you
 * may also visualize the grid shapes with the image.
 * 
 * @author Andrej Golovko - jambit GmbH
 * 
 */
public class FretImageView extends ImageView {

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
    private Paint paint = new Paint();

    // associated parent fretView
    private FretView fretView;

    /* x-coordinate of the touch on the screen */
    private int x;

    /* y-coordinate of the touch on the screen */
    private int y;

    private Map<Layer, Set<Position>> mapLayer2Positions = new Hashtable<Layer, Set<Position>>();

    /** layer where the touches on the image itself will be drawn. */

    private Layer layerTouches = new Layer(FretView.LAYER_Z_TOUCHES, getResources().getColor(R.color.red));

    private int startAreaFret = 0;

    private int endAreaFret = 12;

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

    protected void show(Layer layer, ScaleGrid gridShape) {
        List<Position> list = gridShape.strongPositions();
        Position[] positions = list.toArray(new Position[list.size()]);
        show(layer, positions);
    }

    protected void show(Layer layer, Position... newPositions) {

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

    protected void clearLayer(Layer layer) {
        mapLayer2Positions.remove(layer);
        postInvalidate();

    }

    protected void clearAllLayers() {
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

        paint.setAlpha(255);

        for (Layer layer : layers) {

            Set<Position> positions = mapLayer2Positions.get(layer);
            for (Position position : positions) {
                int pxFret = midlesOfFrets[position.getFret()];
                int pxStr = midlesOfStrings[position.getStringIndex()];

                /*
                 * support for the differently colored positions.
                 * 
                 * if the position is not PositionColored and does not have own color - use color of the layer to paint
                 * it.
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

        paint.setColor(Color.GRAY);
        paint.setAlpha(150); // with 0, the transparency is at maximum

        int start = midlesOfFrets[startAreaFret];
        if (startAreaFret > 0) {
            start -= Math.round((midlesOfFrets[startAreaFret] - midlesOfFrets[startAreaFret - 1]) / 2);
        } else {
            start = 0;
        }

        int end = midlesOfFrets[endAreaFret];
        if (endAreaFret > 0) {
            end += Math.round((midlesOfFrets[endAreaFret] - midlesOfFrets[endAreaFret - 1]) / 2);
        }

        canvas.drawRect(0, 0, start, getHeight(), paint);
        canvas.drawRect(end, 0, getWidth(), getHeight(), paint);
    }

    /**
     * Draw the area over the fret, which is "visible" for user.
     * 
     * The frets' numbers passed as parameters must be in range 0..12 and are inclusive. It means, the parameters (1,3)
     * cause the frets 1,2,3 to be shown in full color, all other frets will be grayed out.
     * 
     * @param fromFret
     *            start fret of the area in range 0..12
     * @param toFret
     *            end fret of the area in range 0..12
     */
    public void showArea(int fromFret, int toFret) {

        if (fromFret > toFret)
            fromFret = toFret;

        if (fromFret < 0)
            fromFret = 0;

        if (toFret > 12)
            toFret = 12;

        startAreaFret = fromFret;
        endAreaFret = toFret;
        postInvalidate();
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

        Note note = GuitarFingeringHelper.getInstance().resolveNote(pos);
        fretView.setNoteName(note.toString());

        NotePlayingEvent npe = new NotePlayingEvent(note, pos);
        if (fretView != null)
            fretView.notifyListeners(npe);

        /*
         * if true returned, the image is invalidated and FretView#onDraw() is called. But it is done already in the
         * show(..) called from current methods. So its enough to return false here.
         */
        return false;
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
