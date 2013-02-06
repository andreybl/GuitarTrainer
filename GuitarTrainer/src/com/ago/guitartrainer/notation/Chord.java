package com.ago.guitartrainer.notation;

import java.util.HashMap;

import com.ago.guitartrainer.utils.MultiMap;

public class Chord {

    /** Example: C */
    public static final Degree[] major = new Degree[] { Degree.ONE, Degree.THREE, Degree.FIVE };

    /** Example: Cm */
    public static final Degree[] minor = new Degree[] { Degree.ONE, Degree.TWO_HALF, Degree.FIVE };

    /** Example: Cdim */
    public static final Degree[] dim = new Degree[] { Degree.ONE, Degree.TWO_HALF, Degree.FOUR_HALF };

    /** Example: Caug, C+ */
    public static final Degree[] aug = new Degree[] { Degree.ONE, Degree.THREE, Degree.FIVE_HALF };

    /** Example: C&#916;7 */
    public static final Degree[] major7thChord = new Degree[] { Degree.ONE, Degree.THREE, Degree.FIVE, Degree.SEVEN };

    /** Example: Cm7 */
    public static final Degree[] minor7thChord = new Degree[] { Degree.ONE, Degree.TWO_HALF, Degree.FIVE,
            Degree.SIX_HALF };

    /** Example: C7 */
    public static final Degree[] dominantSeptChord = new Degree[] { Degree.ONE, Degree.THREE, Degree.FIVE,
            Degree.SIX_HALF };

    /** Example: Cdim7, Co, Co7, C7dim */
    public static final Degree[] dim7thChord = new Degree[] { Degree.ONE, Degree.TWO_HALF, Degree.FOUR_HALF, Degree.SIX };

    /** Example: C7b5, half-diminished */
    public static final Degree[] minor7b5Chord = new Degree[] { Degree.ONE, Degree.TWO_HALF, Degree.FOUR_HALF,
            Degree.SIX };

    public static final Degree[][] CHORDS = new Degree[][] { major, minor, dim, aug, major7thChord, minor7thChord,
            dominantSeptChord, dim7thChord, minor7b5Chord };

    public static int NOTPLAYED = -1;

    // private static Chord INSTANCE;
    //
    // private static MultiMap<Key, Degree, Key> keyMode2Parent = new MultiMap<Key, Degree, Key>();
    // private static MultiMap<Key, Degree, Key> parentMode2Key = new MultiMap<Key, Degree, Key>();
    //
    // private Chord() {
    // registerMode(Key.C, Degree.ONE, Key.C);
    // registerMode(Key.C, Degree.TWO, Key.D);
    // registerMode(Key.C, Degree.THREE, Key.E);
    // registerMode(Key.C, Degree.FOUR, Key.F);
    // registerMode(Key.C, Degree.FIVE, Key.G);
    // registerMode(Key.C, Degree.SIX, Key.A);
    // registerMode(Key.C, Degree.SEVEN, Key.B);
    //
    // }

    //
    // private void registerMode(Key scale, Degree modeDegree, Key modeKey) {
    // parentMode2Key.put(scale, modeDegree, modeKey);
    // keyMode2Parent.put(modeKey, modeDegree, scale);
    //
    // }
    //
    // public static Chord getInstance() {
    // if (INSTANCE == null)
    // INSTANCE = new Chord();
    // return INSTANCE;
    // }
    //
    // public Key getParentScale(Key originalKey, Degree mode) {
    // return null;
    //
    // }
}
