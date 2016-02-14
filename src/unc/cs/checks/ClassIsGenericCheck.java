package unc.cs.checks;

import java.util.List;

import unc.cs.symbolTable.STNameable;
import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.SymbolTableFactory;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.sun.org.apache.bcel.internal.util.Class2HTML;


public  class ClassIsGenericCheck extends STClassVisitedComprehensively {
	/**
	 * A key is pointing to the warning message text in "messages.properties"
	 * file.
	 */
	public static final String MSG_KEY = "classIsGeneric";

//	/** flag to control whether marker interfaces are allowed. */
//	private boolean allowMarkerInterfaces = true;
	
	public  ClassIsGenericCheck () {
		
	}
//	@Override
//	public int[] getDefaultTokens() {
//		return new int[] { TokenTypes.CLASS_DEF };
//	}
//	@Override
//    public void visitClass(DetailAST typeDef) { 
//		super.visitClass(typeDef);
//    	
//		STType anSTClass = SymbolTableFactory.getOrCreateSymbolTable().
//    			getSTClassByFullName(fullTypeName);
//    	if (!typeCheck(anSTClass))
//    		log(typeDef);
//
//    }
//	
//	protected void log(DetailAST ast) {
//	    log(getNameAST(ast).getLineNo(), msgKey(), fullTypeName);
//    }


	protected Boolean typeCheck(STType anSTClass) {
		if (typeAST == null) {
			return false;
		}
		DetailAST generic = typeAST.findFirstToken(TokenTypes.TYPE_PARAMETERS);
				return generic != null;
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
