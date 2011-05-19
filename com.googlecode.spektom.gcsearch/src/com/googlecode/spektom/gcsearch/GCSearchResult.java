package com.googlecode.spektom.gcsearch;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.ISearchResultListener;

import com.google.gdata.data.codesearch.CodeSearchFeed;

public class GCSearchResult implements ISearchResult {

	private GCSearchQuery query;
	private CodeSearchFeed feed;

	public GCSearchResult(GCSearchQuery query, CodeSearchFeed feed) {
		this.query = query;
		this.feed = feed;
	}

	public void addListener(ISearchResultListener l) {
	}

	public void removeListener(ISearchResultListener l) {
	}

	public String getLabel() {
		return null;
	}

	public String getTooltip() {
		return null;
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	public ISearchQuery getQuery() {
		return query;
	}
}
