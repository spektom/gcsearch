package com.googlecode.spektom.gcsearch.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceColors;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.search.internal.core.text.PatternConstructor;
import org.eclipse.search.internal.ui.SearchPlugin;
import org.eclipse.search.ui.ISearchPage;
import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.googlecode.spektom.gcsearch.core.GCQueryParams;
import com.googlecode.spektom.gcsearch.core.GCSearchQuery;
import com.googlecode.spektom.gcsearch.core.Language;
import com.googlecode.spektom.gcsearch.core.License;

public class GCSearchPage extends DialogPage implements ISearchPage {

	private static final int HISTORY_SIZE = 12;
	private ISearchPageContainer container;
	private boolean firstTime = true;
	private Combo patternCombo;
	private CLabel statusLabel;
	private Button caseSensitiveCheckbox;
	private Combo languageCombo;
	private Combo licenseCombo;
	private Text packageText;
	private Text fileText;
	private Text classText;
	private Text functionText;
	private List<GCQueryParams> previousParams;

	public GCSearchPage() {
	}

	public GCSearchPage(String title) {
		super(title);
	}

	public GCSearchPage(String title, ImageDescriptor image) {
		super(title, image);
	}

	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		readConfiguration();

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		GridData compositeData = new GridData(GridData.FILL_BOTH);
		compositeData.grabExcessHorizontalSpace = true;
		compositeData.grabExcessVerticalSpace = true;
		composite.setLayoutData(compositeData);

		new Label(composite, SWT.NONE).setText("Search public source code:");

		Composite patternComposite = new Composite(composite, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		patternComposite.setLayout(layout);
		patternComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		patternCombo = new Combo(patternComposite, SWT.SINGLE | SWT.BORDER);
		GridData patternComboData = new GridData(GridData.FILL_HORIZONTAL);
		patternComboData.widthHint = convertWidthInCharsToPixels(50);
		patternCombo.setLayoutData(patternComboData);
		patternCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateOKStatus();
			}
		});
		patternCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateOKStatus();
			}
		});

		caseSensitiveCheckbox = new Button(patternComposite, SWT.CHECK);
		caseSensitiveCheckbox.setText("&Case sensitive");

		statusLabel = new CLabel(patternComposite, SWT.LEAD);
		statusLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Group searchOptionsGroup = new Group(composite, SWT.NONE);
		searchOptionsGroup.setText("Search &options:");
		searchOptionsGroup.setLayout(new GridLayout(2, false));
		GridData searchOptionsData = new GridData(GridData.FILL_BOTH);
		searchOptionsData.verticalIndent = convertHeightInCharsToPixels(1);
		searchOptionsGroup.setLayoutData(searchOptionsData);

		new Label(searchOptionsGroup, SWT.NONE).setText("&Package:");
		packageText = new Text(searchOptionsGroup, SWT.BORDER);
		packageText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		new Label(searchOptionsGroup, SWT.NONE).setText("&Language:");
		languageCombo = new Combo(searchOptionsGroup, SWT.BORDER
				| SWT.READ_ONLY | SWT.SINGLE);
		languageCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		new Label(searchOptionsGroup, SWT.NONE).setText("&File:");
		fileText = new Text(searchOptionsGroup, SWT.BORDER);
		fileText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		new Label(searchOptionsGroup, SWT.NONE).setText("Cl&ass:");
		classText = new Text(searchOptionsGroup, SWT.BORDER);
		classText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		new Label(searchOptionsGroup, SWT.NONE).setText("&Function:");
		functionText = new Text(searchOptionsGroup, SWT.BORDER);
		functionText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		new Label(searchOptionsGroup, SWT.NONE).setText("Lice&nse:");
		licenseCombo = new Combo(searchOptionsGroup, SWT.BORDER | SWT.READ_ONLY
				| SWT.SINGLE);
		licenseCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		setControl(composite);
	}

	public void dispose() {
		writeConfiguration();
		super.dispose();
	}

	public boolean performAction() {
		NewSearchUI.runQueryInBackground(new GCSearchQuery(
				getSearchParameters()));
		return true;
	}

	public void setContainer(ISearchPageContainer container) {
		this.container = container;
	}

	private ISearchPageContainer getContainer() {
		return this.container;
	}

	private boolean validateRegex() {
		try {
			PatternConstructor.createPattern(patternCombo.getText(),
					caseSensitiveCheckbox.getSelection(), true);
		} catch (PatternSyntaxException e) {
			String locMessage = e.getLocalizedMessage();
			int i = 0;
			while (i < locMessage.length()
					&& "\n\r".indexOf(locMessage.charAt(i)) == -1) { //$NON-NLS-1$
				i++;
			}
			setError(locMessage.substring(0, i));
			return false;
		}
		setError(null);
		return true;
	}

	private void setError(String message) {
		if (message != null) {
			statusLabel.setText(message);
			statusLabel.setForeground(JFaceColors.getErrorText(statusLabel
					.getDisplay()));
		} else {
			statusLabel
					.setText(" (Search via regular expression, e.g. ^java/.*\\.java$)");
			statusLabel.setForeground(null);
		}
	}

	public void setVisible(boolean visible) {
		if (visible) {
			if (firstTime) {
				firstTime = false;

				// Set items here to prevent page from resizing:
				Language activeLanguage = getActiveEditorLanguage();
				int selectionIdx = 0;
				int idx = 0;
				for (Language lang : Language.values()) {
					languageCombo.add(lang.getName(), idx);
					if (lang == activeLanguage) {
						selectionIdx = idx;
					}
					idx++;
				}
				languageCombo.select(selectionIdx);

				for (License lic : License.values()) {
					licenseCombo.add(lic.getName());
				}
				licenseCombo.select(0);

				patternCombo.setItems(getPreviousPatterns());
				String selectedText = getCurrentSelection();
				if (selectedText != null) {
					patternCombo.setText(FindReplaceDocumentAdapter
							.escapeForRegExPattern(selectedText));
				} else {
					patternCombo.select(0);
				}
			}
			patternCombo.setFocus();
		}
		updateOKStatus();
		super.setVisible(visible);
	}

	private String getCurrentSelection() {
		ISelection selection = getContainer().getSelection();
		if (selection instanceof ITextSelection && !selection.isEmpty()) {
			return ((ITextSelection) selection).getText();
		}
		return null;
	}

	private String[] getPreviousPatterns() {
		int size = previousParams.size();
		String[] patterns = new String[size];
		for (int i = 0; i < size; i++) {
			patterns[i] = previousParams.get(i).getPattern();
		}
		return patterns;
	}

	private void updateOKStatus() {
		boolean regexStatus = validateRegex();
		getContainer().setPerformActionEnabled(regexStatus);
	}

	private IEditorPart getActiveEditor() {
		IWorkbenchPage activePage = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		if (activePage != null) {
			IEditorPart activeEditor = activePage.getActiveEditor();
			if (activeEditor == activePage.getActivePart()) {
				return activeEditor;
			}
		}
		return null;
	}

	private String getActiveEditorExtension() {
		IEditorPart activeEditor = getActiveEditor();
		if (activeEditor != null) {
			IEditorInput input = activeEditor.getEditorInput();
			if (input instanceof IPathEditorInput) {
				return ((IPathEditorInput) input).getPath().getFileExtension();
			}
			if (input instanceof IFileEditorInput) {
				return ((IFileEditorInput) input).getFile().getFileExtension();
			}
			if (input instanceof IURIEditorInput) {
				String path = ((IURIEditorInput) input).getURI().getPath();
				int i = path.lastIndexOf('/');
				path = path.substring(i + 1);
				i = path.lastIndexOf('.');
				if (i != -1) {
					return path.substring(i + 1);
				}
			}
		}
		return null;
	}

	private Language getActiveEditorLanguage() {
		return Language.forExtension(getActiveEditorExtension());
	}

	/**
	 * Initializes itself from the stored page settings.
	 */
	private void readConfiguration() {
		previousParams = new ArrayList<GCQueryParams>(20);

		IDialogSettings settings = getDialogSettings();
		try {
			int historySize = settings.getInt("historySize");
			for (int i = 0; i < historySize; ++i) {
				IDialogSettings s = settings.getSection("history" + i);
				if (s != null) {
					previousParams.add(new GCQueryParams(s));
				}
			}
		} catch (NumberFormatException e) {
			// ignore
		}
	}

	/**
	 * Stores it current configuration in the dialog store.
	 */
	private void writeConfiguration() {
		IDialogSettings settings = getDialogSettings();
		if (previousParams != null) {
			int historySize = Math.min(previousParams.size(), HISTORY_SIZE);
			settings.put("historySize", historySize);
			for (int i = 0; i < historySize; ++i) {
				IDialogSettings s = settings.addNewSection("history" + i);
				previousParams.get(i).store(s);
			}
		}
	}

	/**
	 * Returns the page settings for this Text search page.
	 * 
	 * @return the page settings to be used
	 */
	private IDialogSettings getDialogSettings() {
		return SearchPlugin.getDefault().getDialogSettingsSection("GCSearch");
	}

	/**
	 * Return search parameters and update previous searches. An existing entry
	 * will be updated.
	 * 
	 * @return the search parameters
	 */
	private GCQueryParams getSearchParameters() {
		GCQueryParams match = findInPrevious(patternCombo.getText());
		if (match != null) {
			previousParams.remove(match);
		}
		match = new GCQueryParams(patternCombo.getText().trim(),
				caseSensitiveCheckbox.getSelection(),
				Language.forName(languageCombo.getText()),
				License.forName(licenseCombo.getText()), packageText.getText()
						.trim(), fileText.getText().trim(), classText.getText()
						.trim(), functionText.getText().trim());
		previousParams.add(0, match);
		return match;
	}

	private GCQueryParams findInPrevious(String pattern) {
		for (GCQueryParams params : previousParams) {
			if (pattern.equals(params.getPattern())) {
				return params;
			}
		}
		return null;
	}
}
