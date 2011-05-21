package com.googlecode.spektom.gcsearch.core;

import java.io.IOException;

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
	public String getSource() {
		if (source == null) {
			String packageName = getPackage().getName();
			String url;
			if (packageName.startsWith("http://hg.")) {
				// Mercurial
				url = packageName + "/raw-file/tip/" + getName();

			} else if (packageName.startsWith("git://github.com")) {
				packageName = packageName.replaceFirst("git://", "http://");
				// cut last '.git'
				packageName = packageName
						.substring(0, packageName.length() - 4);
				url = packageName + "/raw/master/" + getName();

			} else if (packageName.startsWith("git://android.git.kernel.org")) {
				packageName = "http://android.git.kernel.org/?p="
						+ packageName.substring("git://android.git.kernel.org/"
								.length());
				url = packageName + ";a=blob_plain;f=" + getName();

			} else {
				url = packageName + "/" + getName();
			}
			if (url.startsWith("http://")) {
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
