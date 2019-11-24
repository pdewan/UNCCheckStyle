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
	public static final String CHECK_MSG_KEY = "mnemonicNameCheck";
	public static final String PRINT_MSG_KEY = "mnemonicNamePrint";	

	protected boolean processType = false;
	
	protected boolean processLocals = false;
	protected boolean processParameters = false;
	protected boolean processConstants = false;
	protected boolean processGlobals = false;
	protected boolean processPublicMethods = false;
	protected boolean processNonPublicMethods = false;
	protected int minimumLettersInNameComponent = 1;
	protected int minimumVowelsInNameComponent = 0;
	protected boolean printName = false;
	protected boolean printComponents = false;
	protected boolean checkComponents = false;
	protected Set<String> ignoreNames = new HashSet();

	




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
	
	protected void checkComponent(DetailAST aTreeAST, DetailAST anIdentifierAST, String aComponent, String... anExplanation) {
		NameComponentMetrics aMetrics = NameComponentMetrics.computeComponentMetrics(aComponent);

	}
	protected void checkIdentifier (DetailAST aTreeAST, DetailAST anIdentifierAST, String aName, String anExplanation) {
		String[] aComponents = ComprehensiveVisitCheck.splitCamelCaseHyphenDash(aName);
		if (isPrintComponents()) {
			log(anIdentifierAST, PRINT_MSG_KEY, aName, Arrays.toString(aComponents), anExplanation );
		}
		if (!isCheckComponents())
			return;
		for (String aComponent:aComponents) {
			checkComponent(aTreeAST, anIdentifierAST, aName, aComponent, anExplanation);

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
		for (STVariable anSTVariable:aVariables) {
			boolean isConstant = anSTVariable.isFinal();
			String aName = anSTVariable.getName();
			String aConstantOrVariable = isConstant?"Constant":"Variable";
			String anExplanation = fullTypeName + ".Global " + aConstantOrVariable;

			checkIdentifier(ast, anSTVariable.getAST(), aName, anExplanation);
//			String aComponents = Arrays.toString(ComprehensiveVisitCheck.splitCamelCaseHyphenDash(aName));
//			log(ast, PRINT_MSG_KEY, fullTypeName + ".Global:"  + aName + " Components:" + aComponents);
		}
	}
	protected void processLocalVars(DetailAST ast, STMethod anSTMethod, List<STVariable> aVariables) {
		for (STVariable anSTVariable:aVariables) {
			boolean isConstant = anSTVariable.isFinal();
			String aName = anSTVariable.getName();
			String aConstantOrVariable = isConstant?"Constant":"Variable";
			String anExplanation = fullTypeName + "." + anSTMethod.getName() + ".Local " + aConstantOrVariable;
			checkIdentifier(ast, anSTVariable.getAST(), aName, anExplanation);

		}
	}
	protected void processType(DetailAST ast, STType anSTType) {
		String anExplanation = "";
		anExplanation += 
				anSTType.isEnum()?"Enum": 
				   anSTType.isInterface()?"Interface":"Class";
		String aName = anSTType.getName();
		
			checkIdentifier(ast, ast, aName, anExplanation);
		}
	
	protected void processParameters(DetailAST ast, STMethod anSTMethod, List<STVariable> aVariables) {
		for (STVariable anSTVariable:aVariables) {
			boolean isConstant = anSTVariable.isFinal();
			String aName = anSTVariable.getName();
			String aConstantOrVariable = isConstant?"Constant":"Variable";
			String anExplanation = fullTypeName + "." + anSTMethod.getName() + ".Parameter " + aConstantOrVariable;
			checkIdentifier(ast, anSTVariable.getAST(), aName, anExplanation);
		}
	}
	protected void processMethods(DetailAST ast, STMethod[] aMethods) {
		for (STMethod anSTMethod:aMethods) {
			String aName = anSTMethod.getName();
			String aComponents = Arrays.toString(ComprehensiveVisitCheck.splitCamelCaseHyphenDash(aName));
			log(ast, PRINT_MSG_KEY, fullTypeName + ".Method:"  + aName + " Components:" + aComponents);
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
