package unc.cs.symbolTable;

import com.puppycrawl.tools.checkstyle.checks.naming.AccessModifier;

public interface PropertyInfo {
	public STMethod getGetter();
	public void setGetter(STMethod getter) ;
	public STMethod getSetter();
	public void setSetter(STMethod setter) ;
	public String getType();
	public String getName();
	public boolean isPublic();
	boolean isReadOnly();
	boolean isWriteOnly();
	boolean isEditable();
	STType getDefiningSTType();
	STVariable getVariableSet();
	STVariable getVariableGet();
	AccessModifier getVariableAccessModifier();
	int getGetterMinusSetterAccessMode();
	AccessModifier getAccessModifier();

}
