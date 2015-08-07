package unc.cs.checks;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import unc.cs.symbolTable.AnSTType;
import unc.cs.symbolTable.PropertyInfo;
import unc.cs.symbolTable.STMethod;
import unc.cs.symbolTable.STNameable;
import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.SymbolTableFactory;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.checks.CheckUtils;
import com.puppycrawl.tools.checkstyle.checks.coding.IllegalInstantiationCheck;

public class LegalComponentInstantiationCheck extends ComponentInstantiationCheck {

	/**
	 * A key is pointing to the warning message text in "messages.properties"
	 * file.
	 */
	public static final String MSG_KEY = "legalComponentInstantiation";
//	Map<DetailAST, List<DetailAST>> astToPendingTypeInstantiations = new HashMap();

//	public IllegalComponentInstantiationCheck() {
//		ContinuationNotifierFactory.getOrCreateSingleton()
//				.addContinuationProcessor(this);
//	}
	@Override
	protected String msgKey() {
		return MSG_KEY;
	}

//	@Override
//	public int[] getDefaultTokens() {
//		return new int[] { TokenTypes.PACKAGE_DEF, TokenTypes.CLASS_DEF,
//				// TokenTypes.INTERFACE_DEF,
//				TokenTypes.TYPE_ARGUMENTS,
//				// TokenTypes.TYPE_PARAMETERS,
//				TokenTypes.METHOD_DEF, TokenTypes.CTOR_DEF,
//				TokenTypes.LITERAL_NEW
//		// TokenTypes.METHOD_CALL
//		// TokenTypes.IMPORT, TokenTypes.STATIC_IMPORT,
//		// TokenTypes.PARAMETER_DEF
//		};
//	}

//	private void maybeAddToPendingTypeChecks(DetailAST anInstantiationAST) {
//		if (doPendingCheck(anInstantiationAST, currentTree) == null)
//			pendingChecks().add(anInstantiationAST);
//	}
//
//	public static boolean typesMatch(String aType1, String aType2) {
//		if (aType1.equals(aType2))
//			return true;;
//		return aType1.endsWith("." + aType2) || aType2.endsWith("." + aType1);
//
//	}
//	
//	public static DetailAST getClassDef (DetailAST aTreeAST) {
//		if (aTreeAST == null) return null;
//		if (aTreeAST.getType() == TokenTypes.CLASS_DEF)
//			return aTreeAST;
//		
//		return getClassDef(aTreeAST.getNextSibling());
//	}
//
//	public Boolean componentInstantiated(String anInstantiatedTypeName, DetailAST aTreeAST) {
//		DetailAST aClassDefAST = getClassDef(aTreeAST);
//		DetailAST aTypeAST = aClassDefAST.findFirstToken(TokenTypes.IDENT);
//		String aTypeName = FullIdent.createFullIdent(aTypeAST).getText();
//		
//		STType anInstantiatingSTClass = SymbolTableFactory
//				.getOrCreateSymbolTable().getSTClassByShortName(aTypeName);
//		if (anInstantiatingSTClass == null)
//			return null; // this should never happen
//		Map<String, PropertyInfo> aPropertyInfos = anInstantiatingSTClass
//				.getPropertyInfos();
//		if (aPropertyInfos == null)
//			return null; // our superclass has not been populated
//		STType anInstantiatedSTClass = SymbolTableFactory
//				.getOrCreateSymbolTable().getSTClassByShortName(anInstantiatedTypeName);
//		if (anInstantiatedSTClass == null)
//			return null; // have not built Symbol table for it
//		STNameable[] anInterfaces = anInstantiatedSTClass.getInterfaces();
//		for (String aPropertyName : aPropertyInfos.keySet()) {
//			PropertyInfo aPropertyInfo = aPropertyInfos.get(aPropertyName);
//			STMethod aGetter = aPropertyInfo.getGetter();
//			if (aGetter == null) {
//				continue; // not a real property
//			}
//			String aPropertyType = aGetter.getReturnType();
//			if (typesMatch(anInstantiatedTypeName, aPropertyType))
//				return true; // check failed
//			for (STNameable anInterface : anInterfaces) {
//				if (typesMatch(anInterface.getName(), aPropertyType))
//					return true;
//			}
//		}
//		return false;
//	}

	// fail if instantiate a componnet in a method other than init or
	// constructor
	public Boolean doPendingCheck(DetailAST ast, DetailAST aTreeAST) {
//		if (inConstructorOrInit(ast, aTreeAST))
//			return true;
//		if (currentMethodIsConstructor || AnSTType.isInit(currentMethodName))
//			return true;
//		DetailAST aTypeAST = ast.getFirstChild();
		final FullIdent anIdentifierType = FullIdent.createFullIdentBelow(ast);
		String anInstantiatedTypeName = anIdentifierType.getText();
		Boolean aComponentInstantiated = componentInstantiated(anInstantiatedTypeName, aTreeAST);
		if (aComponentInstantiated == null)
			return null;
		if (!aComponentInstantiated || !inConstructorOrInitOrStatic(ast, aTreeAST))
			return true; // check succeeds, strangely

		String aSourceName = shortFileName(astToFileContents.get(aTreeAST)
				.getFilename());
		// String aSourceName = toTypeName(aTreeAST);
//		if (aTreeAST == currentTree) {
//		log(anIdentifierType.getLineNo(), anIdentifierType.getColumnNo(),
//				msgKey(),  anInstantiatedTypeName,
//				aSourceName );
//		} else {
//			log(0,
//					msgKey(),  anInstantiatedTypeName,
//					aSourceName + ":" + anIdentifierType.getLineNo());
//		}
		super.log(anIdentifierType, aTreeAST, anInstantiatedTypeName);
		return false;

	}

//	void visitInstantiation(DetailAST ast) {
//		if (doPendingCheck(ast, currentTree) == null)
//				pendingChecks().add(ast);
//
//	}

//	boolean checkInstantiation(DetailAST ast) {
//		DetailAST aTypeAST = ast.getNextSibling();
//		maybeAddToPendingTypeChecks(aTypeAST);
//		DetailAST classAST = ast.getNextSibling();
//
//		return (currentMethodIsConstructor || AnSTType
//				.isInit(currentMethodName));
//
//	}

//	public void visitToken(DetailAST ast) {
//
//		if (ast.getType() == TokenTypes.LITERAL_NEW)
//			visitInstantiation(ast);
//		else
//			super.visitToken(ast);
//	}

}
