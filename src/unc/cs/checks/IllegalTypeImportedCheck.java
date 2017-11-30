package unc.cs.checks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.checks.imports.IllegalImportCheck;


public class IllegalTypeImportedCheck extends ComprehensiveVisitCheck {
	public static final String MSG_KEY = "illegalTypeImported";
	
//    List<String>  legalClasses = new ArrayList();
    List<String>  illegalPrefixes;


//    Set<String>  illegalClasses = new HashSet();
    List<String>  legalPrefixes;
    public int[] getDefaultTokens() {
        return new int[] {TokenTypes.IMPORT, TokenTypes.STATIC_IMPORT};
    }
    
    public void setIllegalPrefixes(String... from) {
    	illegalPrefixes = Arrays.asList(from);
//    	for (String aPrefix:from) {
//    		illegalPrefixes.add(aPrefix);
//    	}
    }
    public void setLegalPrefixes(String... from) {
    	legalPrefixes = Arrays.asList(from);
//    	legalPrefixes.addAll(Arrays.asList(STBuilderCheck.getProjectPackagePrefixes()));
//    	for (String aClass:from) {
//    		legalPrefixes.add(aClass);
//    	}
    }
//    public void setLegalPackagesPrefixes(String... from) {
//    	for (String aPrefix:from) {
//    		legalPrefixes.add(aPrefix);
//    	}
//    }
    @Override
    public Boolean doPendingCheck(DetailAST ast, DetailAST aTreeAST) {
        final FullIdent imp;
        if (ast.getType() == TokenTypes.IMPORT) {
            imp = FullIdent.createFullIdentBelow(ast);
        }
        else {
            imp = FullIdent.createFullIdent(
                ast.getFirstChild().getNextSibling());
        }
        String aMyClass = getEnclosingShortTypeName(ast);
        Boolean isIllegal = isIllegalImport(imp.getText(), aMyClass);
        if (isIllegal == null)
        	return null;
        if (isIllegal) {
        	super.log (imp, aTreeAST, imp.getText(), aMyClass);
//        	if (currentTree == aTreeAST) {
//        
//            log(imp.getLineNo(), imp.getColumnNo(),
//                MSG_KEY,
//                imp.getText(), aMyClass);
//            
//        	} 
//        	else {
//        		log(0,
//                        MSG_KEY,
//                        imp.getText(), aMyClass);
//        	}
        }
        	
       return isIllegal;
    }
    
	 @Override
	    public void doVisitToken(DetailAST ast) {
//	        final FullIdent imp;
//	        if (ast.getType() == TokenTypes.IMPORT) {
//	            imp = FullIdent.createFullIdentBelow(ast);
//	        }
//	        else {
//	            imp = FullIdent.createFullIdent(
//	                ast.getFirstChild().getNextSibling());
//	        }
//	        if (isIllegalImport(imp.getText(), getEnclosingShortTypeName(ast))) {
//	            log(ast.getLineNo(),
//	                ast.getColumnNo(),
//	                MSG_KEY,
//	                imp.getText());
//	        }
//		 doPendingCheck(ast, currentTree);
		 if (ast.getType() == TokenTypes.IMPORT || ast.getType() == TokenTypes.STATIC_IMPORT)
			 maybeAddToPendingTypeChecks(ast);
		 else
			 super.doVisitToken(ast);
	    }
	 protected boolean isLocalPackage(String importText) {
		 for (String aPrefix:legalPrefixes) {
			 if (importText.startsWith(aPrefix))
				 return true;
		 }
		 return false;
	 }
	 
//	 protected boolean isPrefix (String aTarget, List<String> aPrefixes, String myClassName) {
//		 for (String aPrefix:aPrefixes) {
//			 String[] aPrefixParts = aPrefix.split(">");
//			 if ((aPrefixParts.length == 2) && !matchesMyClass(myClassName, aPrefixParts[0]))
//				 continue; // not relevant
//			 String aTruePrefix = aPrefixParts.length == 2?aPrefixParts[1]:aPrefix;
//			 if (aTarget.startsWith(aTruePrefix))
//				 return true;
//		 }
//		 return false;
//	 }
	 protected Boolean inIllegalPrefixes(String importText, String myClassName) {
		 return illegalPrefixes != null && illegalPrefixes.size() > 0 && isPrefix(importText, illegalPrefixes, myClassName);
	 }
	 protected Boolean inLegalPrefixes(String importText, String myClassName) {
		 return legalPrefixes != null && legalPrefixes.size() > 0 && isPrefix(importText, legalPrefixes, myClassName);
	 }
	
	 /*
	  * if project import, then not illegal
	  * if a tag then not illegal
	  * if an illegal prefix, then illegal
	  * if  in legal prefixes, not illegal
	  * 
	  */
	 protected Boolean isIllegalImport(String importText, String myClassName) {
		 if (importText.startsWith("java.lang")) {
			 return false; // in case you import, for instance, java.lang.Thread
		 }
		 if (STBuilderCheck.isProjectImport(importText) || importText.endsWith("Tags") ) {
			 return false;
		 }
		 if (inLegalPrefixes(importText, myClassName))
			 return false;
		 if (inIllegalPrefixes(importText, myClassName))
			 return true;
		 return false;
//		 return !STBuilderCheck.isProjectImport(importText) && 
//				 !importText.endsWith("Tags"); //to allow bootstrapping for tag annotations
		 
			 
			 
		 			 
	  }

//	 protected Boolean isIllegalImport(String importText, String myClassName) {
//		 return !(STBuilderCheck.isProjectImport(importText) // if project import then check the rest, if  not a projecct import t
//				|| importText.endsWith("Tags") // to allow bootstrapping for tag annotations
//		        || !inIllegalPrefixes(importText, myClassName)
//		        || inLegalPrefixes(importText, myClassName));
//			 		
//		 
//			 
////		 if (illegalPrefixes != null && illegalPrefixes.size() > 1)
////			 return isPrefix(importText, illegalPrefixes, myClassName);
////		 else if (legalPrefixes != null && legalPrefixes.size() > 1) 
////			 return !isPrefix(importText, legalPrefixes, myClassName);
////		 else
////			 return false;
//			 
//		 			 
//	  }
	@Override
	protected String msgKey() {
		// TODO Auto-generated method stub
		return MSG_KEY;
	}
	

}
