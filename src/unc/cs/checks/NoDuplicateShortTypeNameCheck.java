package unc.cs.checks;

import unc.cs.symbolTable.SymbolTable;
import unc.cs.symbolTable.SymbolTableFactory;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class NoDuplicateShortTypeNameCheck extends TypeVisitedCheck {
	public static final String MSG_KEY = "noDuplicateShortTypeName";
	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CLASS_DEF, TokenTypes.INTERFACE_DEF, TokenTypes.PACKAGE_DEF};
	}
	public void visitToken(DetailAST ast) {
//    	System.out.println("Check called:" + msgKey());

		if (maybeVisitPackage(ast) ) 
			return;
		
		DetailAST aTypeNameAST = ast.findFirstToken(TokenTypes.IDENT);
		String aTypeName = aTypeNameAST.getText();
		String aFullTypeName = packageName + "." + aTypeName;
		SymbolTable aSymbolTable = SymbolTableFactory.getOrCreateSymbolTable();
		if (aSymbolTable.matchingFullSTTypeNames(aTypeName).size() > 1) {
			System.out.println("dupliicateshortname:" + aFullTypeName);
			log(aTypeNameAST.getLineNo(), aTypeNameAST.getColumnNo(), msgKey(),
					aTypeNameAST.getText());
		}			
//		SymbolTableFactory.getOrCreateSymbolTable().getInterfaceNameToAST().put(packageName + "." + aTypeName, ast);
		
	}
	@Override
	protected String msgKey() {
		// TODO Auto-generated method stub
		return MSG_KEY;
	}

}
