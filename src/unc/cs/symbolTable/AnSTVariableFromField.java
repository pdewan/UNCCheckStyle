package unc.cs.symbolTable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.checks.naming.AccessModifier;

import unc.cs.checks.ComprehensiveVisitCheck;
import unc.cs.checks.STBuilderCheck;

public class AnSTVariableFromField extends AnAbstractSTVariable implements STVariable{
	Field field;
	
	public AnSTVariableFromField(STType aDeclaringType, Field aField) {
		super (aField.getName());
		this.declaringType = aDeclaringType;
		declaringMethod = null;
		field = aField;
		isInstance = !Modifier.isStatic(aField.getModifiers());
		isFinal = Modifier.isFinal(aField.getModifiers());
		typeName = aField.getType().getName();
		accessModifier = STBuilderCheck.toAccessModifier(aField.getModifiers());
		accessToken = ComprehensiveVisitCheck.getAccessToken(aField);
	}



//
//	@Override
//	public DetailAST getAST() {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public VariableKind getVariableKind() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isGlobal() {
		return true;
	}

	@Override
	public boolean isLocal() {
		return false;
	}

	@Override
	public boolean isParameter() {
		// TODO Auto-generated method stub
		return false;
	}

//	@Override
//	public boolean isInstance() {
//		return isInstance;
//	}

//	@Override
//	public boolean isFinal() {
//		// TODO Auto-generated method stub
//		return false;
//	}

//	@Override
//	public String getType() {
//		return null;
//	}

	@Override
	public DetailAST getRHS() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public STNameable[] getTags() {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	public STType getDeclaringSTType() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public void setDeclaringSTType(STType aDeclaringSTType) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public Integer getAccessToken() {
//		return null;
//	}

//	@Override
//	public PropertyInfo getSetterPropertyInfo() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public void setSetterPropertyInfo(PropertyInfo setterPropertyInfo) {
//		// TODO Auto-generated method stub
//		
//	}

//	@Override
//	public PropertyInfo getGetterPropertyInfo() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public void setGetterPropertyInfo(PropertyInfo getterPropertyInfo) {
//		// TODO Auto-generated method stub
//		
//	}

//	@Override
//	public Set<STMethod> getMethodsAccessing() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public Set<STMethod> getMethodsAssigning() {
//		// TODO Auto-generated method stub
//		return null;
//	}

//	@Override
//	public Set<DetailAST> getAssignments() {
//		// TODO Auto-generated method stub
//		return null;
//	}

//	@Override
//	public AccessModifier getAccessModifier() {
//		// TODO Auto-generated method stub
//		return null;
//	}

}
