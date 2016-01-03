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

public class OvalPatternCheck extends BoundedShapePatternCheck {
public static final String OVAL_PATTERN = "StructurePatternNames.OVAL_PATTERN";
public static final String ALTERNATE_OVAL_PATTERN = "Oval Pattern";


@Override
public String composePatternName() {
	return OVAL_PATTERN;
}	
@Override
protected  String composeAlternatePatternName() {
	return ALTERNATE_OVAL_PATTERN  ;
}

}
