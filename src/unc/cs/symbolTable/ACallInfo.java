package unc.cs.symbolTable;
import java.util.List;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

public class ACallInfo implements CallInfo {
	String caller;
	String calledType;
	String calledCastType;
	String callee;
	List<String> callerParameterTypes;
	STType calledSTType;
	
	List<DetailAST> actuals;
	
	String[] normalizedCall;
	STMethod callingMethod;
	STMethod calledMethod;
	
	
//	public ACallInfo(String caller, String calledType,
//			String calleee) {
//		super();
//		this.caller = caller;
//		this.calledType = calledType;
//		this.calleee = calleee;
//	}
	public ACallInfo(String caller, List<String> aCallerParameterTypes, String calledType, String aCallee,
			List<DetailAST> actuals, String[] notmalizedCall, String aCalledCastType) {
		super();
		this.caller = caller;
		this.calledType = calledType;
		this.callee = aCallee;
		this.actuals = actuals;
		this.normalizedCall = notmalizedCall;
		callerParameterTypes = aCallerParameterTypes;
		calledCastType = aCalledCastType;
	}
	@Override
	public void setCallingMethod (STMethod anSTMethod) {
		callingMethod = anSTMethod;
	}
	@Override
	public STMethod getCallingMethod() {
		return callingMethod;
	}
	@Override
	public List<String> getCallerParameterTypes() {
		return callerParameterTypes;
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
	public String getCallee() {
		return callee;
	}
	@Override
	public List<DetailAST> getActuals() {
		return actuals;
	}
	@Override
	public String[] getNormalizedCall() {
		return normalizedCall;
	}
	public String toString() {
		return caller + "-->" + calledType + "." + callee;
	}
	@Override
	public STType getCalledSTType() {
		if (calledSTType == null) {
			calledSTType = SymbolTableFactory.getSymbolTable().getSTClassByShortName(calledType);
		}
		return calledSTType;
	}
	@Override
	public STMethod getCalledMethod() {
		if (calledMethod == null) {
			int i = 0;
			STType aCalledType = getCalledSTType();
			if (aCalledType == null)
				return null;
			for (STMethod anSTMethod:aCalledType.getDeclaredMethods()) {
				if (anSTMethod.getName().equals(callee) &&
						anSTMethod.getParameterTypes().length == actuals.size()) { // at some point do overload resolution?
					calledMethod = anSTMethod;
					break;
				}
			}
		}
		return calledMethod;
	}
	 

}
