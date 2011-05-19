package com.googlecode.spektom.gcsearch.ui.viewer;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import com.google.gdata.data.codesearch.CodeSearchEntry;

public class InputTreeElement extends TreeElement {

	private PackageTreeElement[] packages;
	private CodeSearchEntry[] entries;

	public InputTreeElement(CodeSearchEntry[] entries) {
		super("");
		this.entries = entries;
	}

	public InputTreeElement(List<CodeSearchEntry> entries) {
		this(entries.toArray(new CodeSearchEntry[entries.size()]));
	}

	public PackageTreeElement[] getPackages() {
		if (packages == null) {
			HashMap<String, List<CodeSearchEntry>> byPackage = new LinkedHashMap<String, List<CodeSearchEntry>>();
			for (CodeSearchEntry entry : entries) {
				String pkg = entry.getPackage().getName();
				List<CodeSearchEntry> entries = byPackage.get(pkg);
				if (entries == null) {
					entries = new LinkedList<CodeSearchEntry>();
					byPackage.put(pkg, entries);
				}
				entries.add(entry);
			}
			packages = new PackageTreeElement[byPackage.size()];
			int i = 0;
			for (Entry<String, List<CodeSearchEntry>> e : byPackage.entrySet()) {
				List<CodeSearchEntry> entries = e.getValue();
				packages[i++] = new PackageTreeElement(e.getKey(),
						entries.toArray(new CodeSearchEntry[entries.size()]));
			}
		}
		return packages;
	}
}
