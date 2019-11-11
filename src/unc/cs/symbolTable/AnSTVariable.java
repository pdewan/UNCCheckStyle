package unc.cs.symbolTable;

import unc.cs.checks.ComprehensiveVisitCheck;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

public class AnSTVariable extends AnSTNameable implements STVariable{
	String typeName;
	DetailAST rhs;
	VariableKind variableKind;
	STNameable[] tags;
	STType declaringType;
	Integer accessToken;

	boolean isInstance;
	boolean isFinal;

	public AnSTVariable(
			DetailAST ast, 
			String aName,
			String aTypeName,
			DetailAST anRHS,
			VariableKind aVariableKind,
			Integer anAccessToken,
			STNameable[] aTags
			) {
		super(ast, aName);
		typeName = aTypeName;
		rhs = anRHS;
		tags = aTags;
		variableKind = aVariableKind;
		isFinal = ComprehensiveVisitCheck.isFinal(ast);
		isInstance = !ComprehensiveVisitCheck.isStatic(ast);
		accessToken = anAccessToken;
		
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
	@Override
	public Integer getAccessToken() {
		return accessToken;
	}

}
