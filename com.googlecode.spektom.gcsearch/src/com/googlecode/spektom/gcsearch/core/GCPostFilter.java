package com.googlecode.spektom.gcsearch.core;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Google search code API is limited (no filtering params), therefore this class
 * is needed for filtering results.
 * 
 * @author Michael
 * 
 */
public class GCPostFilter {

	private GCQueryParams params;

	public GCPostFilter(GCQueryParams params) {
		this.params = params;
	}

	public GCMatch[] filter(GCMatch[] matches) {

		Pattern p = params.isCaseSensitive() ? Pattern.compile(params
				.getPattern()) : Pattern.compile(params.getPattern(),
				Pattern.CASE_INSENSITIVE);

		List<GCMatch> filtered = new LinkedList<GCMatch>();
		for (GCMatch m : matches) {

			if (!p.matcher(m.getMatch().getLineText().getPlainText()).find()) {
				continue;
			}
			if (params.getFile() != null
					&& !m.getFile().getName().contains(params.getFile())) {
				continue;
			}
			if (params.getPackage() != null
					&& !m.getFile().getPackage().getName()
							.contains(params.getPackage())) {
				continue;
			}
			if (params.getLanguage() != Language.ANY) {
				Language fileLanguage = Language.forExtension(m.getFile()
						.getExtension());
				if (fileLanguage != params.getLanguage()) {
					continue;
				}
			}

			filtered.add(m);
		}
		return filtered.toArray(new GCMatch[filtered.size()]);
	}
}
