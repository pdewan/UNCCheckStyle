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

	public BeanPatternCheck() {
		super.setExpectedPropertiesOfType(composeProperties());
		specifiedPatternName = composePatternName();
		
	}
	
	public Boolean checkIncludeExcludeTagsOfCurrentType() {
		STType anSTType = SymbolTableFactory.getSymbolTable().
				getSTClassByFullName(fullTypeName);
		STNameable aStructurePattern = anSTType.getStructurePatternName();
		return super.checkIncludeExcludeTagsOfCurrentType() &&
				aStructurePattern != null &&
						aStructurePattern.getName().equals(specifiedPatternName);
	}

	public abstract String composeProperties();

	public abstract String composePatternName();

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
