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
import unc.tools.checkstyle.ProjectSTBuilderHolder;

public abstract class ExpectedTypesCheck extends ComprehensiveVisitCheck {
//	public static final String MSG_KEY = "expectedInterfaces";

	protected Map<String, List<String>> typeToTypes = new HashMap<>();
//	protected Map<String, List<STMethod>> typeToMethods = new HashMap<>();

//	public static final String SEPARATOR = ">";
	public int[] getDefaultTokens() {
		return new int[] {
				TokenTypes.CLASS_DEF,
				TokenTypes.INTERFACE_DEF
				};
	}
	


	
	// this should be in an abstract class
		public void setSpecifiedTypesOfType(String aPattern) {
			String[] extractTypeAndInterfaces = aPattern.split(TYPE_SEPARATOR);
			String aType = extractTypeAndInterfaces[0].trim();
			String[] aTypes = extractTypeAndInterfaces[1].split(SET_MEMBER_SEPARATOR);
			for (int i = 0; i < aTypes.length; i++) {
				aTypes[i] = aTypes[i].trim();
			}
			List<String> aTypeList = Arrays.asList(aTypes);
			List<String> anExistingList = typeToTypes.get(aType);
			
			if (anExistingList == null) {
				typeToTypes.put(aType, aTypeList);
			} else {
				List<String> aNewList = new ArrayList();
				aNewList.addAll(aTypeList);
				aNewList.addAll(anExistingList);
				typeToTypes.put(aType, aNewList);

//				anExistingList.addAll(aTypeList);
				
			}
				
		}

	/*
	 * @StructurePatternNames.LinePattern> X:int | Y:int | Width:int
	 * |Height:int,
	 * 
	 * @StructurePatternNames.OvalPatetrn> X:int | Y:int | Width:int |Height:int
	 */
	
	public void setExpectedTypes(String[] aSpecifications) {
//		for (String aSpecification : aSpecifications) {
//			setSpecifiedTypesOfType(aSpecification);
//		}
		setSpecifiedTypes(aSpecifications);

	}
	
	public void setSpecifiedTypes(String[] aSpecifications) {
		for (String aSpecification : aSpecifications) {
			setSpecifiedTypesOfType(aSpecification);
		}

	}
	
	protected void logTypeNotMatched(DetailAST aTypeAST, DetailAST aTreeAST, String aType) {
//		String aSourceName = shortFileName(astToFileContents.get(aTreeAST)
//				.getFilename());
		String aTypeName = getName(getEnclosingTypeDeclaration(aTypeAST));
		super.log(aTypeAST, aTreeAST, aType, aTypeName);
//		if (aTreeAST == currentTree) {
//			DetailAST aLoggedAST = aTreeAST;
//			log(aLoggedAST.getLineNo(),  msgKey(), aSignature, aTypeName, aSourceName);
//
//		} else {
//			log(0, msgKey(), aSignature, aTypeName, aSourceName);
//		}

	}
	
	protected boolean logOnNoMatch() {
		return true;
	}

	
	public Boolean matchType(List<String> aSpecifications,
			List<String> aTypes, DetailAST aTypeAST, DetailAST aTreeAST) {
		Boolean retVal = true;
		int i = 0;
		for (String aSpecification : aSpecifications) {
		
//			String[] aPropertiesPath = aPropertySpecification.split(".");	
			Boolean hasMatched = matchType(maybeStripComment(aSpecification), aTypes);
			if (hasMatched == null) {
//				return null;
				retVal = null; // means we will try again later
				continue;

			}
//			if (!hasMatched) {
//				logTypeNotMatched(aTreeAST, aSpecification);
//				retVal = false;
//			}
			if ((!hasMatched && logOnNoMatch()) || (hasMatched && !logOnNoMatch())) {
				logTypeNotMatched(aTypeAST, aTreeAST, aSpecification);
				if (retVal != null)
					retVal = false;
			}
		}
		return retVal;
	}
	

	
	public Boolean matchType(
			String aSpecification, List<String> aTypes) {
		for (String aMethod : aTypes) {
			Boolean hasMatched = matchesTypeUnifying(aSpecification, aMethod);
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
		int i = 3;
		List<STNameable> aClassNames = getTypes(anSTType);
		if (aClassNames == null) {
			return null;
		}
		
		return matchType(aSpecifiedInterfaces, toNames(aClassNames), anSTType.getAST(), aTree);
	}
	
//	abstract boolean doCheck(STType anSTType) ;


	public Boolean doPendingCheck(DetailAST anAST, DetailAST aTree) {
//		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
//				.getSTClassByShortName(
//						getName(getEnclosingTypeDeclaration(aTree)));
		STType anSTType = getSTType(anAST);
		if (anSTType == null) {
			System.err.println ("Did not find sttype for " + anAST);
			return true;
		}
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
//		if (aSpecifiedType.equals("ABridgeSceneController")) {
//			System.out.println ("found ABridgeSceneController")
//		}
		return matchTypes(anSTType, aSpecifiedTypes, aTree);
	}

	@Override
	protected String msgKey() {
		// TODO Auto-generated method stub
		return MSG_KEY;
	}
//	public void doFinishTree(DetailAST ast) {
//		// STType anSTType =
//		// SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(fullTypeName);
//		// for (STMethod aMethod: anSTType.getMethods()) {
//		// visitMethod(anSTType, aMethod);
//		// }
//		maybeAddToPendingTypeChecks(ast);
//		super.doFinishTree(ast);
//
//	}
	@Override
	public void leaveType(DetailAST ast) {
		if (ProjectSTBuilderHolder.getSTBuilder().getVisitInnerClasses()) {
			maybeAddToPendingTypeChecks(ast);
		}
		super.leaveType(ast);
	}
	public void doFinishTree(DetailAST ast) {
		// STType anSTType =
		// SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(fullTypeName);
		// for (STMethod aMethod: anSTType.getMethods()) {
		// visitMethod(anSTType, aMethod);
		// }
		if (!ProjectSTBuilderHolder.getSTBuilder().getVisitInnerClasses()) {

		maybeAddToPendingTypeChecks(ast);
		}
		super.doFinishTree(ast);

	}


}
