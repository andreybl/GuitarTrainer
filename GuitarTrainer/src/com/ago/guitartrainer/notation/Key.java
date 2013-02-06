package com.ago.guitartrainer.notation;

import java.util.Hashtable;
import java.util.Map;

public enum Key {

    //@formatter:off
    C(0), Cdi(0.5), D(1), Ddi(1.5), 
    E(2), F(2.5), Fdi(3), G(3.5), 
    Gdi(4), A(4.5), Adi(5), B(5.5);
    //@formatter:on

    /**
     * The keys corresponds to the main degrees of the C-major scale: C, D, E etc. The main reason to exclude keys with
     * sharps/flats: the appropriate images are not currently not available in the {@link NotesView}. But on the other
     * side it could be enough just to no the position of the main keys.
     * */
    public static final Key[] mainKeys = new Key[] { Key.C, Key.D, Key.E, Key.F, Key.G, Key.A, Key.B };

    private double keyValue;

    private static Map<Double, Key> mapDoubleToKey = new Hashtable<Double, Key>();

    static {
        mapDoubleToKey.put(C.keyValue, C);
        mapDoubleToKey.put(Cdi.keyValue, Cdi);
        mapDoubleToKey.put(D.keyValue, D);
        mapDoubleToKey.put(Ddi.keyValue, Ddi);
        mapDoubleToKey.put(E.keyValue, E);
        mapDoubleToKey.put(F.keyValue, F);
        mapDoubleToKey.put(Fdi.keyValue, Fdi);
        mapDoubleToKey.put(G.keyValue, G);
        mapDoubleToKey.put(Gdi.keyValue, Gdi);
        mapDoubleToKey.put(A.keyValue, A);
        mapDoubleToKey.put(Adi.keyValue, Adi);
        mapDoubleToKey.put(B.keyValue, B);

    }

    Key(double keyValue) {
        this.keyValue = keyValue;
    }

    private static double MAX_KEY_VALUE = 5.5;

    /**
     * Returns the key for specific mode of the scale
     * 
     * @param scaleKey
     *            key of the scale
     * @param mode
     *            mode of the scale
     * @return key for the scale mode
     */
    public static Key modeByParentAndDegree(Key scaleKey, Degree mode) {
        /*
         * TODO: not clear why the impl work at all
         */

        double sum = scaleKey.keyValue() + mode.degreeValue();

        double modeValue;
        if (sum > MAX_KEY_VALUE && sum < 11) {
            modeValue = ((sum * 10) % (MAX_KEY_VALUE * 10)) / 10;
            modeValue -= 0.5;
        } else if (sum == 11) {
            modeValue = Adi.keyValue;
        } else {
            modeValue = sum;
        }

        return mapDoubleToKey.get(modeValue);

    }

    private double keyValue() {
        return keyValue;
    }

    public static Key parentByModeAndDegree(Key mode, Degree degree) {
        /*
         * TODO: not clear why the impl work at all
         */

        double diff = mode.keyValue() - degree.degreeValue();

        double parentValue;
        if (diff < 0) { // && sum < 11
            parentValue = ((diff * 10) % (MAX_KEY_VALUE * 10));
            if (parentValue != 0)
                parentValue += (MAX_KEY_VALUE * 10);
            parentValue /= 10;
            parentValue += 0.5;
            parentValue = Math.abs(parentValue);
        } else if (diff == 0) {
            parentValue = C.keyValue;
        } else {
            parentValue = diff;
        }

        return mapDoubleToKey.get(parentValue);

    }

}
