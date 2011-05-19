package com.googlecode.spektom.gcsearch.ui.viewer;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class ResultsViewer extends TreeViewer {

	public ResultsViewer(Composite parent) {
		super(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		setContentProvider(new ResultsContentProvider());
		setLabelProvider(new ResultsLabelProvider());
		setUseHashlookup(true);
	}
}
