package unc.cs.checks;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import unc.cs.symbolTable.AnSTMethodFromMethod;
import unc.cs.symbolTable.PropertyInfo;
import unc.cs.symbolTable.STNameable;
import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.SymbolTableFactory;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class NoStructuredSetterCheck extends ComprehensiveVisitCheck {
	public static final String MSG_KEY = "noStructuredSetter";
//	protected Set<String> excludeStructuredTypes = new HashSet();

	
	@Override
	public int[] getDefaultTokens() {
		return new int[] { TokenTypes.PACKAGE_DEF, 
				TokenTypes.CLASS_DEF,
				 TokenTypes.INTERFACE_DEF,
//				 TokenTypes.ENUM_DEF
				
		};
	}
	
//	public void setExcludeStructuredTypes(String[] newVal) {
//		excludeStructuredTypes =  new HashSet(Arrays.asList(newVal));
//	}

	@Override
	protected String msgKey() {
		return MSG_KEY;
	}

	@Override
	public void doVisitToken(DetailAST ast) {
		super.doVisitToken(ast);
		
	}
public void doFinishTree(DetailAST ast) {
		
		maybeAddToPendingTypeChecks(ast);
		super.doFinishTree(ast);

	}

//	protected Boolean isStructuredProperty(PropertyInfo aPropertyInfo) {
//		String aType = aPropertyInfo.getType();
//		STNameable aSetter = aPropertyInfo.getSetter();
//		if (aSetter instanceof AnSTMethodFromMethod) 
//			return false;// external class
//		if (isOEAtomic(aType) || aSetter == null) 
//			return false;
//		STType aPropertyType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aType);
//		if (aPropertyType != null) {
//			if (!aPropertyType.hasSetter())
//				return false; // immutable
//			STNameable[] aTags = aPropertyType.getComputedTags();
////			STNameable[] aTags = aPropertyType.getAllComputedTags();
//			if (excludeStructuredTypes.size() > 0) {
//			if (aTags == null)
//				return null;
//			if (matchesSomeSpecificationTags(Arrays.asList(aTags), excludeStructuredTypes))
//				return false;
//			}
//			return true;
//		}
//		return null;
//			
//	}

	@Override
	public Boolean doPendingCheck(DetailAST anAST, DetailAST aTree) {
		String aTypeName = getEnclosingShortTypeName(anAST);
//		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(fullTypeName);
		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aTypeName);

		if (anSTType == null) // we did not  visit the type
			return true;
		if (anSTType.isEnum())
			return true;
		Map<String, PropertyInfo> aPropertyInfos = anSTType.getPropertyInfos();
		if (aPropertyInfos == null)
			return null;
		Boolean retVal = true;
		for (String aPropertyName:aPropertyInfos.keySet()) {
			PropertyInfo aPropertyInfo = aPropertyInfos.get(aPropertyName);
			String aType = aPropertyInfo.getType();
			STNameable aSetter = aPropertyInfo.getSetter();
			if (aSetter == null)
				return true;
//			if (aSetter instanceof AnSTMethodFromMethod) continue;// external class
//			if (isOEAtomic(aType) || aSetter == null) continue;
//			STType aPropertyType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aType);
//			if (aPropertyType != null) {
//				if (!aPropertyType.hasSetter())
//					continue; // immutable
//				STNameable[] aTags = aPropertyType.getComputedTags();
////				STNameable[] aTags = aPropertyType.getAllComputedTags();
//				if (excludeStructuredTypes.size() > 0) {
//				if (aTags == null)
//					return null;
//				if (matchesSomeSpecificationTags(Arrays.asList(aTags), excludeStructuredTypes))
//					continue;
//				}
//				
//				
//			}
			Boolean isStructuredType = isStructuredProperty(aPropertyInfo);
			if (isStructuredType == null)
				return null;
			if (!isStructuredType)
				continue;
			DetailAST aSetterAST = aSetter.getAST();
//			log(aSetterAST.getLineNo(), msgKey(), aPropertyName, aType);
			log(aSetterAST, aPropertyName, aType);
			retVal = false;
		}
		return retVal;		
		
	}

	
}
