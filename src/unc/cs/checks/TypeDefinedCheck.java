package unc.cs.checks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import unc.cs.symbolTable.SymbolTableFactory;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class TypeDefinedCheck extends ComprehensiveVisitCheck{
	
	protected List<String> expectedTypes = new ArrayList();	
	protected List<String> unmatchedTypes = new ArrayList();
	protected Map<String, String> tagMatches = new HashMap();
//	protected Set<String> matchedTypes = new HashSet();
	protected boolean overlappingTags = true;
	protected boolean logNoMacthes = true;
//	protected boolean shownMissingClasses = true;
//	@Override
//	public int[] getDefaultTokens() {
//		return new int[] {
//				TokenTypes.PACKAGE_DEF,
//				TokenTypes.INTERFACE_DEF,
////				TokenTypes.ANNOTATION,
//				};
//	}
	public TypeDefinedCheck() {
		checkOnBuild = true;
	}
	public void setExpectedTypes(String[] anExpectedClasses) {
		expectedTypes = Arrays.asList(anExpectedClasses);
		unmatchedTypes = new ArrayList(expectedTypes);		
	}
	
	public void setOverlappingTags(boolean newVal) {
		overlappingTags = newVal;		
	}
	
	public void setLogNoMatches(boolean newVal) {
		logNoMacthes = newVal;
	}
	

	public void visitClassOrInterface(DetailAST ast) {  
		
//    	super.visitType(ast);
//    	if (fullTypeName.contains("ListImp")) {
//    		System.out.println ("found inner interface");
//    	}
//    	if (shownMissingClasses) {
//			log("expectedTypes", ast, ast, expectedTypes.toString().replaceAll(",", " "));
//			shownMissingClasses = false;
//
//		} 
    	Boolean check = checkIncludeExcludeTagsOfCurrentType();
    	if (check == null)
    		return;
    	if (!check)
    		return;
    	List<String> checkTags = new ArrayList( overlappingTags?expectedTypes:unmatchedTypes);
//    	System.out.println("Checking full type name: " + fullTypeName);
    	if (tagMatches.containsKey(getFullTypeName())) {
    		tagMatches.remove(getFullTypeName());
    		if (!overlappingTags) {
    			unmatchedTypes.remove(tagMatches.get(getFullTypeName()));
    		}
    	}
    	
    	boolean aFoundMatch = false;
    	for (String anExpectedClassOrTag:checkTags) {
    		if ( matchesMyType(maybeStripComment(anExpectedClassOrTag))) {
    			tagMatches.put(getFullTypeName(), anExpectedClassOrTag);
//    			matchedTypes.add(fullTypeName);
    			unmatchedTypes.remove(anExpectedClassOrTag);
//    			if (shownMissingClasses) {
//    				log("expectedTypes", ast, ast, expectedTypes.toString().replaceAll(",", " "));
//        			shownMissingClasses = false;
//
//    			} 
//    			else {

//    			log(ast, anExpectedClassOrTag, unmatchedTypes.toString().replaceAll(",", " "));
    			log(ast, anExpectedClassOrTag);
    			aFoundMatch = false;
//
//    			}
    		}
    		
    	}
    	if (!aFoundMatch && logNoMacthes) {
			log(ast, "No Expected Tag");
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
