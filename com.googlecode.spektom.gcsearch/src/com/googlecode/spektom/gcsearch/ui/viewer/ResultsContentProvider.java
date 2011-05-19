package com.googlecode.spektom.gcsearch.ui.viewer;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ResultsContentProvider implements ITreeContentProvider {

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof InputTreeElement) {
			return ((InputTreeElement) parentElement).getPackages();
		}
		if (parentElement instanceof PackageTreeElement) {
			return ((PackageTreeElement) parentElement).getFiles();
		}
		if (parentElement instanceof FileTreeElement) {
			return ((FileTreeElement) parentElement).getMatches();
		}
		return new Object[0];
	}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	public Object getParent(Object element) {
		return null;
	}

	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}
}
