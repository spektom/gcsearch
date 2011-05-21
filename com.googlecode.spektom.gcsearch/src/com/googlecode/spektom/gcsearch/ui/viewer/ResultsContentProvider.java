package com.googlecode.spektom.gcsearch.ui.viewer;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.googlecode.spektom.gcsearch.core.GCFile;
import com.googlecode.spektom.gcsearch.core.GCMatch;
import com.googlecode.spektom.gcsearch.core.GCPackage;
import com.googlecode.spektom.gcsearch.core.GCSearchResult;

public class ResultsContentProvider implements ITreeContentProvider {

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof GCSearchResult) {
			return ((GCSearchResult) parentElement).getPackages();
		}
		if (parentElement instanceof GCPackage) {
			return ((GCPackage) parentElement).getFiles();
		}
		if (parentElement instanceof GCFile) {
			return ((GCFile) parentElement).getMatches();
		}
		return new Object[0];
	}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	public Object getParent(Object element) {
		if (element instanceof GCPackage) {
			return ((GCPackage) element).getResult();
		}
		if (element instanceof GCFile) {
			return ((GCFile) element).getPackage();
		}
		if (element instanceof GCMatch) {
			return ((GCMatch) element).getFile();
		}
		return null;
	}

	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}
}
