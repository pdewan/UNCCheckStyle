package unc.cs.checks;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import unc.cs.symbolTable.SymbolTableFactory;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class IllegalTypeDefinedCheck extends TypeVisitedCheck{
	
	public static final String MSG_KEY = "illegalTypeDefined";
	Collection<String> illegalTypeNames = new HashSet();
	
	
	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CLASS_DEF, TokenTypes.ANNOTATION, TokenTypes.INTERFACE_DEF, TokenTypes.PACKAGE_DEF};
	}
	
	public void setIllegalTypeNames(String[] aNames) {
		illegalTypeNames = Arrays.asList(aNames);
		
	}
	
	public void visitType(DetailAST ast) {
    	super.visitType(ast);
    	if (illegalTypeNames.contains(this.shortTypeName))
		   log(getNameAST(ast).getLineNo(), msgKey(), shortTypeName);

    }
	public void doVisitToken(DetailAST ast) {
		
		switch (ast.getType()) {
		case TokenTypes.PACKAGE_DEF: 
			visitPackage(ast);
			return;
		case TokenTypes.CLASS_DEF:
		case TokenTypes.INTERFACE_DEF:
			visitType(ast);
			return;
		
		default:
			System.err.println("Unexpected token");
		}
		
	}
	@Override
	protected String msgKey() {
		// TODO Auto-generated method stub
		return MSG_KEY;
	}
	
}
