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

public abstract  class ClassInstantiatedCheck extends ComprehensiveVisitCheck {
	// move this to a super class
	public static final String CALLER_TYPE_SEPARATOR = "#";
	public static final String MSG_KEY = "classInstantiated";



	protected Map<String, String[]> typeToSpecifications = new HashMap<>();
//	protected Map<String, List<STMethod>> typeToMethods = new HashMap<>();


//	public static final String SEPARATOR = ">";
	public int[] getDefaultTokens() {
		return new int[] {
				TokenTypes.CLASS_DEF
				};
	}

	// this should be in an abstract class
	public void setSpecificationsOfType(String aPattern) {
		String[] extractTypeAndSpecifications = aPattern.split(TYPE_SEPARATOR);
		String aType = extractTypeAndSpecifications[0].trim();
		String[] aSpecifications = extractTypeAndSpecifications[1].split(SET_MEMBER_SEPARATOR);
		
		typeToSpecifications.put(aType, aSpecifications);
//		typeToMethods.put(aType, signaturesToMethods(aSignaturesWithTarget));

	}

	/*
	 * @StructurePatternNames.LinePattern> X:int | Y:int | Width:int
	 * |Height:int,
	 * 
	 * @StructurePatternNames.OvalPatetrn> X:int | Y:int | Width:int |Height:int
	 */
	public void setExpectedInstantiations(String[] aPatterns) {
		for (String aPattern : aPatterns) {
			setSpecificationsOfType(aPattern);
		}
	}
	
	protected String msgKey() {
		return MSG_KEY;
	}
    // "fail" if method matches
	
	
	
	String specifiedType; // was global in pending check
	
	
	
// move to super type at some points
	public Boolean doPendingCheck(DetailAST anAST, DetailAST aTree) {

		specifiedType = null;
//		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
//				.getSTClassByShortName(
//						getName(getEnclosingTypeDeclaration(aTree)));
		STType anSTType = getSTType(aTree);

		if (anSTType.isEnum())
			return true;
		
		specifiedType = findMatchingType(typeToSpecifications.keySet(),
				anSTType);
		if (specifiedType == null)
			return true; // the constraint does not apply to us
		String[] aSpecifications = typeToSpecifications.get(specifiedType);
		boolean returnNull = false; 
		boolean returnValue = true;
		for (String aSpecification:aSpecifications) {
			String aCaller = null;
			STMethod aCallerSpecifiedMethod = null;
			String aType;
			String[] aCallerAndType = aSpecification.split(CALLER_TYPE_SEPARATOR);
			if (aCallerAndType.length == 2) {
				aCaller = aCallerAndType[0];
				aType = aCallerAndType[1];
				aCallerSpecifiedMethod = signatureToMethod(aCaller);
			} else {
				aCaller = null;
				aType = aSpecification;
			}
			List<STMethod> anInstantiatingMethods = anSTType.getInstantiatingMethods(specifiedType);
			if (anInstantiatingMethods == null) {
				returnNull = true;
				
				continue;
//				return null; // should never happen
			}
			if (anInstantiatingMethods.isEmpty()) {
				returnValue = false;
				log(anAST, aTree, aSpecification);
			}
			if (aCaller == null)
				return true;
			if (anInstantiatingMethods.size() > 0)
				log(anAST, aTree, aSpecification);
			STMethod anInstiatingMethod = anInstantiatingMethods.get(0);
			if (!matchesCallingMethod(anSTType, aCallerSpecifiedMethod, anInstiatingMethod)) {
				log(anAST, aTree, aSpecification);
			}
		}
		if (returnNull)
			return null;
		return returnValue;
	}
	
	public void doFinishTree(DetailAST ast) {
		// STType anSTType =
		// SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(fullTypeName);
		// for (STMethod aMethod: anSTType.getMethods()) {
		// visitMethod(anSTType, aMethod);
		// }
		maybeAddToPendingTypeChecks(ast);
		super.doFinishTree(ast);

	}

}
