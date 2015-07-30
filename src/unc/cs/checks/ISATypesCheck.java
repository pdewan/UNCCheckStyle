package unc.cs.checks;

import java.util.List;

import unc.cs.symbolTable.AnSTType;
import unc.cs.symbolTable.STNameable;
import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.SymbolTableFactory;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class ISATypesCheck extends ComprehensiveVisitCheck{
	public static final String MSG_KEY = "isATypes";
	@Override
	public int[] getDefaultTokens() {
		return new int[] {  };
	}
//	public void doVisitToken(DetailAST ast) {
//		// System.out.println("Check called:" + MSG_KEY);
//		switch (ast.getType()) {
//		case TokenTypes.PACKAGE_DEF:
//			visitPackage(ast);
//			return;
//		case TokenTypes.CLASS_DEF:
//		case TokenTypes.INTERFACE_DEF:
//			visitType(ast);
//			return;
//		default:
//			System.err.println("Unexpected token");
//		}
//	}

	@Override
	protected String msgKey() {
		return MSG_KEY;
	}
    public void doFinishTree(DetailAST ast) {
		
		maybeAddToPendingTypeChecks(ast);
		super.doFinishTree(ast);

	}
    
    public Boolean doPendingCheck(DetailAST anAST, DetailAST aTree) {
		String aTypeName = getName(getEnclosingTypeDeclaration(aTree));
		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aTypeName);
		if (anSTType.isEnum())
			return true;
		List<String> aTypes = anSTType.getAllTypeNames();
		if (aTypes == null) 
			return null;
		List<String> aSuperTypes = anSTType.getSuperTypeNames();
		System.out.println("SuperTypes" + aSuperTypes);
		if (aSuperTypes == null) 
			return null;
		System.out.println("Types" + aTypes);
		List<String> aSignatures = anSTType.getAllSignatures();
		if (aSignatures == null) 
			return null;
		System.out.println ("Signatures" + aSignatures);
		List<String> aNonSuperTypes = anSTType.getNonSuperTypes();
		if (aNonSuperTypes == null) 
			return null;
		System.out.println("NonSuperTypes" + aNonSuperTypes);
		List<String> aSubTypes = anSTType.getSubTypes();
		if (aSubTypes == null) 
			return null;
		System.out.println("SubTypes" + aSubTypes);
		List<String> aPeerTypes = anSTType.getPeerTypes();
		if (aPeerTypes == null) 
			return null;
		System.out.println("Peer Types" + aSubTypes);
		
		for (String aPeerType:aPeerTypes) {
			List<String> aCommonSignatures = anSTType.signaturesCommonWith(aPeerType);
			if (aCommonSignatures == null)
				return null;
			System.out.println (anSTType.getName() + " common signaures " + aPeerType + " = " + aCommonSignatures);
			List<String> aCommonSuperTypes = anSTType.namesOfSuperTypesInCommonWith(aPeerType);
			if (aCommonSuperTypes == null)
				return null;
			System.out.println (anSTType.getName() + " common supertypes with " + aPeerType + " =" + aCommonSuperTypes);
			for (String aSignature:aCommonSignatures) {
				Boolean aHasSignature =  AnSTType.containsSignature(aCommonSuperTypes, aSignature);
				if (aHasSignature == null)
					continue;
				System.out.println ("Super types of " + anSTType.getName() + " and " + aPeerType + 
						"  contain signature:" + aSignature + " is " + aHasSignature);
			}			
		}
		return true;
//		if (aTree == currentTree) {
//			DetailAST aTypeTree = getEnclosingTypeDeclaration(aTree);
//			DetailAST aNameAST = getNameAST(aTypeTree);
//			
//
//			log (aNameAST.getLineNo(), msgKey(), aNumDescendents, aMinDescendents, aSourceName );
//		} else {
//			log (0, msgKey(), aNumDescendents, aMinDescendents, aSourceName );
//		}

    	
    }

}
