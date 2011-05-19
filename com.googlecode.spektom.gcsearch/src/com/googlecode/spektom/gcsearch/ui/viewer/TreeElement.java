package com.googlecode.spektom.gcsearch.ui.viewer;


public abstract class TreeElement {

	private String name;

	public TreeElement(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
