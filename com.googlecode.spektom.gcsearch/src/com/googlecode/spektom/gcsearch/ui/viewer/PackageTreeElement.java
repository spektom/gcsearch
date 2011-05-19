package com.googlecode.spektom.gcsearch.ui.viewer;

import com.google.gdata.data.codesearch.CodeSearchEntry;

public class PackageTreeElement extends TreeElement {

	private FileTreeElement[] files;

	public PackageTreeElement(String name, CodeSearchEntry[] entries) {
		super(name);
		this.files = new FileTreeElement[entries.length];
		for (int i = 0; i < entries.length; ++i) {
			this.files[i] = new FileTreeElement(entries[i]);
		}
	}

	public FileTreeElement[] getFiles() {
		return files;
	}
}
