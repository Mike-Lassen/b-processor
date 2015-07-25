package com.bprocessor;

import java.util.List;

public abstract class Format {
	public Format() {
	}
	
	public abstract String format();
	public abstract void apply(String value);
	
	public abstract List<String> values();
}
