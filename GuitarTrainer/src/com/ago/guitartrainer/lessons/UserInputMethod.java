package com.ago.guitartrainer.lessons;

public enum UserInputMethod {
    /**
     * 
     * For each user input methods a motion time (in milliseconds) is provided somewhere else.
     * 
     * The motion time is result of benchmarking the user input on the view, when no mental operation should be executed
     * by the user.
     * 
     * */
    UNDEFINED, FRETVIEWMANUAL, FRETVIEWSOUND, NOTEINPUT, BUTTON

}