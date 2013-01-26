package com.ago.guitartrainer.ui.sideout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.ago.guitartrainer.MasterActivity;
import com.ago.guitartrainer.R;
import com.ago.guitartrainer.ui.MainFragment;

public class MenuFragment extends Fragment {

    private Button btnStartLesson;

    private Button btnEndLesson;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout mainLayout = (LinearLayout) inflater.inflate(R.layout.sideoutmenu_content, container, false);

        btnStartLesson = (Button) mainLayout.findViewById(R.id.btn_start_lesson);
        btnEndLesson = (Button) mainLayout.findViewById(R.id.btn_end_lesson);

        OnClickListener onClickListener = new InnerOnClickListener();
        btnStartLesson.setOnClickListener(onClickListener);
        btnEndLesson.setOnClickListener(onClickListener);

        return mainLayout;
    }


    private class InnerOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            MasterActivity activity = (MasterActivity) getActivity();

            switch (v.getId()) {
            case R.id.btn_start_lesson:

                break;
            case R.id.btn_end_lesson:

                break;
            default:
                break;
            }
            
            // TODO: close
//            activity.getSlideoutHelper().close();
//            activity.startFragment(MainFragment.class);

        }

    }

}
