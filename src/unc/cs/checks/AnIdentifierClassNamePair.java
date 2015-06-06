package unc.cs.checks;

import com.puppycrawl.tools.checkstyle.api.FullIdent;

public class AnIdentifierClassNamePair implements IdentifierClassNamePair {
	FullIdent identifier;
	String className;
	public AnIdentifierClassNamePair(FullIdent anIdentifier, String aClassName) {
		identifier = anIdentifier;
		className = aClassName;
	}
	@Override
	public FullIdent getIdentifier() {
		return identifier;
	}
	@Override
	public String getClassName() {
		return className;
	}
	

}
