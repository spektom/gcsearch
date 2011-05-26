package com.googlecode.spektom.gcsearch.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;

import com.googlecode.spektom.gcsearch.GCPluginImages;

public class ConfigureSearchAction extends Action {

	public ConfigureSearchAction() {
		super("Configure");
		GCPluginImages.setImageDescriptors(this, GCPluginImages.T_TOOL,
				GCPluginImages.IMG_TOOL_PREFS);
		setToolTipText("Configure search settings");
	}

	public void run() {
		PreferenceDialog dialog = PreferencesUtil
				.createPreferenceDialogOn(PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getShell(),
						GCPreferencePage.ID, new String[0], null);
		dialog.open();
	}
}