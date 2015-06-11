package unc.cs.checks;

import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.SymbolTableFactory;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.sun.org.apache.bcel.internal.util.Class2HTML;


public  class ClassHasAtLeastOneInterfaceCheck extends STClassVisited {
	/**
	 * A key is pointing to the warning message text in "messages.properties"
	 * file.
	 */
	public static final String MSG_KEY = "classHasAtLeastOneInterface";

//	/** flag to control whether marker interfaces are allowed. */
//	private boolean allowMarkerInterfaces = true;
	
	public  ClassHasAtLeastOneInterfaceCheck () {
		
	}
	protected boolean typeCheck(STType anSTClass) {
		return anSTClass.getInterfaces().length != 0; 
	}
//	protected void log(DetailAST ast) {
//		log(ast.getLineNo(), msgKey(), typeName);
//	}
	@Override
	protected String msgKey() {
		// TODO Auto-generated method stub
		return MSG_KEY;
	}

	
	
}
