package unc.cs.checks;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import unc.cs.symbolTable.STMethod;
import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.STVariable;

public class MnemonicNameCheck extends STTypeVisited {
	public static final String MIN_VOWEL_LENGTH_MSG_KEY = "minimumVowelInNameCheck";
	public static final String MIN_NAME_LENGTH_CHECK = "minimumLettersInNameCheck";
	public static final String IN_DICTIONARY_CHECK = "nameInDictionaryCheck";



	public static final String CHECK_MSG_KEY = "mnemonicNameCheck";
	public static final String PRINT_MSG_KEY = "mnemonicNamePrint";	
	
	protected String[] allowedComponents = {};
	protected Set<String> allowedComponentsSet = new HashSet();

	

	protected boolean processType = false;
	
	protected boolean processLocals = false;
	protected boolean processParameters = false;
	protected boolean processConstants = false;
	protected boolean processGlobals = false;
	protected boolean processPublicMethods = false;
	protected boolean processNonPublicMethods = false;
	protected int minimumLettersInNameComponent = 3;
	protected int minimumVowelsInNameComponent = 1;
	protected boolean printName = false;
	protected boolean printComponents = false;
	protected boolean checkComponents = false;
	
	protected boolean checkVowelLength = false;
	

	protected boolean checkNumLetters = false;
	

	protected boolean checkInDictionary = false;



	
	protected Set<String> ignoreNames = new HashSet();

	
	public boolean isCheckNumLetters() {
		return checkNumLetters;
	}

	public void setCheckNumLetters(boolean checkLetterLength) {
		this.checkNumLetters = checkLetterLength;
	}

	public boolean isCheckNumVowels() {
		return checkVowelLength;
	}

	public void setCheckNumVowels(boolean checkVowelLength) {
		this.checkVowelLength = checkVowelLength;
	}
	public boolean isCheckInDictionary() {
		return checkInDictionary;
	}

	public void setCheckInDictionary(boolean newVal) {
		this.checkInDictionary = newVal;
	}


	public void setIgnoreNames(String[] anExceptions) {
		for (String anException : anExceptions) {
			ignoreNames.add(anException);
		}
	}

	public boolean isProcessType() {
		return processType;
	}


	public void setProcessType(boolean processType) {
		this.processType = processType;
	}
	public boolean isCheckComponents() {
		return checkComponents;
	}
	public void setCheckComponents(boolean checkComponents) {
		this.checkComponents = checkComponents;
	}
	public boolean isProcessLocals() {
		return processLocals;
	}
	public void setProcessLocals(boolean processLocals) {
		this.processLocals = processLocals;
	}
	public boolean isProcessParameters() {
		return processParameters;
	}
	public void setProcessParameters(boolean processParameters) {
		this.processParameters = processParameters;
	}
	public boolean isProcessConstants() {
		return processConstants;
	}
	public void setProcessConstants(boolean processConstants) {
		this.processConstants = processConstants;
	}
	public boolean isProcessGlobals() {
		return processGlobals;
	}
	public void setProcessGlobals(boolean processGlobals) {
		this.processGlobals = processGlobals;
	}
	public boolean isProcessPublicMethods() {
		return processPublicMethods;
	}
	public void setProcessPublicMethods(boolean processPublicMethods) {
		this.processPublicMethods = processPublicMethods;
	}
	public boolean isProcessNonPublicMethods() {
		return processNonPublicMethods;
	}
	public void setProcessNonPublicMethods(boolean processNonPublicMethods) {
		this.processNonPublicMethods = processNonPublicMethods;
	}
	public int getMinimumLettersInNameComponent() {
		return minimumLettersInNameComponent;
	}
	public void setMinimumLettersInNameComponent(int minimumLettersInNameComponent) {
		this.minimumLettersInNameComponent = minimumLettersInNameComponent;
	}
	public int getMinimumVowelsInNameComponent() {
		return minimumVowelsInNameComponent;
	}
	public void setMinimumVowelsInNameComponent(int minimumVowelsInNameComponent) {
		this.minimumVowelsInNameComponent = minimumVowelsInNameComponent;
	}
	public boolean isPrintName() {
		return printName;
	}
	public void setPrintName(boolean printName) {
		this.printName = printName;
	}
	public boolean isPrintComponents() {
		return printComponents;
	}
	public void setPrintComponents(boolean printComponents) {
		this.printComponents = printComponents;
	}
	public String[] getAllowedComponents() {
		return allowedComponents;
	}

	public void setAllowedComponents(String[] allowedComponents) {
		this.allowedComponents = allowedComponents;
		for (String aComponent:allowedComponents) {
			allowedComponentsSet.add(aComponent.toLowerCase());
//			allowedComponentsSet.add((aComponent + 's').toLowerCase());
			
		}
	}
	@Override
	protected String msgKey() {
		// TODO Auto-generated method stub
		return null;
	}
	public void doVisitToken(DetailAST ast) {		
		switch (ast.getType()) {
		case TokenTypes.PACKAGE_DEF: 
			visitPackage(ast);
			return;
		case TokenTypes.CLASS_DEF:
//			if (fullTypeName == null)

			visitType(ast);
			return;	
		case TokenTypes.INTERFACE_DEF:
//			if (fullTypeName == null)

			visitType(ast);
			return;
		case TokenTypes.ENUM_DEF:
//			if (fullTypeName == null)

			visitType(ast);
			return;	
		default:
			System.err.println("Unexpected token");
		}
		
	}
	
	protected void checkComponent(DetailAST aTreeAST, DetailAST anIdentifierAST, String aName, String aComponent,STVariable anStVariable) {
		NameComponentMetrics aMetrics = NameComponentMetrics.computeComponentMetrics(aComponent);
//		checkNumLetters(aMetrics, aTreeAST, anIdentifierAST, aName, aComponent, anStVariable);		
//		checkNumVowels(aMetrics, aTreeAST, anIdentifierAST, aName, aComponent, anStVariable);
		if (allowedComponentsSet.contains(aComponent)) {
			return;
		}
		if (aMetrics.numLetters == 0) {
			return; // numbers are ok
		}
		if (!checkInDictionary(aMetrics, aTreeAST, anIdentifierAST, aName, aComponent, anStVariable) ) {
			checkNumLetters(aMetrics, aTreeAST, anIdentifierAST, aName, aComponent, anStVariable);		
			checkNumVowels(aMetrics, aTreeAST, anIdentifierAST, aName, aComponent, anStVariable);
		}
	}

	protected void checkNumLetters(NameComponentMetrics aMetrics, DetailAST aTreeAST, DetailAST anIdentifierAST,
			String aVariable, String aComponent, STVariable anStVariable) {
		if (!isCheckNumLetters())
			return;
		if (aMetrics.numLetters < minimumLettersInNameComponent) {
			log(anIdentifierAST, MIN_NAME_LENGTH_CHECK, "Class " + getFullTypeName(), aVariable, aComponent, minimumLettersInNameComponent);

//			log(MIN_NAME_LENGTH_CHECK, anIdentifierAST, aTreeAST, aVariable, aComponent, MIN_NAME_LENGTH_CHECK);
		}
	}
	protected void checkNumVowels(NameComponentMetrics aMetrics, DetailAST aTreeAST, DetailAST anIdentifierAST,
			String aVariable, String aComponent, STVariable anStVariable) {
		if (!isCheckNumVowels())
			return;
		if (aMetrics.numVowels < minimumVowelsInNameComponent) {
			log(anIdentifierAST, MIN_VOWEL_LENGTH_MSG_KEY, "Class " + getFullTypeName(), aVariable, aComponent, minimumVowelsInNameComponent);

//			log(MIN_VOWEL_LENGTH_MSG_KEY, anIdentifierAST,aTreeAST, MIN_VOWEL_LENGTH_MSG_KEY, aVariable, aComponent, MIN_VOWEL_LENGTH_MSG_KEY);
		}
	}
	/**
	 *return true if we know for sure the word is in dictionary
	 */
	protected boolean checkInDictionary(NameComponentMetrics aMetrics, DetailAST aTreeAST, DetailAST anIdentifierAST,
			String aVariable, String aComponent, STVariable anStVariable) {
		if (!isCheckInDictionary())
			return false;
//		if (aComponent.matches(".*[0-9].*")) { 
//		    return true; // it is effectively in dictionary
//		}
		if (!aMetrics.isDictionaryWord) {
//			log(IN_DICTIONARY_CHECK, anIdentifierAST, aTreeAST, aVariable, aComponent);
			log(anIdentifierAST, IN_DICTIONARY_CHECK,  "Class " + getFullTypeName(), aVariable, aComponent);
			return false;
		} else {
			return true;
		}
	}
	protected void checkIdentifier (DetailAST aTreeAST, DetailAST anIdentifierAST, String aName, String anExplanation, STVariable anStVariable) {
		String[] aComponents = ComprehensiveVisitCheck.splitCamelCaseHyphenDash(aName);
		if (isPrintComponents()) {
			
			log(anIdentifierAST, PRINT_MSG_KEY, aName, Arrays.toString(aComponents), anExplanation );
		}
		
		for (String aComponent:aComponents) {
			checkComponent(aTreeAST, anIdentifierAST, aName, aComponent.toLowerCase(), anStVariable);

		}
	}
	@Override
	protected boolean typeCheck(STType anSTClass) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	protected void doLeaveToken(DetailAST ast) {
		// TODO Auto-generated method stub
		
	}
	protected void processGlobalVars(DetailAST ast, List<STVariable> aVariables) {
		if (aVariables == null) {
			System.err.println ("Null global variables");
			return;
		}
		for (STVariable anSTVariable:aVariables) {
			boolean isConstant = anSTVariable.isFinal();
			String aName = anSTVariable.getName();
			String aConstantOrVariable = isConstant?"Constant":"Variable";
			String anExplanation = getFullTypeName() + ".Global " + aConstantOrVariable;

			checkIdentifier(ast, anSTVariable.getAST(), aName, anExplanation, anSTVariable);
//			String aComponents = Arrays.toString(ComprehensiveVisitCheck.splitCamelCaseHyphenDash(aName));
//			log(ast, PRINT_MSG_KEY, fullTypeName + ".Global:"  + aName + " Components:" + aComponents);
		}
	}
	protected void processLocalVars(DetailAST ast, STMethod anSTMethod, List<STVariable> aVariables) {
		if (aVariables == null) {
			System.err.println("NUll variables in:" + anSTMethod);
			return;
		}
		for (STVariable anSTVariable:aVariables) {
			boolean isConstant = anSTVariable.isFinal();
			String aName = anSTVariable.getName();
			String aConstantOrVariable = isConstant?"Constant":"Variable";
			String anExplanation = getFullTypeName() + "." + anSTMethod.getName() + ".Local " + aConstantOrVariable;
			checkIdentifier(ast, anSTVariable.getAST(), aName, anExplanation, anSTVariable);

		}
	}
	protected void processType(DetailAST ast, STType anSTType) {
		String anExplanation = "";
		anExplanation += 
				anSTType.isAnnotation()?"Annotation":
				anSTType.isEnum()?"Enum": 
				   anSTType.isInterface()?"Interface":
					   "Class";
		String aName = anSTType.getName();
		
			checkIdentifier(ast, ast, aName, anExplanation, null);
		}
	
	protected void processParameters(DetailAST ast, STMethod anSTMethod, List<STVariable> aVariables) {
		if (aVariables == null) {
			System.err.println("Null parameters in:" + anSTMethod);
			return;
		}
		for (STVariable anSTVariable:aVariables) {
			boolean isConstant = anSTVariable.isFinal();
			String aName = anSTVariable.getName();
			String aConstantOrVariable = isConstant?"Constant":"Variable";
			String anExplanation = getFullTypeName() + "." + anSTMethod.getName() + ".Parameter " + aConstantOrVariable;
			checkIdentifier(ast, anSTVariable.getAST(), aName, anExplanation, anSTVariable);
		}
	}
	protected void processMethods(DetailAST ast, STMethod[] aMethods) {
		for (STMethod anSTMethod:aMethods) {
			String aName = anSTMethod.getName();
			String aComponents = Arrays.toString(ComprehensiveVisitCheck.splitCamelCaseHyphenDash(aName));
			if (isPrintComponents()) {
			log(ast, PRINT_MSG_KEY, getFullTypeName() + ".Method:"  + aName + " Components:" + aComponents);
			}
			processLocalVars(ast, anSTMethod, anSTMethod.getLocalVariables());
			processParameters(ast, anSTMethod, anSTMethod.getParameters() );
		}
	}
	
	protected void checkSTType(DetailAST ast, STType anSTClass) {
		if (anSTClass == null)
			return;
		processType(ast, anSTClass);
		processGlobalVars(ast, anSTClass.getDeclaredSTGlobals());
		processMethods(ast, anSTClass.getDeclaredMethods());
		for (STMethod anSTMethod:anSTClass.getDeclaredMethods()) {
			processLocalVars(ast, anSTMethod, anSTMethod.getLocalVariables());
			processParameters(ast, anSTMethod, anSTMethod.getParameters());
		}
		
//		anSTClass.getDeclaredSTGlobals();
//		log(ast, PRINT_MSG_KEY);
//		System.out.println()
//		if (!typeCheck(anSTClass))
//    		super.logType(ast);
	}

	
}
