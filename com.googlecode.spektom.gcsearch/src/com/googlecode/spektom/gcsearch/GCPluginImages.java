package com.googlecode.spektom.gcsearch;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.WorkbenchImages;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.osgi.framework.Bundle;

/**
 * Bundle of all images used by the GC plugin.
 */
public class GCPluginImages {

	// The plugin registry
	private final static ImageRegistry PLUGIN_REGISTRY = GCActivator
			.getDefault().getImageRegistry();

	private static final IPath ICONS_PATH = new Path("$nl$/icons/full"); //$NON-NLS-1$

	public static final String T_OBJ = "obj16/"; //$NON-NLS-1$
	public static final String T_WIZBAN = "wizban/"; //$NON-NLS-1$
	public static final String T_LCL = "lcl16/"; //$NON-NLS-1$
	public static final String T_TOOL = "tool16/"; //$NON-NLS-1$
	public static final String T_EVIEW = "eview16/"; //$NON-NLS-1$

	private static final String NAME_PREFIX = GCActivator.PLUGIN_ID + "."; //$NON-NLS-1$
	private static final int NAME_PREFIX_LENGTH = NAME_PREFIX.length();

	// Define image names
	public static final String IMG_OBJ_CODE = NAME_PREFIX + "code.png"; //$NON-NLS-1$
	public static final String IMG_OBJ_PROJECT = NAME_PREFIX + "project.png"; //$NON-NLS-1$

	// Define images
	public static final ImageDescriptor DESC_OBJ_CODE = createManaged(T_OBJ,
			IMG_OBJ_CODE);
	public static final ImageDescriptor DESC_OBJ_PROJECT = createManaged(T_OBJ,
			IMG_OBJ_PROJECT);

	public static Image get(String key) {
		return PLUGIN_REGISTRY.get(key);
	}

	private static ImageDescriptor createManaged(String prefix, String name) {
		ImageDescriptor result = create(prefix,
				name.substring(NAME_PREFIX_LENGTH), true);
		PLUGIN_REGISTRY.put(name, result);
		return result;
	}

	/**
	 * Creates an image descriptor for the given prefix and name in the plugin
	 * bundle. The path can contain variables like $NL$. If no image could be
	 * found, <code>useMissingImageDescriptor</code> decides if either the
	 * 'missing image descriptor' is returned or <code>null</code>. or
	 * <code>null</code>.
	 */
	private static ImageDescriptor create(String prefix, String name,
			boolean useMissingImageDescriptor) {
		IPath path = ICONS_PATH.append(prefix).append(name);
		return createImageDescriptor(GCActivator.getDefault().getBundle(),
				path, useMissingImageDescriptor);
	}

	/**
	 * Sets all available image descriptors for the given action.
	 */
	public static void setImageDescriptors(IAction action, String type,
			String relPath) {
		relPath = relPath.substring(NAME_PREFIX_LENGTH);

		action.setDisabledImageDescriptor(create("d" + type, relPath, false)); //$NON-NLS-1$

		ImageDescriptor desc = create("e" + type, relPath, true); //$NON-NLS-1$
		action.setHoverImageDescriptor(desc);
		action.setImageDescriptor(desc);
	}

	/**
	 * Creates an image descriptor for the given path in a bundle. The path can
	 * contain variables like $NL$. If no image could be found,
	 * <code>useMissingImageDescriptor</code> decides if either the 'missing
	 * image descriptor' is returned or <code>null</code>. Added for 3.1.1.
	 */
	public static ImageDescriptor createImageDescriptor(Bundle bundle,
			IPath path, boolean useMissingImageDescriptor) {
		URL url = FileLocator.find(bundle, path, null);
		if (url != null) {
			return ImageDescriptor.createFromURL(url);
		}
		if (useMissingImageDescriptor) {
			return ImageDescriptor.getMissingImageDescriptor();
		}
		return null;
	}

	public static Image getFileIcon(String fileExt) {
		String key = "FILE_" + fileExt;
		Image image = PLUGIN_REGISTRY.get(key);
		if (image == null) {
			try {
				IEditorDescriptor editorDescriptor = IDE
						.getEditorDescriptor("test." + fileExt);
				if (editorDescriptor != null
						&& editorDescriptor.isInternal()
						&& !IDEWorkbenchPlugin.DEFAULT_TEXT_EDITOR_ID
								.equals(editorDescriptor.getId())) {
					image = editorDescriptor.getImageDescriptor().createImage();
				}
			} catch (PartInitException e) {
			}
			if (image == null) {
				Program program = Program.findProgram(fileExt);
				ImageData imageData = (program == null ? null : program
						.getImageData());
				if (imageData != null) {
					image = new Image(Display.getCurrent(), imageData);
				}
			}
			if (image == null) {
				image = WorkbenchImages.getImage(ISharedImages.IMG_OBJ_FILE);
			}
			PLUGIN_REGISTRY.put(key, image);
		}
		return image;
	}
}
