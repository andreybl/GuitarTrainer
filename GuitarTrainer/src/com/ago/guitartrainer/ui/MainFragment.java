package com.ago.guitartrainer.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ago.guitartrainer.MasterActivity;
import com.ago.guitartrainer.R;
import com.ago.guitartrainer.ui.sideout.MenuFragment;
import com.ago.guitartrainer.ui.sideout.SlideoutHelper;

public class MainFragment extends Fragment {

    private SlideoutHelper mSlideoutHelper;

    public SlideoutHelper getSlideoutHelper() {
        return mSlideoutHelper;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        LinearLayout mainLayout = (LinearLayout) inflater.inflate(R.layout.main2, container, false);

        OnClickListener innerOnClickListener = new InnerOnClickListener();
        View btnSideOutMenu = mainLayout.findViewById(R.id.btn_sideout_menu);
        btnSideOutMenu.setOnClickListener(innerOnClickListener);

        return mainLayout;
    }

    /*
     * INNER CLASSES ******
     */
    private class InnerOnClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources()
                    .getDisplayMetrics());

            width = 460;

            /*
             * make screenshot of the main view
             */
            SlideoutHelper.prepareScreenshot(getActivity(), R.id.layout_main_view, width);

            /*
             * show the slide-out menu in a separate activity. Note, that no standard animation is applied during the
             * transition to MenuActivity
             */
            // startActivity(new Intent(GuitarTrainerActivity2.this, MenuActivity.class));
            // overridePendingTransition(0, 0);

            mSlideoutHelper = new SlideoutHelper(getActivity(), false);
            mSlideoutHelper.activate();

//            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
//            transaction = transaction.add(new MenuFragment(), "menu");
//            transaction.commit();

//            MasterActivity activity = (MasterActivity)getActivity();
//            activity.startFragment(MenuFragment.class);
            
            mSlideoutHelper.open();
        }
    }
}
