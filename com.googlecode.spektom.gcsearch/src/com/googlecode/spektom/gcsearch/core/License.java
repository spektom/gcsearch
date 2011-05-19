package com.googlecode.spektom.gcsearch.core;

public enum License {

	ANY("", " -- any license -- "),
	ALADDIN("aladdin", "Aladdin Public License"),
	ARTISTIC("artistic", "Artistic License"),
	APACHE("apache", "Apache License"),
	APPLE("apple", "Apple Public Source License"),
	BSD("bsd", "BSD License"),
	CPL("cpl", "Common Public License"),
	EPL("epl", "Eclipse Public License"),
	AGPL("agpl", "GNU Affero General Public License"),
	GPL("gpl", "GNU General Public License"),
	LGPL("lgpl", "GNU Lesser General Public License"),
	DISCLAIMER("disclaimer", "Historical Permission Notice and Disclaimer"),
	IBM("ibm", "IBM Public License"),
	LUCENT("lucent", "Lucent Public License"),
	MIT("mit", "MIT License"),
	MOZILLA("mozilla", "Mozilla Public License"),
	NASA("nasa", "NASA Open Source Agreement"),
	PYTHON("python", "Python Software Foundation License"),
	QPL("qpl", "Q Public License"),
	SLEEPYCAT("sleepycat", "Sleepycat License"),
	ZOPE("zope", "Zope Public License");

	private String code;
	private String name;

	License(String code, String name) {
		this.code = code;
		this.name = name;
	}

	/**
	 * @return license code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @return license name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return license by code. If the license not found the method returns
	 *         <code>null</code>.
	 */
	public static License forCode(String code) {
		for (License lic : License.values()) {
			if (code.equals(lic.getCode())) {
				return lic;
			}
		}
		return null;
	}

	/**
	 * @return license by name. If the license not found the method returns
	 *         <code>null</code>.
	 */
	public static License forName(String name) {
		for (License lic : License.values()) {
			if (name.equals(lic.getName())) {
				return lic;
			}
		}
		return null;
	}
}