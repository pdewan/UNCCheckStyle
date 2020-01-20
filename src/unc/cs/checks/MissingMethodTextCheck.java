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
import java.util.regex.Pattern;

import unc.cs.symbolTable.AnSTMethod;
import unc.cs.symbolTable.CallInfo;
import unc.cs.symbolTable.PropertyInfo;
import unc.cs.symbolTable.STMethod;
import unc.cs.symbolTable.STNameable;
import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.SymbolTableFactory;
import unc.tools.checkstyle.ProjectSTBuilderHolder;
/**
 * 
 * A warning thing, gives message if expected method not called.
 * Expected method call gives info message if expected method called
 *
 */
public  class MissingMethodTextCheck extends MissingMethodCallCheck {
	public static final String CALLER_TEXT_SEPARATOR = CALLER_TYPE_SEPARATOR;
	public static final String MSG_KEY = "missingMethodText";
//	public static final String WRONG_CALLER = "wrongCaller";

	
	
	public void setExpectedTexts(String[] aPatterns) {
		super.setExpectedStrings(aPatterns);
//		for (String aPattern : aPatterns) {
//			setExpectedSignaturesOfType(aPattern);
//		}

	}
    // "fail" if method matches
	

	
	@Override
	protected String msgKey() {
		return MSG_KEY;
	}
	/**
	 * For parsing, we can have overrident method.
	 * 
	 */
	protected boolean matchMethodToString(STMethod aCallingMethod, String aSpecifiedText, Pattern aSpecifiedPattern) {
		String aMethodText = toStringList(aCallingMethod.getAST());
//	    return aMethodText.matches(aSpecifiedText);
		return aSpecifiedPattern.matcher(aMethodText).matches();
	}
	protected Boolean processString(STType aCallingSTType, STMethod aCallingMethod, STMethod aCallingSpecifiedMethod, String aString, String aSpecifiedText, Pattern aSpecifiedPattern) {
		Boolean aMatch = matchesCallingMethod(aCallingSTType, aCallingSpecifiedMethod, aCallingMethod);
		if (aMatch == null) {
			return null;
		}
		if (!aMatch) {
			return aMatch;
		}
//		String aMethodText = toStringList(aCallingMethod.getAST());
	    Boolean retVal = matchMethodToString(aCallingMethod, aSpecifiedText, aSpecifiedPattern);
	    if (retVal) {
	    	return true;
	    }
	    
	    Set<STMethod> aCalledMethodsSet =aCallingMethod.getAllCalledMethods();
	    STMethod[] aCallerMethods = new STMethod[aCalledMethodsSet.size()];
	    aCalledMethodsSet.toArray(aCallerMethods);
	    
	    return processString(aCallingSTType, aCallerMethods, aString, aSpecifiedText, aCallingSpecifiedMethod, aSpecifiedPattern);
	}
	protected Boolean processString( STType anSTType, STMethod[] anSTMethods,  String aString, String aText, STMethod aSpecifiedMethod, Pattern aSpecifiedPattern) {
		Boolean retVal = false;
		for (STMethod anActualMethod:anSTMethods) {
			Boolean aMethodRetVal = processString(anSTType, anActualMethod, aSpecifiedMethod, aString, aText, aSpecifiedPattern);
			if (aMethodRetVal == null) {
				retVal = null;
				continue;
			}
			if (aMethodRetVal) {
				return true;
			}			
			
		}
		return retVal;
	}

	

	protected Boolean processString( STType anSTType, STMethod[] anSTMethods,  String aString) {
		String[] aCallerAndText = aString.split(CALLER_TYPE_SEPARATOR);
		boolean aMatchAnything = aCallerAndText.length == 1;
		String aText = aString;
		STMethod aSpecifiedMethod = null;
		Boolean retVal = false;
		if (!aMatchAnything) {
			aText = aCallerAndText[1];
			aSpecifiedMethod =  signatureToMethod(aCallerAndText[0]);
		}

		Pattern aSpecifiedPattern =  Pattern.compile(aText, Pattern.DOTALL);
		return processString(anSTType, anSTMethods, aString, aText, aSpecifiedMethod, aSpecifiedPattern);

//		for (STMethod anActualMethod:anSTMethods) {
//			Boolean aMethodRetVal = processString(anSTType, anActualMethod, aSpecifiedMethod, aString, aText, aSpecifiedPattern);
//			if (aMethodRetVal == null) {
//				retVal = null;
//				continue;
//			}
//			if (aMethodRetVal) {
//				return true;
//			}			
//			
//		}
//		return retVal;

		
		
	}
	
	
		
	/*
	 * Will ignore aSpecifiedType
	 */
	protected Boolean processStrings(DetailAST anAST, DetailAST aTree, STType anSTType, String aSpecifiedType, String[] aStrings) {
		STMethod[] aMethods = anSTType.getMethods();
		Boolean retVal = true;
		for (String aString:aStrings) {
			Boolean aStringCheck = processString(anSTType, aMethods, aString);
			if (aStringCheck == null) {
				retVal = null;
				continue;
			}
			if (!aStringCheck) {
				log(anAST, aTree, aString);

			}
			if (retVal != null) {
				retVal = retVal && aStringCheck;
			}
		}
		return retVal;
		
	
	}
	public Boolean doPendingCheck(DetailAST anAST, DetailAST aTree) {
		return doStringArrayBasedPendingCheck(anAST, aTree);
	}

//	public Boolean doPendingCheck(DetailAST anAST, DetailAST aTree) {
//		if (fullTypeName == null)
//			return true;
//		specifiedType = null;
////		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
////				.getSTClassByShortName(
////						getName(getEnclosingTypeDeclaration(aTree)));
////		int i = 0;
//		STType anSTType = getSTType(anAST);
//		
//		if (anSTType == null) {
//			System.out.println ("MissingMethodCall:Did not find sttype for " + fullTypeName + " " + toStringList(aTree));
//			return true;
//		}
//
//		if (anSTType.isEnum())
//			return true;
//		
////		specifiedType = findMatchingType(typeToSignaturesWithTargets.keySet(),
////				anSTType);
//		specifiedType = findMatchingType(typeToStrings.keySet(),
//				anSTType);
//		if (specifiedType == null)
//			return true; // the constraint does not apply to us
//
//		// maybe have a separate check for local calls?
//		List<CallInfo> aCalls = anSTType.getAllMethodsCalled();
//		// for efficiency, let us remove mapped calls
//
//
//		if (aCalls == null)
//			return null;
//		List<CallInfo> aCallsToBeChecked = new ArrayList(aCalls);
//
//		String[] aSpecifications = typeToSignaturesWithTargets.get(specifiedType);
//		boolean returnNull = false; 
////		int i = 0;
//		for (String aSpecification:aSpecifications) {
////			if (aSpecification.contains("say")) {
////				System.out.println ("found specification:");
////			}
//			boolean found = false;
//			for (CallInfo aCallInfo:aCallsToBeChecked ) {
//				String aNormalizedLongName = toLongName(aCallInfo.getNormalizedCall());
//				String shortMethodName = toShortTypeName(aNormalizedLongName);
////				if (aSpecification.contains("run") && aCallInfo.getCallee().contains("run")) {
////					System.out.println ("Found specification");
////				}
//
////				if (aCallInfo.toString().contains("move"))	{
////					System.out.println ("found move");
////				}
////				if (aCallInfo.toString().contains("sleep"))	{
////					System.out.println ("found sleep");
////				}
//				Boolean matches = matches(anSTType, maybeStripComment(aSpecification), shortMethodName, aNormalizedLongName, aCallInfo);
//
////				Boolean matches = matches(toShortTypeName(anSTType.getName()), aSpecification, shortMethodName, aNormalizedLongName, aCallInfo);
//				if (matches == null) {
//					//commenting out this part, perhaps something else will go wrong
////					if (!aSpecification.contains("!")) { // local call go onto another call, not really what is the difference?
////						continue;
////					}
//						
//					returnNull = true;
//					found = true; // we will come back to this
//					continue;
////					return null;
//				}
//				if (matches) {
//					found = true;
//					// same call may be made directly or indirectly, and can cause problems if removed
////					aCallsToBeChecked.remove(aCallInfo);
//					break;
//				}				
//			}
//			if (!found) {
////				if (aSpecification.contains("run")) {
////					System.out.println ("found specification");
////				}
//				log(anAST, aTree, aSpecification);
//			}
//			
//		}
//		if (returnNull)
//			return null;
//		return true;
//	}

}
