package com.ago.guitartrainer.ui.dialogs;

import android.app.Dialog;
import android.content.Context;

public class AbstractCustomDialog extends Dialog {
    
    public AbstractCustomDialog(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    /**
     * The padding from the dialog border to the screen border, given as percent of the total screen size
     */
    protected final double PADDING_TO_BORDER_AS_PERCENT = 0.2;

}
