package com.googlecode.spektom.gcsearch.core;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.googlecode.spektom.gcsearch.utils.HttpUtils;

/**
 * This class implements site-specific download logic through dirty if-else
 * statements.
 * 
 * @author Michael
 * 
 */
public class SourceDownloader {

	private static final Pattern SF_CVS = Pattern
			.compile("cvs :pserver:anonymous@(\\w+)\\.cvs\\.sourceforge.net:/cvsroot/\\w+ (.*)"); //$NON-NLS-1$

	private static final Pattern MOZILLA_CVS = Pattern
			.compile("cvs :pserver:guest@mozdev.org:/cvs (.*)"); //$NON-NLS-1$

	/**
	 * Rerieves file and returns its content.
	 * 
	 * @param file
	 *            File reference
	 * @return file content, or <code>null</code> in case it couldn't be
	 *         retrieved.
	 */
	public static String download(GCFile file) {

		String packageName = file.getPackage().getName();
		String fileName = file.getName();

		String url = null;
		Matcher m = null;

		if (packageName.endsWith(".gz") || packageName.endsWith(".bz2")
				|| packageName.endsWith(".tar") || packageName.endsWith(".zip")) {
			// Can't retrieve file placed inside of archive.

		} else if (packageName.startsWith("http://hg.")) {
			// Mercurial
			url = packageName + "/raw-file/tip/" + fileName;

		} else if (packageName.startsWith("git://github.com")) {
			packageName = packageName.replaceFirst("git://", "http://");
			// cut last '.git'
			packageName = packageName.substring(0, packageName.length() - 4);
			url = packageName + "/raw/master/" + fileName;

		} else if (packageName.startsWith("git://android.git.kernel.org")) {
			packageName = "http://android.git.kernel.org/?p="
					+ packageName.substring("git://android.git.kernel.org/"
							.length());
			url = packageName + ";a=blob_plain;f=" + fileName;

		} else if ((m = SF_CVS.matcher(packageName)) != null && m.matches()) {
			String subDir = m.group(2);
			if (subDir == ".") {
				subDir = "";
			}
			url = "http://" + m.group(1) + ".cvs.sourceforge.net/viewvc/"
					+ m.group(1) + "/" + subDir + "/" + fileName;

		} else if ((m = MOZILLA_CVS.matcher(packageName)) != null
				&& m.matches()) {
			String subDir = m.group(1);
			if (subDir == ".") {
				subDir = "";
			}
			url = "http://www.mozdev.org/source/browse/~checkout~/" + subDir
					+ "/" + fileName;

		} else {
			url = packageName + "/" + fileName;
		}

		if (url != null) {
			if (url.startsWith("http://") || url.startsWith("https://")) {
				try {
					return HttpUtils.getString(url);
				} catch (IOException e) {
					e.printStackTrace();
					// Couldn't retrieve
				}
			}
		}
		return null;
	}
}
