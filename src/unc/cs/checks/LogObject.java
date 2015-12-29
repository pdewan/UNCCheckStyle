package unc.cs.checks;

public interface LogObject {

	public int getLine();

	public String getKey();

	public Object[] getArgs();

	int getColumn();

}