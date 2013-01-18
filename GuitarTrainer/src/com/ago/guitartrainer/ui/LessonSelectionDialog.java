package com.ago.guitartrainer.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ago.guitartrainer.R;
import com.ago.guitartrainer.lessons.ILesson;
import com.ago.guitartrainer.lessons.SimpleLesson;

/**
 * Dialog for selecting one of the predefine lessons.
 * 
 * The dialog is dismissed just after the lesson is selected. Only one lesson can be selected. The lesson must be
 * selected before the training can start.
 * 
 * @author Andrej Golovko - jambit GmbH
 * 
 */
public class LessonSelectionDialog extends Dialog {

    private ILesson currentSelection;

    private ListView lvLessons;

    private ArrayAdapter<ILesson> adapter;

    public LessonSelectionDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.list_lessons);

        lvLessons = (ListView) findViewById(R.id.list_lessons);

        List<ILesson> values = tmpAvailableLesons();

        adapter = new LessonsArrayAdapter(getContext(), values);
        lvLessons.setAdapter(adapter);

        OnItemClickListener onItemSelected = new InnerOnItemClickListener();
        lvLessons.setOnItemClickListener(onItemSelected);

    }

    /**
     * Temporary method delivering content of the list 
     * 
     * @return
     */
    private List<ILesson> tmpAvailableLesons() {
        List<ILesson> lessons = new ArrayList<ILesson>();
        
        SimpleLesson lesson1 = new SimpleLesson();
        SimpleLesson lesson2 = new SimpleLesson();
        
        lessons.add(lesson1);
        lessons.add(lesson2);

        return lessons;
    }

    /**
     * Returns lesson selected in the dialog.
     * 
     * Null is returned, if no lesson was selected.
     * 
     * @return selected lesson, or <code>null</code>
     */
    public ILesson currentLesson() {
        return currentSelection;
    }

    /*
     * *** INNER CLASSES
     */
    private class InnerOnItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
            currentSelection = adapter.getItem(pos);
            LessonSelectionDialog.this.dismiss();

        }
    }

}
