package unc.cs.checks;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import unc.cs.symbolTable.PropertyInfo;
import unc.cs.symbolTable.STMethod;
import unc.cs.symbolTable.STNameable;
import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.SymbolTableFactory;

public abstract class BeanPatternCheck extends ExpectedGettersCheck {
	String specifiedPatternName;
	String alternatePatternName;
	static Map<String, String> specifiedToAlternateName = new HashMap();
	
	public static String getAlternateName(String anActualName) {
		return specifiedToAlternateName.get(anActualName);
	}

	public BeanPatternCheck() {
		super.setExpectedStringsOfType(composeProperties());
		specifiedPatternName = composePatternName();
		alternatePatternName = composeAlternatePatternName();
		// this is tricky as the expected pattern check may happen before the checks
		// that populate the data structure
		connect(specifiedPatternName, alternatePatternName);
	}
	
	public static void connect (String aSpecifiedName, String anAlternateName) {
		specifiedToAlternateName.put(aSpecifiedName, anAlternateName);	
		specifiedToAlternateName.put(toShortTypeName(aSpecifiedName), anAlternateName);	

	}
	
	public Boolean checkIncludeExcludeTagsOfCurrentType() {
		if (fullTypeName == null) {
			fullTypeName = getFullTypeName(currentTree);
			if (fullTypeName == null) {
			System.err.println ("Full type name not initialized ");
			return false;

			}
		}
		STType anSTType = SymbolTableFactory.getSymbolTable().
				getSTClassByFullName(fullTypeName);
		if (anSTType == null) { // inner class
			System.err.println ("cannot find entry for" + fullTypeName);
			
			return false;
		}
		STNameable aStructurePattern = anSTType.getStructurePatternName();
		return super.checkIncludeExcludeTagsOfCurrentType() &&
				aStructurePattern != null &&
						aStructurePattern.getName().equals(specifiedPatternName);
	}

	public abstract String composeProperties();

	public abstract String composePatternName();
	protected  String composeAlternatePatternName() {
		return composePatternName();
	}
	static {
		connect("StucturePatternNames.BEAN_PATTERN", "Bean Pattern");
	}


	// @Override
	// protected String msgKey() {
	// // TODO Auto-generated method stub
	// return MSG_KEY;
	// }
//	@Override
//	public Boolean matchesType(String aDescriptor, String aShortClassName) {
//		STNameable aStructurePattern = getPattern(aShortClassName);
//		if (aStructurePattern == null)
//			return false;
//		return aStructurePattern.getName().equals(patternName);
//		
//	}

}
