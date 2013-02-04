package com.ago.guitartrainer.ui;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Canvas;

import com.ago.guitartrainer.instruments.blockfleute.Hole;

public class BlockfleuteImageView extends FretImageView {

    private static Map<Hole, Integer[]> mapHole2Coordinates = new HashMap<Hole, Integer[]>();

    static {
        mapHole2Coordinates.put(Hole.HOLE1, new Integer[] { 123, 456 });
        mapHole2Coordinates.put(Hole.HOLE2, new Integer[] { 123, 456 });
        mapHole2Coordinates.put(Hole.HOLE3, new Integer[] { 123, 456 });
    }

    // TODO: exceptions to size of selected dots
    // TODO: dot mode

    public BlockfleuteImageView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
    }
}
