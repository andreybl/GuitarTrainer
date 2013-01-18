package com.ago.guitartrainer;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.ago.guitartrainer.ui.MainFragment;

public class MasterActivity extends FragmentActivity {

    private static String TAG = "GT-MasterActivity";

//    private SlidingMenu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().hide();
        }

        setContentView(R.layout.main2);
        
        // set the Above View
        setContentView(R.layout.content_frame);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new MainFragment()).commit();

        // configure the SlidingMenu
//        menu = new SlidingMenu(this);
//        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
//        menu.setShadowWidthRes(R.dimen.shadow_width);
//        menu.setShadowDrawable(R.drawable.shadow);
//        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
//        menu.setFadeDegree(0.35f);
//        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
//        menu.setMenu(R.layout.menu_frame);
    }

//    @Override
//    public void onBackPressed() {
//        if (menu.isMenuShowing()) {
//            menu.showContent();
//        } else {
//            super.onBackPressed();
//        }
//    }

    // public void startFragment(final Class<? extends Fragment> nextFragmentClazz) {
    //
    // /*
    // * name of the current transition, which should be logged in the log messages. it is built accumulatively.
    // */
    // String transitionBadge = "";
    //
    // // Get the currently displayed fragment
    // Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
    //
    // /*
    // * Step 3. Transit to the next fragment
    // */
    // // We use the fragment's class name as tag used to identify the fragment transaction on the back stack.
    // // We can do this since only one instance of each fragment class should be active (or on the back stack) at
    // // the same time.
    // String fragmentTag = nextFragmentClazz.getName();
    //
    // // Using the fragment manager we look if the requested next fragment already exists on the back stack
    // FragmentManager fragmentManager = getSupportFragmentManager();
    // boolean fragmentFoundOnBackStack = false;
    // for (int i = fragmentManager.getBackStackEntryCount() - 1; i >= 0; i--) {
    // BackStackEntry backStackEntry = fragmentManager.getBackStackEntryAt(i);
    // if (backStackEntry.getName().equals(fragmentTag)) {
    // fragmentFoundOnBackStack = true;
    // // Just pop the right back stack state which will launch the right fragment and also automatically
    // // play the right transition animation
    // fragmentManager.popBackStack(fragmentTag, 0);
    //
    // // Set currentNavigationDepth to new value
    // // currentNavigationDepth = nextNavigationDepth;
    //
    // // Since the back stack should only contain one instance of each fragment we can exit the loop
    // break;
    // }
    // }
    // // If the fragment was not found on the back stack we have to create a new instance + transaction
    // if (!fragmentFoundOnBackStack) {
    // try {
    // // Create new fragment that will replace the current fragment
    // Fragment newFragment = nextFragmentClazz.newInstance();
    //
    // // Create transaction for replacing the fragments
    // FragmentTransaction transaction = fragmentManager.beginTransaction();
    //
    // // Set transition animation for exchanging the fragments; we also set the inverse animation should
    // // the transaction be popped from the back stack later
    // // Replace whatever is in the fragment_container view with the new fragment
    // transaction.replace(R.id.fragment_container, newFragment);
    // // // Add new state to back stack
    // transaction.addToBackStack(fragmentTag);
    //
    // // Schedule commit of transaction
    // transaction.commit();
    //
    // } catch (Exception e) {
    // Log.e(TAG, "Error while trying to instantiate new fragment, transition: " + transitionBadge, e);
    // }
    // }
    // }

}
