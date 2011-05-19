package com.googlecode.spektom.gcsearch;

import org.eclipse.jface.dialogs.IDialogSettings;

public class GCQueryParams {

	private String pattern;
	private boolean caseSensitive;
	private Language language;
	private License license;
	private String packege;
	private String file;
	private String clazz;
	private String function;

	public GCQueryParams(IDialogSettings settings) {
		pattern = settings.get("pattern");
		if (settings.get("caseSensitive") != null) {
			caseSensitive = true;
		}
		String langCode = settings.get("language");
		if (langCode != null) {
			language = Language.forCode(langCode);
		} else {
			language = Language.ANY;
		}
		String licCode = settings.get("license");
		if (licCode != null) {
			license = License.forCode(licCode);
		} else {
			license = License.ANY;
		}
		packege = settings.get("package");
		file = settings.get("file");
		clazz = settings.get("class");
		function = settings.get("function");
	}

	public GCQueryParams(String pattern, boolean caseSensitive,
			Language language, License license, String packege, String file,
			String clazz, String function) {

		this.pattern = pattern;
		this.caseSensitive = caseSensitive;
		this.language = language;
		this.license = license;

		if (packege != null && packege.length() > 0) {
			this.packege = packege;
		}
		if (file != null && file.length() > 0) {
			this.file = file;
		}
		if (clazz != null && clazz.length() > 0) {
			this.clazz = clazz;
		}
		if (function != null && function.length() > 0) {
			this.function = function;
		}
	}

	public String getPattern() {
		return pattern;
	}

	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	public Language getLanguage() {
		return language;
	}

	public License getLicense() {
		return license;
	}

	public String getPackage() {
		return packege;
	}

	public String getFile() {
		return file;
	}

	public String getClazz() {
		return clazz;
	}

	public String getFunction() {
		return function;
	}

	public void store(IDialogSettings settings) {
		settings.put("pattern", pattern);
		if (caseSensitive) {
			settings.put("caseSensitive", true);
		}
		if (language != Language.ANY) {
			settings.put("language", language.getCode());
		}
		if (license != License.ANY) {
			settings.put("license", license.getCode());
		}
		if (packege != null) {
			settings.put("package", packege);
		}
		if (file != null) {
			settings.put("file", file);
		}
		if (clazz != null) {
			settings.put("class", clazz);
		}
		if (function != null) {
			settings.put("function", function);
		}
	}
}
