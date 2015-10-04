package unc.cs.checks;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import unc.cs.symbolTable.AnSTMethod;
import unc.cs.symbolTable.PropertyInfo;
import unc.cs.symbolTable.STMethod;
import unc.cs.symbolTable.STNameable;
import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.SymbolTableFactory;

public  class ExpectedPatternCheck extends ComprehensiveVisitCheck {
	public static final String MSG_KEY = "expectedPattern";

	protected Map<String, String> typeToPattern = new HashMap<>();

//	public static final String SEPARATOR = ">";


	// this should be in an abstract class
	public void setExpectedPatternOfType(String aPattern) {
		String[] extractTypeAndSignatures = aPattern.split(TYPE_SEPARATOR);
		String aType = extractTypeAndSignatures[0].trim();
		String aStructurePatern = extractTypeAndSignatures[1].trim();
		typeToPattern.put(aType, aStructurePatern);
	}
	
	public void setExpectedPattern(String[] aPatterns) {
		for (String aPattern : aPatterns) {
			setExpectedPatternOfType(aPattern);
		}

	}
	

	
	protected void logPatternNotMatched(DetailAST aTreeAST, String aPattern) {

		String aTypeName = getName(getEnclosingTypeDeclaration(aTreeAST));
		super.log(aTreeAST, aTreeAST, aPattern, aTypeName);


	}

	
	


	public Boolean doPendingCheck(DetailAST anAST, DetailAST aTree) {
//		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
//				.getSTClassByShortName(
//						getName(getEnclosingTypeDeclaration(aTree)));
		STType anSTType = getSTType(aTree);

		if (anSTType.isEnum())
			return true;
		
		String aSpecifiedType = findMatchingType(typeToPattern.keySet(),
				anSTType);
		if (aSpecifiedType == null)
			return true; // the constraint does not apply to us
		String anExpectedPattern = typeToPattern.get(aSpecifiedType);

		STNameable anActualPattern = anSTType.getStructurePatternName();
		boolean retVal = true;
		if (anActualPattern == null) {
			retVal = false;
		}
		else {
		
			retVal = anActualPattern.getName().endsWith(anExpectedPattern);
		}
		if (!retVal) {
			logPatternNotMatched(anAST, anExpectedPattern);
		}
		return retVal;
	}
//	

	

	@Override
	protected String msgKey() {
		// TODO Auto-generated method stub
		return MSG_KEY;
	}
	public void doFinishTree(DetailAST ast) {
	
		maybeAddToPendingTypeChecks(ast);
		super.doFinishTree(ast);

	}


}
