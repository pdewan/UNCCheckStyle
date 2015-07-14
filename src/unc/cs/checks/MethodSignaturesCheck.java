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
import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.SymbolTableFactory;

public  class MethodSignaturesCheck extends ComprehensiveVisitCheck {
	public static final String MSG_KEY = "methodSignatures";

	protected Map<String, String[]> typeToSignature = new HashMap<>();
	public static final String SEPARATOR = ">";



	public void setExpectedSignaturesOfType(String aPattern) {
		String[] extractTypeAndSignatures = aPattern.split(">");
		String aType = extractTypeAndSignatures[0].trim();
		String[] aSignatures = extractTypeAndSignatures[1].split("\\|");
		typeToSignature.put(aType, aSignatures);
	}

	/*
	 * @StructurePatternNames.LinePattern> X:int | Y:int | Width:int
	 * |Height:int,
	 * 
	 * @StructurePatternNames.OvalPatetrn> X:int | Y:int | Width:int |Height:int
	 */
	public void setExpectedSignatures(String[] aPatterns) {
		for (String aPattern : aPatterns) {
			setExpectedSignaturesOfType(aPattern);
		}

	}

	
	protected void logSignatureNotMatched(DetailAST aTreeAST, String aSignature) {
		String aSourceName = shortFileName(astToFileContents.get(aTreeAST)
				.getFilename());
		if (aTreeAST == currentTree) {
			DetailAST aLoggedAST = matchedTypeOrTagAST == null?aTreeAST:matchedTypeOrTagAST;

			log(aLoggedAST.getLineNo(), aLoggedAST.getColumnNo(), msgKey(), aSignature, aSourceName);
		} else {
			log(0, msgKey(), aSignature, aSourceName);
		}

	}

	public Boolean matchSignatures(String[] aSpecifications,
			List<String> aSignatures, DetailAST aTreeAST) {
		boolean retVal = true;
		for (String aSpecification : aSpecifications) {
		
//			String[] aPropertiesPath = aPropertySpecification.split(".");			
			if (!matchSignature(aSpecification, aSignatures)) {
				logSignatureNotMatched(aTreeAST, aSpecification);
				retVal = false;
			}
		}
		return retVal;
	}

	public Boolean matchSignature(
			String aSpecification, List<String> aSignatures) {
		for (String aSignature : aSignatures) {
			if (aSpecification.equals(aSignature))
				// return
				// aSpecifiedType.equalsIgnoreCase(aPropertyInfos.get(aProperty).getGetter().getReturnType());
				return true;

			else
				continue;
		}
		return false;
	}


	public Boolean doPendingCheck(DetailAST anAST, DetailAST aTree) {
		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
				.getSTClassByShortName(
						getName(getEnclosingTypeDeclaration(aTree)));
		String aSpecifiedType = findMatchingType(typeToSignature.keySet(),
				anSTType);
		if (aSpecifiedType == null)
			return true; // the constraint does not apply to us
		
		List<String> aSignatures = anSTType.getAllSignatures();

		if (aSignatures == null) 
			return null;
		String[] aSpecifiedSignatures = typeToSignature.get(aSpecifiedType);
		return matchSignatures(aSpecifiedSignatures, aSignatures, aTree);
	}

	@Override
	protected String msgKey() {
		// TODO Auto-generated method stub
		return MSG_KEY;
	}


}
