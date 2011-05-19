package com.googlecode.spektom.gcsearch.ui.viewer;

import java.util.List;

import com.google.gdata.data.codesearch.CodeSearchEntry;
import com.google.gdata.data.codesearch.Match;

public class FileTreeElement extends TreeElement {

	private CodeSearchEntry entry;
	private String extension;

	public FileTreeElement(CodeSearchEntry entry) {
		super(entry.getFile().getName());

		String fileName = getName();
		this.entry = entry;
		int i = getName().lastIndexOf('.');
		extension = i == -1 ? "" : fileName.substring(i + 1); //$NON-NLS-1$
	}

	/**
	 * @return file extension
	 */
	public String getExtension() {
		return extension;
	}

	public Match[] getMatches() {
		List<Match> matches = entry.getMatches();
		return matches.toArray(new Match[matches.size()]);
	}
}
