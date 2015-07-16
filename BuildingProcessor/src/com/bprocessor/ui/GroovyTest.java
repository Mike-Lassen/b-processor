package com.bprocessor.ui;

import java.io.File;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyShell;

public class GroovyTest {
	/**
	 * @param args
	 */
	@SuppressWarnings("rawtypes")
	public static void main(String[] args) {
		Binding binding = new Binding();
		GroovyShell shell = new GroovyShell(binding);
		shell.evaluate("println 'Hello, World'");
		try {
			Object result = shell.evaluate(new File("script.txt"));
			if (result != null) {
				if (result instanceof Plugin) {
					Plugin plugin = (Plugin) result;
					plugin.prepare();
				}
				if (result instanceof Closure) {
					((Closure) result).call();
				}
				System.out.println(result.getClass().getName() + ": " + result.toString());
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
