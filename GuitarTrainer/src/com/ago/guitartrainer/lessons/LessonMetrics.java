package com.ago.guitartrainer.lessons;

import com.j256.ormlite.field.DatabaseField;

public class LessonMetrics {

    @DatabaseField(generatedId = true)
    private int id;

    /** simple class name of the lesson as returned with {@link Class#getSimpleName()} */
    @DatabaseField
    public String lessonClazz;

    /** total duration of the lesson as accumulated through all its loops */
    @DatabaseField
    private long totalLessonDuration;

    private long lastLessonDuration;

    /** number of time the lesson were run */
    private int loop = 0;

    /** timestamp for the start of the current loop */
    private long currentLoopStart;

    /** either timestamp of the end for current loop, or 0 */
    private long currentLoopEnd = 0;

    private long currentLoopDuration = 0;

    /** counter for questions asked in the current lesson loop */
    private int questionsCounter;

    private boolean isRunning = false;

    public int getId() {
        return id;
    }

    /**
     * 
     * @param lastLessonDuration
     *            duration of the last lesson in ms
     */
    public void submitLastLessonDuration(long lastLessonDuration) {
        this.lastLessonDuration = lastLessonDuration;
        totalLessonDuration += lastLessonDuration;
    }

    public long durationTotal() {
        return totalLessonDuration;
    }

    /**
     * Increase the loop of the lesson and return its new value.
     * 
     * Is usually done just before the first question inside of the lesson is asked to the user.
     * 
     * @return the loop count after its value were increased
     * */
    public int increaseLoop() {
        return loop++;
    }

    public int currentLoop() {
        return loop;
    }

    public void startTime() {
        isRunning = true;
        currentLoopStart = System.currentTimeMillis();
    }

    /**
     * 
     * 
     * @param duration
     *            of the current lesson
     * */
    public long stopTime() {
        isRunning = false;
        currentLoopEnd = System.currentTimeMillis();

        long duration = (currentLoopEnd - currentLoopStart);
        totalLessonDuration += duration;

        return duration;
    }

    /**
     * Depending on whether the lesson was finished, returns either how long the lesson continued till now or the
     * duration of the finished lesson.
     * 
     * */
    public long currentDuration() {
        if (currentLoopDuration != 0)
            return currentLoopDuration; // the question was accomplished already
        else if (currentLoopStart == 0)
            return 0; // the question was not started yet
        else
            return System.currentTimeMillis() - currentLoopStart; // the question is running

    }

    public boolean isFinished() {
        return !isRunning;
    }

    public int increaseQuestionsCounter() {
        questionsCounter++;
        return questionsCounter;
    }

}