package com.ago.guitartrainer.ui.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.ago.guitartrainer.R;

public class InstrumentSelectionDialog extends AbstractCustomDialog {

    public InstrumentSelectionDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        int width = dm.widthPixels - (int) Math.round(dm.widthPixels * PADDING_TO_BORDER_AS_PERCENT);
        int height = dm.heightPixels - (int) Math.round(dm.heightPixels * PADDING_TO_BORDER_AS_PERCENT);

        setContentView(R.layout.dialog_instrumentselection);

        final LinearLayout mainLayout = (LinearLayout) findViewById(R.id.dialog_instrumentselection);
        mainLayout.setLayoutParams(new FrameLayout.LayoutParams(width, height));
    }

}
