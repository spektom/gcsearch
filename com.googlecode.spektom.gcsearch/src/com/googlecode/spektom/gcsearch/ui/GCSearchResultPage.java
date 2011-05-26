package com.googlecode.spektom.gcsearch.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.IFileSystem;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.DefaultLineTracker;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.search.internal.ui.CopyToClipboardAction;
import org.eclipse.search.ui.IContextMenuConstants;
import org.eclipse.search.ui.IQueryListener;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.ISearchResultPage;
import org.eclipse.search.ui.ISearchResultViewPart;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.search2.internal.ui.SearchView;
import org.eclipse.search2.internal.ui.basic.views.CollapseAllAction;
import org.eclipse.search2.internal.ui.basic.views.ExpandAllAction;
import org.eclipse.search2.internal.ui.basic.views.INavigate;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.texteditor.ITextEditor;

import com.googlecode.spektom.gcsearch.GCPluginImages;
import com.googlecode.spektom.gcsearch.core.GCFile;
import com.googlecode.spektom.gcsearch.core.GCMatch;
import com.googlecode.spektom.gcsearch.core.GCPackage;
import com.googlecode.spektom.gcsearch.core.GCSearchResult;
import com.googlecode.spektom.gcsearch.core.IGCMatchContainer;
import com.googlecode.spektom.gcsearch.ui.viewer.ResultsViewer;
import com.googlecode.spektom.gcsearch.ui.viewer.TreeViewerNavigator;

public class GCSearchResultPage extends Page implements ISearchResultPage {

	protected static final GCMatch[] EMPTY_MATCH_ARRAY = new GCMatch[0];
	private String id;
	private Composite control;
	private GCSearchResult result;
	private ISearchResultViewPart view;
	private ResultsViewer viewer;
	private int currentMatchIndex;
	// Actions:
	private CopyToClipboardAction copyToClipboardAction;
	private Action showNextAction;
	private Action showPreviousAction;
	private ExpandAllAction expandAllAction;
	private CollapseAllAction collapseAllAction;
	private Action configureAction;
	private QueryListener queryListener;
	private Map<GCFile, File> openedFiles;
	private Map<GCFile, Integer> openedFilesRefCnt;
	private boolean implicitSelection;

	public GCSearchResultPage() {
		openedFiles = new HashMap<GCFile, File>();
		openedFilesRefCnt = new HashMap<GCFile, Integer>();

		showNextAction = new ShowNextResultAction(this);
		showPreviousAction = new ShowPreviousResultAction(this);
		copyToClipboardAction = new CopyToClipboardAction();
		expandAllAction = new ExpandAllAction();
		collapseAllAction = new CollapseAllAction();
		configureAction = new ConfigureSearchAction();
	}

	public void createControl(Composite parent) {
		control = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		control.setLayout(layout);
		control.setLayoutData(new GridData(GridData.FILL_BOTH));

		MenuManager menuManager = new MenuManager("#PopUp"); //$NON-NLS-1$
		menuManager.setRemoveAllWhenShown(true);
		menuManager.setParent(getSite().getActionBars().getMenuManager());
		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager mgr) {
				SearchView.createContextMenuGroups(mgr);
				fillContextMenu(mgr);
				view.fillContextMenu(mgr);
			}
		});

		SelectionProviderAdapter viewerAdapter = new SelectionProviderAdapter();
		getSite().setSelectionProvider(viewerAdapter);
		getSite().registerContextMenu(view.getViewSite().getId(), menuManager,
				viewerAdapter);

		viewer = new ResultsViewer(control);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				if (!implicitSelection) {
					currentMatchIndex = -1;
				}
			}
		});
		viewer.addSelectionChangedListener(viewerAdapter);
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event
						.getSelection();
				Object obj = selection.getFirstElement();
				if (obj instanceof GCPackage) {
					viewer.setExpandedState(obj, !viewer.getExpandedState(obj));
				} else if (obj instanceof GCFile) {
					GCMatch[] matches = ((GCFile) obj).getMatches();
					if (matches.length > 0) {
						showMatch(matches[0], true);
					}
				} else if (obj instanceof GCMatch) {
					showMatch((GCMatch) obj, true);
				}
			}
		});

		Menu menu = menuManager.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);

		IToolBarManager toolbarManager = getSite().getActionBars()
				.getToolBarManager();
		toolbarManager.removeAll();
		SearchView.createToolBarGroups(toolbarManager);
		fillToolbar(toolbarManager);
		toolbarManager.update(false);

		copyToClipboardAction.setViewer(viewer);
		collapseAllAction.setViewer(viewer);
		expandAllAction.setViewer(viewer);

		NewSearchUI.addQueryListener(queryListener = new QueryListener());
	}

	public void dispose() {
		super.dispose();
		NewSearchUI.removeQueryListener(queryListener);
	}

	public Control getControl() {
		return control;
	}

	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public void init(IPageSite pageSite) {
		super.init(pageSite);
		IMenuManager menuManager = pageSite.getActionBars().getMenuManager();
		initActionDefinitionIDs();
		menuManager.updateAll(true);
		pageSite.getActionBars().updateActionBars();
	}

	private void initActionDefinitionIDs() {
		copyToClipboardAction
				.setActionDefinitionId(IWorkbenchCommandConstants.EDIT_COPY);
		showNextAction
				.setActionDefinitionId(IWorkbenchCommandConstants.NAVIGATE_NEXT);
		showPreviousAction
				.setActionDefinitionId(IWorkbenchCommandConstants.NAVIGATE_PREVIOUS);
	}

	protected void fillContextMenu(IMenuManager mgr) {
		mgr.appendToGroup(IContextMenuConstants.GROUP_SHOW, showNextAction);
		mgr.appendToGroup(IContextMenuConstants.GROUP_SHOW, showPreviousAction);
		mgr.appendToGroup(IContextMenuConstants.GROUP_SHOW,
				copyToClipboardAction);
		mgr.appendToGroup(IContextMenuConstants.GROUP_SHOW, expandAllAction);
	}

	protected void fillToolbar(IToolBarManager tbm) {
		tbm.appendToGroup(IContextMenuConstants.GROUP_SHOW, showNextAction);
		tbm.appendToGroup(IContextMenuConstants.GROUP_SHOW, showPreviousAction);
		IActionBars actionBars = getSite().getActionBars();
		if (actionBars != null) {
			actionBars.setGlobalActionHandler(ActionFactory.NEXT.getId(),
					showNextAction);
			actionBars.setGlobalActionHandler(ActionFactory.PREVIOUS.getId(),
					showPreviousAction);
			actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(),
					copyToClipboardAction);
		}
		tbm.appendToGroup(IContextMenuConstants.GROUP_VIEWER_SETUP,
				expandAllAction);
		tbm.appendToGroup(IContextMenuConstants.GROUP_VIEWER_SETUP,
				collapseAllAction);
		tbm.appendToGroup(IContextMenuConstants.GROUP_PROPERTIES,
				configureAction);
	}

	public Object getUIState() {
		return viewer.getSelection();
	}

	public void setInput(ISearchResult result, Object uiState) {
		GCSearchResult oldInput = this.result;
		if (oldInput != null) {
			viewer.setInput(null);
		}

		this.result = (GCSearchResult) result;
		viewer.setInput(this.result);

		if (uiState instanceof ISelection) {
			implicitSelection = true;
			try {
				viewer.setSelection((ISelection) uiState, true);
			} finally {
				implicitSelection = false;
			}
		} else {
			navigateNext(true);
		}
	}

	public GCSearchResult getInput() {
		return result;
	}

	public void setViewPart(ISearchResultViewPart part) {
		this.view = part;
	}

	public void restoreState(IMemento memento) {
	}

	public void saveState(IMemento memento) {
	}

	public void setID(String id) {
		this.id = id;
	}

	public String getID() {
		return id;
	}

	public String getLabel() {
		GCSearchResult result = getInput();
		if (result == null) {
			return ""; //$NON-NLS-1$
		}
		return result.getLabel();
	}

	/**
	 * Selects the element corresponding to the next match and shows the match
	 * in an editor. Note that this will cycle back to the first match after the
	 * last match.
	 */
	public void gotoNextMatch() {
		gotoNextMatch(false);
	}

	private void gotoNextMatch(boolean activateEditor) {
		currentMatchIndex++;
		GCMatch nextMatch = getCurrentMatch();
		if (nextMatch == null) {
			navigateNext(true);
			currentMatchIndex = 0;
		}
		showCurrentMatch(activateEditor);
	}

	/**
	 * Selects the element corresponding to the previous match and shows the
	 * match in an editor. Note that this will cycle back to the last match
	 * after the first match.
	 */
	public void gotoPreviousMatch() {
		gotoPreviousMatch(false);
	}

	private void gotoPreviousMatch(boolean activateEditor) {
		currentMatchIndex--;
		GCMatch nextMatch = getCurrentMatch();
		if (nextMatch == null) {
			navigateNext(false);
			currentMatchIndex = getDisplayedMatchCount(getFirstSelectedElement()) - 1;
		}
		showCurrentMatch(activateEditor);
	}

	private void navigateNext(boolean forward) {
		INavigate navigator = new TreeViewerNavigator(this, viewer);
		navigator.navigateNext(forward);
	}

	/**
	 * Returns the currently selected match.
	 * 
	 * @return the selected match or <code>null</code> if none are selected
	 */
	public GCMatch getCurrentMatch() {
		Object element = getFirstSelectedElement();
		if (element != null) {
			GCMatch[] matches = getDisplayedMatches(element);
			if (currentMatchIndex >= 0 && currentMatchIndex < matches.length) {
				return matches[currentMatchIndex];
			}
		}
		return null;
	}

	protected void showMatch(final GCMatch currentMatch, int currentOffset,
			int currentLength, boolean activate) throws PartInitException,
			IOException {

		String source = currentMatch.getFile().getSource();
		if (source != null) {

			// Find relevant editor:
			String editorId = IDEWorkbenchPlugin.DEFAULT_TEXT_EDITOR_ID;
			String fileExtension = currentMatch.getFile().getExtension();
			IEditorDescriptor editorDescriptor = IDE
					.getEditorDescriptor("test." + fileExtension);
			if (editorDescriptor != null && editorDescriptor.isInternal()) {
				editorId = editorDescriptor.getId();
			}

			File tempFile = openedFiles.get(currentMatch.getFile());
			if (tempFile == null) {
				// Create a dummy file store:
				tempFile = File.createTempFile("gcsearch", "." + fileExtension); //$NON-NLS-1$ //$NON-NLS-2$
				OutputStreamWriter writer = new OutputStreamWriter(
						new FileOutputStream(tempFile), "UTF-8");
				try {
					writer.write(source);
				} finally {
					writer.close();
				}
				openedFiles.put(currentMatch.getFile(), tempFile);
				openedFilesRefCnt.put(currentMatch.getFile(), 1);
			} else {
				openedFilesRefCnt.put(currentMatch.getFile(),
						openedFilesRefCnt.get(currentMatch.getFile()) + 1);
			}

			IFileSystem fileSystem = EFS.getLocalFileSystem();
			IFileStore fileStore = fileSystem.fromLocalFile(tempFile);
			FileStoreEditorInput input = new FileStoreEditorInput(fileStore) {
				public ImageDescriptor getImageDescriptor() {
					return new ImageDataImageDescriptor(
							GCPluginImages.getFileIcon(currentMatch.getFile()
									.getExtension()));
				}

				public String getName() {
					String fileName = currentMatch.getFile().getName();
					int i = fileName.lastIndexOf('/');
					if (i != -1) {
						fileName = fileName.substring(i + 1);
					}
					return fileName;
				}

				public String getToolTipText() {
					return currentMatch.getFile().getName();
				}

				public IPersistableElement getPersistable() {
					return null;
				}
			};
			final IEditorPart editor = IDE.openEditor(getSite().getPage(),
					input, editorId);
			if (editor instanceof ITextEditor) {
				((ITextEditor) editor).selectAndReveal(currentOffset,
						currentLength);
			}

			getSite().getPage().addPartListener(new IPartListener() {

				public void partOpened(IWorkbenchPart part) {
				}

				public void partDeactivated(IWorkbenchPart part) {
				}

				public void partClosed(IWorkbenchPart part) {
					if (part == editor) {
						getSite().getPage().removePartListener(this);

						Integer refs = openedFilesRefCnt.get(currentMatch
								.getFile());
						if (refs == 1) {
							openedFilesRefCnt.remove(currentMatch.getFile());
							File tempFile = openedFiles.remove(currentMatch
									.getFile());
							tempFile.delete();
						} else {
							openedFilesRefCnt.put(currentMatch.getFile(),
									refs - 1);
						}
					}
				}

				public void partBroughtToTop(IWorkbenchPart part) {
				}

				public void partActivated(IWorkbenchPart part) {
				}
			});
		}
	}

	public void internalSelect(Object data) {
		implicitSelection = true;
		try {
			viewer.setSelection(new StructuredSelection(data), true);
		} finally {
			implicitSelection = false;
		}
	}

	private void showMatch(final GCMatch currentMatch,
			final boolean activateEditor) {

		internalSelect(currentMatch);

		final ISafeRunnable runnable = new ISafeRunnable() {
			public void handleException(Throwable exception) {
				if (exception instanceof PartInitException) {
					PartInitException e = (PartInitException) exception;
					ErrorDialog.openError(getSite().getShell(), "Show Match",
							"Could not find an editor for the current match",
							e.getStatus());
				}
			}

			public void run() throws Exception {
				IRegion location = getCurrentMatchLocation(currentMatch);
				showMatch(currentMatch, location.getOffset(),
						location.getLength(), activateEditor);
			}
		};

		Job job = new Job("Retrieving resource...") {
			protected IStatus run(IProgressMonitor monitor) {
				currentMatch.getFile().getSource();
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						SafeRunner.run(runnable);
					}
				});
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

	private boolean showCurrentMatch(boolean activateEditor) {
		GCMatch currentMatch = getCurrentMatch();
		if (currentMatch != null) {
			showMatch(currentMatch, activateEditor);
			return true;
		}
		return false;
	}

	public IRegion getCurrentMatchLocation(GCMatch currentMatch) {
		String source = currentMatch.getFile().getSource();
		if (source != null) {
			DefaultLineTracker lineTracker = new DefaultLineTracker();
			lineTracker.set(source);
			try {
				return lineTracker.getLineInformation(Integer
						.parseInt(currentMatch.getMatch().getLineNumber()) - 1);
			} catch (Exception e) {
			}
		}
		return new Region(0, 0);
	}

	public GCMatch[] getDisplayedMatches(Object element) {
		if (element instanceof GCFile) {
			return ((IGCMatchContainer) element).getMatches();
		}
		return EMPTY_MATCH_ARRAY;
	}

	public int getDisplayedMatchCount(Object element) {
		if (element instanceof GCFile) {
			return ((GCFile) element).getMatchCount();
		}
		return 0;
	}

	private Object getFirstSelectedElement() {
		IStructuredSelection selection = (IStructuredSelection) viewer
				.getSelection();
		if (selection.size() > 0) {
			Object selected = selection.getFirstElement();
			if (selected instanceof GCMatch) {
				return ((GCMatch) selected).getFile();
			}
			return selected;
		}
		return null;
	}

	private class QueryListener implements IQueryListener {

		public void queryAdded(ISearchQuery query) {
		}

		public void queryRemoved(ISearchQuery query) {
		}

		public void queryStarting(ISearchQuery query) {
		}

		public void queryFinished(ISearchQuery query) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					viewer.refresh();

					if (viewer.getSelection().isEmpty()) {
						navigateNext(true);
					}
					view.updateLabel();
				}
			});
		}
	}

	private class SelectionProviderAdapter implements ISelectionProvider,
			ISelectionChangedListener {
		private List<ISelectionChangedListener> listeners = new ArrayList<ISelectionChangedListener>(
				5);

		public void addSelectionChangedListener(
				ISelectionChangedListener listener) {
			listeners.add(listener);
		}

		public ISelection getSelection() {
			return viewer.getSelection();
		}

		public void removeSelectionChangedListener(
				ISelectionChangedListener listener) {
			listeners.remove(listener);
		}

		public void setSelection(ISelection selection) {
			viewer.setSelection(selection);
		}

		public void selectionChanged(SelectionChangedEvent event) {
			// forward to my listeners
			SelectionChangedEvent wrappedEvent = new SelectionChangedEvent(
					this, event.getSelection());
			for (Object element : listeners) {
				ISelectionChangedListener listener = (ISelectionChangedListener) element;
				listener.selectionChanged(wrappedEvent);
			}
		}

	}
}
