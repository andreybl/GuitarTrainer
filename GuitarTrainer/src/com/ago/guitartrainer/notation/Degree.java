package com.ago.guitartrainer.notation;

import java.util.Hashtable;
import java.util.Map;

public enum Degree {
    //@formatter:off
    ONE(0), ONE_HALF(0.5), TWO(1), TWO_HALF(1.5), 
    THREE(2), FOUR(2.5), FOUR_HALF(3), FIVE(3.5), 
    FIVE_HALF(4), SIX(4.5), SIX_HALF(5), SEVEN(5.5);
    //@formatter:on

    private double degreeValue;

    public static final Degree[] STRONG_DEGREES = new Degree[] { ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN };

    private static Map<Double, Degree> mapDoubleToDegree = new Hashtable<Double, Degree>();
    static {
        mapDoubleToDegree.put(ONE.degreeValue, ONE);
        mapDoubleToDegree.put(ONE_HALF.degreeValue, ONE_HALF);
        mapDoubleToDegree.put(TWO.degreeValue, TWO);
        mapDoubleToDegree.put(TWO_HALF.degreeValue, TWO_HALF);
        mapDoubleToDegree.put(THREE.degreeValue, THREE);
        mapDoubleToDegree.put(FOUR.degreeValue, FOUR);
        mapDoubleToDegree.put(FOUR_HALF.degreeValue, FOUR_HALF);
        mapDoubleToDegree.put(FIVE.degreeValue, FIVE);
        mapDoubleToDegree.put(FIVE_HALF.degreeValue, FIVE_HALF);
        mapDoubleToDegree.put(SIX.degreeValue, SIX);
        mapDoubleToDegree.put(SIX_HALF.degreeValue, SIX_HALF);
        mapDoubleToDegree.put(SEVEN.degreeValue, SEVEN);
    }

    Degree(double degreeValue) {
        this.degreeValue = degreeValue;
    }

    public boolean isRoot() {
        if (this == ONE)
            return true;
        else
            return false;
    }

    public double degreeValue(){
        return degreeValue;
    }
    
    /**
     * The current degree instance is unmodified
     * 
     * @param frets
     * @return
     */
    public Degree addFrets(int frets) {

        int f = frets % 12;

        double newDegreeValue = this.degreeValue + ((double) f / 2);

        double f2 = newDegreeValue % 6;

        return mapDoubleToDegree.get(f2);
    }

}