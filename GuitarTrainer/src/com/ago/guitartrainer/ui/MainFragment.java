package com.ago.guitartrainer.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ago.guitartrainer.R;
import com.ago.guitartrainer.lessons.ILesson;

public class MainFragment extends Fragment {

    // private Button btnSideOutMenu;

    private static String TAG = "GT-MainFragment";

    private Button btnSelectLessonDialog;

    private Button btnStartLesson;

    private Button btnNextLesson;

    private Button btnStopLesson;

    private ILesson currentLesson;

    private FretView fretView;

    private ShapesView shapestView;

    private DegreesView degreesView;

    private NotesView notesView;

    private TextView tvLessonStatus;

    /*
     * TODO: rework the concept. The fragment - and basically its views - are required in lessons. But getting the
     * fragment in such way as singleton is not nice.
     */
    private static MainFragment instance;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        LinearLayout mainLayout = (LinearLayout) inflater.inflate(R.layout.main2, container, false);
        /* Start: custom views: fret/shape/notes etc. */
        fretView = (FretView) mainLayout.findViewById(R.id.view_fretview);
        shapestView = (ShapesView) mainLayout.findViewById(R.id.view_shapesview);
        degreesView = (DegreesView) mainLayout.findViewById(R.id.view_degreesview);
        notesView = (NotesView) mainLayout.findViewById(R.id.view_notesview);
        tvLessonStatus = (TextView) mainLayout.findViewById(R.id.tv_lesson_status);
        /* End: custom views: fret/shape/notes etc. */

        // btnSideOutMenu = (Button) mainLayout.findViewById(R.id.btn_sideout_menu);

        OnClickListener innerOnClickListener = new InnerOnClickListener();

        /* Start: buttons for lesson control */
        btnSelectLessonDialog = (Button) mainLayout.findViewById(R.id.btn_lesson_select);
        btnStartLesson = (Button) mainLayout.findViewById(R.id.btn_lesson_start);
        btnNextLesson = (Button) mainLayout.findViewById(R.id.btn_lesson_next);
        btnStopLesson = (Button) mainLayout.findViewById(R.id.btn_lesson_stop);

        btnSelectLessonDialog.setOnClickListener(innerOnClickListener);
        btnStartLesson.setOnClickListener(innerOnClickListener);
        btnNextLesson.setOnClickListener(innerOnClickListener);
        btnStopLesson.setOnClickListener(innerOnClickListener);

        btnStartLesson.setEnabled(false);
        btnNextLesson.setEnabled(false);
        btnStopLesson.setEnabled(false);
        
        // Note: the assignment must be done before the ILesson is instantiated.
        instance = this;

        /* try to recall the previous lesson type from the shared preferences */
        String lastLessonClazz = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(
                IPrefKeys.KEY_LESSON_CLAZZ, null);
        if (lastLessonClazz != null) {
            Class<?> clazz;
            try {
                clazz = Class.forName(lastLessonClazz);
                currentLesson = (ILesson) clazz.newInstance();
            } catch (ClassNotFoundException e) {
                Log.e(TAG, e.getMessage());
            } catch (java.lang.InstantiationException e) {
                Log.e(TAG, e.getMessage());
            } catch (IllegalAccessException e) {
                Log.e(TAG, e.getMessage());
            }

            if (currentLesson != null) {
                currentLesson.prepareUi();
                btnStartLesson.setEnabled(true);
                btnSelectLessonDialog.setText(currentLesson.getTitle());
            }
        }

        return mainLayout;
    }

    public static MainFragment getInstance() {
        return instance;
    }

    public FretView getFretView() {
        return fretView;
    }

    // TODO: rename
    public ShapesView getShapestView() {
        return shapestView;
    }

    public DegreesView getDegreesView() {
        return degreesView;
    }

    public NotesView getNotesView() {
        return notesView;
    }

    public TextView getLessonStatusView() {
        return tvLessonStatus;
    }

    /*
     * INNER CLASSES ******
     */
    /**
     * Listener for clicks on lesson control buttons and on slide-out menu button.
     * 
     * The listener does not take into account any events on custom view.
     * 
     * @author Andrej Golovko - jambit GmbH
     * 
     */
    private class InnerOnClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
            case R.id.btn_lesson_select: {
                final LessonSelectionDialog lessonDialog = new LessonSelectionDialog(getActivity());
                lessonDialog.setOnDismissListener(new OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        currentLesson = lessonDialog.currentLesson();
                        Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                        editor.putString(IPrefKeys.KEY_LESSON_CLAZZ, currentLesson.getClass().getName());
                        editor.commit();
                        if (currentLesson != null) {
                            btnStartLesson.setEnabled(true);
                            btnSelectLessonDialog.setText(currentLesson.getTitle());
                        }
                    }
                });

                lessonDialog.show();

                break;
            }
            case R.id.btn_lesson_start: {
                btnStartLesson.setEnabled(false);
                btnSelectLessonDialog.setEnabled(false);
                btnStopLesson.setEnabled(true);
                btnNextLesson.setEnabled(true);

                if (currentLesson != null)
                    currentLesson.next();
                break;
            }
            case R.id.btn_lesson_next: {
                // we skip to the next Question inside of the lesson,
                // we do NOT skip to the next lesson here

                if (currentLesson != null)
                    currentLesson.next();
                break;
            }
            case R.id.btn_lesson_stop: {
                btnSelectLessonDialog.setEnabled(true);
                btnStartLesson.setEnabled(true);
                btnStopLesson.setEnabled(false);
                btnNextLesson.setEnabled(false);
                if (currentLesson != null)
                    currentLesson.stop();
                break;
            }
            default:
                break;
            }

        }
    }
}
