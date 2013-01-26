package com.ago.guitartrainer.ui.sideout;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ago.guitartrainer.R;

public class SlideoutHelper {

    private static Bitmap sCoverBitmap = null;
    private static int sWidth = -1;

    private static final int DURATION_MS = 400;
    private ImageView mCover;
    private Activity mActivity;

    /**
     * define the side, on which the side out menu appears. If true, the menu is shown on the right side. On the left
     * side otherwise.
     * */
    private boolean mReverse = false;

    private Animation mStartAnimation;
    
    private Animation mStopAnimation;

    /**
     * The call makes a snapshot (e.g. screenshot) of the main activity view.
     * 
     * The method is called every time the button is clicked to show slide-out menu. The screenshot of the main view is
     * saved in {@link #sCoverBitmap} variable. This image will be used to swipe the main view to the right, when the
     * button for slide-out menu is clicked.
     * 
     * The "width" parameter controls, how much of the main menu is left shown on the screen during slide-out menu is
     * also shown to the user.
     * 
     * @param activity
     *            in which the button of the slide-out menu is available
     * @param id
     *            of the layout, which screenshot must be taken
     * @param width
     *            of the main view visible part during the slide-out menu is being shown.
     */
    public static void prepareScreenshot(Activity activity, int id, int width) {
        if (sCoverBitmap != null) {
            sCoverBitmap.recycle();
        }
        Rect rectgle = new Rect();
        Window window = activity.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectgle);
        int statusBarHeight = rectgle.top;

        ViewGroup v1 = (ViewGroup) activity.findViewById(id).getRootView();
        v1.setDrawingCacheEnabled(true);
        Bitmap source = Bitmap.createBitmap(v1.getDrawingCache());
        v1.setDrawingCacheEnabled(false);
        if (statusBarHeight != 0) {
            sCoverBitmap = Bitmap.createBitmap(source, 0, statusBarHeight, source.getWidth(), source.getHeight()
                    - statusBarHeight);
            source.recycle();
        } else {
            sCoverBitmap = source;
        }
        sWidth = width;
    }

    public SlideoutHelper(Activity activity, boolean reverse) {
        mActivity = activity;
        mReverse = reverse;
    }

    public void activate() {
        // Not required now because fragments are used
        // Was required earlier, when activity was used for slide-out menu.  
//        mActivity.setContentView(R.layout.slideout);
        
        mCover = (ImageView) mActivity.findViewById(R.id.slidedout_cover);
        mCover.setImageBitmap(sCoverBitmap);
        mCover.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * user click on screenshot, which represent the main view The click on the screenshot causes the slide
                 * out menu to be closed.
                 */
                close();
            }
        });
        int x = (int) (sWidth * 1.2f);
        if (mReverse) {
            // never called
//            @SuppressWarnings("deprecation")
            final RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(x, 0);
            mActivity.findViewById(R.id.fragment_container).setLayoutParams(lp);
        } else {
//            @SuppressWarnings("deprecation")
            final RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(0, 0);
            mActivity.findViewById(R.id.fragment_container).setLayoutParams(lp);
        }
        initAnimations();
    }

    public void open() {
        mCover.startAnimation(mStartAnimation);
    }

    public void close() {
        mCover.startAnimation(mStopAnimation);
    }

    private void initAnimations() {
        int displayWidth = ((WindowManager) mActivity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                .getWidth();
        final int shift = (mReverse ? -1 : 1) * (sWidth - displayWidth);
        mStartAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0, TranslateAnimation.ABSOLUTE, -shift,
                TranslateAnimation.ABSOLUTE, 0, TranslateAnimation.ABSOLUTE, 0);

        mStopAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0, TranslateAnimation.ABSOLUTE, shift,
                TranslateAnimation.ABSOLUTE, 0, TranslateAnimation.ABSOLUTE, 0);
        mStartAnimation.setDuration(DURATION_MS);
        mStartAnimation.setFillAfter(true);
        AnimationListener startAnimationListener = new StartAnimationListener();
        mStartAnimation.setAnimationListener(startAnimationListener);

        mStopAnimation.setDuration(DURATION_MS);
        mStopAnimation.setFillAfter(true);
        AnimationListener stopAnimationListener = new StopAnimationListener();
        mStopAnimation.setAnimationListener(stopAnimationListener);
    }

    /*
     * INNER CLASSES *************
     */

    private class StartAnimationListener implements AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mCover.setAnimation(null);

            int displayWidth = ((WindowManager) mActivity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                    .getWidth();
            final int shift = (mReverse ? -1 : 1) * (sWidth - displayWidth);

            final RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(-shift, 0);
            mCover.setLayoutParams(lp);
        }
    }

    private class StopAnimationListener implements AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
//            mActivity.getFragmentManager().popBackStack();
            mActivity.onBackPressed();
            
//            mActivity.finish();
//            mActivity.overridePendingTransition(0, 0);
        }
    }

}
