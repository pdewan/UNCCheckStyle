package unc.cs.checks;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.api.DetailAST;

/**
 * Implements Bloch, Effective Java, Item 17 - Use Interfaces only to define
 * types.
 * 
 * <p>
 * An interface should describe a <em>type</em>, it is therefore inappropriate
 * to define an interface that does not contain any methods but only constants.
 * </p>
 * 
 * <p>
 * The check can be configured to also disallow marker interfaces like
 * <code>java.io.Serializable</code>, that do not contain methods or constants
 * at all.
 * </p>
 * 
 * @author lkuehne
 */
public final class ClassHasOneInterfaceCheck extends UNCCheck {

	/**
	 * A key is pointing to the warning message text in "messages.properties"
	 * file.
	 */
	public static final String MSG_KEY = "classHasOneInterface";

	/** flag to control whether marker interfaces are allowed. */
	private boolean allowMarkerInterfaces = true;


	public ClassHasOneInterfaceCheck() {

	}
	   

	public static boolean isPublicInstanceMethod(DetailAST methodOrVariableDef) {
		boolean foundPublic = false;
		final DetailAST modifiersAst = methodOrVariableDef
				.findFirstToken(TokenTypes.MODIFIERS);
		if (modifiersAst.getFirstChild() != null) {

			for (DetailAST modifier = modifiersAst.getFirstChild(); modifier != null; modifier = modifier
					.getNextSibling()) {
				// System.out.println("Checking modifier:" + modifier);
				if (modifier.getType() == TokenTypes.LITERAL_STATIC) {
					// System.out.println("Not instance");
					return false;
				}
				if (modifier.getType() == TokenTypes.LITERAL_PUBLIC) {
					foundPublic = true;
				}

			}
		}
		// System.out.println("instance");

		return foundPublic;
	}

	@Override
	public int[] getDefaultTokens() {
		return new int[] { TokenTypes.CLASS_DEF};
	}

//	@Override
//	public int[] getRequiredTokens() {
//		return getDefaultTokens();
//	}
//
//	@Override
//	public int[] getAcceptableTokens() {
//		return new int[] { TokenTypes.CLASS_DEF };
//	}

	boolean hasOneInterface(DetailAST aClassDef) {
		
		int numInterfaces = 0;
		DetailAST implementsClause = aClassDef
				.findFirstToken(TokenTypes.IMPLEMENTS_CLAUSE);

		if (implementsClause == null)
			return false;
		DetailAST anImplementedInterface = implementsClause
				.findFirstToken(TokenTypes.IDENT);
		while (anImplementedInterface != null) {
			if (anImplementedInterface.getType() == TokenTypes.IDENT)
				numInterfaces++;
			anImplementedInterface = anImplementedInterface.getNextSibling();
		}
		return numInterfaces == 1;
	}

	@Override
	public void visitToken(DetailAST ast) {	
    	System.out.println("Check called:" + MSG_KEY);
		DetailAST classNameAST = ast.findFirstToken(TokenTypes.IDENT);
    	String name = classNameAST.getText();
    	System.out.println ("Visiting class:" + name);
		final DetailAST objBlock = ast.findFirstToken(TokenTypes.OBJBLOCK);
		if (hasOneInterface(ast)) {
			return;
		}

		DetailAST methodDef = objBlock.findFirstToken(TokenTypes.METHOD_DEF);
		while (methodDef != null) {
			if (methodDef.getType() == TokenTypes.METHOD_DEF) // for some reason
																// curly brace
																// is last
																// sibling {

				// System.out.println("Checking:" + methodDef);
				if (isPublicInstanceMethod(methodDef)) {
					// System.out.println("Started Logging");

					extendibleLog(ast.getLineNo(), MSG_KEY);
					// System.out.println("Ended Logging");

					return;
				}

			// System.out.println("Setting next sibling");

			methodDef = methodDef.getNextSibling();

			// System.out.println("Set next sibling");

		}

		// final DetailAST variableDef = objBlock
		// .findFirstToken(TokenTypes.VARIABLE_DEF);
		// final boolean methodRequired = !allowMarkerInterfaces
		// || variableDef != null;
		//
		// if (methodDef == null && methodRequired) {
		// log(ast.getLineNo(), MSG_KEY);
		// }

	}

	/**
	 * Controls whether marker interfaces like Serializable are allowed.
	 * 
	 * @param flag
	 *            whether to allow marker interfaces or not
	 */
	public void setAllowMarkerInterfaces(boolean flag) {
		allowMarkerInterfaces = flag;
	}
}
