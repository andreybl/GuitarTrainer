package com.ago.guitartrainer.ui;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.ago.guitartrainer.Degree;
import com.ago.guitartrainer.Notes.Note;
import com.ago.guitartrainer.Notes.Position;
import com.ago.guitartrainer.gridshapes.AlphaGridShape;

public class FretImageView extends ImageView {

    private int[] midlesOfFrets = new int[] { 40, 116, 229, 343, 453, 553, 650, 743, 831, 912, 991, 1074, 1151 };

    private int[] midlesOfStrings = new int[] { 22, 52, 82, 109, 139, 169 };
    
    /** paint object used to draw on the canvas */
    Paint paint = new Paint();

    /* x-coordinate of the touch on the screen */
    private int x;
    
    /* y-coordinate of the touch on the screen */
    private int y;

    
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

        paint.setColor(Color.RED);
        AlphaGridShape aShape = new AlphaGridShape(Note.D4);

        if (x < 40 && y > 50 && y <= 100) {
            paint.setColor(Color.RED);
            for (int i = 0; i < midlesOfStrings.length; i++) {
                for (int j = 0; j < midlesOfFrets.length; j++) {
                    canvas.drawCircle(midlesOfFrets[j], midlesOfStrings[i], 10, paint);
                }
            }
        } else if (x < 40 && y > 100 && y <= 150) {
            List<Position> positions = aShape.calculatePositions(Degree.SEVEN);
            for (Position p : positions) {
                canvas.drawCircle(midlesOfFrets[p.fret], midlesOfStrings[p.string], 10, paint);
            }

        } else if (x < 40 && y > 150) {
            List<Position> positions = aShape.getStrongPositions();
            for (Position p : positions) {
                canvas.drawCircle(midlesOfFrets[p.fret], midlesOfStrings[p.string], 10, paint);
            }

        }

        paint.setColor(Color.GREEN);

        canvas.drawCircle(x, 2, 5, paint);
        canvas.drawCircle(40, y, 5, paint);
        canvas.drawCircle(x, y, 10, paint);

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        x = (int) event.getX();
        y = (int) event.getY();
        System.out.println("IMAGE:" + x + ";" + y);
        invalidate();
        return true;
    }
}
