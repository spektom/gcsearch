package com.googlecode.spektom.gcsearch.core;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;

import com.google.gdata.client.codesearch.CodeSearchService;
import com.google.gdata.data.codesearch.CodeSearchFeed;
import com.google.gdata.util.ServiceException;
import com.googlecode.spektom.gcsearch.GCActivator;

public class GCSearchQuery implements ISearchQuery {

	private static final int MAX_RESULTS = 100;
	private GCQueryParams params;
	private GCSearchResult result;

	public GCSearchQuery(GCQueryParams params) {
		this.params = params;
		this.result = new GCSearchResult(this);
	}

	public IStatus run(IProgressMonitor monitor)
			throws OperationCanceledException {

		CodeSearchService gcService = new CodeSearchService("spektom-gcsearch");

		StringBuilder urlBuf = new StringBuilder(
				"https://www.google.com/codesearch/feeds/search?");
		urlBuf.append("q=").append(encode(params.getPattern()));
		urlBuf.append("&max-results=").append(MAX_RESULTS);

		// The rest of parameters are not supported :(
		// http://code.google.com/apis/codesearch/docs/2.0/reference.html#Elements
		if (params.getLicense() != License.ANY) {
			urlBuf.append("&license=").append(
					encode(params.getLicense().getCode()));
		}
		if (params.getLanguage() != Language.ANY) {
			urlBuf.append("&lang=").append(
					encode(params.getLanguage().getCode()));
		}
		if (params.getFile() != null) {
			urlBuf.append("&filename=").append(encode(params.getFile()));
		}
		if (params.getPackage() != null) {
			urlBuf.append("&package=").append(encode(params.getPackage()));
		}
		if (params.getClazz() != null) {
			urlBuf.append("&class=").append(encode(params.getClazz()));
		}
		if (params.getFunction() != null) {
			urlBuf.append("&function=").append(encode(params.getFunction()));
		}

		Throwable exception = null;
		try {
			URL feedUrl = new URL(urlBuf.toString());
			CodeSearchFeed resultFeed = gcService.getFeed(feedUrl,
					CodeSearchFeed.class);
			result.setMatches(resultFeed);

		} catch (MalformedURLException e) {
			// Must not happen
		} catch (IOException e) {
			exception = e;
		} catch (ServiceException e) {
			exception = e;
		}
		if (exception != null) {
			return new Status(IStatus.ERROR, GCActivator.PLUGIN_ID,
					"An error has occurred while searching for code", exception);
		}
		return monitor.isCanceled() ? Status.CANCEL_STATUS : Status.OK_STATUS;
	}

	public String getLabel() {
		return "Google Code Search";
	}

	public boolean canRerun() {
		return true;
	}

	public boolean canRunInBackground() {
		return true;
	}

	public ISearchResult getSearchResult() {
		return result;
	}

	public GCQueryParams getParams() {
		return params;
	}

	private static String encode(String str) {
		try {
			return URLEncoder.encode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return str;
			// must not happen
		}
	}
}
