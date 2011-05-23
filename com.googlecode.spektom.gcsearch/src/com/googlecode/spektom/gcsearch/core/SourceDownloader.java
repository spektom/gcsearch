package com.googlecode.spektom.gcsearch.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;

import com.googlecode.spektom.gcsearch.GCActivator;
import com.googlecode.spektom.gcsearch.utils.HttpUtils;

/**
 * This class implements site-specific download logic through dirty if-else
 * statements.
 * 
 * @author Michael
 * 
 */
public class SourceDownloader {

	private static Map<Pattern, String> patterns;
	static {
		loadPatterns();
	}

	private static void loadPatterns() {
		patterns = new LinkedHashMap<Pattern, String>();
		try {
			URL url = FileLocator.find(GCActivator.getDefault().getBundle(),
					new Path("resources/rules.txt"), null);
			if (url != null) {
				url = FileLocator.resolve(url);

				BufferedReader r = new BufferedReader(new InputStreamReader(
						url.openStream()));
				try {
					String line;
					while ((line = r.readLine()) != null) {
						if (line.startsWith("#")) {
							continue;
						}
						line = line.trim();
						if (line.length() == 0) {
							continue;
						}

						String regex = line;
						String replacement = "";
						int i = line.indexOf("==");
						if (i != -1) {
							regex = line.substring(0, i);
							replacement = line.substring(i + 2);
						}
						try {
							patterns.put(Pattern.compile(regex.trim()),
									replacement.trim());
						} catch (PatternSyntaxException e) {
							GCActivator
									.getDefault()
									.getLog()
									.log(new Status(IStatus.WARNING,
											GCActivator.PLUGIN_ID,
											"Rule expression can't be compiled: "
													+ regex));
						}
					}
				} finally {
					r.close();
				}
			}
		} catch (Exception e) {
			GCActivator
					.getDefault()
					.getLog()
					.log(new Status(IStatus.ERROR, GCActivator.PLUGIN_ID,
							"Can't load patterns", e));
		}
	}

	/**
	 * Rerieves file and returns its content.
	 * 
	 * @param file
	 *            File reference
	 * @return file content, or <code>null</code> in case it couldn't be
	 *         retrieved.
	 */
	public static String download(GCFile file) {

		String url = file.getPackage().getName();

		for (Entry<Pattern, String> e : patterns.entrySet()) {
			Matcher m = e.getKey().matcher(url);
			if (m.matches()) {
				url = m.replaceFirst(e.getValue());
				break;
			}
		}

		url = url.replace("%FILE%", file.getName());
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
