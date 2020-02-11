package unc.cs.checks;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import unc.cs.symbolTable.AnSTMethod;
import unc.cs.symbolTable.CallInfo;
import unc.cs.symbolTable.NoMethod;
import unc.cs.symbolTable.PropertyInfo;
import unc.cs.symbolTable.STMethod;
import unc.cs.symbolTable.STNameable;
import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.SymbolTableFactory;

public abstract  class MethodCallCheck extends MethodCallVisitedCheck {
	public static final String MSG_KEY = "expectedMethodCall";
	public static final String TYPE_SIGNATURE_SEPARATOR = "!"; // why not "."
	public static final String CALLER_TYPE_SEPARATOR = "#";
	public static final String TREE_REGEX_START = "%";
	public static final String CALLER_PARAMETER_SPECIFIER = "\\$";

	


//	protected Map<String, String[]> typeToSignaturesWithTargets = new HashMap<>();
//	protected Map<String, List<STMethod>> typeToMethods = new HashMap<>();

	
//	public static final String SEPARATOR = ">";
	

	// this should be in an abstract class
//	public void setExpectedSignaturesOfType(String aPattern) {
//		String[] extractTypeAndSignatures = aPattern.split(TYPE_SEPARATOR);
//		String aType = extractTypeAndSignatures[0].trim();
//		String[] aSignaturesWithTarget = extractTypeAndSignatures[1].split(SET_MEMBER_SEPARATOR);
//		trim (aSignaturesWithTarget);
//		typeToSignaturesWithTargets.put(aType, aSignaturesWithTarget);
////		typeToMethods.put(aType, signaturesToMethods(aSignaturesWithTarget));
//
//	}

	/*
	 * @StructurePatternNames.LinePattern> X:int | Y:int | Width:int
	 * |Height:int,
	 * 
	 * @StructurePatternNames.OvalPatetrn> X:int | Y:int | Width:int |Height:int
	 */
//	public void setExpectedCalls(String[] aPatterns) {
//		for (String aPattern : aPatterns) {
//			setExpectedSignaturesOfType(aPattern);
//		}
//
//	}
    // "fail" if method matches
	
//	protected abstract boolean returnValueOnMatch();
    String matchedSignature;
	@Override
	protected Boolean check(STType aCallingType, DetailAST ast,
			String aShortMethodName, String aLongMethodName, CallInfo aCallInfo) {
		matchedSignature = "";
//		String[] aSignaturesWithTargets = typeToSignaturesWithTargets.get(specifiedType);
		String[] aSignaturesWithTargets = typeToStrings.get(specifiedType);

		for (String aSignatureWithTarget:aSignaturesWithTargets) {
			
//			Boolean retVal = matches(toShortTypeName (aCallingType.getName()),aSignatureWithTarget, aShortMethodName, aLongMethodName, aCallInfo);
			Boolean retVal = matches(aCallingType, aSignatureWithTarget, aShortMethodName, aLongMethodName, aCallInfo);

			if (retVal == null)
				return null;
			if (retVal) {
				matchedSignature = aSignatureWithTarget;
				return returnValueOnMatch();
			}
		}
		return !returnValueOnMatch();
	}
	
//	public Boolean matchesCallingMethod (STType anSTType, STMethod aSpecifiedMethod, STMethod anActualMethod) {
//		int i = 0;
//		if (matchSignature(aSpecifiedMethod, anActualMethod)) // check if there is a direct call by the specified method
////		if (retVal)
//			return true;
//		// now go through the call graph and see if the specified method calls a method that matches the actual method
//		List<STMethod> aMatchingSpecifiedMethods = getMatchingMethods(anSTType, aSpecifiedMethod);
//		for (STMethod aRootMethod:aMatchingSpecifiedMethods) {
//			if (aRootMethod == null)
//				continue;
//			Boolean callsInternally = aRootMethod.callsInternally(anActualMethod);
//			if (callsInternally == null) {
//				continue;
//			}
//			if (callsInternally)
////			if (aRootMethod.callsInternally(anActualMethod))
//				return true;
////			List<STMethod> aCalledMethods = aRootMethod.getLocalCallClosure();
////			for (STMethod aCalledMethod:aCalledMethods) {
////				if (anActualMethod == aCalledMethod)
////					return true;
////			}			
//		}
//		return false;
//
//	}
	static StringBuffer aComposedString = new StringBuffer(300);

	public static String substituteParameters (String aSpecification, STMethod aCallingMethod) {
		if (aCallingMethod == null) 
			return aSpecification;
		List<String> aCallerFormals = Arrays.asList(aCallingMethod.getParameterNames());
		String aCallerName = aCallingMethod.getName();
		String[] aMethodAndParameters = aSpecification.split(CALLER_PARAMETER_SPECIFIER);
		aComposedString.setLength(0);
		aComposedString.append(aMethodAndParameters[0]);
		for (int anIndex = 1; anIndex < aMethodAndParameters.length; anIndex++) {
			String aParameterStart = aMethodAndParameters[anIndex];
			if (aParameterStart.length() == 0) {
				System.err.println(CALLER_PARAMETER_SPECIFIER + " missing an argument at position " + anIndex  + " in " + aSpecification);
				continue;
			}
			String aParameterText = aParameterStart.substring(0, 1);
			try {
				int aParameterNumber = Integer.parseInt(aParameterText);
				if (aParameterNumber > aCallerFormals.size()) {
					System.err.println(CALLER_PARAMETER_SPECIFIER + " missing a legal index at position " + anIndex + " in " + aSpecification);

				}
				if (aParameterNumber == 0) {
					aComposedString.append(aCallerName);
				} else {
				aComposedString.append(aCallerFormals.get(aParameterNumber - 1));
				}
				aComposedString.append(aParameterStart.substring(1));
			} catch (Exception e) {
				System.err.println(CALLER_PARAMETER_SPECIFIER + " missing an int argument at position " + anIndex + " in " + aSpecification);

			}
		}
		return aComposedString.toString();
		
	}
	public  boolean parameterMatches (List<DetailAST> aCalledActuals, String aRegex, int anActualIndex ) {
//		if (aCallerIndex < 0 || aCallerIndex >= aCallerFormals.size()) {
//			System.err.println ("Index " + aCallerIndex + " out of bounds in:" + aCallerFormals);
//			return false;
//		}
		if (anActualIndex >= aCalledActuals.size()) { // should happen only when doing all
			return false;
		}
//		String aCallerFormal = aCallerFormals.get(aCallerIndex);
		DetailAST aCalledActual = aCalledActuals.get(anActualIndex);
		String aCalledActualText = aCalledActual.toStringTree();
		String aCalledActualList = aCalledActual.toStringList();
		
		return aCalledActualText.matches(aRegex); // basically called is dome function of caller parameter
		
	}
	static StringBuffer treeText = new StringBuffer(4096);
	protected boolean parametersMatch(STMethod aCallingMethod, CallInfo aCallInfo, String aSpecifiedParameters) {
		if (aSpecifiedParameters == null )
			return true;
//		List<String> aCallerFormals = Arrays.asList(aCallingMethod.getParameterNames());
		List<DetailAST> aCalledActuals = aCallInfo.getActuals();
		StringBuffer aCalledActualsText = new StringBuffer();
		treeText.setLength(0);
		for (int actualIndex = 0; actualIndex < aCalledActuals.size(); actualIndex++) {
			treeText.append(aCalledActuals.get(actualIndex).toStringTree());
		}
		return treeText.toString().matches(aSpecifiedParameters);
	}
		
	
	
	protected boolean incrementalParametersMatch(STMethod aCallingMethod, CallInfo aCallInfo, List<String> aSpecifiedParameters) {
		if (aSpecifiedParameters == null || aSpecifiedParameters.size() == 0)
			return true;
//		List<String> aCallerFormals = Arrays.asList(aCallingMethod.getParameterNames());
		List<DetailAST> aCalledActuals = aCallInfo.getActuals();
		for (int actualIndex = 0; actualIndex < aSpecifiedParameters.size(); actualIndex++) {
			String aSpecifiedParameterText = aSpecifiedParameters.get(actualIndex);
			try {
//				int aCallerIndex = Integer.parseInt(aSpecifiedParameterText) - 1; // countparams from 1 onwards
				if (!parameterMatches(aCalledActuals, aSpecifiedParameterText, actualIndex))
						return false;
				
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println ("Specified parameter is not a number: " + aSpecifiedParameterText);
			}			
		}
		return true;
		
	}
	
	protected  Boolean matches (STType aCallingSTType, String aSpecifier, String aShortMethodName,
			String aLongMethodName, CallInfo aCallInfo) {
		String aCallingType = toShortTypeOrVariableName(aCallingSTType.getName());
		String[] aCallerAndRest = aSpecifier.split(CALLER_TYPE_SEPARATOR);
		String aCaller = MATCH_ANYTHING;
		String aNonTargetPart = aSpecifier;
//		List<String> aCalledParameters = null;

		if (aCallerAndRest.length == 2) {
			aCaller = aCallerAndRest[0].trim();
			aNonTargetPart = aCallerAndRest[1];
		}
			

		STMethod aCallingMethod = aCallInfo.getCallingMethod();

		STMethod aCallingSpecifiedMethod = signatureToMethod(aCaller);

		Boolean aMatch = matchesCallingMethod(aCallingSTType, aCallingSpecifiedMethod, aCallingMethod);
		if (aMatch == null) {
			return null;
		}
		if (!aMatch) {

//		if (!matchesCallingMethod(aCallingSTType, aCallingSpecifiedMethod, aCallingMethod)) {
			return false;
		}
		String anEvaluatedNonTargetPart = substituteParameters(aNonTargetPart, aCallingMethod);
		String[] aNameParts = anEvaluatedNonTargetPart.split(TREE_REGEX_START);
		String aSignatureWithTarget = aNameParts[0];
		String aParametersText = null;
		if (aNameParts.length > 1) {
			aParametersText = aNameParts[1];
		}
//		if (aNameParts.length > 1) {
//			aCalledParameters = new ArrayList<>();
//			for (int i = 1; i < aNameParts.length; i++) {
//				aCalledParameters.add(aNameParts[i]);
//			}							
//		}		
		
//		STMethod  aCallingMatchingMethod = getMatchingMethod(aCallingSTType, aCallingSpecifiedMethod);

		String[] aSignatureAndTarget = aSignatureWithTarget.split(TYPE_SIGNATURE_SEPARATOR);
		String aSignature ;
		String aSpecifiedTarget;
		String aCalledType = aCallInfo.getCalledType();
		if (aSignatureAndTarget == null ) {
			
			System.err.println ("Null signature!");
			return false;
		}
		if (aSignatureAndTarget.length == 0 ) {
			
			System.err.println ("signature with no elements");
			return false;
		}
		
		if (aSignatureAndTarget.length == 1 ) {
			
//			System.out.println ("signature with only one element");
			aSignature = aSignatureAndTarget[0];
			if (aCalledType.equals("super")) {
				aSpecifiedTarget = "super"; // so we can match super with either super or the called type
			} else {
			String[] dotSplit = aSignature.split("\\.");
			if (dotSplit.length > 1) {
				aSpecifiedTarget = dotSplit[0]; // consistent with call info
			} else {
			aSpecifiedTarget = aCallingType;
			}
			}
//			aSpecifiedTarget = aCallInfo.getCalledType(); // assuming local call
			// the following moved to below
//			if (aSpecifiedTarget.contains("]") || 
//					aSpecifiedTarget.contains("[") ||
//					aSpecifiedTarget.contains("(") ||
//					aSpecifiedTarget.contains(")"))
//				return false;
//			return false;
		}
//		if (aSignatureAndTarget.length < 2 ||
//				aSignatureAndTarget[1] == null ) {
//			
//			System.out.println ("Null signature");
//			return false;
//		}
//		String aSignature = aSignatureAndTarget[1].trim();
//		String aSpecifiedTarget = aSignatureAndTarget[0].trim();
		else {
		aSignature = aSignatureAndTarget[1].trim();
		aSpecifiedTarget = aSignatureAndTarget[0].trim();
		}
//		if (aCalledType.contains("]") || // array element
//				aCalledType.contains("[") ||
//				aCalledType.contains("(") ||// casts
//				aCalledType.contains(")"))
////			return false;
//			return true; // assume the type is right, 
		STMethod aSpecifiedMethod = signatureToMethod(aSignature);
		Boolean result = matches(aSpecifiedTarget, aSpecifiedMethod, aShortMethodName, aLongMethodName, aCallInfo);
//		if (result == null) {
//			System.out.println ("Null result, comment ths out");
//		}
//		if (aCalledParameters == null)
//			return result;
		if (aParametersText == null)
			return result;
		
		if (result != true)
			return result;

//		return incrementalParametersMatch(aCallingMethod, aCallInfo, aCalledParameters);

		return parametersMatch(aCallingMethod, aCallInfo, aParametersText);

	
		
	}
	protected void log(DetailAST ast, DetailAST aTreeAST, String aShortMethodName,
			String aLongMethodName, CallInfo aCallInfo) {
			log(ast, aTreeAST, specifiedType + " = " + matchedSignature);
			
		}
//	public Boolean matchSignature(
//			STMethod aSpecification, STMethod aMethod) {
//		variablesAdded.clear();
//		String aReturnType = aSpecification.getReturnType();
//		STNameable[] typeTags = null;
//		if (aReturnType != null && aReturnType.startsWith(TAG_STRING)) {
//			
//			STType aReturnSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aReturnType.substring(1));
//			if (aReturnSTType == null)
//				return null;
//			typeTags = aReturnSTType.getComputedTags();
//		}
//		
//		if (aMethod == null) {
//			System.err.println("Null method name");
//			return true;
//		}
//		
//		Boolean retVal  = 
////				aSpecification.getParameterTypes().length == aMethod.getParameterTypes().length &&
//				matchesNameVariableOrTag(
//						aSpecification.getName(), 
//						aMethod.getName(), 
//						aMethod.getComputedTags()) &&
//				(aReturnType== null ||
//				matchesNameVariableOrTag(aReturnType, aMethod.getReturnType(), typeTags)
//
////				matchesNameVariableOrTag(aSpecification.getReturnType(), aMethod.getReturnType(), typeTags)
//				);
//				
//		if (!retVal) {
//			backTrackUnification();
//			return false;
//		}
//		String[] aSpecificationParameterTypes = aSpecification.getParameterTypes();
//		String[] aMethodParameterTypes = aMethod.getParameterTypes();
//		
//		if (aSpecificationParameterTypes == null)
//			return true;
//		if (aSpecificationParameterTypes.length == 1) {
//			if (aSpecificationParameterTypes[0].equals(MATCH_ANYTHING))
//				return true;
//		}
//		if (aSpecificationParameterTypes.length != aMethodParameterTypes.length) {
//			return false;
//		}
//		for (int i = 0; i < aSpecificationParameterTypes.length; i++) {
//			
//			String aParameterType = aSpecificationParameterTypes[i];
//
//			STNameable[] parameterTags =null;
//			if (aParameterType.startsWith(TAG_STRING)) {
//				
//				STType aParameterSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aParameterType.substring(1));
//				if (aParameterSTType == null)
//					return null;
//				parameterTags = aParameterSTType.getComputedTags();
//			}
//			
//			if (!matchesNameVariableOrTag(aSpecificationParameterTypes[i], aMethodParameterTypes[i], parameterTags)) {
////				backTrackUnification();
//				return false;
//			}
//		}
//		return true;		
//		
//	}
	protected static boolean isStarParameters(String[] aParameters) {
		if (aParameters == null)
			return true;
		return aParameters.length == 1 && aParameters[0].equals(MATCH_ANYTHING);
	}
	static STMethod noMethod = new NoMethod();
//	protected List<STMethod> getMatchingMethods(STType aTargetSTType, STMethod aSpecifiedMethod) {
//		List<STMethod> result = new ArrayList();
//		STMethod[] aMethods = aTargetSTType.getMethods();
//		if (aMethods == null)
//			return null;
//		boolean hadNullMatch = false;
//		for (STMethod anSTMethod:aMethods) {
//			Boolean aMatch = matchSignature(aSpecifiedMethod, anSTMethod);
//			if (aMatch == null) {
//				hadNullMatch = true;
//				continue;
//			}
//				
////			if (!matchSignature(aSpecifiedMethod, anSTMethod))
//
//			if (!aMatch)
//				continue;
//			result.add(anSTMethod);
////			if (anSTMethod.getName().equals(aCallInfo.getCalleee()) && 
////					anSTMethod.getParameterTypes().length == aCallInfo.getActuals().size()) {
////				return hasTag(anSTMethod, aSpecifiedMethod.getName());
////			}
//		}
//		if (hadNullMatch)
//			return null; // either way we do not know if something bad happened
//		return result;
//		
//	}
	protected Boolean starTargetAndMethodNameMatch (String aSpecifiedTarget,  STMethod aSpecifiedMethod, String aShortMethodName,
			String aLongMethodName, CallInfo aCallInfo) {
		if (!aSpecifiedTarget.contains("*")) {
			return false;			
		}
		return nameOrImpliedTagMethodMatch(aSpecifiedMethod.getName(), aShortMethodName);

	}
	protected Boolean nameOrImpliedTagMethodMatch (String aSpecifiedMethod, String anActualMethod ) {
		String aNormalizedMethodName = anActualMethod;
		if (aSpecifiedMethod.startsWith(TAG_STRING)) {
			aNormalizedMethodName = TAG_STRING + anActualMethod;
			
		}
		return aSpecifiedMethod.equalsIgnoreCase(aNormalizedMethodName);
	}
	protected Boolean matches (String aSpecifiedTarget,  STMethod aSpecifiedMethod, String aShortMethodName,
			String aLongMethodName, CallInfo aCallInfo) {
		
//		int i = 0;
//		String aRegex = "(.*)" + aSpecifiedMethod.getName() + "(.*)";
//		String aRegex = aSpecifiedMethod.getName();
		if ( !isStarParameters(aSpecifiedMethod.getParameterTypes()) &&
				aCallInfo.getActuals().size() != aSpecifiedMethod.getParameterTypes().length)
			return false;
		String aTypeName = aCallInfo.getCalledType();
		if (aTypeName == null || aTypeName.contains(("["))) {
			return null;
		}
		
		 if (!aSpecifiedMethod.getName().startsWith(TAG_STRING) && !aSpecifiedTarget.startsWith(TAG_STRING)) { // we do not need to determine tags
			 if (aCallInfo.getNormalizedCall().length > 2) { // handling system.out.println
//					return aLongMethodName.toLowerCase().matches(aSpecifiedMethod.getName());
					return aLongMethodName.matches(aSpecifiedMethod.getName());

				}
			 Boolean retVal = aShortMethodName.matches(aSpecifiedMethod.getName());
			 if (!retVal) {
				 return  false;
			 }
			 return matchTypeISA(aSpecifiedTarget, toShortTypeOrVariableName(aTypeName));

		 }
		 Boolean matchesType = matchesTypeUnifying(aSpecifiedTarget, aTypeName);
		 if (matchesType == null)
			 return null;
		 if (!matchesType) {
				 return false;
		 }

		 if (!aSpecifiedMethod.getName().startsWith(TAG_STRING) ) { // we do not need to determine method tags
			 return aShortMethodName.matches(aSpecifiedMethod.getName()); // not checking for parameters?
		 }
		
		if (!isIdentifier(aTypeName)) { // (cast etc,could not match, assume internal call)
			aTypeName = aSpecifiedTarget;
		}
		// this is dangerous as one might have star and non star text in specification
		if (aTypeName.contains("*")) {
			return (aCallInfo.getCallee().equals(aSpecifiedMethod.getName())) ;
		}
//		if (starTargetAndMethodNameMatch(aSpecifiedTarget, aSpecifiedMethod, aShortMethodName, aLongMethodName, aCallInfo)) {
//			return aSpecifiedMethod.getParameterTypes().length == aCallInfo.getCallerParameterTypes().size(); // crude check
//		}
		STType aTargetSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aTypeName);
//		if (aTargetSTType == null && !aSpecifiedTarget.equalsIgnoreCase(MATCH_ANYTHING)) {
		
//		//KLUDGE!!!!! False Negatives
//
//		if (aSpecifiedTarget.equals(MATCH_ANYTHING) || aSpecifiedTarget.equals(".*") || aSpecifiedTarget.equalsIgnoreCase("(.*)")) {
//			return aSpecifiedMethod.getName().equals(aCallInfo.getCallee()) &&
//					aSpecifiedMethod.getParameterTypes().length == aCallInfo.getActuals().size();
//					
//			
//		}
		if (aTargetSTType == null) {
			
			return null;
		}
		STMethod[] aMethods = aTargetSTType.getMethods();
		if (aMethods == null)
			return null;
		boolean hadNullMatch = false;
		for (STMethod anSTMethod:aMethods) {
			Boolean aMatch = matchSignature(aSpecifiedMethod, anSTMethod);
			if (aMatch == null) {
				hadNullMatch = true;
				continue;
			}
				
//			if (!matchSignature(aSpecifiedMethod, anSTMethod))

			if (!aMatch)
				continue;
			if (anSTMethod.getName().equals(aCallInfo.getCallee()) && 
					anSTMethod.getParameterTypes().length == aCallInfo.getActuals().size()) {
				return hasTag(anSTMethod, aSpecifiedMethod.getName()); // do we need this, does matchSignature not do this already
			}
		}
		if (hadNullMatch)
			return null; // either way we do not know if something bad happened
		// we land here when the target type was wrong because of a multi level method call
		if (starTargetAndMethodNameMatch(aSpecifiedTarget, aSpecifiedMethod, aShortMethodName, aLongMethodName, aCallInfo)) {
			return aSpecifiedMethod.getParameterTypes().length == aCallInfo.getCallerParameterTypes().size(); // crude check
		}
		return false;
		

//		return aShortMethodName.matches(aSpecifiedMethod.getName());
		
	
		
	}
	
	String specifiedType;
	/**
	 * This method should never be called and be overridden
	 */
	public Boolean doPendingCheck(DetailAST anAST, DetailAST aTree) {
		specifiedType = null;

		STType anSTType = getSTType(aTree);

		if (anSTType.isEnum() || anSTType.isInterface())
			return true;
		
//		specifiedType = findMatchingType(typeToSignaturesWithTargets.keySet(),
		specifiedType = findMatchingType(typeToStrings.keySet(),

				anSTType);
		if (specifiedType == null)
			return true; // the constraint does not apply to us
		return super.doPendingCheck(anAST, aTree);
	}
	@Override
	protected String msgKey() {
		return MSG_KEY;
	}
	public static void main (String[] args) {
		String[] split1 = "hello#dolly".split("#");
		String[] split2 = "hello#dolly".split("\\#");
	}

}
