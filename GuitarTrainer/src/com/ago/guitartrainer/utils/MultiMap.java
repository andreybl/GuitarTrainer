package com.ago.guitartrainer.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * A fast hack for multikeyp map, because I did not find an viable solution. Considered:
 * <ul>
 * <li> Guava: users reports performance problems with lib; also its size is about 1.5Mb
 * <li> Java Commons: not Android port found 
 * </ul>
 * 
 * @author Andrej Golovko - jambit GmbH
 *
 * @param <A>
 * @param <B>
 * @param <C>
 */
public class MultiMap<A, B, C> {

    private Map<A, Map<B, C>> map = new HashMap<A, Map<B, C>>();

    public void put(A key1, B key2, C value) {
        if (!map.containsKey(key1)) {
            Map<B, C> subMap = new HashMap<B, C>();
            subMap.put(key2, value);
            map.put(key1, subMap);
        } else {
            Map<B, C> subMap = map.get(key1);
            subMap.put(key2, value);
        }
    }

    public C get(A key1, B key2) {
        C result = null;
        
        if (map.containsKey(key1)) {
            Map<B, C> subMap = map.get(key1);
            if (subMap.containsKey(key2)) {
                result = subMap.get(key2);
            }
        }
        
        return result;
    }
}
