package com.bprocessor.util;

public interface Filter<T> {
	boolean evaluate(T object);
}
