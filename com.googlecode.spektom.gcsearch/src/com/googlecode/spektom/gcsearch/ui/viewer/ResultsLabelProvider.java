package com.googlecode.spektom.gcsearch.ui.viewer;

import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.search.internal.ui.SearchPluginImages;
import org.eclipse.swt.graphics.Image;

import com.google.gdata.data.codesearch.Match;
import com.googlecode.spektom.gcsearch.GCPluginImages;

public class ResultsLabelProvider extends LabelProvider implements
		IStyledLabelProvider {

	public Image getImage(Object element) {
		if (element instanceof PackageTreeElement) {
			return GCPluginImages.get(GCPluginImages.IMG_OBJ_PROJECT);
		}
		if (element instanceof FileTreeElement) {
			return GCPluginImages.getFileIcon(((FileTreeElement) element)
					.getExtension());
		}
		if (element instanceof Match) {
			return SearchPluginImages
					.get(SearchPluginImages.IMG_OBJ_TEXT_SEARCH_LINE);
		}
		return super.getImage(element);
	}

	public String getText(Object element) {
		if (element instanceof TreeElement) {
			return ((TreeElement) element).getName();
		}
		if (element instanceof Match) {
			Match match = (Match) element;
			return match.getLineText().getPlainText();
		}
		return super.getText(element);
	}

	public StyledString getStyledText(Object element) {
		if (!(element instanceof Match)) {
			return new StyledString(super.getText(element));
		}

		Match match = (Match) element;
		StyledString str = new StyledString(match.getLineText().getPlainText());
		str.append(" line:" + match.getLineNumber(),
				StyledString.QUALIFIER_STYLER);
		return str;
	}
}
