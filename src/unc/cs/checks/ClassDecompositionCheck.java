package unc.cs.checks;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import unc.cs.symbolTable.PropertyInfo;
import unc.cs.symbolTable.STMethod;
import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.SymbolTableFactory;

public  class ClassDecompositionCheck extends ComprehensiveVisitCheck {
	public static final String MSG_KEY = "classDecomposition";
	

	public int[] getDefaultTokens() {
		return new int[] {
//				
				};
	}



//	
//	public void setExpectedGlobals(String[] aPatterns) {
//		setExpectedTypesAndSpecifications(aPatterns);
////		for (String aPattern : aPatterns) {
////			setExpectedSpecificationOfType(aPattern);
////		}
//
//	}

	
	protected void logMultipleCoupledSets(DetailAST aTreeAST, List<Set<STMethod>> aFinalSets ) {
		log (aTreeAST, aFinalSets.toString());

	}

	

	protected void initializeUncoupledSets(STType anSTType, List<Set<STMethod>> anUncoupledMethods) {
		STMethod[] anSTMethods = anSTType.getDeclaredMethods();
		anUncoupledMethods.clear();
		Set<STMethod> aSeedSet = new HashSet(); 
		anUncoupledMethods.add(aSeedSet);
		for (STMethod anSTMethod: anSTMethods) {
			if (!anSTMethod.isInstance()) continue;
//			if (anSTMethod.isGetter() || anSTMethod.isSetter()) {
			if (ignoredMethod(anSTMethod)) {
				continue;
			}
			if (isSeedMethod(anSTMethod)) {

				aSeedSet.add(anSTMethod);
				continue;
			}
			Set<STMethod> aSet = new HashSet();
			aSet.add(anSTMethod);
			anUncoupledMethods.add(aSet);
		}
	}
	
	protected boolean mergeUncoupledSets(List<Set<STMethod>> anUncoupledMethods) {
		boolean aMatched = false;
		for (int anIndex1 = 0; anIndex1 < anUncoupledMethods.size(); anIndex1++) {
			if (mergeUncoupledSets(anUncoupledMethods, anIndex1)) {
				aMatched = true;
			}
		}
		return aMatched;
		
	}
	protected boolean mergeUncoupledSets(List<Set<STMethod>> anUncoupledMethods, int anIndex1) {
		Set<STMethod> aSet1 = anUncoupledMethods.get(anIndex1);
		if (aSet1.isEmpty()) {
			return false;
		}
		boolean merged = false;
		for (int anIndex2 = anIndex1 + 1; anIndex2 < anUncoupledMethods.size(); anIndex2++) {
			Set<STMethod> aSet2 = anUncoupledMethods.get(anIndex2);
			if (accessCommonVariables(aSet1, aSet2)) {
				aSet1.addAll(aSet2);
				aSet2.clear();	
				merged = true;
			}
		}
		return merged;
		
	}
	
	protected List<Set<STMethod>> findUncoupledSets (STType anSTType) {
		List<Set<STMethod>> anUncoupledMethodSets = new ArrayList();
		initializeUncoupledSets(anSTType, anUncoupledMethodSets);
		while (true) {
			boolean aMerged = mergeUncoupledSets(anUncoupledMethodSets);
			if (!aMerged) {
				break;
			}
		}
		List<Set<STMethod>> aFinalSets = new ArrayList();
		for (Set<STMethod> aSet:anUncoupledMethodSets) {
			if (!aSet.isEmpty()) {
				aFinalSets.add(aSet);
			}
		}
		return aFinalSets;
		

	}
	
	protected boolean accessCommonVariables (Set<STMethod> aSet1, Set<STMethod> aSet2) {
		for (STMethod aMethod1:aSet1) {
			if (accessCommonVariables(aMethod1, aSet2))
				return true;
		}
		return false;
	}
	
	protected boolean accessCommonVariables (STMethod aMethod, Set<STMethod> anSTMethods) {
		for (STMethod aMethod2:anSTMethods) {
			if (acessesCommonVariables(aMethod, aMethod2)) {
				return true;
			}
		}
		return false;
	}
	protected boolean ignoredMethod(STMethod anSTMethod) {
		return anSTMethod.getGlobalsAccessed().isEmpty() ||
				!checkIncludeExcludeTagsOfMethod(Arrays.asList(anSTMethod.getComputedTags()));
	}
	
	
	protected boolean isSeedMethod(STMethod anSTMethod) {
		return 
//				anSTMethod.getGlobalsAccessed().isEmpty() ||
				anSTMethod.isPublicGetter() || 
				anSTMethod.isPublicSetter(); 
//				!checkIncludeExcludeTagsOfMethod(Arrays.asList(anSTMethod.getComputedTags()));
	}

	public Boolean matchType(String aSpecifiedType, String anActualType) {
		return unifyingMatchesNameVariableOrTag(aSpecifiedType, anActualType, null);
	}
	public static <T> Set<T> intersection (Collection<T> aList1, Collection<T> aList2) {
		
		Set<T> aSet1 = new HashSet(aList1);
		Set<T> aSet2 = new HashSet(aList2);
		boolean retVal = aSet1.retainAll(aSet2);
		return aSet1;		
		
	}
	
	public static boolean acessesCommonVariables (STMethod anSTMethod1, STMethod anSTMethod2) {
		
		return !intersection (
				anSTMethod1.getGlobalsAccessed(),
				anSTMethod2.getGlobalsAccessed()).isEmpty();
//		Set<String> aSet1 = new HashSet(aVariables1);
//		Set<String> aSet2 = new HashSet(aVariables2);
//		boolean retVal = aSet1.retainAll(aSet2);
		
		
	}
		
	

	public Boolean doPendingCheck(DetailAST anAST, DetailAST aTree) {
//		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
//				.getSTClassByShortName(
//						getName(getEnclosingTypeDeclaration(aTree)));
		STType anSTType = getSTType(aTree);
		if (anSTType == null) {
			System.err.println("ST Type is null!");
			System.err.println("Symboltable names" + SymbolTableFactory.getOrCreateSymbolTable().getAllTypeNames());
//			return true;
		}
		if (anSTType.isEnum() ||
				anSTType.isInterface()) // why duplicate checking for interfaces
			return true;
//		String aSpecifiedType = findMatchingType(typeToSpecifications.keySet(),
//				anSTType);
//		if (aSpecifiedType == null)
//			return true; // the constraint does not apply to us
		List<Set<STMethod>> aFinalSets = findUncoupledSets(anSTType); 
		if ( aFinalSets.size() > 1) {
			logMultipleCoupledSets(anAST, aFinalSets);
			return false;
		}
		return true;
		
	}

	@Override
	protected String msgKey() {
		// TODO Auto-generated method stub
		return MSG_KEY;
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
