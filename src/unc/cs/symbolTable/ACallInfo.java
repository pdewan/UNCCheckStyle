package unc.cs.symbolTable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

import unc.cs.checks.ComprehensiveVisitCheck;
import unc.cs.checks.TagBasedCheck;

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
	boolean constructor;
	
	
	String[] normalizedCall;
	STMethod callingMethod;
	Set<STMethod> matchingCalledMethods;
	boolean hasUnkownCalledType = false;
	protected STMethod[] stMethods;
	
	


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
//		if (calledType.contains("QUIT") || (calledType.contains("double"))) {
//			System.err.println ("Found  QUIT or double");
//		};
//		if (aCallee != null && aCallee.contains("reduce")) {
//			System.err.println("found reduce");
//		}
		ast = anAST;
		this.caller = caller;
		this.calledType = calledType;
		this.callee = aCallee;
		this.actuals = actuals;
		this.normalizedCall = aNormalizedCall;
		callerParameterTypes = aCallerParameterTypes;
		calledCastType = aCalledCastType;
		callingType = aCallingType;
		if (calledType.equals("super") || calledType.equals(callingType) || TagBasedCheck.hasVariableNameSyntax(calledType)) {
			hasUnkownCalledType = true;
		}
		if (callee != null && calledType != null) {
			constructor = calledType.equals(callee) || calledType.endsWith("." + callee);
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
		calledType = calledSTType.getName();
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
			String aNameUsed = calledType;
			if (calledType.startsWith(ComprehensiveVisitCheck.VARIABLE_PREFIX)) { // we screwed up with variable identification perhaps
				aNameUsed = calledType.substring(ComprehensiveVisitCheck.VARIABLE_PREFIX.length());
			}
			calledSTType = SymbolTableFactory.getSymbolTable().getSTClassByShortName(aNameUsed);
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
			matchingCalledMethods = computingMatchingCalledMethods();
//			STType aCalledSTType = getCalledSTType();
//			if (aCalledSTType == null) {
//				if (!Character.isUpperCase(calledType.charAt(0))) {
//					matchingCalledMethods = new HashSet<>();
//				} else {
//					return null;
//				}
//			} else {
//			matchingCalledMethods = ComprehensiveVisitCheck.getMatchingCalledMethods( aCalledSTType, this);
//			}
//			if (matchingCalledMethods == null) {
//				return null;
//			}
//			if (matchingCalledMethods.size() == 0) {
//				matchingCalledMethods = ComprehensiveVisitCheck.getMatchingCalledMethodsInSomeSTType(this);
//			}
//			if (matchingCalledMethods.size() == 0) {
//				STMethod anSTMethod = new AnSTMethod(callee, this.actuals.size());
//				anSTMethod.setUnresolvedMethod(true);
//				matchingCalledMethods.add(anSTMethod);
//				
//			} else if (matchingCalledMethods.size() > 0) {
//				for (STMethod aMethod:matchingCalledMethods) {
//					aMethod.setAmbiguouslyOverloadedMethods(true);
//				}
//			}
////			matchingCalledMethods = new HashSet();
////			STType aCalledType = getCalledSTType();
////			if (aCalledType == null)
////				return null;
////			for (STMethod anSTMethod:aCalledType.getDeclaredMethods()) {
////				if (anSTMethod.getName().equals(callee) &&
////						anSTMethod.getParameterTypes().length == actuals.size()) { // at some point do overload resolution?
////					matchingCalledMethods.add(anSTMethod);
//////					anSTMethod.addCaller(callingMethod);
//////					break;
////				}
////			}
		}
		return matchingCalledMethods;
	}
	public Set<STMethod> computingMatchingCalledMethods() {
		 Set<STMethod> retVal;
			STType aCalledSTType = getCalledSTType();
			if (aCalledSTType == null) {
				if (!Character.isUpperCase(ComprehensiveVisitCheck.toShortTypeName(calledType).charAt(0))) {
					retVal = new HashSet<>();
				} else {
					return null;
				}
			} else {
				retVal = ComprehensiveVisitCheck.getMatchingCalledMethods( aCalledSTType, this);
			}
			if (retVal == null) {
				return null;
			}
			if (retVal.size() == 0) {
				retVal = ComprehensiveVisitCheck.getMatchingCalledMethodsInSomeSTType(this);
			}
			if (retVal.size() == 0) {
				STMethod anSTMethod = new AnSTMethod(callee, this.actuals.size());
				anSTMethod.setUnresolvedMethod(true);
				retVal.add(anSTMethod);
				
			} else if (retVal.size() > 1) {
				for (STMethod aMethod:retVal) {
					aMethod.setAmbiguouslyOverloadedMethods(true);
				}
			}
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
		
		return retVal;
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
	static STMethod[] emptySTMethodArray = {};
	@Override
	public STMethod[] getCalledSTMethods() {
//		if (callee.contains("reduce")) {
//			System.err.println("found reduce");
//		}
//		if (calledType.contains("Interr")) {
//			System.err.println("found Inter");
//		}
		if (stMethods == null) {
			STMethod[] anSTMethods = AnAbstractSTMethod.toSTMethods(this);
			if (anSTMethods == null) {
				return null;
			}
			if (anSTMethods.length <= 1) {
				stMethods = anSTMethods;
//				return stMethods;
			}
			else  {
				List<STMethod> anSTMethodsList = new ArrayList();
				for (STMethod anSTMethod:anSTMethods) {
					if (anSTMethod.isPublic()) {
						anSTMethodsList.add(anSTMethod);
					}
				}
				if (anSTMethodsList.size() > 1) {
				for (STMethod anSTMethod:anSTMethodsList) {
					
					anSTMethod.setAmbiguouslyOverloadedMethods(true);
				}
				}
				if (anSTMethods.length == anSTMethodsList.size()) {
					stMethods = anSTMethods;
				} else {
					stMethods = anSTMethodsList.toArray(emptySTMethodArray);
				}
			}
		}
		return stMethods;
	}
	@Override
	public boolean isConstructor() {
		return constructor;
	}
}
