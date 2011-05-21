package com.googlecode.spektom.gcsearch.core;

import java.io.IOException;
import java.util.List;

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
		return entry.getMatches().size();
	}

	public GCMatch[] getMatches() {
		if (gcMatches == null) {
			List<Match> matches = entry.getMatches();
			gcMatches = new GCMatch[matches.size()];
			int i = 0;
			for (Match m : matches) {
				gcMatches[i++] = new GCMatch(this, m);
			}
		}
		return gcMatches;
	}

	/**
	 * Retrieves source of this file
	 * 
	 * @return file contents or <code>null</code> if it couldn't be retrieved
	 */
	public String getSource() {
		if (source == null) {
			String packageName = getPackage().getName();
			if (packageName.toLowerCase().startsWith("http://")) {
				String url;
				if (packageName.startsWith("http://hg.")) {
					// Mercurial
					url = packageName + "/raw-file/tip/" + getName();
				} else {
					url = packageName + "/" + getName();
				}
				try {
					source = HttpUtils.getString(url);
				} catch (IOException e) {
					// Couldn't retrieve
				}
			}
		}
		return source;
	}
}
