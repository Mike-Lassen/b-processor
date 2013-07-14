package com.bprocessor.util;

public interface Filter<T> {
	public boolean evaluate(T object);
}
