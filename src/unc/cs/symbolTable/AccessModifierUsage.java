package unc.cs.symbolTable;

import com.puppycrawl.tools.checkstyle.checks.naming.AccessModifier;

import unc.cs.checks.STBuilderCheck;

public class AccessModifierUsage {
	STNameable subject;
	AccessModifier declared;
	AccessModifier used;
	int difference;

	public AccessModifierUsage(STNameable aSubject, AccessModifier declared, AccessModifier used) {
		super();
		this.declared = declared;
		this.used = used;
		subject = aSubject;
	}
	public STNameable getSubject() {
		return subject;
	}
	public AccessModifier getDeclared() {
		return declared;
	}
	public AccessModifier getUsed() {
		return used;
	}
	public int getDifference() {
		if (used == null) {
			return APropertyInfo.INFINITE_ACCESS_DIFFERENCE;
		}
		return (used.ordinal() - declared.ordinal());
	}
	public String toString() {
		return "(" + subject + ", " +  declared + ", " + used  + ", " + getDifference() + ")";
	}
	

}
