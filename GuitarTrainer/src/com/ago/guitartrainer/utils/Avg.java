package com.ago.guitartrainer.utils;

/**
 * A class to track the accumulated average
 * 
 * @author Andrej Golovko - jambit GmbH
 * 
 */
public class Avg {

    private int counter = 0;

    private double accumulatedAvg = 0;

    public double addValue(double val) {
        accumulatedAvg = (counter * accumulatedAvg + val) / ++counter;
        return accumulatedAvg;
    }

    public double getValue() {
        return accumulatedAvg;
    }

    public double getLaps() {
        return counter;
    }
}
