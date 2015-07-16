package unc.cs.checks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import unc.cs.symbolTable.AnSTType;
import unc.cs.symbolTable.STMethod;
import unc.cs.symbolTable.STNameable;
import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.SymbolTableFactory;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
// not really using methods of superclass
public class PeerCommonSignaturesCheck extends ExpectedSignaturesCheck{
	public static final String MSG_KEY = "peerCommonSignatures";
	
	List<STMethod> includeSignatures = new ArrayList();
	List<STMethod> excludeSignatures = new ArrayList();
	
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
	public int[] getDefaultTokens() {
		return new int[] { 
				TokenTypes.PACKAGE_DEF,
				TokenTypes.CLASS_DEF,
				TokenTypes.INTERFACE_DEF
				
		};
	}
	
	public void setIncludeSignatures(String[] aSignatures) {
		for (String aSignature:aSignatures) {
			includeSignatures.add(signatureToMethod(aSignature));
		}
	}
	public void setExcludeSignatures(String[] aSignatures) {
		for (String aSignature:aSignatures) {
			excludeSignatures.add(signatureToMethod(aSignature));
		}
	}
	
	@Override
	protected String msgKey() {
		return MSG_KEY;
	}
    public void doFinishTree(DetailAST ast) {
		
		maybeAddToPendingTypeChecks(ast);
		super.doFinishTree(ast);

	}
    protected void logPeerSignatureNotMatched(DetailAST aTreeAST, String aSignature, String aRemoteTypeName) {
		String aSourceName = shortFileName(astToFileContents.get(aTreeAST)
				.getFilename());
		String aTypeName = getName(getEnclosingTypeDeclaration(aTreeAST));
		if (aTreeAST == currentTree) {
//			DetailAST aLoggedAST = matchedTypeOrTagAST == null?aTreeAST:matchedTypeOrTagAST;

			log(aTreeAST.getLineNo(),  msgKey(), aSignature, aTypeName, aRemoteTypeName, aSourceName);
		} else {
			log(0, msgKey(), aSignature, aTypeName, aRemoteTypeName, aSourceName);
		}

	}
    public Boolean compareCommonSignatures(STType anSTType, String aPeerType, DetailAST aTree) {
    	Boolean result = true;
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
				return null;
			if (aHasSignature)
				continue;
			result = false;
			logPeerSignatureNotMatched(aTree, aSignature, aPeerType);			
		}	
		return result;    	
    }
    public Boolean matchMethodWithIncludeAndExcludeSignatures (STMethod anSTMethod) {
    	Boolean retVal = true;
    	if (includeSignatures.size() > 0) {
    		retVal = matchMethod(includeSignatures, anSTMethod);
    	}
    	if (excludeSignatures.size() > 0) {
    		retVal = !matchMethod(excludeSignatures, anSTMethod);
    	}
    	return retVal;
    }
    public List<STMethod> filterByIncludeAndExcludeSignatures(List<STMethod> aMethods) {
    	if (aMethods == null)
    		return null;
    	List<STMethod> result = new ArrayList();
    	for (STMethod aMethod:aMethods) {
    	if (matchMethodWithIncludeAndExcludeSignatures(aMethod)) {
    		result.add(aMethod);
    	}
    	}
    	return result;
    	
    }
    public Boolean compareCommonMethods(STType anSTType, String aPeerType, DetailAST aTree) {
    	Boolean result = true;
    	List<STMethod> aCommonMethods = filterByIncludeAndExcludeSignatures(anSTType.methodsCommonWith(aPeerType));
		if (aCommonMethods == null)
			return null;
		System.out.println (anSTType.getName() + " common methods " + aPeerType + " = " + aCommonMethods);
		List<String> aCommonSuperTypes = anSTType.namesOfSuperTypesInCommonWith(aPeerType);
		if (aCommonSuperTypes == null)
			return null;
		System.out.println (anSTType.getName() + " common supertypes with " + aPeerType + " =" + aCommonSuperTypes);
		
		for (STMethod aMethod:aCommonMethods) {
			Boolean aHasSignature =  AnSTType.containsMethod(aCommonSuperTypes, aMethod);
			if (aHasSignature == null)
				return null;
			if (aHasSignature)
				continue;
			result = false;
			logPeerSignatureNotMatched(aTree, aMethod.getSignature(), aPeerType);			
		}	
		return result;    	
    }
    
//    public Boolean doPendingCheck(DetailAST anAST, DetailAST aTree) {
//		String aTypeName = getName(getEnclosingTypeDeclaration(aTree));
//		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aTypeName);
//
//		List<String> aPeerTypes = filterTypes(anSTType.getPeerTypes(), aTypeName);
//		if (aPeerTypes == null) 
//			return null;
//		System.out.println("Peer Types" + aPeerTypes);
//		
//		for (String aPeerType:aPeerTypes) {
//			List<String> aCommonSignatures = anSTType.signaturesCommonWith(aPeerType);
//			if (aCommonSignatures == null)
//				return null;
//			compareCommonSignatures(anSTType, aPeerType, aTree);
//			
//		}
//		return true;
//    	
//    }
    public Boolean doPendingCheck(DetailAST anAST, DetailAST aTree) {
		String aTypeName = getName(getEnclosingTypeDeclaration(aTree));
		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aTypeName);

		List<String> aPeerTypes = filterTypes(anSTType.getPeerTypes(), aTypeName);
		if (aPeerTypes == null) 
			return null;
		System.out.println("Peer Types" + aPeerTypes);
		
		for (String aPeerType:aPeerTypes) {
//			List<STMethod> aCommonMethods = filterByIncludeAndExcludeSignatiures(anSTType.methodsCommonWith(aPeerType));
//			if (aCommonMethods == null)
//				return null;
			if (compareCommonMethods(anSTType, aPeerType, aTree) == null)
				return null;
			
		}
		return true;
    	
    }

}
