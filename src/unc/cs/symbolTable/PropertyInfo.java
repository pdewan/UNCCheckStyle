package unc.cs.symbolTable;

public interface PropertyInfo {
	public STMethod getGetter();
	public void setGetter(STMethod getter) ;
	public STMethod getSetter();
	public void setSetter(STMethod setter) ;
	public String getType();
	public String getName();

}
