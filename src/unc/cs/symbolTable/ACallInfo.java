package unc.cs.symbolTable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

import unc.cs.checks.ComprehensiveVisitCheck;

public class ACallInfo implements CallInfo {
	DetailAST ast;
	String caller;
	String calledType;
	String calledCastType;
	String callee;
	List<String> callerParameterTypes;
	STType calledSTType;
	String callingType;;
	List<DetailAST> actuals;
	
	String[] normalizedCall;
	STMethod callingMethod;
	Set<STMethod> matchingCalledMethods;
	boolean hasUnkownCalledType = false;
	
	
//	public ACallInfo(String caller, String calledType,
//			String calleee) {
//		super();
//		this.caller = caller;
//		this.calledType = calledType;
//		this.calleee = calleee;
//	}
	public ACallInfo(DetailAST anAST, String aCallingType, String caller, List<String> aCallerParameterTypes, String calledType, String aCallee,
			List<DetailAST> actuals, String[] aNormalizedCall, String aCalledCastType) {
		super();
		ast = anAST;
		this.caller = caller;
		this.calledType = calledType;
		this.callee = aCallee;
		this.actuals = actuals;
		this.normalizedCall = aNormalizedCall;
		callerParameterTypes = aCallerParameterTypes;
		calledCastType = aCalledCastType;
		callingType = aCallingType;
		if (calledType.equals("super") || calledType.equals(callingType)) {
			hasUnkownCalledType = true;
		}
		
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
	/**
	 * To be corrected later, when we have all super types
	 */
	@Override
	public void setCalledType(String newVal) {
		calledType = newVal; 
	}
	@Override
	public void setCalledSTType(STType newVal) {
		calledSTType = newVal; 
		hasUnkownCalledType = false;

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
//	@Override
//	public Set<STMethod> getMatchingCalledMethods() {
//		if (matchingCalledMethods == null) {
//			matchingCalledMethods = new HashSet();
//			STType aCalledType = getCalledSTType();
//			if (aCalledType == null)
//				return null;
//			for (STMethod anSTMethod:aCalledType.getDeclaredMethods()) {
//				if (anSTMethod.getName().equals(callee) &&
//						anSTMethod.getParameterTypes().length == actuals.size()) { // at some point do overload resolution?
//					matchingCalledMethods.add(anSTMethod);
////					anSTMethod.addCaller(callingMethod);
////					break;
//				}
//			}
//		}
//		return matchingCalledMethods;
//	}
	public Set<STMethod> getMatchingCalledMethods() {
		if (matchingCalledMethods == null) {
			matchingCalledMethods = ComprehensiveVisitCheck.getMatchingCalledMethods( getCalledSTType(), this);
//			matchingCalledMethods = new HashSet();
//			STType aCalledType = getCalledSTType();
//			if (aCalledType == null)
//				return null;
//			for (STMethod anSTMethod:aCalledType.getDeclaredMethods()) {
//				if (anSTMethod.getName().equals(callee) &&
//						anSTMethod.getParameterTypes().length == actuals.size()) { // at some point do overload resolution?
//					matchingCalledMethods.add(anSTMethod);
////					anSTMethod.addCaller(callingMethod);
////					break;
//				}
//			}
		}
		return matchingCalledMethods;
	}
	 

	@Override
	public DetailAST getAST() {
		return ast;
	}
	@Override
	public boolean hasUnknownCalledType() {
		return hasUnkownCalledType;
	}
	@Override
	public String getCallingType() {
		return callingType;
	}
}
