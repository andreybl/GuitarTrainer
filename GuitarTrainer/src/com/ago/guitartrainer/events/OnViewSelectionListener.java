package com.ago.guitartrainer.events;

import com.ago.guitartrainer.ui.FretView;
import com.ago.guitartrainer.ui.NotesView;

import android.widget.GridView;

/**
 * 
 * The listener must be implemented by the observer for the views, which are 
 * used for user inputs: {@link NotesView}, {@link FretView}, {@link GridView} etc.
 * 
 * Only events as result of user selection are fired to observers implementing the interface.
 * 
 * @author Andrej Golovko - jambit GmbH
 *
 * @param <T> type of the element for the view
 */
public interface OnViewSelectionListener<T> {

    public void onViewElementSelected(T element);
}
