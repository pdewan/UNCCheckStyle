package unc.cs.symbolTable;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

public class AnSTVariable extends AnSTNameable implements STVariable{
	String typeName;
	DetailAST rhs;
	VariableKind variableKind;
	STNameable[] tags;
	STType declaringType;

	boolean isInstance;
	boolean isFinal;

	public AnSTVariable(
			DetailAST ast, 
			String aName,
			String aTypeName,
			DetailAST anRHS,
			VariableKind aVariableKind,
			STNameable[] aTags
			) {
		super(ast, aName);
		
	}

	@Override
	public VariableKind getVariableKind() {
		return variableKind;
	}

	@Override
	public boolean isGlobal() {
		return variableKind == VariableKind.GLOBAL ;
	}

	@Override
	public boolean isLocal() {
		return variableKind == VariableKind.LOCAL ;
	}

	@Override
	public boolean isParameter() {
		return variableKind == VariableKind.PARAMETER;
	}

	@Override
	public boolean isInstance() {
		return false;
	}

	@Override
	public boolean isFinal() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getType() {
		return typeName;
	}

	@Override
	public DetailAST getRHS() {
		return rhs;
	}

	@Override
	public STNameable[] getTags() {
		return tags;
	}

	@Override
	public STType getDeclaringSTType() {
		return declaringType;
	}

	@Override
	public void setDeclaringSTType(STType aDeclaringSTType) {
		declaringType = aDeclaringSTType;
		
	}

}
