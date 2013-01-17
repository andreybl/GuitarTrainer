package com.ago.guitartrainer.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.ago.guitartrainer.R;
import com.ago.guitartrainer.lessons.ILesson;
import com.ago.guitartrainer.ui.sideout.SlideoutHelper;

public class MainFragment extends Fragment {

    private Button btnSideOutMenu;

    private Button btnSelectLessonDialog;

    private Button btnStartLesson;

    private Button btnNextLesson;

    private Button btnStopLesson;

    private ILesson currentLesson;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        LinearLayout mainLayout = (LinearLayout) inflater.inflate(R.layout.main2, container, false);

        btnSideOutMenu = (Button) mainLayout.findViewById(R.id.btn_sideout_menu);

        OnClickListener innerOnClickListener = new InnerOnClickListener();
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

        btnSideOutMenu.setOnClickListener(innerOnClickListener);

        return mainLayout;
    }

    /*
     * INNER CLASSES ******
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
                break;
            }
            case R.id.btn_lesson_next: {
                // we skip to the next Question inside of the lesson,
                // we do NOT skip to the next lesson here

                // TODO: implement the skipping
                break;
            }
            case R.id.btn_lesson_stop: {
                btnSelectLessonDialog.setEnabled(true);
                btnStartLesson.setEnabled(true);
                btnStopLesson.setEnabled(false);
                btnNextLesson.setEnabled(false);
                break;
            }
            default:
                break;
            }

        }
    }
}
