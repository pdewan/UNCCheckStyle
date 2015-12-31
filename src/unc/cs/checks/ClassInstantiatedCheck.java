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
	public static final String MULTIPLE_INSTANTIATING_CODE = "multipleInstantiatingMethods";




	protected Map<String, String[]> typeToSpecifications = new HashMap<>();
//	protected Map<String, List<STMethod>> typeToMethods = new HashMap<>();


//	public static final String SEPARATOR = ">";
	public int[] getDefaultTokens() {
		return new int[] {
//				TokenTypes.PACKAGE_DEF,
//				TokenTypes.CLASS_DEF
				};
	}
//	public void visitClass(DetailAST ast) {
//		visitTypeMinimal(ast); //just get the name
//	}

	// this should be in an abstract class
	public void setSpecificationsOfType(String aPattern) {
		String[] extractTypeAndSpecifications = aPattern.split(TYPE_SEPARATOR);
		String aType = extractTypeAndSpecifications[0].trim();
		String[] aSpecifications = extractTypeAndSpecifications[1].split(SET_MEMBER_SEPARATOR);
		
		typeToSpecifications.put(aType, aSpecifications);
//		typeToMethods.put(aType, signaturesToMethods(aSignaturesWithTarget));

	}

	
	public void setInstantiations(String[] aPatterns) {
		for (String aPattern : aPatterns) {
			setSpecificationsOfType(aPattern);
		}
	}
	
	protected String msgKey() {
		return MSG_KEY;
	}
	
	
	
	String specifiedType; // was global in pending check
	
	protected boolean logOnNoMatch() {
		return true;
	}
	
// move to super type at some points
	public Boolean doPendingCheck(DetailAST anAST, DetailAST aTree) {
		specifiedType = null;
//		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
//				.getSTClassByShortName(
//						getName(getEnclosingTypeDeclaration(aTree)));
		STType anSTType = getSTType(aTree);

		if (anSTType.isEnum() || anSTType.isInterface())
			return true;
		
		specifiedType = findMatchingType(typeToSpecifications.keySet(),
				anSTType);
		if (specifiedType == null)
			return true; // the constraint does not apply to us
		String[] aSpecifications = typeToSpecifications.get(specifiedType);
		boolean returnNull = false; 
		boolean returnValue = true;
		for (String aSpecification:aSpecifications) {
//			int i = 0;
			String aCaller = null;
			STMethod aCallerSpecifiedMethod = null;
			String aType;
			String[] aCallerAndType = aSpecification.split(CALLER_TYPE_SEPARATOR);
			if (aCallerAndType.length == 2) {
				aCaller = aCallerAndType[0].trim();
				aType = aCallerAndType[1].trim();
				aCallerSpecifiedMethod = signatureToMethod(aCaller);
			} else {
				aCaller = null;
				aType = aSpecification.trim();
			}
			List<STMethod> anInstantiatingMethods = anSTType.getInstantiatingMethods(aType);
			if (anInstantiatingMethods == null || anInstantiatingMethods.contains(null)) {
				returnNull = true;
				
				continue;
//				return null; // should never happen
			}
			if (anInstantiatingMethods.isEmpty() && logOnNoMatch()) {
				returnValue = false;
				log(anAST, aTree, anSTType.getName(), aType);
				continue;
			} else if (anInstantiatingMethods.isEmpty() && !logOnNoMatch()) {
				// no instantiations, so success
				continue;
			}
			// so we know it is not empty now
			if (aCaller == null && logOnNoMatch()) 
				// we know it is not empty and caller is not specified, so success
				continue;
			
			if (aCaller == null && !logOnNoMatch()) {
				// not empty, caller is not specified, so failure
				returnValue = false;
				log(anAST, aTree, anSTType.getName(), aType);
				continue;
			}
			if (anInstantiatingMethods.size() != 1 && logOnNoMatch()) {
				// instantiated by more than one method, so failure if logOnNoMatch
				log(MULTIPLE_INSTANTIATING_CODE, anAST, aTree, anSTType.getName()+"." + aCaller, aType, anInstantiatingMethods.toString() );
				continue;
			}
			
			
			STMethod anInstiatingMethod = anInstantiatingMethods.get(0);
			Boolean aMatch = matchesCallingMethod(anSTType, aCallerSpecifiedMethod, anInstiatingMethod);
			if (aMatch == null) {
				returnNull = true;
				continue;
			}
			if (!aMatch && logOnNoMatch() || aMatch && !logOnNoMatch()) {
				log(anAST, aTree, anSTType.getName() + "." + aCaller, aType );
			} 
		}
		if (returnNull) {
//			flushLog();
			return null;
		}
		return returnValue;
	}
	
	public void doFinishTree(DetailAST ast) {
		
//		if (fullTypeName == null) {
//			return; // interface or come other non class
//		}
		maybeAddToPendingTypeChecks(ast);
		super.doFinishTree(ast);

	}

}
