package com.ago.guitartrainer.ui.dialogs;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.ago.guitartrainer.R;
import com.ago.guitartrainer.lessons.ILesson;
import com.ago.guitartrainer.lessons.custom.LessonKeychordDegree2Parentkey;
import com.ago.guitartrainer.lessons.custom.LessonNote2Position;
import com.ago.guitartrainer.lessons.custom.LessonPosition2Note;
import com.ago.guitartrainer.lessons.custom.LessonScalegridChord2Positions;
import com.ago.guitartrainer.lessons.custom.LessonScalegridDegree2Position;
import com.ago.guitartrainer.ui.LessonsArrayAdapter;

/**
 * Dialog for selecting one of the predefine lessons.
 * 
 * The dialog is dismissed just after the lesson is selected. Only one lesson can be selected. The lesson must be
 * selected before the training can start.
 * 
 * @author Andrej Golovko - jambit GmbH
 * 
 */
public class LessonSelectionDialog extends AbstractCustomDialog {

    private ILesson currentSelection;

    private ListView lvLessons;

    private ArrayAdapter<ILesson> adapter;

    public LessonSelectionDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        int width = dm.widthPixels - (int) Math.round(dm.widthPixels * PADDING_TO_BORDER_AS_PERCENT);
        int height = dm.heightPixels - (int) Math.round(dm.heightPixels * PADDING_TO_BORDER_AS_PERCENT);

        setContentView(R.layout.dialog_lessonselection);

        final LinearLayout mainLayout = (LinearLayout) findViewById(R.id.dialog_lessonselection);
        mainLayout.setLayoutParams(new FrameLayout.LayoutParams(width, height));

        lvLessons = (ListView) findViewById(R.id.list_lessons);

        List<ILesson> values = tmpAvailableLesons();

        adapter = new LessonsArrayAdapter(getContext(), values);
        lvLessons.setAdapter(adapter);

        OnItemClickListener onItemSelected = new InnerOnItemClickListener();
        lvLessons.setOnItemClickListener(onItemSelected);

        // getActionBar().setTitle(R.string.dialog_lessonselection_title);

    }

    /**
     * Temporary method delivering content of the list
     * 
     * @return
     */
    private List<ILesson> tmpAvailableLesons() {
        List<ILesson> lessons = new ArrayList<ILesson>();

        ILesson lesson1 = new LessonPosition2Note();
        ILesson lesson2 = new LessonNote2Position();
        ILesson lesson3 = new LessonScalegridDegree2Position();
        ILesson lesson4 = new LessonScalegridChord2Positions();
        ILesson lesson5 = new LessonKeychordDegree2Parentkey();

        lessons.add(lesson1);
        lessons.add(lesson2);
        lessons.add(lesson3);
        lessons.add(lesson4);
        lessons.add(lesson5);

        return lessons;
    }

    /**
     * Returns lesson selected in the dialog.
     * 
     * Null is returned, if no lesson was selected.
     * 
     * @return selected lesson, or <code>null</code>
     */
    public ILesson selectedLesson() {
        return currentSelection;
    }

    /*
     * *** INNER CLASSES
     */
    private class InnerOnItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
            currentSelection = adapter.getItem(pos);
            // currentSelection.prepareUi();
            LessonSelectionDialog.this.dismiss();

        }
    }

}
