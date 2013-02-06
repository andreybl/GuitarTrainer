package com.ago.guitartrainer.lessons.custom;

import com.ago.guitartrainer.MasterActivity;
import com.ago.guitartrainer.R;
import com.ago.guitartrainer.events.OnViewSelectionListener;
import com.ago.guitartrainer.fragments.FragmentKeychordDegree2Parentkey;
import com.ago.guitartrainer.notation.Degree;
import com.ago.guitartrainer.notation.Key;
import com.ago.guitartrainer.ui.LearningStatusView;
import com.ago.guitartrainer.utils.LessonsUtils;

public class LessonKeychordDegree2Parentkey extends ALesson {

    private FragmentKeychordDegree2Parentkey fragment;

    @Override
    public String getTitle() {
        String str = MasterActivity.getInstance().getResources()
                .getString(R.string.lesson_keychorddegree2parentkey_title);
        return str;
    }

    @Override
    public String getDescription() {
        String str = MasterActivity.getInstance().getResources()
                .getString(R.string.lesson_keychorddegree2parentkey_description);
        return str;
    }

    @Override
    public void showMetrics() {
        // TODO Auto-generated method stub

    }

    @Override
    public void doNext() {
        Key originalKey;
        if (fragment.getKeysView().isRandomInput()) {
            originalKey = LessonsUtils.randomKey();
            fragment.getKeysView().show(originalKey);
        } else {
            originalKey = fragment.getKeysView().element();
        }

        Degree mode;
        if (fragment.getDegreesView().isRandomInput()) {
            mode = LessonsUtils.randomDegree();
            fragment.getDegreesView().show(mode);
        } else {
            mode = fragment.getDegreesView().element();
        }

        expectedParentScale = Key.parentScaleByKeyAndMode(originalKey, mode);

    }

    private Key expectedParentScale;

    @Override
    public void doStop() {

    }

    @Override
    protected LearningStatusView getLearningStatusView() {
        return fragment.getLearningStatusView();
    }

    public void onFragmentInitializationCompleted(FragmentKeychordDegree2Parentkey fragment) {
        this.fragment = fragment;

        fragment.getKeysView().isMainKeysOnly(false);
        // fragment.getChordsView();
        // fragment.getDegreesView();
        fragment.getParentKeysView().isMainKeysOnly(false);
        fragment.getParentKeysView().isOutput(true);

        OnViewSelectionListener<Key> onSelectionListener = new InnerOnSelectionListener();
        fragment.getParentKeysView().registerListener(onSelectionListener);

    }

    /*
     * **** INNER CLASS
     */
    private class InnerOnSelectionListener implements OnViewSelectionListener<Key> {

        @Override
        public void onViewElementSelected(Key element) {
            if (!isLessonRunning())
                return;

            if (expectedParentScale == element) {
                onSuccess();
            } else {
                onFailure();
            }
        }
    }
}
