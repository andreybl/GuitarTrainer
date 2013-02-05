package com.ago.guitartrainer.notation;


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

    public static final Degree[][] CHORDS = new Degree[][]{major, minor, dim, aug, major7thChord, minor7thChord, dominantSeptChord, dim7thChord};
    

    public static int NOTPLAYED = -1;

    public Chord() {
        super();
        /*-
         * The fret offsets are specified as integers, whereas we start from the 
         * first string. In total, each array of offset has 6 elements (according 
         * to string number). 
         * Example:  
         *      new Integer[] { 0, 1, 4, 2, 3, -1 }
         * This is the major7thChord in Alpha scale grid. The "-1" means - the 
         * string is not played.
         *      
         * 
         */
        {

        }

    }
}
