package unc.cs.checks;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import unc.cs.symbolTable.PropertyInfo;
import unc.cs.symbolTable.STMethod;
import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.SymbolTableFactory;

public class ExpectedGettersCheck extends BeanPropertiesCheck {
	public static final String MSG_KEY = "expectedGetters";
	@Override
	protected String msgKey() {
		// TODO Auto-generated method stub
		return MSG_KEY;
	}

//	protected Map<String, String[]> typeToProperty = new HashMap<>();
//	public static final String SEPARATOR = ">";
//
//	public void doVisitToken(DetailAST ast) {
//		// System.out.println("Check called:" + MSG_KEY);
//		switch (ast.getType()) {
//		case TokenTypes.PACKAGE_DEF:
//			visitPackage(ast);
//			return;
//		case TokenTypes.CLASS_DEF:
//			visitType(ast);
//			return;
//		default:
//			System.err.println("Unexpected token");
//		}
//	}

//	@Override
//	public int[] getDefaultTokens() {
//		return new int[] { TokenTypes.CLASS_DEF, TokenTypes.INTERFACE_DEF,
//				TokenTypes.PACKAGE_DEF };
//	}
//
//	public void setExpectedPropertiesOfType(String aPattern) {
//		String[] extractTypeAndProperties = aPattern.split(">");
//		String aType = extractTypeAndProperties[0].trim();
//		String[] aProperties = extractTypeAndProperties[1].split("\\|");
//		typeToProperty.put(aType, aProperties);
//	}

//	/*
//	 * @StructurePatternNames.LinePattern> X:int | Y:int | Width:int
//	 * |Height:int,
//	 * 
//	 * @StructurePatternNames.OvalPatetrn> X:int | Y:int | Width:int |Height:int
//	 */
//	public void setExpectedProperties(String[] aPatterns) {
//		for (String aPattern : aPatterns) {
//			setExpectedPropertiesOfType(aPattern);
//		}
//
//	}

	// public void visitType(DetailAST ast) {
	// super.visitType(ast);
	// }
	// public void doVisitToken(DetailAST ast) {
	// // System.out.println("Check called:" + MSG_KEY);
	// switch (ast.getType()) {
	// case TokenTypes.PACKAGE_DEF:
	// visitPackage(ast);
	// return;
	// case TokenTypes.CLASS_DEF:
	// visitType(ast);
	// return;
	// case TokenTypes.INTERFACE_DEF:
	// visitType(ast);
	// return;
	// default:
	// System.err.println("Unexpected token");
	// }
	// }

	// public static Boolean matchType (Set<String> aSpecifiedTypes, STType
	// anSTType) {
	// for (String aSpecifiedType:aSpecifiedTypes) {
	// checkTagsOfType(aSpecifiedType, anSTType);
	// }
	// }
//	protected void logPropertyNotMatched(DetailAST aTreeAST, String aProperty,
//			String aType) {
//		String aSourceName = shortFileName(astToFileContents.get(aTreeAST)
//				.getFilename());
//		if (aTreeAST == currentTree) {
//
//			log(aTreeAST.getLineNo(), msgKey(), aProperty, aType, aSourceName);
//		} else {
//			log(0, msgKey(), aProperty, aType, aSourceName);
//		}
//
//	}

//	public Boolean matchProperties(String[] aSpecifiedProperties,
//			Map<String, PropertyInfo> aPropertyInfos, DetailAST aTreeAST) {
//		boolean retVal = true;
//		for (String aSpecifiedProperty : aSpecifiedProperties) {
//			String[] aPropertyAndType = aSpecifiedProperty.split(":");
//			String aType = aPropertyAndType[1].trim();
//			String aProperty = aPropertyAndType[0].trim();
//			if (!matchProperty(aType, aProperty, aPropertyInfos)) {
//				logPropertyNotMatched(aTreeAST, aProperty, aType);
//				retVal = false;
//			}
//		}
//		return retVal;
//	}

//	public Boolean matchProperty(String aSpecifiedType,
//			String aSpecifiedPoperty, Map<String, PropertyInfo> aPropertyInfos) {
//		for (String aProperty : aPropertyInfos.keySet()) {
//			if (aSpecifiedPoperty.equalsIgnoreCase(aProperty))
//				// return
//				// aSpecifiedType.equalsIgnoreCase(aPropertyInfos.get(aProperty).getGetter().getReturnType());
//				return matchType(aSpecifiedType, aProperty, aPropertyInfos);
//
//			else
//				continue;
//		}
//		return false;
//	}

	public Boolean matchType(String aSpecifiedType, String aProperty,
			Map<String, PropertyInfo> aPropertyInfos) {

		return matchGetter(aSpecifiedType, aProperty, aPropertyInfos);

	}

//	public Boolean matchGetter(String aSpecifiedType, String aProperty,
//			Map<String, PropertyInfo> aPropertyInfos) {
//
//		return aSpecifiedType.equalsIgnoreCase(aPropertyInfos.get(aProperty)
//				.getGetter().getReturnType());
//
//	}

//	// public boolean match(String aSpecifiedType, String aSpecifiedPoperty,
//	// Map<String, PropertyInfo> aPropertyInfos) {
//	// return matchGetter(aSpecifiedType, aSpecifiedPoperty, aPropertyInfos);
//	// }
//	public Boolean matchSetter(String aSpecifiedType, String aProperty,
//			Map<String, PropertyInfo> aPropertyInfos) {
//
//		return aSpecifiedType.equalsIgnoreCase(aPropertyInfos.get(aProperty)
//				.getSetter().getReturnType());
//
//	}
//
//	// public boolean match(String aSpecifiedType, String aSpecifiedPoperty,
//	// Map<String, PropertyInfo> aPropertyInfos) {
//	// return matchGetter(aSpecifiedType, aSpecifiedPoperty, aPropertyInfos);
//	// }
//	public Boolean doPendingCheck(DetailAST anAST, DetailAST aTree) {
//		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
//				.getSTClassByShortName(
//						getName(getEnclosingClassDeclaration(aTree)));
//		String aSpecifiedType = findMatchingType(typeToProperty.keySet(),
//				anSTType);
//		if (aSpecifiedType == null)
//			return true; // the constraint does not apply to us
//
//		Map<String, PropertyInfo> aPropertyInfos = anSTType.getPropertyInfos();
//		String[] aSpecifiedProperties = typeToProperty.get(aSpecifiedType);
//		return matchProperties(aSpecifiedProperties, aPropertyInfos, aTree);
//	}

	

//	public void finishTree(DetailAST ast) {
//		// STType anSTType =
//		// SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(fullTypeName);
//		// for (STMethod aMethod: anSTType.getMethods()) {
//		// visitMethod(anSTType, aMethod);
//		// }
//		maybeAddToPendingTypeChecks(ast);
//		super.finishTree(ast);
//
//	}
}