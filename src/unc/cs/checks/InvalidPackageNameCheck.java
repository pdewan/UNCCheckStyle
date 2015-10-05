package unc.cs.checks;

import java.util.Arrays;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public  class InvalidPackageNameCheck extends ComprehensiveVisitCheck {
	public static final String MSG_KEY = "invalidPackageName";

	public InvalidPackageNameCheck() {

	}
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.PACKAGE_DEF};
	} 
	public void doVisitToken(DetailAST ast) {		
		switch (ast.getType()) {
		case TokenTypes.PACKAGE_DEF: 
			visitPackage(ast);
			return;
		
		default:
			System.err.println("Unexpected token");
		}
		
	}
	public void visitPackage(DetailAST ast) {
		super.visitPackage(ast);
		String[] prefixes = STBuilderCheck.getProjectPackagePrefixes();
		if (prefixes == null || prefixes.length == 0) {
			return;			
		}
		if (packageName == null) {
			log(ast,  "default", Arrays.asList(prefixes).toString());
		}
		for (String aPrefix:prefixes) {
			if (packageName.startsWith(aPrefix)) {
				return;
			}
		}
		
		log(ast,  packageName, Arrays.asList(prefixes).toString());
		

	}
	
	@Override
	protected String msgKey() {
		// TODO Auto-generated method stub
		return MSG_KEY;
	}
	
}
