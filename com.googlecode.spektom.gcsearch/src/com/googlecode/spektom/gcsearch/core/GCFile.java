package com.googlecode.spektom.gcsearch.core;

import com.google.gdata.data.codesearch.CodeSearchEntry;
import com.google.gdata.data.codesearch.Match;

public class GCFile implements IGCMatchContainer {

	private GCPackage packege;
	private String fileName;
	private CodeSearchEntry entry;
	private String source;
	private GCMatch[] gcMatches;

	public GCFile(GCPackage packege, CodeSearchEntry entry) {
		this.packege = packege;
		this.fileName = entry.getFile().getName();
		this.entry = entry;
	}

	public CodeSearchEntry getEntry() {
		return entry;
	}

	public GCPackage getPackage() {
		return packege;
	}

	public String getName() {
		return fileName;
	}

	/**
	 * @return file extension
	 */
	public String getExtension() {
		int i = getName().lastIndexOf('.');
		return i == -1 ? "" : fileName.substring(i + 1); //$NON-NLS-1$
	}

	public int getMatchCount() {
		return getMatches().length;
	}

	public GCMatch[] getMatches() {
		if (gcMatches == null) {
			GCPostFilter postFilter = new GCPostFilter(getPackage().getResult()
					.getQuery().getParams());
			GCMatch[] matches = new GCMatch[entry.getMatches().size()];
			int i = 0;
			for (Match m : entry.getMatches()) {
				matches[i++] = new GCMatch(this, m);
			}
			gcMatches = postFilter.filter(matches);
		}
		return gcMatches;
	}

	/**
	 * Retrieves source of this file
	 * 
	 * @return file contents or <code>null</code> if it couldn't be retrieved
	 */
	public synchronized String getSource() {
		if (source == null) {
			source = SourceDownloader.download(this);
		}
		return source;
	}
}
