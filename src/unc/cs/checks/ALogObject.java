package unc.cs.checks;

public class ALogObject implements LogObject {	
	int line;
	int column;
	String key;
	Object[] args;
	public ALogObject (int aLine, int aColumn, String aKey, Object... anArgs) {
		line = aLine;
		column = aColumn;
		key = aKey;
		args = anArgs;
	}
	@Override
	public int getColumn() {
		return column;
	}
	@Override
	public int getLine() {
		return line;
	}
	@Override
	public String getKey() {
		return key;
	}
	@Override
	public Object[] getArgs() {
		return args;
	}
}
