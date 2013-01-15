package com.ago.guitartrainer.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.ago.guitartrainer.gridshapes.AlphaGridShape;
import com.ago.guitartrainer.gridshapes.GridShape;
import com.ago.guitartrainer.notation.Degree;
import com.ago.guitartrainer.notation.Note;
import com.ago.guitartrainer.notation.Position;

// TODO: make it private to FretView?
public class FretImageView extends ImageView {

    private int[] midlesOfFrets = new int[] { 40, 116, 229, 343, 453, 553, 650, 743, 831, 912, 991, 1074, 1151 };

    private int[] midlesOfStrings = new int[] { 22, 52, 82, 109, 139, 169 };

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
            int pxFret = midlesOfFrets[position.fret];
            int pxStr = midlesOfStrings[position.string];
            
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

    public void showOnFret(int color, GridShape... gridShape) {
        for (GridShape gs : gridShape) {
            List<Position> strongs = gs.strongPositions();
            for (Position position : strongs) {
                showOnFret(color, position);
            }
        }        
    }
    
    public void showOnFret(int color, List<Position> positions) {
        for (Position position : positions) {
            positionsAndColor.put(position, color);
        }
    }

    public void showOnFret(int color, Position... positions) {
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
