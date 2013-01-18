package com.ago.guitartrainer.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ago.guitartrainer.R;
import com.ago.guitartrainer.gridshapes.GridShape;
import com.ago.guitartrainer.notation.Position;

public class FretView extends LinearLayout {

    private FretImageView fretImageView;

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
        View mainLayout = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.fret_view, this, true);

        fretImageView = (FretImageView) mainLayout.findViewById(R.id.img_fretimageview);

        // TODO: register listeners for the fretImageView or for the current view

    }

    public void showOnFret(Position p) {
        fretImageView.showOnFret(Color.BLACK, p);
        fretImageView.invalidate();
    }

    public void clearFret() {
        fretImageView.clear();
        fretImageView.invalidate();
    }

    /*
     * **** INNER CLASSES
     */
    private class InnerOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            System.out.println("xxx");
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
    // TODO: introduce the Visitor patter, where the logic of drawing is incapsulated in a class
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
         * */ 
        private int[] midlesOfFrets = new int[] { 38, 100, 200, 300, 400, 484, 573, 650, 743, 731, 804, 873, 942, 1012 };
        private int[] midlesOfStrings = new int[] { 22, 42, 69, 96, 122, 149 };

        
        /** paint object used to draw on the canvas */
        Paint paint = new Paint();

        /* x-coordinate of the touch on the screen */
        private int x;

        /* y-coordinate of the touch on the screen */
        private int y;

        private Map<Position, Integer> positionsAndColor = new HashMap<Position, Integer>();

        public FretImageView(Context context) {
            super(context);
        }

        public FretImageView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public FretImageView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            for (Position position : positionsAndColor.keySet()) {
                int pxFret = midlesOfFrets[position.getFret()];
                int pxStr = midlesOfStrings[position.getStringIndex()];

                paint.setColor(positionsAndColor.get(position));

                canvas.drawCircle(pxFret, pxStr, 10, paint);
            }

        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent event) {
            x = (int) event.getX();
            y = (int) event.getY();
            System.out.println("IMAGE:" + x + ";" + y);
            invalidate();
            return true;
        }

        protected void showOnFret(int color, GridShape... gridShape) {
            for (GridShape gs : gridShape) {
                List<Position> strongs = gs.strongPositions();
                for (Position position : strongs) {
                    showOnFret(color, position);
                }
            }
        }

        protected void showOnFret(int color, List<Position> positions) {
            for (Position position : positions) {
                positionsAndColor.put(position, color);
            }
        }

        protected void showOnFret(int color, Position... positions) {
            for (Position position : positions) {
                positionsAndColor.put(position, color);
            }
        }

        public void draw() {
            invalidate();
        }

        public void clear() {
            positionsAndColor.clear();
            invalidate();
        }
    }
}
