package unc.cs.symbolTable;

import unc.cs.checks.ComprehensiveVisitCheck;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

public class AnSTVariable extends AnSTNameable implements STVariable{
	String typeName;
	DetailAST rhs;
	VariableKind variableKind;
	STNameable[] tags;
	STType declaringType;
	Integer accessToken;
	protected Set<STMethod> methodsAccessing = new HashSet() ;
	protected Set<STMethod> methodsAssigning = new HashSet();
	protected Set<STType> references;
	protected Set<STType> assignments;
	protected PropertyInfo setterPropertyInfo;
	protected PropertyInfo getterPropertyInfo;




	boolean isInstance;
	boolean isFinal;
	 

	public AnSTVariable(
			STType anSTType,
			DetailAST ast, 
			String aName,
			String aTypeName,
			DetailAST anRHS,
			VariableKind aVariableKind,
			Integer anAccessToken,
			STNameable[] aTags
			) {
		super(ast, aName);
		declaringType = anSTType;
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
		return isInstance;
	}
	
	String toStatic() {
		return isInstance()?"":"static ";
	}
	
	public String toString() {
		return 
				ComprehensiveVisitCheck.toAccessString(getAccessToken()) +
				toStatic() + getType() + " " + getName();
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
	@Override
	public Set<STMethod> getMethodsAccessing() {
		return methodsAccessing;
	}
	@Override
	public Set<STMethod> getMethodsAssigning() {
		return methodsAssigning;
	}

	public Set<STType> getReferences() {
		return references;
	}

	public Set<STType> getAssignments() {
		return assignments;
	}
	public void setMethodsAccessing(Set<STMethod> methodsAccessing) {
		this.methodsAccessing = methodsAccessing;
	}

	public void setMethodsAssigning(Set<STMethod> methodsAssigning) {
		this.methodsAssigning = methodsAssigning;
	}

	public void setReferences(Set<STType> references) {
		this.references = references;
	}

	public void setAssignments(Set<STType> assignments) {
		this.assignments = assignments;
	}

	@Override
	public PropertyInfo getSetterPropertyInfo() {
		return setterPropertyInfo;
	}
	@Override
	public void setSetterPropertyInfo(PropertyInfo setterPropertyInfo) {
		this.setterPropertyInfo = setterPropertyInfo;
	}
	@Override
	public PropertyInfo getGetterPropertyInfo() {
		return getterPropertyInfo;
	}
	@Override
	public void setGetterPropertyInfo(PropertyInfo getterPropertyInfo) {
		this.getterPropertyInfo = getterPropertyInfo;
	}



}
