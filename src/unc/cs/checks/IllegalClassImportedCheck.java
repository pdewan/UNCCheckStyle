package unc.cs.checks;

import java.util.HashSet;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.checks.imports.IllegalImportCheck;


public class IllegalClassImportedCheck extends UNCCheck {
	public static final String MSG_KEY = "illegalClassImported";

    Set<String>  illegalClasses = new HashSet();
    public int[] getDefaultTokens() {
        return new int[] {TokenTypes.IMPORT, TokenTypes.STATIC_IMPORT};
    }
    
    public void setIllegalClasses(String... from) {
    	for (String aClass:illegalClasses) {
    		illegalClasses.add(aClass);
    	}
    }
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
	 protected boolean isIllegalImport(String importText) {
		 return (illegalClasses.contains(importText));
	  }
	@Override
	protected String msgKey() {
		// TODO Auto-generated method stub
		return MSG_KEY;
	}
	

}
