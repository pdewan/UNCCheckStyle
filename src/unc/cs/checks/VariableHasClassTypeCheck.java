package unc.cs.checks;

////////////////////////////////////////////////////////////////////////////////
//checkstyle: Checks Java source code for adherence to a set of rules.
//Copyright (C) 2001-2015 the original author or authors.
//
//This library is free software; you can redistribute it and/or
//modify it under the terms of the GNU Lesser General Public
//License as published by the Free Software Foundation; either
//version 2.1 of the License, or (at your option) any later version.
//
//This library is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//Lesser General Public License for more details.
//
//You should have received a copy of the GNU Lesser General Public
//License along with this library; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
////////////////////////////////////////////////////////////////////////////////

import com.google.common.collect.Sets;
import com.puppycrawl.tools.checkstyle.Utils;
import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FileContents;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.checks.AbstractFormatCheck;
import com.puppycrawl.tools.checkstyle.checks.CheckUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.SymbolTableFactory;

public final class VariableHasClassTypeCheck extends ComprehensiveVisitCheck implements
		ContinuationProcessor {

	public VariableHasClassTypeCheck() {
		ContinuationNotifierFactory.getOrCreateSingleton()
				.addContinuationProcessor(this);
	}

	/**
	 * A key is pointing to the warning message text in "messages.properties"
	 * file.
	 */
	public static final String MSG_KEY = "variableHasClassType";
//	Map<DetailAST, List<DetailAST>> astToPendingChecks = new HashMap();
//	Map<DetailAST, FileContents> astToFileContents = new HashMap();
	// List<FullIdent> pendingTypeUses = new ArrayList();

	/*
	 * Actually ignored types not ncessary with deferred processing but reduces
	 * size of pending checkables
	 */
	static final String[] IGNORED_TYPES = { "void", "int", "double", "float",
			"boolean", "short", "char", "Integer", "Double", "Float",
			"Boolean", "Short", "Character", "String", "Scanner", "List",
			"HashSet", "Set" };

	final static Set<String> ignoreTypesSet = new HashSet();

	@Override
	public int[] getDefaultTokens() {
		return new int[] { TokenTypes.VARIABLE_DEF, TokenTypes.PARAMETER_DEF,
				TokenTypes.METHOD_DEF };
	}

	@Override
	public void visitToken(DetailAST ast) {
		System.out.println("Check called:" + MSG_KEY);

		switch (ast.getType()) {
		case TokenTypes.METHOD_DEF:
			visitMethodDef(ast);
			break;
		case TokenTypes.VARIABLE_DEF:
			visitVariableDef(ast);
			break;
		case TokenTypes.PARAMETER_DEF:
			visitParameterDef(ast);
			break;

		default:
			throw new IllegalStateException(ast.toString());
		}
	}

	@Override
	protected String msgKey() {
		// TODO Auto-generated method stub
		return MSG_KEY;
	}

	/**
	 * Checks return type of a given method.
	 * 
	 * @param methodDef
	 *            method for check.
	 */
	private void visitMethodDef(DetailAST methodDef) {
		// if (isCheckedMethod(methodDef)) {
		maybeAddToPendingTypeChecks(methodDef);
		// }
	}

	/**
	 * Checks type of parameters.
	 * 
	 * @param paradef
	 *            parameter list for check.
	 */
	private void visitParameterDef(DetailAST paradef) {
		final DetailAST grandParentAST = paradef.getParent().getParent();

		if (grandParentAST.getType() == TokenTypes.METHOD_DEF)
		// && isCheckedMethod(grandParentAST)) {
		maybeAddToPendingTypeChecks(paradef);
		// }
	}

	/**
	 * Checks type of given variable.
	 * 
	 * @param variableDef
	 *            variable to check.
	 */
	private void visitVariableDef(DetailAST variableDef) {
		maybeAddToPendingTypeChecks(variableDef);
	}

	/**
	 * Checks type of given method, parameter or variable.
	 * 
	 * @param ast
	 *            node to check.
	 */
	private void maybeAddToPendingTypeChecks(DetailAST ast) {
		final DetailAST aType = ast.findFirstToken(TokenTypes.TYPE);
		final DetailAST anIdentifier = ast.findFirstToken(TokenTypes.IDENT);
		final FullIdent anIdentifierType = CheckUtils.createFullType(aType);
		if (ignoreTypesSet.contains(anIdentifierType.getText()))
			return;
		if (doPendingCheck(ast, currentTree) == null)
			pendingChecks().add(ast);

		// if (isMatchingClassName(ident.getText())) {
		// log(ident.getLineNo(), ident.getColumnNo(), msgKey(),
		// ident.getText());
		// }
	}

//	public static String shortFileName(String longName) {
//		int index = longName.lastIndexOf('/');
//		if (index <= 0)
//			index = longName.lastIndexOf('\\');
//		if (index <= 0)
//			return longName;
//		return longName.substring(index + 1);
//	}

	public static String toTypeName(DetailAST aTreeAST) {
		DetailAST aTypeDeclaration = aTreeAST
				.findFirstToken(TokenTypes.CLASS_DEF);
		DetailAST aTypeName = aTreeAST.findFirstToken(TokenTypes.IDENT);
		return aTypeName.getText();
	}

	/*
	 * check returns non null if check finished
	 */
//	public Boolean checkIdentifierType(DetailAST ast, DetailAST aTreeAST) {
//		final DetailAST aType = ast.findFirstToken(TokenTypes.TYPE);
//		final DetailAST anIdentifier = ast.findFirstToken(TokenTypes.IDENT);
//		final FullIdent anIdentifierType = CheckUtils.createFullType(aType);
//		String aTypeName = anIdentifierType.getText();
//		if (!SymbolTableFactory.getOrCreateSymbolTable().isType(aTypeName))
//			return null;
//		if (isMatchingClassName(anIdentifierType.getText())) {
//			 String aSourceName =
//			 shortFileName(astToFileContents.get(aTreeAST).getFilename());
////			String aSourceName = toTypeName(aTreeAST);
//			log(anIdentifierType.getLineNo(), anIdentifierType.getColumnNo(),
//					msgKey(), anIdentifierType.getText(),
//					anIdentifier.getText(),
//					aSourceName + ":" + anIdentifier.getLineNo());
//			return true;
//		}
//		return false;
//	}
	protected boolean checkType(STType anSTClass) {
		return anSTClass.isInterface();
	}
	public Boolean doPendingCheck(DetailAST ast, DetailAST aTreeAST) {
		final DetailAST aType = ast.findFirstToken(TokenTypes.TYPE);
		final DetailAST anIdentifier = ast.findFirstToken(TokenTypes.IDENT);
		final FullIdent anIdentifierType = CheckUtils.createFullType(aType);
		String aTypeName = anIdentifierType.getText();
		STType anSTClass = SymbolTableFactory.getOrCreateSymbolTable().
				getSTClassByShortName(aTypeName);
		if (anSTClass == null)
//		if (!SymbolTableFactory.getOrCreateSymbolTable().isType(aTypeName))
			return null;
		if (!checkType(anSTClass)) {
			 String aSourceName =
			 shortFileName(astToFileContents.get(aTreeAST).getFilename());
//			String aSourceName = toTypeName(aTreeAST);
			log(anIdentifierType.getLineNo(), anIdentifierType.getColumnNo(),
					msgKey(), anIdentifierType.getText(),
					anIdentifier.getText(),
					aSourceName + ":" + anIdentifier.getLineNo());
			return true;
		}
		return false;
	}
	
	


//	public void doPendingChecks() {
//		// for (List<FullIdent> aPendingTypeUses:astToPendingTypeUses.values())
//		// {
//
//		for (DetailAST aPendingAST : astToPendingChecks.keySet()) {
//			List<DetailAST> aPendingChecks = astToPendingChecks.get(aPendingAST);
//			// FileContents aFileContents = astToFileContents.get(anAST);
//			// setFileContents(aFileContents);
//
//			if (aPendingChecks.isEmpty())
//				continue;
//			List<DetailAST> aPendingTypeChecksCopy = new ArrayList(
//					aPendingChecks);
//			for (DetailAST aPendingCheck : aPendingTypeChecksCopy) {
//				if (doPendingCheck(aPendingCheck, aPendingAST) != null)
//					aPendingChecks.remove(aPendingCheck);
//
//			}
//		}
//	}

	/**
	 * @param className
	 *            class name to check.
	 * @return true if given class name is one of illegal classes or if it
	 *         matches to abstract class names pattern.
	 */
	boolean isMatchingClassName(String className) {
		return SymbolTableFactory.getOrCreateSymbolTable().isClass(className);
	}

	@Override
	public void processDeferredChecks() {
		doPendingChecks();

	}

	List<DetailAST> pendingChecks() {
		List<DetailAST> result = astToPendingChecks.get(currentTree);
		if (result == null) {
			result = new ArrayList<>();
			astToPendingChecks.put(currentTree, result);
		}
		return result;
	}

	DetailAST currentTree;

	@Override
	public void beginTree(DetailAST ast) {
		System.out.println("ID =" + getId());
		System.out.println("Message Bundle:" + getMessageBundle());
		astToFileContents.put(ast, getFileContents());
		currentTree = ast;
		pendingChecks().clear();
	}

	@Override
	public void finishTree(DetailAST ast) {
		ContinuationNotifierFactory.getOrCreateSingleton()
				.notifyContinuationProcessors();
	}
	

	static {
		for (String aType : IGNORED_TYPES) {
			ignoreTypesSet.add(aType);
		}
	}

}
