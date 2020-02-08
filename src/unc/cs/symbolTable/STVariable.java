package unc.cs.symbolTable;

import java.util.Set;

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
	PropertyInfo getSetterPropertyInfo();
	void setSetterPropertyInfo(PropertyInfo setterPropertyInfo);
	PropertyInfo getGetterPropertyInfo();
	void setGetterPropertyInfo(PropertyInfo getterPropertyInfo);
	Set<STMethod> getMethodsAccessing();
	Set<STMethod> getMethodsAssigning();
	
}
