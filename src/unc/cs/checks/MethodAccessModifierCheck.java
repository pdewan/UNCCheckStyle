package unc.cs.checks;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.checks.naming.AccessModifier;

import unc.cs.symbolTable.APropertyInfo;
import unc.cs.symbolTable.AccessModifierUsage;
import unc.cs.symbolTable.STMethod;
import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.SymbolTableFactory;

public class MethodAccessModifierCheck extends ComprehensiveVisitCheck {
	public static final String MSG_KEY_WARNING = "methodAccessModifierUnmatched";
	public static final String MSG_KEY_INFO = "methodAccessModifierMatched";

	@Override
	public int[] getDefaultTokens() {
		return new int[] {
				TokenTypes.CLASS_DEF, 
				TokenTypes.PACKAGE_DEF,
				TokenTypes.METHOD_DEF,
				TokenTypes.PARAMETER_DEF,
				TokenTypes.TYPE_PARAMETERS,
				TokenTypes.IMPORT
				};
	} 
	@Override
	protected String msgKeyWarning() {
		return MSG_KEY_WARNING;
	}
	@Override
	protected String msgKeyInfo() {
		return MSG_KEY_INFO;
	}
	
	@Override
	protected String msgKey() {
		return isInfo()?msgKeyInfo():msgKeyWarning();
	}
	
	static String[] emptyArray = {};
	@Override
	protected void doLeaveToken(DetailAST ast) {
//		if (STBuilderCheck.isFirstPass) {
//			return;
//		}
		super.doLeaveToken(ast);
	}
	@Override
	protected void doVisitToken(DetailAST ast) {
//		if (STBuilderCheck.isFirstPass) {
//			return;
//		}
		super.doVisitToken(ast);
	}
	@Override
	protected void leaveMethod(DetailAST methodDef) {
		if (STBuilderCheck.isFirstPass) {
			return;
		}
		super.leaveMethod(methodDef); // should probably do this at the end
		if (getFullTypeName() == null) { // interface so return
			return;
		}
		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(getFullTypeName());
		if (anSTType == null) {
			System.err.println("Null STType for:" + getFullTypeName());
			return;
		}
		if (currentMethodName == null) {
			System.err.println("Null method name");
			return;
		}
		String[] aParameterTypes = currentMethodParameterTypes
				.toArray(new String[0]);
//		for (int i = 0; i < aParameterTypes.length; i++) {
//			aParameterTypes[i] = toNormalizedTypeParameterName(aParameterTypes[i]);
//		}
		String[] aLongParameterTypes = toLongTypeNames(aParameterTypes);
//		STMethod anSTMethod = anSTType.getMethod(currentMethodName, currentMethodParameterTypes.toArray(emptyArray) );
		STMethod anSTMethod = anSTType.getMethod(currentMethodName, aLongParameterTypes );

		if (anSTMethod == null) {
			System.err.println("null st method for" + currentMethodName);
			return;
		}
		List<AccessModifierUsage> aUsages = anSTMethod.getAccessModifiersUsed();
		if (aUsages == null || aUsages.size() == 0) {
			return;
		}
		boolean aMatch = true;
		int aMinDifference = APropertyInfo.INFINITE_ACCESS_DIFFERENCE;
		List<String> aReferencingTypes = new ArrayList();
		List<String> aUsedAccessModifiers = new ArrayList();
		if (aUsages == null) {
			System.err.println ("Null usages");
			return;
		}
		for (AccessModifierUsage aUsage:aUsages) {
			if (aUsage == null) {
//				System.err.println ("Null usage");
				continue;
			}
			aMinDifference = Math.min(aMinDifference, aUsage.getDifference());
			aReferencingTypes.add(aUsage.getTypeReferencing().getName());
			aUsedAccessModifiers.add(aUsage.getUsed().toString());
		}
		if (aMinDifference == 0 && isInfo()) {
			log (methodDef, anSTMethod.getAccessModifier().toString(),
					aReferencingTypes.toString(), aUsedAccessModifiers.toString() );
		} 
		else if (aMinDifference > 0 && !isInfo()) {
			log (methodDef, anSTMethod.getAccessModifier().toString(),
					aReferencingTypes.toString(), aUsedAccessModifiers.toString() );
		}
		
		
	}
	

	
	

}