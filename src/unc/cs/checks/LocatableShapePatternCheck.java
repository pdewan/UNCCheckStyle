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

public abstract class LocatableShapePatternCheck extends BeanPatternCheck {
//	public static final String MSG_KEY = "linePattern";
//	public  final String LOCATABLE_PATTERN = "@" +composePatternName() + TYPE_SEPARATOR + "X:int | Y:int";

//@Override
//public String composeProperties() {
//	return LINE_PATTERN;
//}
	
	protected String locatablePattern() {
//		return LOCATABLE_PATTERN ;
//		return TAG_STRING +composePatternName() + TYPE_SEPARATOR + "X:int | Y:int";
		return TAG_STRING +composePatternName() + TYPE_SEPARATOR + "X:int" + BASIC_SET_MEMBER_SEPARATOR + "Y:int";

	}
	@Override
	public String composeProperties() {
		return locatablePattern();
	}


	
//	public LinePatternCheck() {
//		super.setExpectedPropertiesOfType(LINE_PATTERN);		
//	}
//	@Override
//	protected String msgKey() {
//		// TODO Auto-generated method stub
//		return MSG_KEY;
//	}
//	public Boolean matchesType(String aDescriptor, String aShortClassName) {
//		STNameable aStructurePattern = getPattern(aShortClassName);
//		if (aStructurePattern == null)
//			return true;
//		return aStructurePattern.getName().equals("StructurePatternNames.LINE_PATTERN");
////		if (aDescriptor == null || aDescriptor.length() == 0)
////			return true;
////		if (!aDescriptor.startsWith(TAG_STRING)) {
////			return aShortClassName.equals(aDescriptor);
////		}
////		List<STNameable> aTags = getTags(aShortClassName);
////		if (aTags == null)
////			return null;
////
////		String aTag = aDescriptor.substring(1);
////
////		return contains(aTags, aTag);
//	}
	


}
