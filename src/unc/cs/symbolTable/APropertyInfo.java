package unc.cs.symbolTable;

public class APropertyInfo implements PropertyInfo {
	STMethod getter;
	STMethod setter;
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
	

}
