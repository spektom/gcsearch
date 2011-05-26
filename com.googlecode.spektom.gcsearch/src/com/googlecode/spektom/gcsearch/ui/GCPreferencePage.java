package com.googlecode.spektom.gcsearch.ui;

import java.io.IOException;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osgi.service.prefs.BackingStoreException;

import com.googlecode.spektom.gcsearch.GCActivator;
import com.googlecode.spektom.gcsearch.GCPluginImages;
import com.googlecode.spektom.gcsearch.Preferences;
import com.googlecode.spektom.gcsearch.core.SourceRetrievalRules;
import com.googlecode.spektom.gcsearch.core.SourceRetrievalRules.Rule;

public class GCPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	public static final String ID = "com.googlecode.spektom.gcsearch.ui.GCPreferencePage"; //$NON-NLS-1$
	private Text maxResults;
	private TableViewer viewer;

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

		Composite viewerComposite = new Composite(composite, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 0;
		viewerComposite.setLayout(layout);
		layoutData = new GridData(GridData.FILL_BOTH);
		layoutData.horizontalSpan = 2;
		viewerComposite.setLayoutData(layoutData);

		label = new Label(viewerComposite, SWT.WRAP);
		label.setText("Source retrieval rules:");
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.horizontalSpan = 2;
		label.setLayoutData(layoutData);

		viewer = new TableViewer(viewerComposite, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		final Table table = viewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setContentProvider(new RulesContentProvider());
		viewer.setLabelProvider(new RulesLabelProvider());

		TableLayout tableLayout = new TableLayout();
		String columns[] = { "Package Pattern", "Target URL" };
		int weight[] = { 50, 50 };
		for (int i = 0; i < columns.length; ++i) {
			TableColumn column = new TableColumn(table, SWT.NULL);
			column.setText(columns[i]);
			tableLayout.addColumnData(new ColumnWeightData(weight[i]));
		}
		table.setLayout(tableLayout);

		ToolBar btnToolbar = new ToolBar(viewerComposite, SWT.VERTICAL
				| SWT.FLAT);
		btnToolbar.setLayoutData(new GridData(GridData.FILL_VERTICAL
				| GridData.VERTICAL_ALIGN_BEGINNING));

		ToolBarManager manager = new ToolBarManager(btnToolbar);

		final IAction addAction = new Action() {
			public void run() {
				EditRuleDialog dialog = new EditRuleDialog(getShell(), null);
				if (dialog.open() == Window.OK) {
					Rule[] rules = (Rule[]) viewer.getInput();
					Rule[] newRules = new Rule[rules.length + 1];
					System.arraycopy(rules, 0, newRules, 0, rules.length);
					newRules[rules.length] = dialog.getResult();

					viewer.getTable().setRedraw(false);
					viewer.setInput(newRules);
					viewer.getTable().setRedraw(true);
				}
			}
		};
		addAction.setToolTipText("Add new rule");
		GCPluginImages.setImageDescriptors(addAction, GCPluginImages.T_TOOL,
				GCPluginImages.IMG_TOOL_ADD);
		manager.add(addAction);

		final IAction editAction = new Action() {
			public void run() {
				Rule selected = (Rule) ((IStructuredSelection) viewer
						.getSelection()).getFirstElement();
				EditRuleDialog dialog = new EditRuleDialog(getShell(), selected);
				if (dialog.open() == Window.OK) {
					Rule[] rules = (Rule[]) viewer.getInput();
					for (int i = 0; i < rules.length; ++i) {
						if (rules[i] == selected) {
							rules[i] = dialog.getResult();
							break;
						}
					}
					viewer.getTable().setRedraw(false);
					viewer.refresh();
					viewer.getTable().setRedraw(true);
				}
			}
		};
		editAction.setToolTipText("Edit rule");
		GCPluginImages.setImageDescriptors(editAction, GCPluginImages.T_TOOL,
				GCPluginImages.IMG_TOOL_EDIT);
		manager.add(editAction);

		final IAction deleteAction = new Action() {
			public void run() {
				Object[] selected = ((IStructuredSelection) viewer
						.getSelection()).toArray();
				Rule[] rules = (Rule[]) viewer.getInput();
				Rule[] newRules = new Rule[rules.length - selected.length];
				int i = 0;
				for (Rule rule : rules) {
					boolean toRemove = false;
					for (Object s : selected) {
						if (s == rule) {
							toRemove = true;
							break;
						}
					}
					if (!toRemove) {
						newRules[i++] = rule;
					}
				}
				viewer.getTable().setRedraw(false);
				viewer.setInput(newRules);
				viewer.getTable().setRedraw(true);
			}
		};
		deleteAction.setToolTipText("Delete rule(s)");
		GCPluginImages.setImageDescriptors(deleteAction, GCPluginImages.T_TOOL,
				GCPluginImages.IMG_TOOL_DELETE);
		manager.add(deleteAction);

		final IAction moveUpAction = new Action() {
			public void run() {
				Rule selected = (Rule) ((IStructuredSelection) viewer
						.getSelection()).getFirstElement();
				Rule[] rules = (Rule[]) viewer.getInput();
				for (int i = 1; i < rules.length; ++i) {
					if (rules[i] == selected) {
						Rule tmp = rules[i - 1];
						rules[i - 1] = selected;
						rules[i] = tmp;
						break;
					}
				}
				viewer.getTable().setRedraw(false);
				viewer.refresh();
				viewer.getTable().setRedraw(true);
			}
		};
		moveUpAction.setToolTipText("Move up");
		GCPluginImages.setImageDescriptors(moveUpAction, GCPluginImages.T_TOOL,
				GCPluginImages.IMG_TOOL_UP);
		manager.add(moveUpAction);

		final IAction moveDownAction = new Action() {
			public void run() {
				Rule selected = (Rule) ((IStructuredSelection) viewer
						.getSelection()).getFirstElement();
				Rule[] rules = (Rule[]) viewer.getInput();
				for (int i = 0; i < rules.length - 1; ++i) {
					if (rules[i] == selected) {
						Rule tmp = rules[i + 1];
						rules[i + 1] = selected;
						rules[i] = tmp;
						break;
					}
				}
				viewer.getTable().setRedraw(false);
				viewer.refresh();
				viewer.getTable().setRedraw(true);
			}
		};
		moveDownAction.setToolTipText("Move down");
		GCPluginImages.setImageDescriptors(moveDownAction,
				GCPluginImages.T_TOOL, GCPluginImages.IMG_TOOL_DOWN);
		manager.add(moveDownAction);

		manager.update(true);
		btnToolbar.pack();

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				Object[] selection = ((IStructuredSelection) event
						.getSelection()).toArray();
				Rule[] rules = (Rule[]) viewer.getInput();
				editAction.setEnabled(selection.length == 1);
				deleteAction.setEnabled(selection.length > 0);
				moveUpAction.setEnabled(selection.length == 1
						&& selection[0] != rules[0]);
				moveDownAction.setEnabled(selection.length == 1
						&& selection[0] != rules[rules.length - 1]);
			}
		});

		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				editAction.run();
			}
		});

		viewer.setSelection(new StructuredSelection());

		// Initialize values:
		IPreferencesService preferencesService = Platform
				.getPreferencesService();
		maxResults.setText(Integer.toString(preferencesService.getInt(
				GCActivator.PLUGIN_ID, Preferences.MAX_RESULTS, 0, null)));

		viewer.getTable().setRedraw(false);
		viewer.setInput(SourceRetrievalRules.loadRules());
		viewer.getTable().setRedraw(true);

		return parent;
	}

	protected void performDefaults() {
		IEclipsePreferences node = new DefaultScope()
				.getNode(GCActivator.PLUGIN_ID);
		maxResults.setText(Integer.toString(node.getInt(
				Preferences.MAX_RESULTS, 0)));

		try {
			viewer.getTable().setRedraw(false);
			viewer.setInput(SourceRetrievalRules.loadDefaultRules());
			viewer.getTable().setRedraw(true);
		} catch (IOException e) {
			GCActivator.log("Error loading default rules", e);
		}

		super.performDefaults();
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

		SourceRetrievalRules.updateRules((Rule[]) viewer.getInput());

		return super.performOk();
	}

	private static class RulesContentProvider implements
			IStructuredContentProvider {

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof Rule[]) {
				return (Rule[]) inputElement;
			}
			return new Object[0];
		}
	}

	private static class RulesLabelProvider extends LabelProvider implements
			ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			switch (columnIndex) {
			case 0:
				return ((Rule) element).getPackagePattern().pattern();
			case 1:
				return ((Rule) element).getTargetUrl();
			}
			return ""; //$NON-NLS-1$
		}
	}
}
