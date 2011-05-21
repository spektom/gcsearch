package com.googlecode.spektom.gcsearch;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class GCActivator extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "com.googlecode.spektom.gcsearch"; //$NON-NLS-1$
	private static GCActivator plugin;

	public GCActivator() {
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static GCActivator getDefault() {
		return plugin;
	}
}