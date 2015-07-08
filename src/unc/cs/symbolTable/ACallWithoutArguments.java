package unc.cs.symbolTable;
/*
 * No arguments .... cannot handle overloading
 */
public class ACallWithoutArguments implements CallWithoutArguments {
	String caller;
	String calledType;
	String calleee;
	
	public ACallWithoutArguments(String caller, String calledType,
			String calleee) {
		super();
		this.caller = caller;
		this.calledType = calledType;
		this.calleee = calleee;
	}
	@Override
	public String getCaller() {
		return caller;
	}
	@Override
	public String getCalledType() {
		return calledType;
	}
	@Override
	public String getCalleee() {
		return calleee;
	}	

}
