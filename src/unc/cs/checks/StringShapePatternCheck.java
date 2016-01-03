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

public class StringShapePatternCheck extends LocatableShapePatternCheck {
	public static final String STRING_PROPERTIES = "Text: String";
	public static final String STRING_PATTERN = "StructurePatternNames.STRING_PATTERN";
	public static final String ALTERNATE_STRING_PATTERN = "String Pattern";


	@Override
	public String composePatternName() {
		return STRING_PATTERN;
	}
	@Override
	protected  String composeAlternatePatternName() {
		return ALTERNATE_STRING_PATTERN;
	}
	protected String stringShapePattern() {
		return locatablePattern() + "|"  + stringProperties();
	}
	@Override
	public String composeProperties() {
		return stringShapePattern();
	}	
	protected String stringProperties() {
		return STRING_PROPERTIES ;
	}
}
