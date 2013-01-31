package com.ago.guitartrainer.ui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ago.guitartrainer.R;
import com.ago.guitartrainer.lessons.ILesson;
import com.ago.guitartrainer.lessons.LessonMetrics;

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

            LessonMetrics lessonMetrics = lesson.getLessonMetrics();
            if (lessonMetrics != null) {
                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa");
                Date dt = new Date(lessonMetrics.durationTotal());
                String strValue = timeFormat.format(dt);
                tvTime.setText(strValue);
            } else {
                tvTime.setText("unknown");
            }

        }

        return row;
    }
}
