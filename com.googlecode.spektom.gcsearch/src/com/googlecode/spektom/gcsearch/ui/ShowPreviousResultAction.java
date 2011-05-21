package com.googlecode.spektom.gcsearch.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.search.internal.ui.SearchPluginImages;

public class ShowPreviousResultAction extends Action {

	private GCSearchResultPage page;

	public ShowPreviousResultAction(GCSearchResultPage page) {
		super("Previous Match");
		SearchPluginImages.setImageDescriptors(this, SearchPluginImages.T_LCL,
				SearchPluginImages.IMG_LCL_SEARCH_PREV);
		setToolTipText("Show Previous Match");
		this.page = page;
	}

	public void run() {
		page.gotoPreviousMatch();
	}
}