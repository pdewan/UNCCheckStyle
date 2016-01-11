package unc.cs.checks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import unc.cs.parseTree.AMethodParseTree;
import unc.cs.parseTree.AReturnOperation;
import unc.cs.parseTree.AnIndependentNodes;
import unc.cs.parseTree.AnIFStatement;
import unc.cs.parseTree.AtomicOperation;
import unc.cs.parseTree.IFStatement;
import unc.cs.parseTree.MethodParseTree;
import unc.cs.parseTree.CheckedNode;
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
		CheckedNode aParseTree = null;
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
	public static Boolean matchParseTree(List<STMethod> anSTMethods, CheckedNode aStatement, List<DetailAST> aMatchedNodes) {
		boolean foundNull = false;
		
		for (STMethod anSTMethod:anSTMethods) {
			Boolean aMatch = matchParseTree(anSTMethod.getAST(), aStatement, aMatchedNodes);
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
	// this is a top level thing, so we do not have to return
	//need to change everything to Boolean as we have a list of matched nodes
	public static Boolean matchNodes(DetailAST anAST,
			CheckedNode aStatementNodes,
			List<DetailAST> aMatchedNodes) {
		List<CheckedNode> aStatements = ((AnIndependentNodes) aStatementNodes)
				.getNodes();
		boolean foundNull = false;
		Boolean returnValue = true;
		for (CheckedNode aStatement : aStatements) {
			Boolean aMatch = matchParseTree(anAST, aStatement, aMatchedNodes);
			if (aMatch == null) {
				foundNull = true;
				continue;

			} 
			if (!aMatch) {
				returnValue = false;
//				returnValue = noAST; // should log here
			}
		}
		return returnValue;
	}
	public static Boolean matchAtomicOperation(DetailAST anAST,
			AtomicOperation anAtomicOperation, List<DetailAST> aMatchedNodes) {
		DetailAST aMatchingNode =  getFirstInOrderUnmatchedMatchingNode(anAST, anAtomicOperation.getTokenTypes(), aMatchedNodes);
		return (aMatchingNode != null) ;
//		return false;
	}
	public static Boolean matchIF(DetailAST anAST,
			IFStatement anIFStatement, List<DetailAST> aMatchedNodes) {
		DetailAST aMatchingNode =  getFirstInOrderUnmatchedMatchingNode(anAST, anIFStatement.getTokenTypes(), aMatchedNodes);
		
		return (aMatchingNode != null) ;
//		return false;
	}
	
	public static Boolean matchParseTree(DetailAST anAST, CheckedNode aStatement, List<DetailAST> aMatchedNodes) {
		if (aStatement instanceof AnIndependentNodes) {
			return matchNodes(anAST, aStatement, aMatchedNodes);			
		} else if (aStatement instanceof AReturnOperation) {
			return matchAtomicOperation(anAST, (AtomicOperation) aStatement, aMatchedNodes);
		} else if (aStatement instanceof AnIFStatement) {
			return matchIF(anAST, (IFStatement) aStatement, aMatchedNodes);
		}
		return false;
		
	}
//	public static Boolean matchParseTree(DetailAST anAST, 
//			CheckedStatement aStatement, 
//			List<DetailAST> aMatchedASTs) {
//		if (aStatement instanceof AStatementNodes) {
//			return matchNodes(anAST, aStatement, aMatchedASTs) != null;			
//		} else if (aStatement instanceof AReturnOperation) {
//			return matchAtomicOperation(anAST, (AtomicOperation) aStatement, aMatchedASTs );
//		}
//		return false;
//		
//	}

	public Boolean matchParseTree(STType anSTType, String[] aSpecifications) {
		Boolean foundNull = false;
		boolean retVal = true;
		Boolean aMatch = null;
	
		for (String aSpecification : aSpecifications) {
			MethodParseTree aMethodParseTree = specificationToParseTree
					.get(aSpecification);
			STMethod aSpecifiedMethod = aMethodParseTree.getMethod();
			CheckedNode aStatement = aMethodParseTree.getParseTree();
			if (aSpecifiedMethod == null) {
				List<DetailAST> aMatchedNodes = new ArrayList();
				aMatch= matchParseTree(anSTType.getAST(), aStatement, aMatchedNodes);
				if (aMatch == null) {
					foundNull = true;
					continue;
				}
				if (!aMatch) {
					retVal = false;
					continue;
				}
			} else {
				List<STMethod> aMatchingMethods = getMatchingMethods(anSTType,
						aSpecifiedMethod);
				if (aMatchingMethods == null)
					return true;
				if (aMatchingMethods.size() == 0) {
					return false;
				}
				for (STMethod anSTMethod : aMatchingMethods) {
					List<DetailAST> aMatchedNodes = new ArrayList();

					aMatch = matchParseTree(anSTMethod.getAST(), aStatement, aMatchedNodes);
					if (aMatch == null) {
						foundNull = true;
						continue;
					}
					if (!aMatch) {
						retVal = false;
						continue;
					}

				}

			}
		}
		if (foundNull)
			return null;
		return retVal;
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
		String aSpecifiedType = findMatchingType(typeToSpecifications.keySet(),
				anSTType);
		if (aSpecifiedType == null)
			return true; // the constraint does not apply to us
		
		
		String[] aSpecifications = typeToSpecifications.get(aSpecifiedType);

		return matchParseTree(anSTType, aSpecifications);
	}

	@Override
	

	public void doFinishTree(DetailAST ast) {
		
		maybeAddToPendingTypeChecks(ast);
		super.doFinishTree(ast);

	}

}
