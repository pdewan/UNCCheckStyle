package unc.cs.checks;

import java.util.List;

import unc.cs.symbolTable.STNameable;
import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.SymbolTableFactory;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.sun.org.apache.bcel.internal.util.Class2HTML;


public  class ClassHasAtLeastOneInterfaceCheck extends STClassVisitedComprehensively {
	/**
	 * A key is pointing to the warning message text in "messages.properties"
	 * file.
	 */
	public static final String MSG_KEY = "classHasAtLeastOneInterface";

//	/** flag to control whether marker interfaces are allowed. */
//	private boolean allowMarkerInterfaces = true;
	
	public  ClassHasAtLeastOneInterfaceCheck () {
		
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
//		if (anSTClass.isInterface())
//			return true;
//		if (anSTClass.getName().contains("Vertical") || anSTClass.getName().contains("Anim")) {
////			System.out.println(" Let us check sizes:");
//			
//			System.out.println ("Signatures:" );
//			
//		}
		List<String> aSignatures = anSTClass.getPublicInstanceSignatures();
		if (aSignatures == null)
			return null;
		if (aSignatures.size() == 0)
			return true;

		STNameable[] anInterfaces = anSTClass.getDeclaredInterfaces();

		if (anInterfaces.length >= 1)
			return true;
		STNameable aSuperTypename = anSTClass.getSuperClass();
		if (aSuperTypename == null)
			return false; // no super type that implements interfaces
		STType aSuperSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aSuperTypename.getName());
		if (aSuperSTType == null) {
			return null; // later
		}
		List<String> aSuperSignatures = aSuperSTType.getPublicInstanceSignatures();
//		if (anSTClass.getName().contains("Vertical") || anSTClass.getName().contains("Anim")) {
////			System.out.println(" Let us check sizes:");
//			if (aSuperSignatures.size() != aSignatures.size()) {
//			System.out.println ("Signatures:" + aSignatures);
//			System.out.println("Super signature s:" + aSuperSignatures);
//			}
//		}
		if (aSuperSignatures == null)
			return null;
		return aSuperSignatures.size() == aSignatures.size(); // no new method, let super type worry about implementing interfaces

//		List<STNameable> anInterfaces = anSTClass.getAllInterfaces();
//		
//		if (anInterfaces == null)
//			return null;
////		boolean result = anAllInterfaces. size() >= 1;
//////		if (!result && anSTClass.getName().contains("Vertical") || anSTClass.getName().contains("Anim")) {
//////			System.out.println("failed at least one interface checK " + anSTClass.getName());
//////		}
////		if (!result) {
////			System.out.println("failed at least one interface checK " + anSTClass.getName());
////		}
//		return anInterfaces. size() >= 1; 

//		return anSTClass.getAllInterfaces().size() != 0; 
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
