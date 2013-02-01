package com.ago.guitartrainer.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.ago.guitartrainer.notation.Position;

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

    public static boolean isEmpty(List<Position> positions) {
        if (positions==null)
            return true;
        
        if (positions.size()==0)
            return true;
        
        return false;
    }
    
    public static boolean isEmpty(Collection<Position> positions) {
        if (positions==null)
            return true;
        
        if (positions.size()==0)
            return true;
        
        return false;
    }

    /**
     * Test if the array are equal - e.g. contains exactly the same element. Order is not important. 
     * 
     * @param arr1
     * @param arr2
     * @return
     */
    public static boolean isEqual(List<Position> arr1, List<Position> arr2) {
        List<Position> interception = new ArrayList<Position>();

        interception.addAll(arr1);

        interception.retainAll(arr2);
        
        boolean isEqual = interception.size() == arr1.size();

        return isEqual;
    }
    
    public static boolean isEqual(Collection<Position> arr1, Collection<Position> arr2) {
        List<Position> interception = new ArrayList<Position>();

        interception.addAll(arr1);

        interception.retainAll(arr2);
        
        boolean isEqual = interception.size() == arr1.size();

        return isEqual;
    }
}
