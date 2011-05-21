package com.googlecode.spektom.gcsearch.core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.ISearchResultListener;

import com.google.gdata.data.codesearch.CodeSearchEntry;
import com.google.gdata.data.codesearch.CodeSearchFeed;
import com.googlecode.spektom.gcsearch.GCPluginImages;

public class GCSearchResult implements ISearchResult, IGCMatchContainer {

	private GCSearchQuery query;
	private CodeSearchFeed feed;
	private GCPackage[] packages;

	public GCSearchResult(GCSearchQuery query) {
		this.query = query;
	}

	public void setMatches(CodeSearchFeed feed) {
		this.feed = feed;
	}

	public void addListener(ISearchResultListener l) {
	}

	public void removeListener(ISearchResultListener l) {
	}

	public String getLabel() {
		return new StringBuilder("'").append(query.getParams().getPattern())
				.append("' - ").append(getMatchCount()).append(" matches")
				.toString();
	}

	public String getTooltip() {
		return getLabel();
	}

	public ImageDescriptor getImageDescriptor() {
		return GCPluginImages.DESC_OBJ_CODE;
	}

	public GCSearchQuery getQuery() {
		return query;
	}

	public GCPackage[] getPackages() {
		if (feed == null) {
			return new GCPackage[0];
		}
		if (packages == null) {
			HashMap<String, List<CodeSearchEntry>> byPackage = new LinkedHashMap<String, List<CodeSearchEntry>>();
			for (CodeSearchEntry entry : feed.getEntries()) {
				String pkg = entry.getPackage().getName();
				List<CodeSearchEntry> entries = byPackage.get(pkg);
				if (entries == null) {
					entries = new LinkedList<CodeSearchEntry>();
					byPackage.put(pkg, entries);
				}
				entries.add(entry);
			}
			packages = new GCPackage[byPackage.size()];
			int i = 0;
			for (Entry<String, List<CodeSearchEntry>> e : byPackage.entrySet()) {
				List<CodeSearchEntry> entries = e.getValue();
				packages[i++] = new GCPackage(this, e.getKey(),
						entries.toArray(new CodeSearchEntry[entries.size()]));
			}
		}
		return packages;
	}

	public int getMatchCount() {
		int matchCount = 0;
		for (GCPackage p : getPackages()) {
			matchCount += p.getMatchCount();
		}
		return matchCount;
	}

	public GCMatch[] getMatches() {
		List<GCMatch> matches = new LinkedList<GCMatch>();
		for (GCPackage p : getPackages()) {
			matches.addAll(Arrays.asList(p.getMatches()));
		}
		return matches.toArray(new GCMatch[matches.size()]);
	}
}
