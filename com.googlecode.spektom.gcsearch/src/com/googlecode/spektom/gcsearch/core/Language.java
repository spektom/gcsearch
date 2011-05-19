package com.googlecode.spektom.gcsearch.core;

public enum Language {

	ANY("", " -- any language -- ", new String[] { "" }),
	ACTIONSCRIPT("actionscript", "ActionScript", new String[] { "as" }),
	ADA("ada", "Ada", new String[] { "ada" }),
	APPLESCRIPT("applescript", "AppleScript", new String[] { "applescript" }),
	ASP("asp", "ASP", new String[] { "asp", "aspx" }),
	ASSEMBLY("assembly", "Assembly", new String[] { "s", "a", "asm", "a86" }),
	AUTOCONF("autoconf", "Autoconf", new String[] { "autoconf" }),
	AUTOMAKE("automake", "Automake", new String[] { "in", "am" }),
	AWK("awk", "Awk", new String[] { "awk" }),
	BASIC("basic", "Basic/Visual Basic", new String[] { "bas" }),
	BAT("bat", "Batch file", new String[] { "bat" }),
	C("c", "C", new String[] { "c" }),
	CPP("c++", "C++", new String[] { "cpp", "h", "cc" }),
	CSHARP("c#", "C#", new String[] { "cs" }),
	CAJA("caja", "Caja", new String[] { "caja" }),
	COBOL("cobol", "COBOL", new String[] { "cbl" }),
	COLDFUSION("coldfusion", "ColdFusion", new String[] { "cfc" }),
	CONFIGURE("configure", "Configure script", new String[] { "configure" }),
	CSS("css", "CSS", new String[] { "css" }),
	D("d", "D", new String[] { "d" }),
	EIFFEL("eiffel", "Eiffel", new String[] { "e" }),
	ERLANG("erlang", "Erlang", new String[] { "erl" }),
	FORTRAN(
			"fortran", "Fortran", new String[] { "f77", "for", "f", "f90", "pyf" }),
	GO("go", "Go", new String[] { "go" }),
	HASKELL("haskell", "Haskell", new String[] { "hs", "lhs" }),
	INFORM("inform", "Inform", new String[] { "inform" }),
	JAVA("java", "Java", new String[] { "java" }),
	JAVASCRIPT("javascript", "JavaScript", new String[] { "js", "jsm" }),
	JSP("jsp", "JSP", new String[] { "jsp" }),
	LEX("lex", "Lex", new String[] { "l" }),
	LIMBO("limbo", "Limbo", new String[] { "limbo" }),
	LISP("lisp", "Lisp", new String[] { "lisp", "lsp", "emacs" }),
	LOLCODE("lolcode", "LolCode", new String[] { "lolcode" }),
	LUA("lua", "Lua", new String[] { "lua" }),
	M4("m4", "m4", new String[] { "m4" }),
	MAKEFILE("makefile", "Makefile", new String[] { "makefile" }),
	MAPLE("maple", "Maple", new String[] { "mws", "mw" }),
	MATHEMATICA("mathematica", "Mathematica", new String[] { "nb", "ma", "mb" }),
	MATLAB("matlab", "Matlab", new String[] { "mat", "matlab" }),
	MESSAGECATALOG("messagecatalog", "Message catalog", new String[] { "msg" }),
	MODULA2("modula2", "Modula-2", new String[] { "m2" }),
	MODULA3("modula3", "Modula-3", new String[] { "m3" }),
	OBJECTIVEC("objectivec", "Objective C", new String[] { "m" }),
	OCAML("ocaml", "OCaml", new String[] { "ml", "mli" }),
	PASCAL("pascal", "Pascal/Delphi", new String[] { "p", "tpu", "tps" }),
	PERL("perl", "Perl", new String[] { "pl", "pm" }),
	PHP("php", "PHP", new String[] { "php", "php3", "php4", "php5", "inc" }),
	POD("pod", "Plain Old Documentation", new String[] { "pod" }),
	PROLOG("prolog", "Prolog", new String[] { "pl", "pro" }),
	PROTO("proto", "Protocol Buffers", new String[] { "proto" }),
	PYTHON("python", "Python", new String[] { "py" }),
	R("r", "R", new String[] { "r" }),
	REBOL("rebol", "REBOL", new String[] { "rebol" }),
	RUBY("ruby", "Ruby", new String[] { "rb" }),
	SAS("sas", "SAS script", new String[] { "sas" }),
	SCHEME("scheme", "Scheme", new String[] { "ss" }),
	SCILAB("scilab", "Scilab", new String[] { "sce", "sci" }),
	SHELL("shell", "Shell", new String[] { "sh" }),
	SGML("sgml", "SGML", new String[] { "sgml" }),
	SMALLTALK("smalltalk", "Smalltalk", new String[] { "st", "sm", "sll" }),
	SQL("sql", "SQL", new String[] { "sql" }),
	SML("sml", "Standard ML", new String[] { "sml" }),
	SVG("svg", "SVG", new String[] { "svg" }),
	TCL("tcl", "Tcl", new String[] { "tcl" }),
	TEX("tex", "TeX/LaTeX", new String[] { "tex" }),
	TEXINFO("texinfo", "Texinfo", new String[] { "texi" }),
	TROFF("troff", "Troff", new String[] { "t", "troff" }),
	VERILOG("verilog", "Verilog", new String[] { "v", "vh" }),
	VHDL("vhdl", "VHDL", new String[] { "vhd", "vhdl" }),
	VIM("vim", "Vim script", new String[] { "vim" }),
	XSLT("xslt", "XSLT", new String[] { "xslt", "xsl" }),
	XUL("xul", "XUL", new String[] { "xul" }),
	YACC("yacc", "Yacc", new String[] { "y", "yacc" });

	private String[] exts;
	private String code;
	private String name;

	Language(String code, String name, String[] exts) {
		this.code = code;
		this.name = name;
		this.exts = exts;
	}

	/**
	 * @return language code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @return language name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return file extensions used for source code in this language
	 */
	public String[] getFileExtensions() {
		return exts;
	}
	
	/**
	 * @return language by code. If the language not found the method returns
	 *         <code>null</code>.
	 */
	public static Language forCode(String code) {
		for (Language lang : Language.values()) {
			if (code.equals(lang.getCode())) {
				return lang;
			}
		}
		return null;
	}
	
	/**
	 * @return language by name. If the language not found the method returns
	 *         <code>null</code>.
	 */
	public static Language forName(String name) {
		for (Language lang : Language.values()) {
			if (name.equals(lang.getName())) {
				return lang;
			}
		}
		return null;
	}

	/**
	 * @param fileExtension
	 *            File extension
	 * @return relevant language for the given file extension. In case the most
	 *         relevant language couldn't be found, {@link Language#ANY} is
	 *         returned.
	 */
	public static Language forExtension(String fileExtension) {
		if (fileExtension != null) {
			fileExtension = fileExtension.toLowerCase();
			for (Language lang : Language.values()) {
				for (String ext : lang.getFileExtensions()) {
					if (fileExtension.equals(ext)) {
						return lang;
					}
				}
			}
		}
		return Language.ANY;
	}
}
