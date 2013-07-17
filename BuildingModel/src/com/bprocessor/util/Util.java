package com.bprocessor.util;

import java.util.Collection;

public class Util {
	public static <R, V> void map(Collection<R> destination, Collection<V> source, Function<R, V> function) {
		for (V current : source) {
			destination.add(function.apply(current));
		}
	}
}
