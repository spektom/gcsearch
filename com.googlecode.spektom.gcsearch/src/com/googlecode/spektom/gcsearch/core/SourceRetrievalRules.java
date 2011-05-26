package com.googlecode.spektom.gcsearch.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;

import com.googlecode.spektom.gcsearch.GCActivator;

public class SourceRetrievalRules {

	private static final String RULE_SEP = "=="; //$NON-NLS-1$
	private static final File SAVED_RULES_FILE = new File(GCActivator
			.getDefault().getStateLocation().toFile(), "rules.txt");
	private static Rule[] rules;

	private static Rule[] loadRules(InputStream inputStream) throws IOException {
		List<Rule> rules = new LinkedList<Rule>();
		BufferedReader r = new BufferedReader(
				new InputStreamReader(inputStream));
		String line;
		while ((line = r.readLine()) != null) {
			if (line.startsWith("#")) {
				continue;
			}
			line = line.trim();
			if (line.length() == 0) {
				continue;
			}

			String packagePattern = line;
			String targetUrl = "";
			int i = line.indexOf(RULE_SEP);
			if (i != -1) {
				packagePattern = line.substring(0, i);
				targetUrl = line.substring(i + 2);
			}
			try {
				rules.add(new Rule(Pattern.compile(packagePattern.trim()), targetUrl
						.trim()));
			} catch (PatternSyntaxException e) {
				GCActivator.log("Rule expression can't be compiled: " + packagePattern);
			}
		}
		return rules.toArray(new Rule[rules.size()]);
	}

	public static Rule[] loadDefaultRules() throws IOException {
		URL url = FileLocator.find(GCActivator.getDefault().getBundle(),
				new Path("resources/rules.txt"), null);
		if (url != null) {
			url = FileLocator.resolve(url);
		}
		InputStream s = url.openStream();
		try {
			return loadRules(s);
		} finally {
			s.close();
		}
	}

	public static Rule[] loadRules() {
		if (rules == null) {
			try {
				if (SAVED_RULES_FILE.exists()) {
					FileInputStream s = new FileInputStream(SAVED_RULES_FILE);
					try {
						rules = loadRules(s);
					} finally {
						s.close();
					}
				} else {
					rules = loadDefaultRules();
				}
			} catch (IOException e) {
				GCActivator.log("Can't load rules", e);
			}
		}
		return rules;
	}

	public static void updateRules(Rule[] newRules) {
		try {
			PrintStream ps = new PrintStream(new FileOutputStream(
					SAVED_RULES_FILE));
			try {
				for (Rule rule : newRules) {
					ps.println(rule.getPackagePattern() + RULE_SEP
							+ rule.getTargetUrl());
				}
				rules = new Rule[newRules.length];
				System.arraycopy(newRules, 0, rules, 0, newRules.length);
			} finally {
				ps.close();
			}
		} catch (IOException e) {
			GCActivator.log("Can't save rules", e);
		}
	}

	public static String apply(String packageName, String fileName) {
		for (Rule rule : loadRules()) {
			Matcher m = rule.getPackagePattern().matcher(packageName);
			if (m.matches()) {
				packageName = m.replaceFirst(rule.getTargetUrl());
				break;
			}
		}
		return packageName.replace("%FILE%", fileName);
	}

	public static class Rule {
		private Pattern packagePattern;
		private String targetUrl;

		public Rule(Pattern packagePattern, String targetUrl) {
			this.packagePattern = packagePattern;
			this.targetUrl = targetUrl;
		}

		public Pattern getPackagePattern() {
			return packagePattern;
		}

		public String getTargetUrl() {
			return targetUrl;
		}
	}
}
