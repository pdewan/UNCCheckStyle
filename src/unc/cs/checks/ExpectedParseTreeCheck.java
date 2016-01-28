package unc.cs.checks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import unc.cs.parseTree.ACallOperation;
import unc.cs.parseTree.AMethodParseTree;
import unc.cs.parseTree.AReturnOperation;
import unc.cs.parseTree.AnIndependentNodes;
import unc.cs.parseTree.AnIFStatement;
import unc.cs.parseTree.AtomicOperation;
import unc.cs.parseTree.CallOperation;
import unc.cs.parseTree.IFStatement;
import unc.cs.parseTree.Body;
import unc.cs.parseTree.MethodParseTree;
import unc.cs.parseTree.CheckedNode;
import unc.cs.parseTree.TransitiveOperation;
import unc.cs.parseTree.TreeSpecificationParser;
import unc.cs.symbolTable.CallInfo;
import unc.cs.symbolTable.STMethod;
import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.SymbolTableFactory;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class ExpectedParseTreeCheck extends MethodCallCheck{
	public static final String MSG_KEY = "expectedNodes";
	protected Map<String, MethodParseTree> specificationToParseTree = new HashMap();
	
	@Override
	protected String msgKey() {
		return MSG_KEY;
	}
	public int[] getDefaultTokens() {
		return new int[] { };

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
			String aType, String aNode) {
		log (aTreeAST, checkedTree, aSpecification, aType, aNode);

	}
	public  Boolean matchParseTree(List<STMethod> anSTMethods, CheckedNode aStatement, List<DetailAST> aMatchedNodes) {
		boolean foundNull = false;
		
		for (STMethod anSTMethod:anSTMethods) {
			Boolean aMatch = matchParseTree(anSTMethod, anSTMethod.getAST(), aStatement, aMatchedNodes);
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
	protected String lastNodeMatched;
	protected String lastNodeNotMatched;
//	public static Boolean matchStatementNodes(DetailAST anAST, CheckedStatement aStatement) {
	// this is a top level thing, so we do not have to return
	//need to change everything to Boolean as we have a list of matched nodes
	public  Boolean matchNodes(STMethod aMethod,
			DetailAST anAST,
			CheckedNode aStatementNodes, List<DetailAST> aMatchedNodes) {
		List<CheckedNode> aStatements = ((AnIndependentNodes) aStatementNodes)
				.getNodes();
		boolean foundNull = false;
		Boolean returnValue = true;
		for (CheckedNode aStatement : aStatements) {
			Boolean aMatch = matchParseTree(aMethod, anAST, aStatement, aMatchedNodes);
			if (aMatch == null) {
				foundNull = true;
				continue;

			} 
			if (!aMatch) {
				returnValue = false;
//				lastNodeNotMatched = aStatement.toString();
//				logNodesNotMatched(anAST, aSt, aType)
//				returnValue = noAST; // should log here
			} else {
//				lastNodeMatched = aStatement;

			}
		}
		if (foundNull) {
			return null;
		}
		return returnValue;
	}
	
	public  Boolean matchAtomicOperation(STMethod aMethod,
			DetailAST anAST, AtomicOperation anAtomicOperation, List<DetailAST> aMatchedNodes) {
		DetailAST aMatchingNode =  findFirstInOrderUnmatchedMatchingNode(anAST, anAtomicOperation.getTokenTypes(), aMatchedNodes);
		
		Boolean result = (aMatchingNode != null) ;
		return result;
//		return false
	}
	public  Boolean matchTransitiveOperation(STMethod aMethod,
			DetailAST anAST, TransitiveOperation aTransitiveOperation, List<DetailAST> aMatchedNodes) {
		DetailAST aMatchingNode =  findFirstInOrderUnmatchedMatchingNode(anAST, aTransitiveOperation.getTokenTypes(), aMatchedNodes);
		if (aMatchingNode == null) {
			processMatchResult(false, aTransitiveOperation.toString());

			return false;
		}
		DetailAST anOperand = aMatchingNode.getNextSibling();
		
		String anOperandText = anOperand.toStringTree();
		boolean retVal = anOperandText.matches(aTransitiveOperation.getOperand());
		processMatchResult(retVal, aTransitiveOperation.getOperand());

		return retVal;
		
		
//		return false;
	}
//	public static DetailAST getExpression (DetailAST anIfAST) {
//		return anIfAST.getFirstChild().getNextSibling();
//	}
//	public static DetailAST getThenPart (DetailAST anIfAST) {
//		return getExpression(anIfAST).getNextSibling().getNextSibling();
//	}
//	public static DetailAST getElsePart (DetailAST anIfAST) {
//		DetailAST aThenPart = getThenPart(anIfAST);
//		if (aThenPart.getNextSibling().getType() != TokenTypes.LITERAL_ELSE) {
//			return null;
//		}
//		return aThenPart.getNextSibling().getFirstChild();
//	}
	public  Boolean matchIf(STMethod aMethod,
			DetailAST anAST, IFStatement anIFStatement, List<DetailAST> aMatchedNodes) {
		DetailAST aMatchingNode =  findFirstInOrderUnmatchedMatchingNode(anAST, anIFStatement.getTokenTypes(), aMatchedNodes);
		if (aMatchingNode == null) {
			processMatchResult(false, anIFStatement.toString());

			return false;
		}	
		DetailAST anExpression = aMatchingNode.getFirstChild().getNextSibling();
		String anExpressionText = anExpression.toStringTree();
		String aSpecification = anIFStatement.getExpression();
		if (aMethod != null) {
			aSpecification = substituteParameters(aSpecification, aMethod);
		}
		if (!anExpressionText.matches(aSpecification)) {
			processMatchResult(false, anExpressionText);

			return false;
		}
		CheckedNode aThenPart = anIFStatement.getThenPart();
		if (aThenPart == null) {
			return true; // this should not really happen
		}
		DetailAST aThenAST = anExpression.getNextSibling().getNextSibling();
		Boolean aMatch = matchParseTree(aMethod, aThenAST, aThenPart, aMatchedNodes);
		
		if (!aMatch) {
			processMatchResult(false, aThenPart.toString());
			return false;
		}
		CheckedNode anElsePart = anIFStatement.getElsePart();
		if (anElsePart == null)
			return true;
		if (aThenAST.getNextSibling().getType() != TokenTypes.LITERAL_ELSE) {
			processMatchResult(false, anIFStatement.toString()); // no else part
		}
		DetailAST anElseAST = aThenAST.getNextSibling().getFirstChild();
			
		Boolean retVal = matchParseTree(aMethod, anElseAST, anElsePart, aMatchedNodes);
	
		processMatchResult(retVal, anElsePart.toString());
		return retVal;
		
//		return (aMatchingNode != null) ;
//		return false;
	}
	
	
	public  Boolean matchCall(STMethod aMethod,
			DetailAST anAST, CallOperation aCallOperation, List<DetailAST> aMatchedNodes) {
		CallInfo[] aCalls = aMethod.getMethodsCalled();
		Boolean foundNull = false;	

		for (CallInfo aCallInfo:aCalls) {
			if (aMatchedNodes.contains(aCallInfo.getAST())) {
				continue;
			}
//			STMethod aSpecifiedMethod = aCallOperation.getMethod();
			String aSpecification = aCallOperation.getOperand();
//			List<STMethod> anActualMethods = aCallInfo.getMatchingCalledMethods(); // these are internal methods
//			if (anActualMethods == null) {
//				return null;
//			}
			int i = 0;
//			for (STMethod anSTMethod:anActualMethods) {
				String aNormalizedLongName = ComprehensiveVisitCheck.toLongName(aCallInfo.getNormalizedCall());
				String shortMethodName = ComprehensiveVisitCheck.toShortTypeName(aNormalizedLongName);
				// specified method has been matched already, matches will assume aSpecifiation has a caller
				Boolean aMatch = matches(aMethod.getDeclaringSTType(), maybeStripComment(aSpecification), shortMethodName, aNormalizedLongName, aCallInfo);
				if (aMatch == null) {
					foundNull = true;
				
				} 
				else if (aMatch) {
					aMatchedNodes.add(aCallInfo.getAST());
//					lastNodeMatched = aCallOperation.toString();
					processMatchResult(true, aCallOperation.toString());
					return true;
				}
			}					
//		}
		
		if (foundNull)
			return null;
//		logNodesNotMatched(anAST, currentSpecification, currentType, aCallOperation.toString());
//		lastNodeNotMatched = aCallOperation.toString();
		processMatchResult(false, aCallOperation.toString());

		return false;		
		
//		return false;
	}
	protected void processMatchResult (Boolean aMatch, String aNode) {
		if (aMatch) {
			lastNodeMatched = aNode;
		} else {
			lastNodeNotMatched = aNode;
		}
			
	}
	public  Boolean matchMethodBody(STMethod aMethod, DetailAST anAST,
		Body aBody) {
		String aNormalizedBody = substituteParameters(aBody.getOperand(), aMethod);
		String aBodyText = anAST.toStringTree();
		boolean retVal = aBodyText.matches(aNormalizedBody);
		processMatchResult(retVal, aBody.toString());
		return retVal;
		
		
//	Boolean aMatch = matches(aMethod.getDeclaringSTType(), maybeStripComment(aSpecification), shortMethodName, aNormalizedLongName, aCallInfo);
				
		
		
		
//		return false;
	}
	
	public  Boolean matchParseTree(STMethod aMethod, DetailAST anAST, CheckedNode aStatement, List<DetailAST> aMatchedNodes) {
//		String aStringTree = anAST.toStringTree();
//		String aStringList = anAST.toStringList();
//		String aString = anAST.toString();
		int i = 0;
		Boolean retVal = false;
		if (aStatement instanceof Body) {
			retVal = matchMethodBody(aMethod, anAST, (Body) aStatement);
		} else if (aStatement instanceof AnIndependentNodes) {
			retVal = matchNodes(aMethod, anAST, aStatement, aMatchedNodes);			
		} else if (aStatement instanceof AReturnOperation) {
			retVal = matchTransitiveOperation(aMethod, anAST, (TransitiveOperation) aStatement, aMatchedNodes);
		} else if (aStatement instanceof ACallOperation) {
			retVal = matchCall(aMethod, anAST, (CallOperation) aStatement, aMatchedNodes);
		} else if (aStatement instanceof AnIFStatement) {
			retVal = matchIf(aMethod, anAST, (IFStatement) aStatement, aMatchedNodes);
		}
//		if (!(aStatement instanceof AnIndependentNodes)) {
//		if (retVal ) {
//			lastNodeMatched = aStatement.toString();
//		} else {
//			lastNodeNotMatched = aStatement.toString();
//		}
//		}
//		if (!retVal) {
//			logNodesNotMatched(anAST, currentSpecification, currentType, aStatement.toString());
//		}
		return retVal;
		
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
	
	// global variables, ugh, but too many methods have to share this info
	protected String currentSpecification = "";
	protected String currentType = "";

	public Boolean matchParseTree(STType anSTType, String[] aSpecifications) {
		Boolean foundNull = false;
		boolean retVal = true;
		Boolean aMatch = null;
		currentType = toShortTypeName(anSTType.getName());
		DetailAST matchedAST = anSTType.getAST();
		for (String aSpecification : aSpecifications) {
			currentSpecification = aSpecification;
			MethodParseTree aMethodParseTree = specificationToParseTree
					.get(aSpecification);
			STMethod aSpecifiedMethod = aMethodParseTree.getMethod();
			CheckedNode aStatement = aMethodParseTree.getParseTree();
			if (aSpecifiedMethod == null) {
				List<DetailAST> aMatchedNodes = new ArrayList();
				aMatch= matchParseTree(null, anSTType.getAST(), aStatement, aMatchedNodes);
				if (aMatch == null) {
					foundNull = true;
					continue;
				}
				if (!aMatch) {
					retVal = false;
					logNodesNotMatched(matchedAST, aSpecification, currentType, lastNodeNotMatched);
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
				boolean specificationMatched = false;
//				retVal = false; // set to true if any method matched
				for (STMethod anSTMethod : aMatchingMethods) {
					List<DetailAST> aMatchedNodes = new ArrayList();
					matchedAST = anSTMethod.getAST();

					aMatch = matchParseTree(anSTMethod, matchedAST, aStatement, aMatchedNodes);
					if (aMatch == null) {
						foundNull = true;
						continue;
					}
					if (aMatch) {
						specificationMatched = true;
//						retVal = true;
						break;
						
					}

				}
				if (!specificationMatched && !foundNull) {
					retVal = false;
					logNodesNotMatched(matchedAST, aSpecification, currentType, lastNodeNotMatched);
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

	@Override
	protected boolean returnValueOnMatch() {
		// TODO Auto-generated method stub
		return false;
	}

}
