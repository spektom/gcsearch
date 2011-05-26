package com.googlecode.spektom.gcsearch.core;

import java.io.IOException;

import com.googlecode.spektom.gcsearch.utils.HttpUtils;

/**
 * This class implements site-specific download logic through dirty if-else
 * statements.
 * 
 * @author Michael
 * 
 */
public class SourceDownloader {

	/**
	 * Rerieves file and returns its content.
	 * 
	 * @param file
	 *            File reference
	 * @return file content, or <code>null</code> in case it couldn't be
	 *         retrieved.
	 */
	public static String download(GCFile file) {

		String url = SourceRetrievalRules.apply(file.getPackage().getName(),
				file.getName());

		if (url.startsWith("http://") || url.startsWith("https://")) {
			try {
				String source = HttpUtils.getString(url);

				// Patch for some git:// Web viewers, that use JavaScript:
				if (file.getPackage().getName().startsWith("git://")
						&& source.contains("Generating....</body>")) {
					source = HttpUtils.getString(url);
				}
				return source;
			} catch (IOException e) {
				e.printStackTrace();
				// Couldn't retrieve
			}
		}
		return null;
	}
}
