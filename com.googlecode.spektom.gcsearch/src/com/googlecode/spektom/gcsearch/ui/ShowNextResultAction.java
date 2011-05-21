package com.googlecode.spektom.gcsearch.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.search.internal.ui.SearchPluginImages;

public class ShowNextResultAction extends Action {

	private GCSearchResultPage page;

	public ShowNextResultAction(GCSearchResultPage page) {
		super("Next Match");
		SearchPluginImages.setImageDescriptors(this, SearchPluginImages.T_LCL,
				SearchPluginImages.IMG_LCL_SEARCH_NEXT);
		setToolTipText("Show Next Match");
		this.page = page;
	}

	public void run() {
		page.gotoNextMatch();
	}
}