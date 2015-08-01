package unc.cs.checks;

import unc.cs.symbolTable.STType;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException;

public abstract class TypeVisitedCheck extends UNCCheck {
	public static final String MSG_KEY = "typeDefined";	
	public static final String DEFAULT_PACKAGE = "default"; 
	protected String packageName;
	protected String fullTypeName, shortTypeName;
	protected DetailAST typeAST;
	protected DetailAST typeNameAST;
	protected boolean isGeneric;
	@Override
    public void doBeginTree(DetailAST ast) {
        packageName = DEFAULT_PACKAGE;
    } 
//	@Override
//	public int[] getDefaultTokens() {
//		return new int[] {TokenTypes.PACKAGE_DEF, TokenTypes.CLASS_DEF,  
//						TokenTypes.INTERFACE_DEF, TokenTypes.METHOD_DEF, TokenTypes.PARAMETER_DEF };
//	}
	
	
	public boolean maybeVisitPackage(DetailAST ast) {
		if (ast.getType() == TokenTypes.PACKAGE_DEF) {
//			packageName = ast.findFirstToken(TokenTypes.IDENT).getText();
//			System.out.println("found package:" + packageName);	
			visitPackage(ast);
			return true;
		}
		return false;
	}
	 public void visitType(DetailAST ast) {  
//	    	
//			maybeVisitPackage(ast);
	    	typeAST = ast;
	    	typeNameAST = ast.findFirstToken(TokenTypes.IDENT);
	    	isGeneric =  (typeNameAST.getNextSibling().getType() == TokenTypes.TYPE_PARAMETERS);
			 shortTypeName = typeNameAST.getText();
			 fullTypeName = packageName + "." + shortTypeName;
//			aFullTypeName = aFullName;
	 }
//	 public void visitEnum(DetailAST ast) {  
////	    	
////			maybeVisitPackage(ast);
//	    	typeAST = ast;
//	    	typeNameAST = ast.getNextSibling();
//	    	
//			 fullTypeName = packageName + "." + shortTypeName;
////			aFullTypeName = aFullName;
//	 }
	 protected static String getEnumName(DetailAST anEnumDef) {
	    	return getEnumNameAST(anEnumDef).toString();
	    }
	    protected static DetailAST getEnumNameAST(DetailAST anEnumDef) {
	    	return anEnumDef.getNextSibling();
	    }
	    public static String getPackageName (DetailAST ast) {
	    	FullIdent aFullIdent = FullIdent.createFullIdent(ast.getFirstChild().getNextSibling());
//			DetailAST anEnclosingType = TagBasedCheck.getEnclosingTypeDeclaration(ast);
//			packageName = getName(ast);
			return aFullIdent.getText();
	    }
	public void visitPackage(DetailAST ast) {
//		FullIdent aFullIdent = FullIdent.createFullIdent(ast.getFirstChild().getNextSibling());
//		packageName = aFullIdent.getText();
		packageName = getPackageName(ast);

	}
	protected void log(DetailAST ast) {
	    log(getNameAST(ast).getLineNo(), msgKey(), fullTypeName);
    }
	// not a full name I assume
	public static String getName (DetailAST anAST) {
//		return anAST.findFirstToken(TokenTypes.IDENT).getText();
		if (anAST.getType() == TokenTypes.ENUM)
			return getEnumName(anAST);
			
		DetailAST aNameAST = anAST.findFirstToken(TokenTypes.IDENT);
//		if (aNameAST == null) {
//			System.err.println("no ident!");
//			return null;
//		}
		return aNameAST.getText();
		
//		return getNameAST(anAST).getText();

	}
//	  protected static String getEnumName(DetailAST anEnumDef) {
//	    	return anEnumDef.getNextSibling().toString();
//	    }
//	    
	
	public static DetailAST getNameAST (DetailAST anAST) {
		return anAST.findFirstToken(TokenTypes.IDENT);
	}
	public static String toShortTypeName (String aTypeName) {
		int aDotIndex = aTypeName.lastIndexOf(".");
		String aShortTypeName = aTypeName;
		if (aDotIndex != -1)
			aShortTypeName = aTypeName.substring(aDotIndex + 1);
		return aShortTypeName;
	}
  
	
	
    
}
