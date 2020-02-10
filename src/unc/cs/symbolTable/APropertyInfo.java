package unc.cs.symbolTable;

import java.util.List;

import unc.cs.checks.ComprehensiveVisitCheck;

public class APropertyInfo implements PropertyInfo {
	private static final int INFINITE_ACCESS_DIFFERENCE = 4;
	STMethod getter;
	STMethod setter;
	String name;
	String type;
	Boolean isPublic;
	Boolean isReadoonly;
	Integer getterMinusSetterAccess; 
	STType definingSTType;
	STVariable variableSet;
	STVariable variableGet;
	boolean propertyInReadVariableName = false;
	
	boolean propertyInWrittenVariableName = false;


	


	

	public APropertyInfo(STType anSTType, String aPropertyName, String aPropertyType) {
		name = aPropertyName;
		type = aPropertyType;
		definingSTType = anSTType;
		
	}
	
	@Override
	public boolean isReadOnly() {
		return setter == null;
	}
	@Override
	public boolean isEditable() {
		return !isReadOnly() && !isWriteOnly();
	}
	@Override
	public boolean isWriteOnly() {
		return getter == null;
	}
	
	
	public boolean isPublic() {
		if (isPublic == null) {
			if (getter == null) {
				isPublic = setter.isPublic(); 
			} else if (setter == null) {
				isPublic = getter.isPublic();
			} else {
				isPublic = getter.isPublic() || setter.isPublic();
			}
		}
		return isPublic;
	}
	
	public STMethod getGetter() {
		return getter;
	}
	public void setGetter(STMethod getter) {
		this.getter = getter;
		List<String> anAccessedGlobals = getter.getGlobalsAccessed();
		if (anAccessedGlobals == null || anAccessedGlobals.size() == 0 ) {
			return;
		}
			if (anAccessedGlobals.size() == 1) {
				String aVariableName = anAccessedGlobals.get(0);
				variableGet = definingSTType.getDeclaredGlobalSTVariable(aVariableName);
				propertyInReadVariableName = aVariableName.toLowerCase().contains(name.toLowerCase());
				
			} else {
				String aLowerCaseName = name.toLowerCase();
				for (String aVariableName:anAccessedGlobals) {
					if (aVariableName.toLowerCase().contains(aLowerCaseName)) {
						variableGet = definingSTType.getDeclaredGlobalSTVariable(aVariableName);
						propertyInReadVariableName = true;
					}
				}
			}	
			if (variableGet != null) {
				variableGet.setGetterPropertyInfo(this);
			}
	}
	public STMethod getSetter() {
		return setter;
	}
	
	public void setSetter(STMethod setter) {
		this.setter = setter;
		if (setter == null) {
			return;
		}
		if (setter.assignsToGlobal()) {
			List<String> anAssignedGlobals = setter.getGlobalsAssigned();
			
			if (anAssignedGlobals.size() == 1) {
				String aVariableName = anAssignedGlobals.get(0);
				variableSet = definingSTType.getDeclaredGlobalSTVariable(aVariableName);
				propertyInWrittenVariableName = aVariableName.toLowerCase().contains(name.toLowerCase());
			} else {
				String aLowerCaseName = name.toLowerCase();
				for (String aVariableName:anAssignedGlobals) {
					if (aVariableName.toLowerCase().equals(aLowerCaseName)) {
						variableSet = definingSTType.getDeclaredGlobalSTVariable(aVariableName);
					}
				}
			}
			if (variableSet != null) {
				variableSet.setGetterPropertyInfo(this);
			}
		}
	}
	@Override
	public String getName() {
		return  name;
	}
	
	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return type;
	} 
	
	
	public String toString() {
		return 
//				(isPublic()?"public ":"") + 
				(isEditable()?"editable " + getGetterMinusSetterAccess() + " " :"") +
				(isReadOnly()?"readonly ":"") + 
				(isWriteOnly()?"writeonly ":"") + 
				getName() + ":" + getType() + "(" +
				((getter != null)?ComprehensiveVisitCheck.toTokenAccessString(getter.getAccessToken()):" null") +
				"," +
				((setter != null)?ComprehensiveVisitCheck.toTokenAccessString(setter.getAccessToken()):" null") 
				+ ")";
	}
	
	public boolean equals(Object anOther) {
		if (anOther instanceof PropertyInfo) {
			PropertyInfo anotherPropertyInfo = (PropertyInfo) anOther;
			return getName().equals(anotherPropertyInfo.getName()) && 
					getType().equals(anotherPropertyInfo.getType());
			
		} else {
			return super.equals(anOther);
		}
	}
	public Integer getGetterMinusSetterAccess() {
		if (isReadOnly()) return INFINITE_ACCESS_DIFFERENCE;
		if (isWriteOnly()) return -INFINITE_ACCESS_DIFFERENCE;
		return
				ComprehensiveVisitCheck.toTokenAccessDegree(getter.getAccessToken()) - 
				ComprehensiveVisitCheck.toTokenAccessDegree(setter.getAccessToken());
	}
	@Override
	public STType getDefiningSTType() {
		return definingSTType;
	}
	@Override
	public STVariable getVariableSet() {
		return variableSet;
	}
	@Override
	public STVariable getVariableGet() {
		return variableGet;
	}
	
	public boolean getSetterAndGetterVariableSame() {
		return variableSet == variableGet;
	}
	public boolean isPropertyInReadVariableName() {
		return propertyInReadVariableName;
	}

	public boolean isPropertyInWrittenVariableName() {
		return propertyInWrittenVariableName;
	}
	public static STVariable findVariableForProperty (List<STVariable> aVariables, String aPropertyName) {
		String aLowerCasePropertyName = aPropertyName.toLowerCase();
		for (STVariable anSTVariable:aVariables) {
			if (anSTVariable.getName().toLowerCase().contains(aPropertyName)) {
				return anSTVariable;
			}
		}
		return null;
	}
}
