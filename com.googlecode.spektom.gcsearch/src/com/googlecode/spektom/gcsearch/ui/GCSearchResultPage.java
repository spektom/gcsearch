package com.googlecode.spektom.gcsearch.ui;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.search.ui.IQueryListener;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.ISearchResultPage;
import org.eclipse.search.ui.ISearchResultViewPart;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.part.Page;

import com.googlecode.spektom.gcsearch.core.GCSearchResult;
import com.googlecode.spektom.gcsearch.ui.viewer.InputTreeElement;
import com.googlecode.spektom.gcsearch.ui.viewer.ResultsViewer;

public class GCSearchResultPage extends Page implements ISearchResultPage {

	private String id;
	private Composite control;
	private GCSearchResult result;
	private ISearchResultViewPart view;
	private ResultsViewer viewer;

	public void createControl(Composite parent) {
		control = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		control.setLayout(layout);
		control.setLayoutData(new GridData(GridData.FILL_BOTH));

		viewer = new ResultsViewer(control);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));

		NewSearchUI.addQueryListener(new QueryListener());
	}

	public Control getControl() {
		return control;
	}

	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public Object getUIState() {
		return viewer.getSelection();
	}

	public void setInput(ISearchResult result, Object uiState) {
		this.result = (GCSearchResult) result;

		if (uiState instanceof ISelection) {
			viewer.setSelection((ISelection) uiState, true);
		}
	}

	public void setViewPart(ISearchResultViewPart part) {
		this.view = part;
	}

	public void restoreState(IMemento memento) {
	}

	public void saveState(IMemento memento) {
	}

	public void setID(String id) {
		this.id = id;
	}

	public String getID() {
		return id;
	}

	public String getLabel() {
		if (result == null) {
			return ""; //$NON-NLS-1$
		}
		return result.getLabel();
	}

	private class QueryListener implements IQueryListener {

		public void queryAdded(ISearchQuery query) {
		}

		public void queryRemoved(ISearchQuery query) {
		}

		public void queryStarting(ISearchQuery query) {
		}

		public void queryFinished(ISearchQuery query) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					if (result != null) {
						viewer.setInput(new InputTreeElement(result
								.getEntries()));
					}
					view.updateLabel();
				}
			});
		}
	}
}
