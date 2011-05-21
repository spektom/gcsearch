package com.googlecode.spektom.gcsearch.ui.viewer;

import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.search.internal.ui.SearchPluginImages;
import org.eclipse.swt.graphics.Image;

import com.googlecode.spektom.gcsearch.GCPluginImages;
import com.googlecode.spektom.gcsearch.core.GCFile;
import com.googlecode.spektom.gcsearch.core.GCMatch;
import com.googlecode.spektom.gcsearch.core.GCPackage;

public class ResultsLabelProvider extends LabelProvider implements
		IStyledLabelProvider {

	public Image getImage(Object element) {
		if (element instanceof GCPackage) {
			return GCPluginImages.get(GCPluginImages.IMG_OBJ_PROJECT);
		}
		if (element instanceof GCFile) {
			return GCPluginImages
					.getFileIcon(((GCFile) element).getExtension());
		}
		if (element instanceof GCMatch) {
			return SearchPluginImages
					.get(SearchPluginImages.IMG_OBJ_TEXT_SEARCH_LINE);
		}
		return super.getImage(element);
	}

	public String getText(Object element) {
		if (element instanceof GCPackage) {
			return ((GCPackage) element).getName();
		}
		if (element instanceof GCFile) {
			return ((GCFile) element).getName();
		}
		if (element instanceof GCMatch) {
			GCMatch match = (GCMatch) element;
			return match.getMatch().getLineText().getPlainText();
		}
		return super.getText(element);
	}

	public StyledString getStyledText(Object element) {
		if (!(element instanceof GCMatch)) {
			return new StyledString(super.getText(element));
		}

		GCMatch match = (GCMatch) element;
		StyledString str = new StyledString(match.getMatch().getLineText()
				.getPlainText());
		str.append(" line:" + match.getMatch().getLineNumber(),
				StyledString.QUALIFIER_STYLER);
		return str;
	}
}
