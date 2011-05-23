package com.googlecode.spektom.gcsearch.ui;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osgi.service.prefs.BackingStoreException;

import com.googlecode.spektom.gcsearch.GCActivator;
import com.googlecode.spektom.gcsearch.Preferences;

public class GCPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	private Text maxResults;

	public GCPreferencePage() {
	}

	public GCPreferencePage(String title) {
		super(title);
	}

	public GCPreferencePage(String title, ImageDescriptor image) {
		super(title, image);
	}

	public void init(IWorkbench workbench) {
	}

	protected Control createContents(Composite parent) {
		initializeDialogUnits(parent);

		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		parent.setLayout(layout);

		Composite composite = new Composite(parent, SWT.NONE);
		layout = new GridLayout();
		layout.verticalSpacing = convertWidthInCharsToPixels(1);
		layout.numColumns = 2;
		composite.setLayout(layout);
		GridData layoutData = new GridData(GridData.FILL_BOTH);
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		composite.setLayoutData(layoutData);

		GridData fieldData = new GridData();
		fieldData.widthHint = convertWidthInCharsToPixels(5);
		fieldData.horizontalIndent = convertWidthInCharsToPixels(1);

		Label label = new Label(composite, SWT.WRAP);
		label.setText("Maximum number of results:");
		maxResults = new Text(composite, SWT.BORDER);
		maxResults.setLayoutData(fieldData);

		// Initialize values:
		IPreferencesService preferencesService = Platform
				.getPreferencesService();
		maxResults.setText(Integer.toString(preferencesService.getInt(
				GCActivator.PLUGIN_ID, Preferences.MAX_RESULTS, 0, null)));

		return parent;
	}

	public boolean performOk() {
		setErrorMessage(null);

		int maxResults = 0;
		try {
			maxResults = Integer.parseInt(this.maxResults.getText().trim());
		} catch (NumberFormatException e) {
		}
		if (maxResults <= 0) {
			setErrorMessage("Number of results must be positive!");
			return false;
		}

		IEclipsePreferences node = new InstanceScope()
				.getNode(GCActivator.PLUGIN_ID);
		node.putInt(Preferences.MAX_RESULTS, maxResults);
		try {
			node.flush();
		} catch (BackingStoreException e) {
		}

		return super.performOk();
	}
}
