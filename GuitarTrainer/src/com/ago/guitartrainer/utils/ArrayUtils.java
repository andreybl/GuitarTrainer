package com.ago.guitartrainer.utils;

import java.util.Collection;

public class ArrayUtils {

    public static boolean inArray(Object o, Object... arr) {

        for (Object object : arr) {
            if (o == object)
                return true;
        }

        return false;
    }
//
//    public static <T> T[] toArray(Collection<T> coll) {
//        T[] arr = coll.toArray(new T[coll.size()]);
//        return arr;
//    }
}
