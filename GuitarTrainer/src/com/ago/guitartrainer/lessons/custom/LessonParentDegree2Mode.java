package com.ago.guitartrainer.lessons.custom;

import com.ago.guitartrainer.MasterActivity;
import com.ago.guitartrainer.R;
import com.ago.guitartrainer.events.OnViewSelectionListener;
import com.ago.guitartrainer.fragments.FragmentParentDegree2Mode;
import com.ago.guitartrainer.notation.Degree;
import com.ago.guitartrainer.notation.Key;
import com.ago.guitartrainer.ui.LearningStatusView;
import com.ago.guitartrainer.utils.LessonsUtils;

public class LessonParentDegree2Mode extends ALesson {

    private FragmentParentDegree2Mode fragment;

    @Override
    public String getTitle() {
        String str = MasterActivity.getInstance().getResources().getString(R.string.lesson_parentdegree2mode_title);
        return str;
    }

    @Override
    public String getDescription() {
        String str = MasterActivity.getInstance().getResources()
                .getString(R.string.lesson_parentdegree2mode_description);
        return str;
    }

    @Override
    public void showMetrics() {
        // TODO Auto-generated method stub

    }

    @Override
    public void doNext() {
        Key parent;
        if (fragment.getModesView().isRandomInput()) {
            parent = LessonsUtils.randomKey();
            fragment.getParentsView().show(parent);
        } else {
            parent = fragment.getParentsView().element();
        }

        Degree degree;
        if (fragment.getDegreesView().isRandomInput()) {
            degree = LessonsUtils.randomDegree();
            fragment.getDegreesView().show(degree);
        } else {
            degree = fragment.getDegreesView().element();
        }

        expectedMode = Key.modeByParentAndDegree(parent, degree);

    }

    private Key expectedMode;

    @Override
    public void doStop() {

    }

    @Override
    protected LearningStatusView getLearningStatusView() {
        return fragment.getLearningStatusView();
    }

    public void onFragmentInitializationCompleted(FragmentParentDegree2Mode fragment) {
        this.fragment = fragment;

        fragment.getModesView().isMainKeysOnly(false);
        fragment.getParentsView().isMainKeysOnly(false);

        fragment.getModesView().isOutput(true);

        OnViewSelectionListener<Key> onSelectionListener = new InnerOnSelectionListener();
        fragment.getModesView().registerListener(onSelectionListener);

    }

    /*
     * **** INNER CLASS
     */
    private class InnerOnSelectionListener implements OnViewSelectionListener<Key> {

        @Override
        public void onViewElementSelected(Key element) {
            if (!isLessonRunning())
                return;

            if (expectedMode == element) {
                onSuccess();
            } else {
                onFailure();
            }
        }
    }

}
