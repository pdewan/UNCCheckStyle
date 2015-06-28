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
	String patternName;
	public BeanPatternCheck() {
		super.setExpectedPropertiesOfType(composeProperties());	
		patternName = composePatternName();
	}
	public abstract String composeProperties();
	public abstract String composePatternName();
//	@Override
//	protected String msgKey() {
//		// TODO Auto-generated method stub
//		return MSG_KEY;
//	}
	public Boolean matchesType(String aDescriptor, String aShortClassName) {
		STNameable aStructurePattern = getPattern(aShortClassName);
		if (aStructurePattern == null)
			return false;
		return aStructurePattern.getName().equals(patternName);
//		if (aDescriptor == null || aDescriptor.length() == 0)
//			return true;
//		if (!aDescriptor.startsWith("@")) {
//			return aShortClassName.equals(aDescriptor);
//		}
//		List<STNameable> aTags = getTags(aShortClassName);
//		if (aTags == null)
//			return null;
//
//		String aTag = aDescriptor.substring(1);
//
//		return contains(aTags, aTag);
	}
	


}
