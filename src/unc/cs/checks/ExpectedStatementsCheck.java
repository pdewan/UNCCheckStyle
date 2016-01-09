package unc.cs.checks;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import unc.cs.parseTree.AMethodParseTree;
import unc.cs.parseTree.AStatementNodes;
import unc.cs.parseTree.MethodParseTree;
import unc.cs.parseTree.CheckedStatement;
import unc.cs.parseTree.TreeSpecificationParser;
import unc.cs.symbolTable.STMethod;
import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.SymbolTableFactory;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

public class ExpectedStatementsCheck extends ComprehensiveVisitCheck{
	public static final String MSG_KEY = "expectedNodes";
	public Map<String, MethodParseTree> specificationToParseTree = new HashMap();

	@Override
	protected String msgKey() {
		return MSG_KEY;
	}
	
	protected void registerSpecifications (String aType, String[] aSpecifications) {
		super.registerSpecifications(aType, aSpecifications);
		for (String aSpecification:aSpecifications) {
			MethodParseTree aParseTree = toMethodParseTree(aSpecification);
			specificationToParseTree.put(aSpecification, aParseTree);
		}
	}
	
	public static MethodParseTree toMethodParseTree (String aSpecification) {
		String[] aMethodAndParseTreeSpecification = aSpecification.split(MethodCallCheck.CALLER_TYPE_SEPARATOR);
		String aParseTreeSpecification = null;
		String aMethodSpecification = null;
		STMethod anSTMethod = null;

		if (aMethodAndParseTreeSpecification.length == 2) {
			aMethodSpecification = aMethodAndParseTreeSpecification[0].trim();
			anSTMethod = signatureToMethod(aMethodSpecification);
			aParseTreeSpecification = maybeStripComment(aMethodAndParseTreeSpecification[1]);			
		} else {
			aParseTreeSpecification = maybeStripComment(aMethodAndParseTreeSpecification[0]);
		}
		CheckedStatement aParseTree = null;
		try {
		 aParseTree = TreeSpecificationParser.parseNodes(aParseTreeSpecification);
		} catch (Exception e) {
			e.printStackTrace();
			aParseTree = null;
		}
		return new AMethodParseTree(anSTMethod, aParseTree);
	}
	
		
	public void setExpectedStatements(String[] aPatterns) {
		setExpectedTypesAndSpecifications(aPatterns);
	}
	
	protected void logNodesNotMatched(DetailAST aTreeAST, String aSpecification,
			String aType) {
		log (aTreeAST, aTreeAST, aSpecification, aType);

	}
	public static Boolean matchParseTree(List<STMethod> anSTMethods, CheckedStatement aStatement) {
		boolean foundNull = false;
		for (STMethod anSTMethod:anSTMethods) {
			Boolean aMatch = matchParseTree(anSTMethod.getAST(), aStatement);
			if (aMatch == null) {
				foundNull = true;
				
			} else if (aMatch) {
				return true;
			}
		}
		if (foundNull)
			return null;
		return false;		
	}
//	public static Boolean matchStatementNodes(DetailAST anAST, CheckedStatement aStatement) {

	
	public static Boolean matchParseTree(DetailAST anAST, CheckedStatement aStatement) {
		if (aStatement instanceof AStatementNodes) {
			Boolean aMatch = matchParseTree(anAST, aStatement);
			if (aMatch == null) {
				return null;
			}
			if (!aMatch) {
				return false;
			}
		}
		return true;
		
	}


	public Boolean matchParseTree(STType anSTType, String[] aSpecifications) {
		boolean retVal = true;
		Set<String> anUnmatchedGlobals = new HashSet(anSTType.getDeclaredGlobals());
		for (String aSpecification : aSpecifications) {
			MethodParseTree aMethodParseTree = specificationToParseTree.get(aSpecification);
			STMethod aSpecifiedMethod = aMethodParseTree.getMethod();
			if (aSpecifiedMethod != null) {
				List<STMethod> aMatchingMethods = getMatchingMethods(anSTType, aSpecifiedMethod);
				if (aMatchingMethods == null)
					return null;
				if (aMatchingMethods.size() == 0) {					
					return false;
				}
				Boolean aMatch = matchParseTree()
			}
			
			
		}
		return retVal;
	}
	

	public Boolean matchGlobal(String aVariableSpecification,
			String aTypeSpecification, Set<String> anUnmatchedGlobals, STType anSTType) {
		Set<String> aSet = anSTType.getDeclaredGlobals();
		int i = 0;
		for (String aVariable : anUnmatchedGlobals) {
			if (aVariable.matches(aVariableSpecification)) {
				String anActualType = anSTType.getDeclaredGlobalVariableType(aVariable);
				
				// return
				// aSpecifiedType.equalsIgnoreCase(aPropertyInfos.get(aProperty).getGetter().getReturnType());
				Boolean retVal = matchesType(aTypeSpecification, anActualType);
						
				if (retVal == null)
					return null;
				if (retVal) {
					anUnmatchedGlobals.remove(aVariable);
					return true;
				} 

			}
		}
		return false;
	}
	public Boolean doPendingCheck(DetailAST anAST, DetailAST aTree) {
//		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
//				.getSTClassByShortName(
//						getName(getEnclosingTypeDeclaration(aTree)));
		STType anSTType = getSTType(aTree);
		if (anSTType == null) {
			System.out.println("ST Type is null!");
			System.out.println("Symboltable names" + SymbolTableFactory.getOrCreateSymbolTable().getAllTypeNames());
//			return true;
		}
		if (anSTType.isEnum() ||
				anSTType.isInterface()) // why duplicate checking for interfaces
			return true;
		String aSpecifiedType = findMatchingType(typeToSpecification.keySet(),
				anSTType);
		if (aSpecifiedType == null)
			return true; // the constraint does not apply to us
		
		
		String[] aSpecifications = typeToSpecification.get(aSpecifiedType);

		return matchParseTree(anSTType, aSpecifications);
	}

	@Override
	

	public void doFinishTree(DetailAST ast) {
		
		maybeAddToPendingTypeChecks(ast);
		super.doFinishTree(ast);

	}

}
