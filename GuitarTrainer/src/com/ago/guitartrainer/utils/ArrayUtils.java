package com.ago.guitartrainer.utils;

public class ArrayUtils {

    public static boolean inArray(Object o, Object... arr) {
        
        for (Object object : arr) {
            if (o == object)
                return true;
        }
        
        return false;
    }
}
