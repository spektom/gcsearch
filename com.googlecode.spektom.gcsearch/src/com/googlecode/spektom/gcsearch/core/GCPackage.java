package com.googlecode.spektom.gcsearch.core;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.google.gdata.data.codesearch.CodeSearchEntry;

public class GCPackage implements IGCMatchContainer {

	private String name;
	private CodeSearchEntry[] entries;
	private GCSearchResult result;
	private GCFile[] files;

	public GCPackage(GCSearchResult result, String name,
			CodeSearchEntry[] entries) {
		this.result = result;
		this.name = name;
		this.entries = entries;
	}

	public GCSearchResult getResult() {
		return result;
	}

	public String getName() {
		return name;
	}

	public GCFile[] getFiles() {
		if (files == null) {
			files = new GCFile[entries.length];
			for (int i = 0; i < entries.length; ++i) {
				files[i] = new GCFile(this, entries[i]);
			}
		}
		return files;
	}

	public int getMatchCount() {
		int matchCount = 0;
		for (GCFile f : getFiles()) {
			matchCount += f.getMatchCount();
		}
		return matchCount;
	}

	public GCMatch[] getMatches() {
		List<GCMatch> matches = new LinkedList<GCMatch>();
		for (GCFile f : getFiles()) {
			matches.addAll(Arrays.asList(f.getMatches()));
		}
		return matches.toArray(new GCMatch[matches.size()]);
	}
}
