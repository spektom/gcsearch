package com.googlecode.spektom.gcsearch;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

public class Preferences extends AbstractPreferenceInitializer {

	public static final String MAX_RESULTS = "maxResults"; //$NON-NLS-1$

	public void initializeDefaultPreferences() {
		IEclipsePreferences node = new DefaultScope()
				.getNode(GCActivator.PLUGIN_ID);
		node.putInt(MAX_RESULTS, 500);
	}
}
