package com.googlecode.spektom.gcsearch.core;

import com.google.gdata.data.codesearch.Match;

public class GCMatch implements IGCMatchContainer {

	private GCFile file;
	private Match match;

	public GCMatch(GCFile file, Match match) {
		this.file = file;
		this.match = match;
	}

	public GCFile getFile() {
		return file;
	}

	public Match getMatch() {
		return match;
	}

	public int getMatchCount() {
		return 0;
	}

	public GCMatch[] getMatches() {
		return new GCMatch[0];
	}
}
