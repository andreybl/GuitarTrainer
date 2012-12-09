package com.ago.guitartrainer;

public enum FingerboardEvents {
    /**
     * specific fret/string position is pressed. It does not means yet, the note is played (sounds). There are several
     * positions could be pressed at the same time. There is always 6 positions pressed on the guitar: the fret 0 is
     * considered to be "pressed", even if not user action is involved.
     * 
     * Note that there is no state like "position unpressed", because there is always exactly 6 positions pressed. And
     * each unpress event for one position is actually associated strongly with press event for some other position.
     */
    POSITION_PRESSED,

    /**
     * The note is considered to be played, when the sound can be detected. Or if the string is vibrating. The note
     * sounding has start, and has an end. The note is sounding after some dB treshold is reached.
     */
    POSITION_PLAYING_ON,

    /**
     * The note just finished to sound.
     */
    POSITION_PLAYING_OFF
}
