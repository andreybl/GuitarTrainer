package com.ago.guitartrainer.lessons.custom;

import com.ago.guitartrainer.MasterActivity;
import com.ago.guitartrainer.R;
import com.ago.guitartrainer.events.OnViewSelectionListener;
import com.ago.guitartrainer.fragments.FragmentModeDegree2Parent;
import com.ago.guitartrainer.notation.Degree;
import com.ago.guitartrainer.notation.Key;
import com.ago.guitartrainer.ui.LearningStatusView;
import com.ago.guitartrainer.utils.LessonsUtils;

public class LessonModeDegree2Parent extends ALesson {

    private FragmentModeDegree2Parent fragment;

    @Override
    public String getTitle() {
        String str = MasterActivity.getInstance().getResources()
                .getString(R.string.lesson_modedegree2parent_title);
        return str;
    }

    @Override
    public String getDescription() {
        String str = MasterActivity.getInstance().getResources()
                .getString(R.string.lesson_modedegree2parent_description);
        return str;
    }

    @Override
    public void showMetrics() {
        // TODO Auto-generated method stub

    }

    @Override
    public void doNext() {
        Key mode;
        if (fragment.getModesView().isRandomInput()) {
            mode = LessonsUtils.randomKey();
            fragment.getModesView().show(mode);
        } else {
            mode = fragment.getModesView().element();
        }

        Degree degree;
        if (fragment.getDegreesView().isRandomInput()) {
            degree = LessonsUtils.randomDegree();
            fragment.getDegreesView().show(degree);
        } else {
            degree = fragment.getDegreesView().element();
        }

        expectedParentScale = Key.parentByModeAndDegree(mode, degree);

    }

    private Key expectedParentScale;

    @Override
    public void doStop() {

    }

    @Override
    protected LearningStatusView getLearningStatusView() {
        return fragment.getLearningStatusView();
    }

    public void onFragmentInitializationCompleted(FragmentModeDegree2Parent fragment) {
        this.fragment = fragment;

        fragment.getModesView().isMainKeysOnly(false);
        // fragment.getChordsView();
        // fragment.getDegreesView();
        fragment.getParentsView().isMainKeysOnly(false);
        fragment.getParentsView().isOutput(true);

        OnViewSelectionListener<Key> onSelectionListener = new InnerOnSelectionListener();
        fragment.getParentsView().registerListener(onSelectionListener);

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
