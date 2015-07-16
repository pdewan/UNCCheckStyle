package unc.cs.symbolTable;

public class APropertyInfo implements PropertyInfo {
	STMethod getter;
	STMethod setter;
	String name;
	String type;
	public APropertyInfo(String aPropertyName, String aPropertyType) {
		name = aPropertyName;
		type = aPropertyType;
	}
	public STMethod getGetter() {
		return getter;
	}
	public void setGetter(STMethod getter) {
		this.getter = getter;
	}
	public STMethod getSetter() {
		return setter;
	}
	public void setSetter(STMethod setter) {
		this.setter = setter;
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
		return getName() + ":" + getType();
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
	
	

}
