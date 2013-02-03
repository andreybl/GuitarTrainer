package com.ago.guitartrainer.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

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

    public static List<Position> intersect(Set<Position> submittedPositions, Collection<Position> positions) {
        List<Position> tmp = new ArrayList<Position>();
        tmp.addAll(submittedPositions);
        
        tmp.retainAll(positions);
        return tmp;
        
    }

    public static boolean isEqual(int[] arr1, int[] arr2) {
        if (arr1==null && arr2==null)
            return true;
        
        if (arr1.length != arr2.length)
            return false;
        
        for (int i=0;i<arr1.length;i++) {
            if (arr1[i] != arr2[i])
                return false;
        }
        
        return true;
    }
}
