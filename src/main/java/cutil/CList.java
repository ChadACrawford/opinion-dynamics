package cutil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Chad on 2/14/15.
 */
public class CList {
	public static <E> E choose(Random rand, List<E> list) {
		int r = rand.nextInt(list.size());
		return list.get(r);
	}

	public static <E> List<E> sample(Random rand, List<E> list, int S) {
		List<E> pool = new ArrayList<E>(list);
		List<E> ret = new ArrayList<E>();
		for (int i = 0; i < S; i++) {
			E item = choose(rand, pool);
			pool.remove(item);
			ret.add(item);
		}
		return ret;
	}
}
