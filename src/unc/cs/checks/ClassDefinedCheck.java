package unc.cs.checks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import unc.cs.symbolTable.SymbolTableFactory;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class ClassDefinedCheck extends ComprehensiveVisitCheck{
	
	public static final String MSG_KEY = "classDefined";
	protected List<String> expectedClasses = new ArrayList();
	
	
	@Override
	public int[] getDefaultTokens() {
		return new int[] {
				TokenTypes.PACKAGE_DEF,
				TokenTypes.CLASS_DEF,
				};
	}
	public void setExpectedClasses(String[] anExpectedClasses) {
		expectedClasses = Arrays.asList(anExpectedClasses);
		
	}
	public void visitType(DetailAST ast) {  

    	super.visitType(ast);
    	for (String anExpectedClassOrTag:expectedClasses) {
    		if ( matchesMyType(anExpectedClassOrTag)) {
//    			DetailAST aTypeAST = getEnclosingClassDeclaration(currentTree);
    			log(currentTree, msgKey(), shortTypeName, expectedClasses.toString());
    		}
    		
    		
    	}


    }
	public void doVisitToken(DetailAST ast) {
		super.doVisitToken(ast);
		
//		switch (ast.getType()) {
////		case TokenTypes.PACKAGE_DEF: 
////			visitPackage(ast);
////			return;
//		case TokenTypes.CLASS_DEF:
//			visitType(ast);
//			return;
//		
//		default:
//			System.err.println("Unexpected token");
//		}
		
	}
	@Override
	protected String msgKey() {
		// TODO Auto-generated method stub
		return MSG_KEY;
	}
	
}
