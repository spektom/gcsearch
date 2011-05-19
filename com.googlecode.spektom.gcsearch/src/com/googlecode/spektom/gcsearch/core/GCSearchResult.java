package com.googlecode.spektom.gcsearch.core;

import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.ISearchResultListener;

import com.google.gdata.data.codesearch.CodeSearchEntry;
import com.google.gdata.data.codesearch.CodeSearchFeed;
import com.googlecode.spektom.gcsearch.GCPluginImages;

public class GCSearchResult implements ISearchResult {

	private GCSearchQuery query;
	private CodeSearchFeed feed;

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
				.append("' - ").append(getTotalResults()).append(" matches")
				.toString();
	}

	public String getTooltip() {
		return getLabel();
	}

	public ImageDescriptor getImageDescriptor() {
		return GCPluginImages.DESC_OBJ_CODE;
	}

	public ISearchQuery getQuery() {
		return query;
	}

	public int getTotalResults() {
		return feed == null ? 0 : feed.getTotalResults();
	}

	public List<CodeSearchEntry> getEntries() {
		return feed == null ? null : feed.getEntries();
	}
}
