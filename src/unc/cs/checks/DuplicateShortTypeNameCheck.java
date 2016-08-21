package unc.cs.checks;

import java.util.Arrays;
import java.util.List;

import unc.cs.symbolTable.SymbolTable;
import unc.cs.symbolTable.SymbolTableFactory;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class DuplicateShortTypeNameCheck extends ComprehensiveVisitCheck {
	public static final String MSG_KEY = "duplicateShortTypeName";
	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CLASS_DEF, TokenTypes.INTERFACE_DEF, TokenTypes.PACKAGE_DEF, 
//				TokenTypes.ANNOTATION
				};
	}
//	public void doVisitToken(DetailAST ast) {
////    	System.out.println("Check called:" + msgKey());
//
//		if (maybeVisitPackage(ast) ) 
//			return;
//		
//		DetailAST aTypeNameAST = ast.findFirstToken(TokenTypes.IDENT);
//		String aTypeName = aTypeNameAST.getText();
//		String aFullTypeName = packageName + "." + aTypeName;
//		SymbolTable aSymbolTable = SymbolTableFactory.getOrCreateSymbolTable();
//		if (aSymbolTable.matchingFullSTTypeNames(aTypeName).size() > 1) {
//			System.out.println("dupliicateshortname:" + aFullTypeName);
//			log(aTypeNameAST.getLineNo(), aTypeNameAST.getColumnNo(), msgKey(),
//					aTypeNameAST.getText());
//		}			
////		SymbolTableFactory.getOrCreateSymbolTable().getInterfaceNameToAST().put(packageName + "." + aTypeName, ast);
//		
//	}
	public void visitType(DetailAST ast) {
//    	System.out.println("Check called:" + msgKey());

//		if (maybeVisitPackage(ast) ) 
//			return;
		super.visitType(ast);
		if (!checkIncludeExcludeTagsOfCurrentType())
			return;
		
		DetailAST aTypeNameAST = ast.findFirstToken(TokenTypes.IDENT);
		String aTypeName = aTypeNameAST.getText();
		String aFullTypeName = packageName + "." + aTypeName;
		SymbolTable aSymbolTable = SymbolTableFactory.getOrCreateSymbolTable();
		List<String> aMatches = aSymbolTable.matchingFullSTTypeNames(aTypeName);
//		if (aSymbolTable.matchingFullSTTypeNames(aTypeName).size() > 1) {

		if (aMatches.size() > 1) {
//			System.out.println("dupliicateshortname:" + aFullTypeName);
//			log(aTypeNameAST.getLineNo(), aTypeNameAST.getColumnNo(), msgKey(),
//					aTypeNameAST.getText());
//			System.out.println("dupliicateshortname:" + aFullTypeName);
			log(aTypeNameAST,
					aMatches.toString());
//					aTypeNameAST.getText());
		}			
//		SymbolTableFactory.getOrCreateSymbolTable().getInterfaceNameToAST().put(packageName + "." + aTypeName, ast);
		
	}
	@Override
	protected String msgKey() {
		// TODO Auto-generated method stub
		return MSG_KEY;
	}

}
