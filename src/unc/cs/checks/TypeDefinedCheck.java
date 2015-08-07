package unc.cs.checks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import unc.cs.symbolTable.SymbolTableFactory;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class TypeDefinedCheck extends ComprehensiveVisitCheck{
	
	protected List<String> expectedTypes = new ArrayList();	
	protected List<String> unmatchedTypes = new ArrayList();
	protected boolean overlappingTags;
//	@Override
//	public int[] getDefaultTokens() {
//		return new int[] {
//				TokenTypes.PACKAGE_DEF,
//				TokenTypes.INTERFACE_DEF,
////				TokenTypes.ANNOTATION,
//				};
//	}
	public void setExpectedTypes(String[] anExpectedClasses) {
		expectedTypes = Arrays.asList(anExpectedClasses);
		unmatchedTypes = new ArrayList(expectedTypes);		
	}
	
	public void setOverlappingTags(boolean newVal) {
		overlappingTags = newVal;		
	}
	
	public void visitType(DetailAST ast) {  

    	super.visitType(ast);
    	Boolean check = checkIncludeExcludeTagsOfCurrentType();
    	if (check == null)
    		return;
    	if (!check)
    		return;
    	List<String> checkTags = new ArrayList( overlappingTags?expectedTypes:unmatchedTypes);
    	
//			log(currentTree, msgKey(), shortTypeName, expectedClasses.toString());

    	for (String anExpectedClassOrTag:checkTags) {
    		if ( matchesMyType(anExpectedClassOrTag)) {
    			unmatchedTypes.remove(anExpectedClassOrTag);
    			DetailAST aTypeAST = getEnclosingClassDeclaration(currentTree);
//    			log(currentTree, msgKey(), shortTypeName, unmatchedClasses.toString());
    			log(currentTree, anExpectedClassOrTag, unmatchedTypes.toString());
    		}
    	}
//    		
//    		
//    	}
//    	
//    	for (String anExpectedClassOrTag:expectedClasses) {
//    		if ( matchesMyType(anExpectedClassOrTag)) {
////    			DetailAST aTypeAST = getEnclosingClassDeclaration(currentTree);
//    			log(currentTree, msgKey(), shortTypeName, expectedClasses.toString());
//    		}
//    		
//    		
//    	}


    }
//	public void doVisitToken(DetailAST ast) {
////    	System.out.println("Check called:" + MSG_KEY);
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
//	public void doVisitToken(DetailAST ast) {
//		super.doVisitToken(ast);
//		
////		switch (ast.getType()) {
//////		case TokenTypes.PACKAGE_DEF: 
//////			visitPackage(ast);
//////			return;
////		case TokenTypes.CLASS_DEF:
////			visitType(ast);
////			return;
////		
////		default:
////			System.err.println("Unexpected token");
////		}
//		
//	}
	@Override
	protected String msgKey() {
		// TODO Auto-generated method stub
		return MSG_KEY;
	}
	
}
