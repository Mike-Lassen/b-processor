package com.bprocessor.util;

public interface Function<R, V> {
	R apply(V value);
}
