package unc.cs.checks;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import unc.cs.symbolTable.CallInfo;
import unc.cs.symbolTable.STMethod;
import unc.cs.symbolTable.STNameable;
import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.SymbolTableFactory;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public abstract class MethodCallVisitedCheck extends ComprehensiveVisitCheck {

	/**
	 * A key is pointing to the warning message text in "messages.properties"
	 * file.
	 */
	public static final String MSG_KEY = "methodCallVisited";

	// protected String shortMethodName;
	// protected String longMethodName;

	@Override
	public int[] getDefaultTokens() {
		return new int[] {
				 TokenTypes.PACKAGE_DEF,
				TokenTypes.CLASS_DEF,
//				TokenTypes.ANNOTATION,
				// TokenTypes.INTERFACE_DEF,
				// TokenTypes.TYPE_ARGUMENTS,
				// TokenTypes.TYPE_PARAMETERS,
				TokenTypes.VARIABLE_DEF, TokenTypes.PARAMETER_DEF,
				TokenTypes.METHOD_DEF, TokenTypes.CTOR_DEF,
				// TokenTypes.IMPORT, TokenTypes.STATIC_IMPORT,
				// TokenTypes.PARAMETER_DEF,
				// TokenTypes.LCURLY,
				// TokenTypes.RCURLY,
				TokenTypes.CTOR_CALL,
//				TokenTypes.LITERAL_NEW,
				TokenTypes.METHOD_CALL };

	}

	// protected boolean typeCheck(STType anSTClass) {
	// return true; // we probably will not flag the type
	// }
//// move this up to ComprehensiveVisitCheck
//	protected void log(DetailAST ast, DetailAST aTreeAST, String anExplanation) {
////		String aSourceName =
////				 shortFileName(astToFileContents.get(aTreeAST).getFilename());
////		if (aTreeAST == currentTree) {
////			log(ast.getLineNo(),
////					 msgKey(),
////					aMethodName, 
////					aSourceName + ":" + ast.getLineNo());
////		} else {
////			log(0, msgKey(), aMethodName,
////					aSourceName + ":"
////							+ ast.getLineNo());
////		}
//		log(ast, aTreeAST, anExplanation, msgKey());
//		
//
//	}
//	protected void log(DetailAST ast, DetailAST aTreeAST, String anExplanation, String aMessageKey) {
//		String aSourceName =
//				 shortFileName(astToFileContents.get(aTreeAST).getFilename());
//		if (aTreeAST == currentTree) {
//			log(ast.getLineNo(),
////					 msgKey(),
//					aMessageKey,
//					anExplanation, 
//					aSourceName + ":" + ast.getLineNo());
//		} else {
//			log(0, 
////					msgKey(), 
//					aMessageKey,
//					anExplanation,
//					aSourceName + ":"
//							+ ast.getLineNo());
//		}
//	}

	protected abstract Boolean check(STType aCallingType, DetailAST ast,
			String aShortMethodName, String aLongMethodName, CallInfo aCallInfo);
	
	protected void log(DetailAST ast, DetailAST aTreeAST, String aShortMethodName,
		String aLongMethodName, CallInfo aCallInfo) {
		log(ast, aTreeAST, aCallInfo);
		
	}

//	public String[] toNormalizedClassBasedCall(String[] aCallParts) {
//		List<String> aCallPartsList = new ArrayList();
//		if (aCallParts.length == 1 || "this".equals(aCallParts[0])) { // put the
//																		// name
//																		// of
//																		// the
//																		// class
//																		// in
//																		// which
//																		// the
//																		// call
//																		// occurs
//			aCallPartsList.add(fullTypeName);
//			aCallPartsList.add(aCallParts[aCallParts.length - 1]);
//		} else if (aCallParts.length == 2) {
//			String aType = lookupType(aCallParts[0]);
//			if (aType != null) { // not a static method
//				aCallPartsList.add(aType);
//				aCallPartsList.add(aCallParts[1]);
//			} else {
//				return aCallParts; // static call
//			}
//		} else {
//			return aCallParts; // System.out.println() probabluy
//		}
//		return aCallPartsList.toArray(new String[0]);
//	}

//	public String toLongName(String[] aNormalizedName) {
//		StringBuffer retVal = new StringBuffer();
//		int index = 0;
//		while (true) {
//			if (index >= aNormalizedName.length) {
//				return retVal.toString();
//			}
//			if (index > 0)
//				retVal.append(".");
//			retVal.append(aNormalizedName[index]);
//			index++;
//		}
//	}

	public void visitMethodCall(DetailAST ast) {
		super.visitMethodCall(ast);
		maybeAddToPendingTypeChecks(ast);
		// // if (ast.getType() != TokenTypes.METHOD_CALL)
		// // return;
		// String shortMethodName = getLastDescendent(ast).getText();
		// FullIdent aFullIdent = FullIdent.createFullIdentBelow(ast);
		// String longMethodName = aFullIdent.getText();
		// String[] aCallParts = longMethodName.split("\\.");
		// String[] aNormalizedParts = toNormalizedClassBasedCall(aCallParts);
		// String aNormalizedLongName = toLongName(aNormalizedParts);
		//
		// // System.out.println("Method text:" +
		// getLastDescendent(ast).getText());
		// Boolean checkResult = check(ast, shortMethodName,
		// aNormalizedLongName, aNormalizedParts);
		// if (checkResult == null) {
		// pendingChecks().add(ast);
		// return;
		// }
		// // if (!check(ast, shortMethodName, aNormalizedLongName,
		// aNormalizedParts))
		// if (!checkResult) {
		// log(ast, shortMethodName);
		// // log(ast.getLineNo(), msgKey(), getLastDescendent(ast).getText());
		// }

	}
	
//	public static String maybeReturnInstantiatedType(DetailAST ast) {
//		DetailAST aNewAST = ast.findFirstToken(TokenTypes.LITERAL_NEW);
//		if (aNewAST != null) {
//			DetailAST anIdentAST = aNewAST.findFirstToken(TokenTypes.IDENT);
//			if (aNewAST.findFirstToken(TokenTypes.ARRAY_DECLARATOR) != null || 
//					aNewAST.findFirstToken(TokenTypes.ARRAY_INIT) != null) {
//				return anIdentAST.getText() + "[]";
//			} else return anIdentAST.getText();
//		} else if (ast.findFirstToken(TokenTypes.STRING_LITERAL) != null) {
//			return "String";
//		} else
//			
//			return null;
//	}
	@Override
	public void visitConstructorCall(DetailAST ast) {
		super.visitConstructorCall(ast);
		maybeAddToPendingTypeChecks(ast);
		// // if (ast.getType() != TokenTypes.METHOD_CALL)
		// // return;
		// String shortMethodName = getLastDescendent(ast).getText();
		// FullIdent aFullIdent = FullIdent.createFullIdentBelow(ast);
		// String longMethodName = aFullIdent.getText();
		// String[] aCallParts = longMethodName.split("\\.");
		// String[] aNormalizedParts = toNormalizedClassBasedCall(aCallParts);
		// String aNormalizedLongName = toLongName(aNormalizedParts);
		//
		// // System.out.println("Method text:" +
		// getLastDescendent(ast).getText());
		// Boolean checkResult = check(ast, shortMethodName,
		// aNormalizedLongName, aNormalizedParts);
		// if (checkResult == null) {
		// pendingChecks().add(ast);
		// return;
		// }
		// // if (!check(ast, shortMethodName, aNormalizedLongName,
		// aNormalizedParts))
		// if (!checkResult) {
		// log(ast, shortMethodName);
		// // log(ast.getLineNo(), msgKey(), getLastDescendent(ast).getText());
		// }

	}
	

	@Override
	public Boolean doPendingCheck(DetailAST ast, DetailAST aTreeAST) {
//		DetailAST aMethodNameAST = getLastDescendent(ast);
//		DetailAST aLeftMostMethodTargetAST = aMethodNameAST
//				.getPreviousSibling();
//		String shortMethodName0 = aMethodNameAST.getText();

		CallInfo aCallInfo = registerMethodCallAndtoNormalizedParts(ast, aTreeAST);
//		String aNormalizedLongName = toLongName(aNormalizedParts);
		String aNormalizedLongName = toLongName(aCallInfo.getNormalizedCall());
		String shortMethodName = toShortTypeName(aNormalizedLongName);


		// System.out.println("Method text:" +
		// getLastDescendent(ast).getText());
		STType anSTType = getSTType(aTreeAST);
//		String aCallingType = toShortTypeName(anSTType.getName());
		Boolean checkResult = check(anSTType, ast, shortMethodName,
				aNormalizedLongName, aCallInfo);
		if (checkResult == null) {
			// pendingChecks().add(ast);
			return null;
		}
		// if (!check(ast, shortMethodName, aNormalizedLongName,
		// aNormalizedParts))
		if (!checkResult) {
//			log(ast, aTreeAST, shortMethodName);
//			log(ast, aTreeAST, aCallInfo);
			log(ast, aTreeAST, shortMethodName, aNormalizedLongName,
					aCallInfo);

			// log(ast.getLineNo(), msgKey(), getLastDescendent(ast).getText());
		}
		return checkResult;

	}

//	public Boolean oldDoPendingCheck(DetailAST ast, DetailAST aTreeAST) {
//		// if (ast.getType() != TokenTypes.METHOD_CALL)
//		// return;
//		String[] aNormalizedParts = null;
//
//		DetailAST aMethodNameAST = getLastDescendent(ast);
//		DetailAST aLeftMostMethodTargetAST = aMethodNameAST
//				.getPreviousSibling();
//		String shortMethodName = aMethodNameAST.getText();
//
//		if (aLeftMostMethodTargetAST.getType() == TokenTypes.STRING_LITERAL) {
//			aNormalizedParts = new String[] { "String", shortMethodName };
//
//		}
//		FullIdent aFullIdent = FullIdent.createFullIdentBelow(ast);
//		String longMethodName = aFullIdent.getText();
//		if (currentTree == aTreeAST) {
//			if (aNormalizedParts == null) {
//				String[] aCallParts = longMethodName.split("\\.");
//				aNormalizedParts = toNormalizedClassBasedCall(aCallParts);
//			}
//			astToContinuationData.put(ast, aNormalizedParts);
//		} else {
//			aNormalizedParts = (String[]) astToContinuationData.get(ast);
//			if (aNormalizedParts == null) {
//				System.err.println("Nprmalizedname not saved");
//			}
//		}
//		String aNormalizedLongName = toLongName(aNormalizedParts);
//
//		// System.out.println("Method text:" +
//		// getLastDescendent(ast).getText());
//		Boolean checkResult = check(ast, shortMethodName, aNormalizedLongName,
//				aNormalizedParts);
//		if (checkResult == null) {
//			// pendingChecks().add(ast);
//			return null;
//		}
//		// if (!check(ast, shortMethodName, aNormalizedLongName,
//		// aNormalizedParts))
//		if (!checkResult) {
//			log(ast, shortMethodName);
//			// log(ast.getLineNo(), msgKey(), getLastDescendent(ast).getText());
//		}
//		return checkResult;
//
//	}

//	public void doVisitToken(DetailAST ast) {
//		if (ast.getType() == TokenTypes.METHOD_CALL)
//			visitCall(ast);
//		else
//			super.doVisitToken(ast);
//	}

	@Override
	protected String msgKey() {
		return MSG_KEY;
	}

	protected Boolean typesMatch(String aSourceTypeSpecification,
			String aDestinationTypeSpecification,
			String anActualDestinationType, DetailAST aCallAST) {
		String aSourceTypeName = this
				.getName(getEnclosingTypeDeclaration(aCallAST));
		Boolean aSourceTypeMatches = matchesType(aSourceTypeSpecification,
				aSourceTypeName);
		if (aSourceTypeMatches == null) { // this should never occur
			return null;
		}
		if (!aSourceTypeMatches)
			return false;
		Boolean aDestinationTypeMatches = matchesType(
				aDestinationTypeSpecification, anActualDestinationType);
		if (aDestinationTypeMatches == null) {
			return null;
		}
		return aDestinationTypeMatches;
	}

	public  Boolean hasTag(STMethod aMethod, String aTag) {
		STNameable[] aTags = aMethod.getTags();
		return hasTag(aMethod.getComputedTags(), aTag);
	}

	// assume classes have been matched
	protected Boolean matchMethod(String aMethodSpecification,
			String aShortMethodClassName, String aShortMethodName) {
		if (aMethodSpecification == null || aMethodSpecification.equals(MATCH_ANYTHING))
			return true;
		if (aMethodSpecification.indexOf(TAG_STRING) == -1)
//			return aMethodSpecification.equals(aShortMethodName);
			return matchesNameVariableOrTag(aMethodSpecification, aShortMethodName, null);
		
		String aSpecificationTag = aMethodSpecification.substring(1);
		STType aTypeST = SymbolTableFactory.getOrCreateSymbolTable()
				.getSTClassByShortName(aShortMethodClassName);
		if (aTypeST.isEnum())
			return true;
		if (aTypeST == null)
			return null;
		STMethod[] aMethods = aTypeST.getMethods();
		if (aMethods == null)
			return null;
		for (STMethod aMethod : aMethods) {
			if (hasTag(aMethod, aSpecificationTag))
				return true;
		}
		return false;

	}

	// assume that classes have already matched
	protected Boolean methodsMatch(String aSourceMethodSpecification,
			String aDestinationMethodSpecification, String aCallingClass,
			String aCalledClass, String aCallingMethodShortName,
			String aCalledMethodShortName) {
		Boolean aSourcesMatch = matchMethod(aSourceMethodSpecification,
				aCallingClass, aCallingMethodShortName);
		if (aSourcesMatch == null)
			return null;
		Boolean aDestinationsMatch = matchMethod(
				aDestinationMethodSpecification, aCalledClass,
				aCalledMethodShortName);
		if (aDestinationsMatch == null)
			return null;
		return aSourcesMatch && aDestinationsMatch;
	}

	// "String.substring, String.charAt, @Tag1>String.length, Bar>Scanner.nextLine, @Tag1.@Tag3>@Tag2.@Tag4"/>

	protected Boolean methodCallContainedInSpecifications(String[] aCallParts,
			List<String> aSpecifications, DetailAST aCallAST) {
		Boolean retVal = false;
		for (String aMember : aSpecifications) {
			SourceAndDestination aSourceAndDestination = new ASourceAndDestination(
					aMember);
			TypeAndMethod aSourceTypeAndMethod = new ATypeAndMethod(
					aSourceAndDestination.getSource());
			TypeAndMethod aDestinationTypeAndMethod = new ATypeAndMethod(
					aSourceAndDestination.getDestination());
			Boolean aTypesMatch = typesMatch(aSourceTypeAndMethod.getType(),
					aDestinationTypeAndMethod.getType(), aCallParts[0],
					aCallAST);
			if (aTypesMatch == null) {
				retVal = null; // if we do not return true, then we do not know,
								// see if this is an imported class
				continue;
			}
			if (!aTypesMatch)
				continue; // some other type may match
			// return false;
			String aCallingClass = getName(getEnclosingTypeDeclaration(aCallAST));
//			STType aCallingC;lass = getSTType(aCallAST);

			String aCalledClass = aCallParts[0];
			String aCallingMethod = getName(getEnclosingMethodDeclaration(aCallAST));
			String aCalledMethod = aCallParts[1];

			Boolean aMethodsMatch = methodsMatch(
					aSourceTypeAndMethod.getMethod(),
					aDestinationTypeAndMethod.getMethod(), aCallingClass,
					aCalledClass, aCallingMethod, aCalledMethod);
			if (aMethodsMatch == null)
				return null;
			if (aMethodsMatch)
				return true;

			//
			//
			// if ((aSourceAndDestination.length == 2) &&
			// !matchesType(myClassName, aSourceAndDestination[0]))
			// continue; // not relevant
			// String aTrueMember = aSourceAndDestination.length ==
			// 2?aSourceAndDestination[1]:aMember;
			// if (aTarget.equals(aTrueMember))
			// return true;
		}
		return retVal;
	}
//	 public void doVisitToken(DetailAST ast) {
//		 switch (ast.getType()) {
//		 case TokenTypes.LITERAL_NEW: 
//				return;
//		 case TokenTypes.CTOR_CALL:
//			 return;
//		 default:
//			 super.doVisitToken(ast);
//		 }
//	 }
}
