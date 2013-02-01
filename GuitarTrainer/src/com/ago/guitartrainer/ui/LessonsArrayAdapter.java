package com.ago.guitartrainer.ui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ago.guitartrainer.R;
import com.ago.guitartrainer.db.DatabaseHelper;
import com.ago.guitartrainer.lessons.ILesson;
import com.ago.guitartrainer.lessons.LessonMetrics;
import com.ago.guitartrainer.utils.TimeUtils;
import com.j256.ormlite.dao.RuntimeExceptionDao;

public class LessonsArrayAdapter extends ArrayAdapter<ILesson> {

    public LessonsArrayAdapter(Context context, List<ILesson> objects) {
        super(context, R.id.list_lessons_item_title, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.lessonselection_listitem, parent, false);

            ILesson lesson = getItem(position);

            TextView tvTitle = (TextView) row.findViewById(R.id.list_lessons_item_title);
            TextView tvTime = (TextView) row.findViewById(R.id.list_lessons_item_time);
            TextView tvDescription = (TextView) row.findViewById(R.id.list_lessons_item_description);

            tvTitle.setText(lesson.getTitle());
            tvDescription.setText(lesson.getDescription());

            LessonMetrics lessonMetrics = DatabaseHelper.getInstance().findLessonMetrics(lesson.getClass());

            if (lessonMetrics != null) {
                String strValue = TimeUtils.formatDuration(lessonMetrics.durationTotal());
                tvTime.setText(strValue);
            } else {
                tvTime.setText("unknown");
            }

        }

        return row;
    }
}
