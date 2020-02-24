package unc.cs.symbolTable;

import unc.cs.checks.ComprehensiveVisitCheck;
import unc.cs.checks.STBuilderCheck;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.checks.naming.AccessModifier;

public class AnSTVariable extends AnAbstractSTVariable  implements STVariable{
//	String typeName;
	DetailAST rhs;
//	VariableKind variableKind;
//	STNameable[] tags;
//	STType declaringType;
//	STMethod declaringMethod;
	DetailAST declaringBlock;
//	Integer accessToken;
//	protected Set<STMethod> methodsAccessing = new HashSet() ;
//	protected Set<STMethod> methodsAssigning = new HashSet();
//	protected Set<STType> referenceTypes;
//	protected Set<DetailAST> assignments = new HashSet<>();
//	protected PropertyInfo setterPropertyInfo;
//	protected PropertyInfo getterPropertyInfo;
//	protected Set<Integer> modifiers;
//	protected AccessModifier accessModifier;


//	protected int numReferences;




	boolean isInstance;
	boolean isFinal;
	 

	public AnSTVariable(
//			STType anSTType,
			String aDeclaringTypeName,
			DetailAST aDeclaringBlock,
			DetailAST ast, 
			String aName,
			String aTypeName,
			DetailAST anRHS,
			VariableKind aVariableKind,
			Integer anAccessToken,
			STNameable[] aTags,
			Set<Integer> aModifiers
			) {
		super(aDeclaringTypeName, ast, aName, aTypeName, aVariableKind, anAccessToken, aTags);
//		declaringType = anSTType;
////		declaringMethod = anSTMethod;
		declaringBlock = aDeclaringBlock;
//				
//		typeName = aTypeName;
		rhs = anRHS;
//		tags = aTags;
//		variableKind = aVariableKind;
//		if (ast != null) {
//			isFinal = ComprehensiveVisitCheck.isFinal(ast);
//		
//		isInstance = !ComprehensiveVisitCheck.isStatic(ast);
//		}
//		accessToken = anAccessToken;
		if (aModifiers != null) {
			accessModifier = STBuilderCheck.toAccessModifier(aModifiers);
		}
		
	}

//	@Override
//	public VariableKind getVariableKind() {
//		return variableKind;
//	}
//
//	@Override
//	public boolean isGlobal() {
//		return variableKind == VariableKind.GLOBAL ;
//	}
//
//	@Override
//	public boolean isLocal() {
//		return variableKind == VariableKind.LOCAL ;
//	}
//
//	@Override
//	public boolean isParameter() {
//		return variableKind == VariableKind.PARAMETER;
//	}
//
//	@Override
//	public boolean isInstance() {
//		return isInstance;
//	}
//	
//	String toStatic() {
//		return isInstance()?"":"static ";
//	}
//	
//	public String toString() {
//		return 
//				ComprehensiveVisitCheck.toAccessString(getAccessToken()) +
//				toStatic() + getType() + " " + getName();
//	}
//
//	@Override
//	public boolean isFinal() {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public String getType() {
//		return typeName;
//	}

	@Override
	public DetailAST getRHS() {
		return rhs;
	}

//	@Override
//	public STNameable[] getTags() {
//		return tags;
//	}
//
//	@Override
//	public STType getDeclaringSTType() {
//		if (declaringType == null) {
//			declaringType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(typeName);
//		}
//		return declaringType;
//	}
//
//	@Override
//	public void setDeclaringSTType(STType aDeclaringSTType) {
//		declaringType = aDeclaringSTType;
//		
//	}
//	@Override
//	public Integer getAccessToken() {
//		return accessToken;
//	}
//	@Override
//	public Set<STMethod> getMethodsAccessing() {
//		return methodsAccessing;
//	}
//	@Override
//	public Set<STMethod> getMethodsAssigning() {
//		return methodsAssigning;
//	}
//
//	public Set<STType> getReferencingTypes() {
//		return referenceTypes;
//	}
//
////	public Set<STType> getAssignments() {
////		return assignments;
////	}
//	public void setMethodsAccessing(Set<STMethod> methodsAccessing) {
//		this.methodsAccessing = methodsAccessing;
//	}
//
//	public void setMethodsAssigning(Set<STMethod> methodsAssigning) {
//		this.methodsAssigning = methodsAssigning;
//	}
//
//	public void setReferenceTypes(Set<STType> references) {
//		this.referenceTypes = references;
//	}
//
////	public void setAssignments(Set<STType> assignments) {
////		this.assignments = assignments;
////	}
//
//	@Override
//	public PropertyInfo getSetterPropertyInfo() {
//		return setterPropertyInfo;
//	}
//	@Override
//	public void setSetterPropertyInfo(PropertyInfo setterPropertyInfo) {
//		this.setterPropertyInfo = setterPropertyInfo;
//	}
//	@Override
//	public PropertyInfo getGetterPropertyInfo() {
//		return getterPropertyInfo;
//	}
//	@Override
//	public void setGetterPropertyInfo(PropertyInfo getterPropertyInfo) {
//		this.getterPropertyInfo = getterPropertyInfo;
//	}
////	@Override
////	public int getNumReferences() {
////		return numReferences;
////	}
////	@Override
////	public void setNumReferences(int numReferences) {
////		this.numReferences = numReferences;
////	}
////	public void incrementNumReferences() {
////		numReferences++;
////	}
//
//	@Override
//	public Set<DetailAST> getAssignments() {
//		return assignments;
//	}
//
//	@Override
//	public AccessModifier getAccessModifier() {
//		// TODO Auto-generated method stub
//		return accessModifier;
//	}


}
