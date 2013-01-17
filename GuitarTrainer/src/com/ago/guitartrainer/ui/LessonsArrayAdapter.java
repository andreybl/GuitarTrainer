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

public class LessonsArrayAdapter extends ArrayAdapter<ILesson> {

    public LessonsArrayAdapter(Context context, List<ILesson> objects) {
        super(context, R.id.list_lessons_item_title, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.list_lessons_item, parent, false);

            ILesson lesson = getItem(position);
            
            TextView tvTitle = (TextView) row.findViewById(R.id.list_lessons_item_title);
            TextView tvTime = (TextView) row.findViewById(R.id.list_lessons_item_time);

            tvTitle.setText(lesson.getTitle());

            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa");
            Date dt = new Date(lesson.getDuration());
            String strValue = timeFormat.format(dt);
            tvTime.setText(strValue);

        }

        return row;
    }
}
