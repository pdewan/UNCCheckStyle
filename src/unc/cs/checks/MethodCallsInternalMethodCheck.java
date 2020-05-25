package unc.cs.checks;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import unc.cs.symbolTable.STMethod;
import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.SymbolTableFactory;

public class MethodCallsInternalMethodCheck extends ComprehensiveVisitCheck {
	public static final String MSG_KEY = "methodCallsInternalMethod";

	@Override
	public int[] getDefaultTokens() {
		return new int[] {
				TokenTypes.CLASS_DEF, 
				TokenTypes.PACKAGE_DEF,
				TokenTypes.METHOD_DEF,
				TokenTypes.PARAMETER_DEF,
				TokenTypes.IMPORT
				};
	} 
	@Override
	protected String msgKey() {
		return MSG_KEY;
	}
	static String[] emptyArray = {};
	protected void doLeaveToken(DetailAST ast) {
		super.doLeaveToken(ast);
	}
	
	@Override
	protected void leaveMethod(DetailAST methodDef) {
		super.leaveMethod(methodDef); // should probably do this at the end
		if (getFullTypeName() == null) { // interface so return
			return;
		}
		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(getFullTypeName());
		if (anSTType == null) {
			System.err.println("Null STType");
			return;
		}
		if (currentMethodName == null) {
			System.err.println("Null method name");
			return;
		}
		String[] aParameterTypes = currentMethodParameterTypes
				.toArray(new String[0]);
		String[] aLongParameterTypes = toLongTypeNames(aParameterTypes);
//		STMethod anSTMethod = anSTType.getMethod(currentMethodName, currentMethodParameterTypes.toArray(emptyArray) );
		STMethod anSTMethod = anSTType.getMethod(currentMethodName, aLongParameterTypes );

		if (anSTMethod == null) {
			System.err.println("null st method for" + currentMethodName);
			return;
		}
		Set<STMethod> aCalledMethods = anSTMethod.getAllInternallyDirectlyAndIndirectlyCalledMethods();
		if (aCalledMethods != null && aCalledMethods.size() > 0) {
			log(methodDef, anSTMethod.getSimpleChecksSignature(), aCalledMethods.toString());
		}
		 
		
	}

	
	

}