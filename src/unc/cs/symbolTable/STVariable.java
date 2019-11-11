package unc.cs.symbolTable;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

public interface STVariable extends STNameable {
	VariableKind getVariableKind();
	boolean isGlobal();
	boolean isLocal();
	boolean isParameter();
	boolean isInstance();
	boolean isFinal();

	String getType();
	DetailAST getRHS();
	STNameable[] getTags();
	STType getDeclaringSTType();
	void setDeclaringSTType(STType aDeclaringSTType);
	Integer getAccessToken();
	
}
