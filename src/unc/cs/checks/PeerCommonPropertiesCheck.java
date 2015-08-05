package unc.cs.checks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import unc.cs.symbolTable.AnSTType;
import unc.cs.symbolTable.PropertyInfo;
import unc.cs.symbolTable.STMethod;
import unc.cs.symbolTable.STNameable;
import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.SymbolTableFactory;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
// not really using methods of superclass
public class PeerCommonPropertiesCheck extends BeanTypedPropertiesCheck{
	public static final String MSG_KEY = "peerCommonProperties";
	String[] includeProperties = {};
	String[] excludeProperties = {};
	
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
	
	public int[] getDefaultTokens() {
		return new int[] { 
				TokenTypes.PACKAGE_DEF,
				TokenTypes.CLASS_DEF,
				TokenTypes.INTERFACE_DEF
				
		};
	}
	
	@Override
	protected String msgKey() {
		return MSG_KEY;
	}
	public void setIncludeProperties(String[] aProperties) {
		includeProperties = aProperties;
	}
	public void setExcludeProperties(String[] aProperties) {
		excludeProperties = aProperties;
	}
    public void doFinishTree(DetailAST ast) {
		
		maybeAddToPendingTypeChecks(ast);
		super.doFinishTree(ast);

	}
    protected void logPeerPropertyNotMatched(DetailAST aTreeAST, PropertyInfo aPropertyInfo, String aRemoteTypeName) {
		String aSourceName = shortFileName(astToFileContents.get(aTreeAST)
				.getFilename());
		String aTypeName = getName(getEnclosingTypeDeclaration(aTreeAST));
		if (aTreeAST == currentTree) {
//			DetailAST aLoggedAST = matchedTypeOrTagAST == null?aTreeAST:matchedTypeOrTagAST;

			log(aTreeAST.getLineNo(), msgKey(), aPropertyInfo, aTypeName, aRemoteTypeName, aSourceName);
		} else {
			log(0, msgKey(), aPropertyInfo, aTypeName, aRemoteTypeName, aSourceName);
		}

	}
    public Boolean compareCommonProperties(STType anSTType, String aPeerType, DetailAST aTree) {
    	Boolean result = true;
    	Collection<PropertyInfo> aCommonProperties = filterByIncludeAndExcludeProperties(anSTType.propertiesCommonWith(aPeerType));
		if (aCommonProperties == null)
			return null;
//		System.out.println (anSTType.getName() + " common properties " + aPeerType + " = " + aCommonProperties);
		List<String> aCommonSuperTypes = anSTType.namesOfSuperTypesInCommonWith(aPeerType);
		if (aCommonSuperTypes == null)
			return null;
//		System.out.println (anSTType.getName() + " common supertypes with " + aPeerType + " =" + aCommonSuperTypes);
		
		for (PropertyInfo aProperty:aCommonProperties) {
			Boolean aHasProperty =  AnSTType.containsProperty(aCommonSuperTypes, aProperty);
			if (aHasProperty == null)
				return null;
			if (aHasProperty)
				continue;
			result = false;
			logPeerPropertyNotMatched(aTree, aProperty, aPeerType);			
		}	
		return result;    	
    }
    public Collection<PropertyInfo> filterByIncludeAndExcludeProperties(Collection<PropertyInfo> aPropertyInfos) {
    	if (aPropertyInfos == null)
    		return null;
    	Collection<PropertyInfo> result = aPropertyInfos;
    	
    	if (includeProperties.length > 0) {
    		result = matchedOrUnMatchedProperties(Arrays.asList(includeProperties), result, true);
    	}
    	if (excludeProperties.length > 0) {
    		result = matchedOrUnMatchedProperties(Arrays.asList(excludeProperties), result, false);
    	}
    	return result;    	
    }
    public Boolean doPendingCheck(DetailAST anAST, DetailAST aTree) {
		String aTypeName = getName(getEnclosingTypeDeclaration(aTree));
//		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aTypeName);
		STType anSTType = getSTType(aTree);

		if (anSTType.isEnum())
			return true;
//		List<String> aTypes = anSTType.getAllTypeNames();
//		if (aTypes == null) 
//			return null;
//		List<String> aSuperTypes = anSTType.getSuperTypeNames();
//		System.out.println("SuperTypes" + aSuperTypes);
//		if (aSuperTypes == null) 
//			return null;
//		System.out.println("Types" + aTypes);
//		List<String> aSignatures = anSTType.getAllSignatures();
//		if (aSignatures == null) 
//			return null;
//		System.out.println ("Signatures" + aSignatures);
//		List<String> aNonSuperTypes = anSTType.getNonSuperTypes();
//		if (aNonSuperTypes == null) 
//			return null;
//		System.out.println("NonSuperTypes" + aNonSuperTypes);
//		List<String> aSubTypes = anSTType.getSubTypes();
//		if (aSubTypes == null) 
//			return null;
//		System.out.println("SubTypes" + aSubTypes);
		List<String> aPeerTypes = filterTypes(anSTType.getPeerTypes(), aTypeName);
		if (aPeerTypes == null) 
			return null;
//		System.out.println("Peer Types" + aPeerTypes);
		
		for (String aPeerType:aPeerTypes) {
//			List<PropertyInfo> aCommonProperties = anSTType.propertiesCommonWith(aPeerType);
//			if (aCommonProperties == null)
//				return null;
			if (compareCommonProperties(anSTType, aPeerType, aTree) == null)
				return null;
//			System.out.println (anSTType.getName() + " common signaures " + aPeerType + " = " + aCommonSignatures);
//			List<String> aCommonSuperTypes = anSTType.namesOfSuperTypesInCommonWith(aPeerType);
//			if (aCommonSuperTypes == null)
//				return null;
//			System.out.println (anSTType.getName() + " common supertypes with " + aPeerType + " =" + aCommonSuperTypes);
//			for (String aSignature:aCommonSignatures) {
//				Boolean aHasSignature =  AnSTType.containsSignature(aCommonSuperTypes, aSignature);
//				if (aHasSignature == null)
//					continue;
//				System.out.println ("Super types of " + anSTType.getName() + " and " + aPeerType + 
//						"  contain signature:" + aSignature + " is " + aHasSignature);
//			}			
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

//	@Override
//	public Boolean matchType(String aSpecifiedType, String aProperty,
//			Map<String, PropertyInfo> aPropertyInfos) {
//		PropertyInfo aPropertyInfo = aPropertyInfos.get(aProperty);
//		return matchType(aSpecifiedType, aPropertyInfo, aPropertyInfos);
//	}

//	@Override
//	public Boolean matchType(String aSpecifiedType, PropertyInfo aProperty,
//			Collection<PropertyInfo> aPropertyInfos) {
//		
//		
//	}



}
