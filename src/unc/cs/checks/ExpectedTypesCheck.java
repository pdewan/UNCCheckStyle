package unc.cs.checks;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.ArrayList;
import java.util.Arrays;
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

public abstract class ExpectedTypesCheck extends ComprehensiveVisitCheck {
//	public static final String MSG_KEY = "expectedInterfaces";

	protected Map<String, List<String>> typeToTypes = new HashMap<>();
//	protected Map<String, List<STMethod>> typeToMethods = new HashMap<>();

//	public static final String SEPARATOR = ">";


	
	// this should be in an abstract class
		public void setExpectedTypesOfType(String aPattern) {
			String[] extractTypeAndInterfaces = aPattern.split(TYPE_SEPARATOR);
			String aType = extractTypeAndInterfaces[0].trim();
			String[] anInterfaces = extractTypeAndInterfaces[1].split(SET_MEMBER_SEPARATOR);
			for (int i = 0; i < anInterfaces.length; i++) {
				anInterfaces[i] = anInterfaces[i].trim();
			}
			List<String> anInterfaceList = Arrays.asList(anInterfaces);
			typeToTypes.put(aType, anInterfaceList);
		}

	/*
	 * @StructurePatternNames.LinePattern> X:int | Y:int | Width:int
	 * |Height:int,
	 * 
	 * @StructurePatternNames.OvalPatetrn> X:int | Y:int | Width:int |Height:int
	 */
	
	public void setExpectedTypes(String[] aSpecifications) {
		for (String aSpecification : aSpecifications) {
			setExpectedTypesOfType(aSpecification);
		}

	}
	

	
	protected void logTypeNotMatched(DetailAST aTreeAST, String aType) {
//		String aSourceName = shortFileName(astToFileContents.get(aTreeAST)
//				.getFilename());
		String aTypeName = getName(getEnclosingTypeDeclaration(aTreeAST));
		super.log(aTreeAST, aTreeAST, aType, aTypeName);
//		if (aTreeAST == currentTree) {
//			DetailAST aLoggedAST = aTreeAST;
//			log(aLoggedAST.getLineNo(),  msgKey(), aSignature, aTypeName, aSourceName);
//
//		} else {
//			log(0, msgKey(), aSignature, aTypeName, aSourceName);
//		}

	}

	
	public Boolean matchType(List<String> aSpecifications,
			List<String> aTypes, DetailAST aTreeAST) {
		boolean retVal = true;
		for (String aSpecification : aSpecifications) {
		
//			String[] aPropertiesPath = aPropertySpecification.split(".");	
			Boolean hasMatched = matchType(aSpecification, aTypes);
			if (hasMatched == null)
				return null;
			if (!hasMatched) {
				logTypeNotMatched(aTreeAST, aSpecification);
				retVal = false;
			}
		}
		return retVal;
	}
	

	
	public Boolean matchType(
			String aSpecification, List<String> aTypes) {
		for (String aMethod : aTypes) {
			Boolean hasMatched = matchesType(aSpecification, aMethod);
			if (hasMatched == null)
				return null;
			if (hasMatched)
				// return
				// aSpecifiedType.equalsIgnoreCase(aPropertyInfos.get(aProperty).getGetter().getReturnType());
				return true;

			else 
				continue;
		}
		return false;
	}
	
	
	abstract protected List<STNameable> getTypes(STType anSTType) ;
	public Boolean matchTypes(STType anSTType, List<String> aSpecifiedInterfaces, DetailAST aTree) {
		List<STNameable> aClassNames = getTypes(anSTType);
		if (aClassNames == null) {
			return null;
		}
		
		return matchType(aSpecifiedInterfaces, toNames(aClassNames), aTree);
	}
	
	abstract boolean doCheck(STType anSTType) ;


	public Boolean doPendingCheck(DetailAST anAST, DetailAST aTree) {
//		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
//				.getSTClassByShortName(
//						getName(getEnclosingTypeDeclaration(aTree)));
		STType anSTType = getSTType(aTree);

		if (anSTType.isEnum())
			return true;
		boolean doCheck = doCheck(anSTType);
		if (!doCheck)
			return true;
		
		String aSpecifiedType = findMatchingType(typeToTypes.keySet(),
				anSTType);
		if (aSpecifiedType == null)
			return true; // the constraint does not apply to us
		
		List<String> aSpecifiedTypes = typeToTypes.get(aSpecifiedType);
		return matchTypes(anSTType, aSpecifiedTypes, aTree);
	}

	@Override
	protected String msgKey() {
		// TODO Auto-generated method stub
		return MSG_KEY;
	}
	public void doFinishTree(DetailAST ast) {
		// STType anSTType =
		// SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(fullTypeName);
		// for (STMethod aMethod: anSTType.getMethods()) {
		// visitMethod(anSTType, aMethod);
		// }
		maybeAddToPendingTypeChecks(ast);
		super.doFinishTree(ast);

	}


}
