package edu.utulsa.masters.cutil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Helper methods for choosing or sampling from a list.
 */
public class CList {
    public static<E> E choose(Random rand, List<E> list) {
        int r = rand.nextInt(list.size());
        return list.get(r);
    }

    /**
     * Randomly sample S items from list. "Faster" and non-destructive alternative to the standard shuffle/select first
     * S items method.
     * @param rand The random object.
     * @param list The list.
     * @param S Random sample.
     * @param <E> List type.
     * @return A list of items
     */
    public static<E> List<E> sample(Random rand, List<E> list, int S) {
        List<E> pool = new ArrayList<E>(list);
        List<E> ret = new ArrayList<E>();
        for(int i = 0; i < S; i++) {
            E item = choose(rand, pool);
            pool.remove(item);
            ret.add(item);
        }
        return ret;
    }
}
