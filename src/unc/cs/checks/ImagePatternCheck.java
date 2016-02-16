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

public class ImagePatternCheck extends LocatableShapePatternCheck {
	public static final String IMAGE_PROPERTIES = "ImageFileName: String";
	public static final String IMAGE_PATTERN = "StructurePatternNames.IMAGE_PATTERN";
	public static final String ALTERNATE_IMAGE_PATTERN = "Image Pattern";


	@Override
	public String composePatternName() {
		return IMAGE_PATTERN;
	}
	@Override
	protected  String composeAlternatePatternName() {
		return ALTERNATE_IMAGE_PATTERN;
	}
	protected String imageShapePattern() {
		return locatablePattern() + BASIC_SET_MEMBER_SEPARATOR + imageProperties();
	}
	@Override
	public String composeProperties() {
		return imageShapePattern();
	}	
	protected String imageProperties() {
		return IMAGE_PROPERTIES ;
	}
}
