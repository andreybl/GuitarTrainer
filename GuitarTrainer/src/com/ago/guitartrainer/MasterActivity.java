package com.ago.guitartrainer;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.ago.guitartrainer.db.DatabaseHelper;
import com.ago.guitartrainer.fragments.FragmentBenchmarkFretview;
import com.ago.guitartrainer.fragments.FragmentFromLessonFactory;
import com.ago.guitartrainer.fragments.FragmentMain;
import com.ago.guitartrainer.lessons.ILesson;
import com.ago.guitartrainer.ui.dialogs.AboutDialog;
import com.ago.guitartrainer.ui.dialogs.InstrumentSelectionDialog;
import com.ago.guitartrainer.ui.dialogs.LessonSelectionDialog;

public class MasterActivity extends FragmentActivity {

    private static String TAG = "GT-MasterActivity";

    private ILesson currentLesson;

    private MenuItem miInstrumentSelect;

    private MenuItem miLessonSelect;

    private MenuItem miStart;

    private MenuItem miStop;

    private MenuItem miNext;

    private MenuItem miMetrics;

    private static MasterActivity INSTANCE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        // getActionBar().hide();
        // }

        // set the Above View
        setContentView(R.layout.content_frame);

        /*
         * Calling this [setDefauulValues()] during onCreate() ensures that your application is properly initialized
         * with default settings, which your application might need to read in order to determine some behaviors (such
         * as whether to download data while on a cellular network).
         */
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        INSTANCE = this;

        currentLesson = currentLessonFromPreferences();

        if (currentLesson != null) {
            Fragment fragment = FragmentFromLessonFactory.fragmentForLesson(currentLesson);
            replaceFragment(fragment);
        } else {
            replaceFragment(new FragmentMain());

        }

    }

    public static MasterActivity getInstance() {
        return INSTANCE;
    }

    private ILesson currentLessonFromPreferences() {
        /* try to recall the previous lesson type from the shared preferences */

        String lastLessonClazz = GuitarTrainerApplication.getPrefs().getString(SettingsActivity.KEY_LESSON_CLAZZ, null);
        ILesson lesson = null;
        if (lastLessonClazz != null) {
            Class<?> clazz;
            try {
                clazz = Class.forName(lastLessonClazz);
                lesson = (ILesson) clazz.newInstance();
            } catch (ClassNotFoundException e) {
                Log.e(TAG, e.getMessage());
            } catch (java.lang.InstantiationException e) {
                Log.e(TAG, e.getMessage());
            } catch (IllegalAccessException e) {
                Log.e(TAG, e.getMessage());
            }
        }

        return lesson;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*
         * The "..." will not be shown, as far as it is in Action Bar, but it is hidden with getActionBar().hide() call
         */
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_master_activity, menu);

        /* Init buttons for lesson control */
        miInstrumentSelect = menu.findItem(R.id.menu_instrument_select);
        miLessonSelect = menu.findItem(R.id.menu_lesson_select);
        miStart = menu.findItem(R.id.menu_lesson_start);
        miNext = menu.findItem(R.id.menu_lesson_next);
        miStop = menu.findItem(R.id.menu_lesson_stop);
        miMetrics = menu.findItem(R.id.menu_lesson_metrics);

        miStart.setEnabled(false);
        miNext.setEnabled(false);
        miStop.setEnabled(false);
        miMetrics.setEnabled(false);
        miInstrumentSelect.setEnabled(true);

        if (currentLesson != null) {
            // currentLesson.prepareUi();
            miStart.setEnabled(true);
            /*
             * TODO: add "Info" icon to inform about lesson,
             * 
             * The name of the lesson could be shown in in the Fragment of lesson itself.
             */
            miMetrics.setEnabled(true);
            // learningStatusView.updateLessonName(currentLesson.getTitle());

        }

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_lesson_select: {
            onMenuLessonSelectionSelected();
            break;
        }
        case R.id.menu_settings: {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            break;
        }
        case R.id.menu_benchmark: {
            replaceFragment(new FragmentBenchmarkFretview());
            break;
        }
        case R.id.menu_reset_database: {
            DatabaseHelper.getInstance().resetData();
            Toast.makeText(this, "The database content truncated", 2000).show();
            break;
        }
        case R.id.menu_lesson_start: {
            miStart.setEnabled(false);
            miLessonSelect.setEnabled(false);
            miStop.setEnabled(true);
            miNext.setEnabled(true);
            miMetrics.setEnabled(false);
            miInstrumentSelect.setEnabled(false);

            if (currentLesson != null)
                currentLesson.next();

            break;
        }
        case R.id.menu_lesson_next: {
            // we skip to the next Question inside of the lesson,
            // we do NOT skip to the next lesson here

            if (currentLesson != null)
                currentLesson.next();
            break;
        }

        case R.id.menu_lesson_stop: {
            miLessonSelect.setEnabled(true);
            miStart.setEnabled(true);
            miStop.setEnabled(false);
            miNext.setEnabled(false);
            miMetrics.setEnabled(true);
            miInstrumentSelect.setEnabled(true);

            if (currentLesson != null)
                currentLesson.stop();

            break;
        }
        case R.id.menu_lesson_metrics: {
            if (currentLesson != null)
                currentLesson.showMetrics();
            break;
        }
        case R.id.menu_instrument_select: {
            onMenuInstrumentSelectionSelected();
            break;
        }

        case R.id.about: {
            AboutDialog about = new AboutDialog(this);
            about.setTitle("about this app");

            about.show();

            break;
        }
        }

        return false;
    }

    private void onMenuInstrumentSelectionSelected() {
        final Dialog dialog = new InstrumentSelectionDialog(this);
        dialog.show();

    }

    private void onMenuLessonSelectionSelected() {
        final LessonSelectionDialog lessonDialog = new LessonSelectionDialog(this);
        lessonDialog.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                ILesson selectedLesson = lessonDialog.selectedLesson();
                if (selectedLesson != null) {
                    if (currentLesson != null && currentLesson.isRunning())
                        currentLesson.stop();

                    currentLesson = selectedLesson;

                    Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                    editor.putString(SettingsActivity.KEY_LESSON_CLAZZ, currentLesson.getClass().getName());
                    editor.commit();

                    miStart.setEnabled(true);
                    miMetrics.setEnabled(true);
                    miInstrumentSelect.setEnabled(true);

                    Fragment fragment = FragmentFromLessonFactory.fragmentForLesson(currentLesson);
                    replaceFragment(fragment);

                    // TODO: use the line in some way
                    // learningStatusView.updateLessonName(currentLesson.getTitle());
                } else {
                    Toast.makeText(getApplicationContext(), "No lesson selected. Using the previous one", 2000).show();
                }
            }
        });

        lessonDialog.show();
    }

    public void replaceFragment(Fragment frg) {
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, frg);
        ft.addToBackStack(null);
        ft.commit();
    }

}
