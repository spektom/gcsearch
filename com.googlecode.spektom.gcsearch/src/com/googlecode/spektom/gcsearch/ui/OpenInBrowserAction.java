package com.googlecode.spektom.gcsearch.ui;

import java.net.URL;
import java.net.URLEncoder;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

import com.googlecode.spektom.gcsearch.GCActivator;
import com.googlecode.spektom.gcsearch.GCPluginImages;
import com.googlecode.spektom.gcsearch.core.GCQueryParams;
import com.googlecode.spektom.gcsearch.core.GCSearchQuery;
import com.googlecode.spektom.gcsearch.core.Language;
import com.googlecode.spektom.gcsearch.core.License;

public class OpenInBrowserAction extends Action {

	private GCSearchQuery query;

	public OpenInBrowserAction() {
		super("Open Results in Browser");
		GCPluginImages.setImageDescriptors(this, GCPluginImages.T_TOOL,
				GCPluginImages.IMG_TOOL_BROWSER);
		setToolTipText("Open Results in Browser");
	}

	public void setQuery(GCSearchQuery query) {
		this.query = query;
	}

	public void run() {
		try {
			StringBuilder urlBuf = new StringBuilder(
					"http://google.com/codesearch#search/&q=");
			GCQueryParams params = query.getParams();
			urlBuf.append(URLEncoder.encode(params.getPattern(), "UTF-8"));
			if (params.getPackage() != null) {
				urlBuf.append("%20package:").append(
						URLEncoder.encode(params.getPackage(), "UTF-8"));
			}
			if (params.getLanguage() != Language.ANY) {
				urlBuf.append("%20lang:").append(
						URLEncoder.encode(params.getLanguage().getCode(),
								"UTF-8"));
			}
			if (params.getLicense() != License.ANY) {
				urlBuf.append("%20license:").append(
						URLEncoder.encode(params.getLicense().getCode(),
								"UTF-8"));
			}
			if (params.isCaseSensitive()) {
				urlBuf.append("%20case:yes");
			}
			URL url = new URL(urlBuf.toString());

			IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench()
					.getBrowserSupport();
			try {
				IWebBrowser browser = browserSupport.createBrowser(
						IWorkbenchBrowserSupport.AS_EDITOR
								| IWorkbenchBrowserSupport.NAVIGATION_BAR
								| IWorkbenchBrowserSupport.STATUS,
						GCActivator.PLUGIN_ID, "Google Code Search",
						urlBuf.toString());
				browser.openURL(url);
			} catch (PartInitException e) {
				// Fall back to external browser:
				IWebBrowser browser = browserSupport.getExternalBrowser();
				browser.openURL(url);
			}
		} catch (Exception e) {
			GCActivator.log("Error opening query in browser", e);
		}
	}
}