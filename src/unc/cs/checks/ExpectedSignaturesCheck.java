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
import unc.cs.symbolTable.PropertyInfo;
import unc.cs.symbolTable.STMethod;
import unc.cs.symbolTable.STNameable;
import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.SymbolTableFactory;
import unc.tools.checkstyle.ProjectSTBuilderHolder;

public  class ExpectedSignaturesCheck extends ComprehensiveVisitCheck {
	public static final String MSG_KEY = "expectedSignatures";

//	protected Map<String, String[]> typeToStrings = new HashMap<>();
//	protected String[] strings;
	protected Map<String, List<STMethod>> typeToMethods = new HashMap<>();
	protected List<STMethod> methods;
	@Override
	// get full name
	public int[] getDefaultTokens() {
		return new int[] {
				 TokenTypes.PACKAGE_DEF,
				TokenTypes.CLASS_DEF,
//				TokenTypes.INTERFACE_DEF,

				};

	}



	// this should be in an abstract class
//	public void setExpectedSignaturesOfType(String aPattern) {
//		super.setExpectedStringsOfType(aPattern);
	
//		String[] extractTypeAndSignatures = aPattern.split(TYPE_SEPARATOR);
//		String aType = extractTypeAndSignatures[0].trim();
//		if (extractTypeAndSignatures.length < 2) {
//			System.err.println ("Illegal pattern:" + aPattern + " does not have: " + TYPE_SEPARATOR);
//			return;
//		}
//		String[] aSignatures = extractTypeAndSignatures[1].split(SET_MEMBER_SEPARATOR);
//		trim(aSignatures);
//		typeToStrings.put(aType, aSignatures);
//		typeToMethods.put(aType, signaturesToMethods(aSignatures));
//	}
	@Override
	protected void setStrings(String[] aStrings) {
		super.setStrings(aStrings);
		methods = signaturesToMethods(aStrings);
	}
	
	@Override
	protected void setExpectedStringsOfType(Map<String, String[]> aTypeToProperties, String aType, String[] aStrings) {
		super.setExpectedStringsOfType(aTypeToProperties, aType,  aStrings);
		typeToMethods.put(aType, signaturesToMethods(aStrings));

	}
	public void setExpectedSignatures(String[] aSignatures) {
		super.setExpectedStrings(aSignatures);
	}
	
//	public void setExpectedSignatures(String[] aPatterns) {
//		for (String aPattern : aPatterns) {
//			setExpectedSignaturesOfType(aPattern);
//		}
//
//	}

	
	protected void logSignatureNotMatched(DetailAST anAST, DetailAST aTreeAST, String aSignature) {
//		String aSourceName = shortFileName(astToFileContents.get(aTreeAST)
//				.getFilename());
//		if (aSignature.contains("map")) {
//			System.err.println("found map signature");
//		}
		String aTypeName = getName(getEnclosingTypeDeclaration(anAST));
		super.log(anAST, aTreeAST, aSignature, aTypeName);
//		if (aTreeAST == currentTree) {
//			DetailAST aLoggedAST = aTreeAST;
//			log(aLoggedAST.getLineNo(),  msgKey(), aSignature, aTypeName, aSourceName);
//
//		} else {
//			log(0, msgKey(), aSignature, aTypeName, aSourceName);
//		}

	}

	public Boolean matchSignatures(String[] aSpecifications,
			STMethod[] aMethods, DetailAST aTypeAST, DetailAST aTreeAST) {
		boolean retVal = true;
		for (String aSpecification : aSpecifications) {
		
//			String[] aPropertiesPath = aPropertySpecification.split(".");			
			if (!matchSignature(aSpecification, aMethods)) {
				logSignatureNotMatched(aTypeAST, aTreeAST, aSpecification);
				retVal = false;
			}
		}
		return retVal;
	}
	public Boolean matchMethods(List<STMethod> aSpecifications,
			STMethod[] aMethods, DetailAST aTypeAST, DetailAST aTreeAST) {
		boolean retVal = true;
		List<STMethod> aMethodsCopy = new ArrayList<STMethod>(Arrays.asList(aMethods));
		for (STMethod aSpecification : aSpecifications) {
//			if (aSpecification.getName().contains("remote")) {
//				System.err.println("found method specified");
//			}
//			String[] aPropertiesPath = aPropertySpecification.split(".");	
			Boolean hasMatched = matchMethod(aSpecification, aMethodsCopy);
			if (hasMatched == null)
				return null;
			if (!hasMatched) {
				String anOriginalSignature = methodToSignature.get(aSpecification);
				if (anOriginalSignature == null) {
					anOriginalSignature = aSpecification.getSignature();
				}
//				if (anOriginalSignature.contains("Remote")) {
//					System.err.println("Found remote");
//				}
				logSignatureNotMatched(aTypeAST, aTreeAST, 
						anOriginalSignature
//						aSpecification.getSignature()
						);
				retVal = false;
			} 
		}
		return retVal;
	}

	
	public Boolean matchSignature(
			String aSpecification, STMethod[] aMethods) {
//		if (aSpecification.contains("parseSleep")) {
//			System.out.println("found method specified");
//		}
		for (STMethod aMethod : aMethods) {
			if (matchSignature(aSpecification, aMethod))
				// return
				// aSpecifiedType.equalsIgnoreCase(aPropertyInfos.get(aProperty).getGetter().getReturnType());
				return true;

			else 
				continue;
		}
		return false;
	}
	public Boolean matchMethod(
			STMethod aSpecification, List<STMethod> aMethods) {
		for (STMethod aMethod : aMethods) {
//			if (aMethod.getName().contains("parseSleep")) {
//				System.out.println ("found candidate method");
//			}
			Boolean hasMatched = matchSignature(aSpecification, aMethod);
			if (hasMatched == null)
				return null;
			if (hasMatched) {
				aMethods.remove(aMethod);
				// return
				// aSpecifiedType.equalsIgnoreCase(aPropertyInfos.get(aProperty).getGetter().getReturnType());
				return true;

			} else 
				continue;
		}
		return false;
	}
	public Boolean matchMethod(
			List<STMethod> aSpecifications, STMethod aMethod) {
		for (STMethod aSpecification : aSpecifications) {
			if (matchSignature(aSpecification, aMethod))
				// return
				// aSpecifiedType.equalsIgnoreCase(aPropertyInfos.get(aProperty).getGetter().getReturnType());
				return true;

			else 
				continue;
		}
		return false;
	}
	public Boolean matchSignature(
			String aSpecification, STMethod aMethod) {
//		return matchSignature(signatureToMethod(aSpecification), aMethod);
		return matchSignature(signatureToMethodorOrConstructor(aSpecification), aMethod);

		
	}
//	public  STMethod signatureToMethodorOrConstructor(String aSignature) {
//		return signatureToMethod(aSignature);
//	}
	boolean isMatchAnyting (Object[] aList) {
//		return aList == null || ( aList != null && aList.length == 1 && MATCH_ANYTHING.equals(aList[0]);

		return aList == null || 
				(aList.length == 1 && MATCH_ANYTHING.equals(aList[0]));
	}
	


	
	public Boolean matchParametersGeneral(STMethod aSpecification, STMethod aMethod) {
		if (isMatchAnyting(aSpecification.getParameterTypes())) {
			return true;
		}
		if (aSpecification.getParameterTypes().length != aMethod.getParameterTypes().length) {
			return false;
		}
		
		return matchParameters(aSpecification, aMethod);
		
	}

	public Boolean matchSignature(
			STMethod aSpecification, STMethod aMethod) {
		variablesAdded.clear();
		
		Boolean aNameMatch = unifyingMatchesNameVariableOrTag(aSpecification.getName(), aMethod.getName(), aMethod.getComputedTags());
		if (aNameMatch == null)
			return null;
		if (!aNameMatch) {
			backTrackUnification();
			return false;
		}
//		if (aSpecification.getName().contains("arse")) {
//			System.out.println ("Found method to be matched");
//		}
		Boolean aMatchParameters = matchParametersGeneral(aSpecification, aMethod);
		if (aMatchParameters == null) {
			return null;
		}
		if (!aMatchParameters) {
			return  false;
		}
		Boolean aMatchRetVal = matchReturnTypeISA(aSpecification, aMethod);
		if (aMatchRetVal == null) {
			return null;
		}
		if (!aMatchRetVal) {
			backTrackUnification();
			return false;
		}
		return true;
		

		
	}
	public Boolean matchParameters(
			STMethod aSpecification, STMethod aMethod) {		
		String[] aSpecificationParameterTypes = aSpecification.getParameterTypes();
		String[] aMethodParameterTypes = aMethod.getParameterTypes();
		for (int i = 0; i < aSpecificationParameterTypes.length; i++) {
			String aParameterType = aSpecificationParameterTypes[i];

			STNameable[] parameterTags =null;
			if (aParameterType.startsWith(TAG_STRING)) {
				
//				STType aParameterSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aParameterType.substring(1));
				STType aParameterSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aMethodParameterTypes[i]);

				if (aParameterSTType == null)
					return null;
				parameterTags = aParameterSTType.getComputedTags();
			}
			
			if (!unifyingMatchesNameVariableOrTag(aSpecificationParameterTypes[i], aMethodParameterTypes[i], parameterTags)) {
				if (!matchISA(aSpecificationParameterTypes[i], aMethodParameterTypes[i])) {
				backTrackUnification();
				return false;
				}
			}
		}
		return true;		
		
	}
	
	protected STMethod[] getMatchedMethods(STType anSTType) {
		return anSTType.getMethods();
	}
	
	public Boolean matchSignatures(STType anSTType, String[] aSpecifiedSignatures, DetailAST aTree) {
//		STMethod[] aMethods = anSTType.getMethods();
		STMethod[] aMethods = getMatchedMethods(anSTType);
		if (aMethods == null) 
			return null;
		return matchSignatures(aSpecifiedSignatures, aMethods, anSTType.getAST(), aTree);
	}

	public Boolean matchMethods(STType anSTType, List<STMethod> aSpecifiedSignatures, DetailAST aTree) {
//		STMethod[] aMethods = anSTType.getMethods();
		STMethod[] aMethods = getMatchedMethods(anSTType);
		if (aMethods == null) 
			return null;
		return matchMethods(aSpecifiedSignatures, aMethods, anSTType.getAST(), aTree);
	}
	public Boolean matchSignatures(String aTypeName, String[] aSpecifiedSignatures, DetailAST aTree) {
		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
				.getSTClassByShortName(
						getName(getEnclosingTypeDeclaration(aTree)));
		return matchSignatures(anSTType, aSpecifiedSignatures, aTree);
	}



	public Boolean doPendingCheck(DetailAST anAST, DetailAST aTree) {
		return super.doStringArrayBasedPendingCheck(anAST, aTree);

//		STType anSTType;
//		
//		if (ProjectSTBuilderHolder.getSTBuilder().getVisitInnerClasses()) {
//			int i = 0;
//			anSTType = getSTType(anAST);
//		} else {
//			anSTType =getSTType(aTree);
//		}
//		if (anSTType == null) {
//			System.out.println ("Did not find sttype for " + anAST);
//			return true;
//		}
//		if (anSTType.isEnum() || anSTType.isInterface()) // do not want to tag interface methods
//			return true;
////		if (!visitType(anSTType)) {
////			return true;
////		}
////		if (anSTType.toString().contains("ss")) {
////			System.out.println ("Found Ass");
////		}
//		// this is redundant based on above check,but let us keep
//		if (!doCheck(anSTType)) {
//			return true;
//		}
//		
////		String aSpecifiedType = findMatchingType(typeToSignatures.keySet(),
////				anSTType);
//		List<String> aSpecifiedTypes = findMatchingTypes(typeToStrings.keySet(),
//				anSTType);
//		
////		if (aSpecifiedType == null)
////			return true; // the constraint does not apply to us
//		if (aSpecifiedTypes == null)
//			return true; // the constraint does not apply to us
////		List<STMethod> aSpecifiedSignatures = typeToMethods.get(aSpecifiedType);
////		return matchMethods(anSTType, aSpecifiedSignatures, aTree);
//		Boolean retVal = true;
//		for (String aSpecifiedType : aSpecifiedTypes) {
//			List<STMethod> aSpecifiedSignatures = typeToMethods
//					.get(aSpecifiedType);
//			Boolean aCurrentMatch = matchMethods(anSTType,
//					aSpecifiedSignatures, aTree);
//			if (retVal != null) {
//				retVal = aCurrentMatch == null ? null : aCurrentMatch && retVal;
//
//			}
//		}
//		return retVal;
	}
	@Override
	protected Boolean processStrings(DetailAST anAST, DetailAST aTree, STType anSTType, String aSpecifiedType, String[] aStrings) {
		 List<STMethod> aSpecifiedSignatures = methods;
		 if (methods == null) {
			 aSpecifiedSignatures = typeToMethods
						.get(aSpecifiedType);
		 }
		
//		   List<STMethod> aSpecifiedSignatures = typeToMethods
//					.get(aSpecifiedType);

			return matchMethods(anSTType,
					aSpecifiedSignatures, aTree);
			
		
	}

	@Override
	protected String msgKey() {
		// TODO Auto-generated method stub
		return MSG_KEY;
	}
	@Override
	public void leaveType(DetailAST ast) {
		if (ProjectSTBuilderHolder.getSTBuilder().getVisitInnerClasses()) {
			maybeAddToPendingTypeChecks(ast);
		}
		super.leaveType(ast);
	}
	public void doFinishTree(DetailAST ast) {
		// STType anSTType =
		// SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(fullTypeName);
		// for (STMethod aMethod: anSTType.getMethods()) {
		// visitMethod(anSTType, aMethod);
		// }
		if (!ProjectSTBuilderHolder.getSTBuilder().getVisitInnerClasses()) {

		maybeAddToPendingTypeChecks(ast);
		}
		super.doFinishTree(ast);

	}
	@Override
	public void beginTree(DetailAST ast) {
		super.beginTree(ast);
	}




}
