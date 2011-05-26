package com.googlecode.spektom.gcsearch.ui;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.search.internal.core.text.PatternConstructor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.googlecode.spektom.gcsearch.GCActivator;
import com.googlecode.spektom.gcsearch.core.SourceRetrievalRules.Rule;

public class EditRuleDialog extends StatusDialog {

	private Text packagePatternText;
	private Text targetUrlText;
	private Rule editRule;
	private Rule resultRule;

	private Listener modifyListener = new Listener() {
		public void handleEvent(Event event) {
			checkState();
		}
	};

	public EditRuleDialog(Shell parent, Rule input) {
		super(parent);
		this.editRule = input;
	}

	protected Control createDialogArea(Composite parent) {
		getShell().setText(editRule == null ? "Add New Rule" : "Edit Rule");

		Composite mainComposite = (Composite) super.createDialogArea(parent);
		((GridLayout) mainComposite.getLayout()).numColumns = 2;
		initializeDialogUnits(mainComposite);

		GridData fieldData = new GridData(GridData.FILL_HORIZONTAL);
		fieldData.widthHint = convertWidthInCharsToPixels(50);

		new Label(mainComposite, SWT.NONE).setText("Package Pattern:");
		packagePatternText = new Text(mainComposite, SWT.BORDER);
		packagePatternText.setLayoutData(fieldData);
		if (editRule != null) {
			packagePatternText.setText(editRule.getPackagePattern().pattern());
		}

		new Label(mainComposite, SWT.NONE).setText("Target URL:");
		targetUrlText = new Text(mainComposite, SWT.BORDER);
		targetUrlText.setLayoutData(fieldData);
		if (editRule != null) {
			targetUrlText.setText(editRule.getTargetUrl());
		}

		checkState();

		packagePatternText.addListener(SWT.Modify, modifyListener);
		targetUrlText.addListener(SWT.Modify, modifyListener);

		return mainComposite;
	}

	protected void setError(String message) {
		if (message == null) {
			updateStatus(Status.OK_STATUS);
		} else {
			updateStatus(new Status(IStatus.ERROR, GCActivator.PLUGIN_ID,
					message));
		}
	}

	protected void checkState() {
		String packagePattern = packagePatternText.getText().trim();
		if (packagePattern.length() == 0) {
			setError("Package pattern must not be empty!");
			return;
		}
		Pattern pattern;
		try {
			pattern = PatternConstructor.createPattern(packagePattern, false,
					true);
		} catch (PatternSyntaxException e) {
			String locMessage = e.getLocalizedMessage();
			int i = 0;
			while (i < locMessage.length()
					&& "\n\r".indexOf(locMessage.charAt(i)) == -1) { //$NON-NLS-1$
				i++;
			}
			setError(locMessage.substring(0, i));
			return;
		}

		resultRule = new Rule(pattern, targetUrlText.getText().trim());

		setError(null);
	}

	public Rule getResult() {
		return resultRule;
	}
}
