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


public class IllegalTypeImportedCheck extends UNCCheck {
	public static final String MSG_KEY = "illegalClassImported";
	
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
	    public void visitToken(DetailAST ast) {
	        final FullIdent imp;
	        if (ast.getType() == TokenTypes.IMPORT) {
	            imp = FullIdent.createFullIdentBelow(ast);
	        }
	        else {
	            imp = FullIdent.createFullIdent(
	                ast.getFirstChild().getNextSibling());
	        }
	        if (isIllegalImport(imp.getText())) {
	            log(ast.getLineNo(),
	                ast.getColumnNo(),
	                MSG_KEY,
	                imp.getText());
	        }
	    }
	 protected boolean isLocalPackage(String importText) {
		 for (String aPrefix:legalPrefixes) {
			 if (importText.startsWith(aPrefix))
				 return true;
		 }
		 return false;
	 }
	 protected boolean isPrefix (String anImport, List<String> aPrefixes) {
		 for (String aPrefix:aPrefixes) {
			 if (anImport.startsWith(aPrefix))
				 return true;
		 }
		 return false;
	 }
	 protected boolean isIllegalImport(String importText) {
		 if (illegalPrefixes != null && illegalPrefixes.size() > 1)
			 return isPrefix(importText, illegalPrefixes);
		 else if (legalPrefixes != null && legalPrefixes.size() > 1) 
			 return !isPrefix(importText, legalPrefixes);
		 else
			 return false;
			 
		 			 
	  }
	@Override
	protected String msgKey() {
		// TODO Auto-generated method stub
		return MSG_KEY;
	}
	

}
