package unc.cs.checks;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import unc.cs.symbolTable.PropertyInfo;
import unc.cs.symbolTable.STMethod;
import unc.cs.symbolTable.STNameable;
import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.SymbolTableFactory;

public  class DescendentPropertiesCheck extends ComprehensiveVisitCheck {
	public static final String MSG_KEY = "descendentProperties";
	protected Map<String, List<String>> typeToProperties = new HashMap();
	protected Map<String, List<String>> propertyToTypes = new HashMap();
	protected Set<String> visitedTypes = new HashSet();


//	public void doVisitToken(DetailAST ast) {
//		// System.out.println("Check called:" + MSG_KEY);
//		switch (ast.getType()) {
//		case TokenTypes.PACKAGE_DEF:
//			visitPackage(ast);
//			return;
//		case TokenTypes.CLASS_DEF:
//		case TokenTypes.INTERFACE_DEF:
//			if (fullTypeName == null)
//
//			visitType(ast);
//			return;
//		default:
//			System.err.println("Unexpected token");
//		}
//	}

	@Override
	public int[] getDefaultTokens() {
		return new int[] { 
//				TokenTypes.CLASS_DEF, TokenTypes.INTERFACE_DEF,
//				TokenTypes.PACKAGE_DEF, 
//				TokenTypes.ANNOTATION 
				};
	}
	
//	public static String toShortTypeName (String aTypeName) {
//		int aDotIndex = aTypeName.lastIndexOf(".");
//		String aShortTypeName = aTypeName;
//		if (aDotIndex != -1)
//			aShortTypeName = aTypeName.substring(aDotIndex + 1);
//		return aShortTypeName;
//	}
	
	protected void associate(String aPropertyName, String aTypeName) {
		if (aTypeName == null || aPropertyName == null)
			return;
//		int aDotIndex = aTypeName.lastIndexOf(".");
//		String aShortTypeName = toShortTypeName(aTypeName);
//		if (aDotIndex != -1)
//			aShortTypeName = aTypeName.substring(aDotIndex);
		List<String> aTypes = getOrCreatePropertyToTypes(aPropertyName);
		aTypes.add(aTypeName);
		List<String> aProperties = getOrCreateTypeToProperties(aTypeName);
		aProperties.add(aPropertyName);
	}
	protected void associate(String aPropertyName, STNameable[] aTags) {
		for (STNameable aTag: aTags)	{
			associate(aPropertyName, aTag);
		}
	}
	
	
	protected void associate(String aPropertyName, STNameable aNameable) {
		if (aNameable == null) return;
		associate(aPropertyName, aNameable.getName());
	}

	
	protected List<String> getOrCreatePropertyToTypes(String aPropertyName) {
		List<String> aTypes = propertyToTypes.get(aPropertyName);
		if (aTypes == null) {
			aTypes = new ArrayList();	
			propertyToTypes.put(aPropertyName, aTypes);
		}
		return aTypes;
	}
	
	protected List<String> getOrCreateTypeToProperties(String aTypeName) {
		List<String> aProperties = typeToProperties.get(aTypeName);
		if (aProperties == null) {
			aProperties = new ArrayList();	
			typeToProperties.put(aTypeName, aProperties);
		}
		return aProperties;
	}
	
	protected void typeRevisited (String aShortTypeName) {
		
	}
	
	public Boolean addProperties(STType anSTType, String aShortOrLongTypeName, String aPrefix) {
//		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
//				.getSTClassByShortName(
//						aShortTypeName);
		String aShortTypeName = toShortTypeOrVariableName(aShortOrLongTypeName);
		if (visitedTypes.contains(aShortTypeName)) {
			typeRevisited(aShortTypeName);
			return true;
		}
		visitedTypes.add(aShortTypeName);
		Map<String, PropertyInfo> aPropertyInfos = anSTType.getPropertyInfos();
		if (aPropertyInfos == null)
			return null;
		for (String aKey:aPropertyInfos.keySet()) {
			String aPropertyName = aPrefix + "." + aKey;
//			STMethod aGetter = aPropertyInfos.get(aKey).getGetter();
//			STMethod aSetter = aPropertyInfos.get(aKey).getSetter();
//			// either the getter or setter will be null
//			String aTypeName = aGetter != null?aGetter.getReturnType():aSetter.getParameterTypes()[0]; 
			String aTypeName = aPropertyInfos.get(aKey).getType();
			associate(aPropertyName, toShortTypeOrVariableName(aTypeName));

			if (isExternalClass(aTypeName)) continue;
			STType aPropertySTType = SymbolTableFactory.getOrCreateSymbolTable()
					.getSTClassByShortName(
							aTypeName);
			if (anSTType.isEnum() || anSTType.isAnnotation())
				return true;
			if (aPropertySTType == null) return null;
//			STNameable[] aTags = aPropertySTType.getTags();
//			STNameable[] aTags = aPropertySTType.getAllComputedTags();
			STNameable[] aTags = aPropertySTType.getComputedTags();

			if (aTags == null)
				return null;

			STNameable aPattern = aPropertySTType.getStructurePatternName();
			associate (aPropertyName, aPattern); // we do not need this now I think
			associate(aPropertyName, aTags);
			Boolean retVal = addProperties(aPropertySTType, aTypeName, aPropertyName);
			if (retVal == null || !retVal)
				return null;
			
			
			// now store the info
		}
		return true;
		
	}
	
	protected void initializeTables() {
		typeToProperties.clear();
		propertyToTypes.clear();
		visitedTypes.clear();
	}

	public Boolean doPendingCheck(DetailAST anAST, DetailAST aTree) {
		initializeTables();
		String aTypeName = getName(getEnclosingTypeDeclaration(aTree));
//		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
//				.getSTClassByShortName(
//						aTypeName);
		STType anSTType = getSTType(aTree);
		if (anSTType.isEnum() 
				|| anSTType.isInterface() || anSTType.isAnnotation())
			return true;

		return addProperties(anSTType, aTypeName, aTypeName);
		
	}


	public void doFinishTree(DetailAST ast) {
		
		maybeAddToPendingTypeChecks(ast);
		super.doFinishTree(ast);

	}

	@Override
	protected String msgKey() {
		return MSG_KEY;
	}
}
