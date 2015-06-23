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

public class BeanPropertiesCheck extends ComprehensiveVisitCheck {
	protected Map<String, String[]> typeToProperty = new HashMap<>();
	public static final String SEPARATOR = ">";
	
	public void doVisitToken(DetailAST ast) {
//    	System.out.println("Check called:" + MSG_KEY);
		switch (ast.getType()) {
		case TokenTypes.PACKAGE_DEF: 
			visitPackage(ast);
			return;
		case TokenTypes.CLASS_DEF:
			visitType(ast);
			return;
		default:
			System.err.println("Unexpected token");
		}
	}

	@Override
	public int[] getDefaultTokens() {
		return new int[] { TokenTypes.CLASS_DEF, TokenTypes.INTERFACE_DEF,
				TokenTypes.PACKAGE_DEF };
	}
	
    public void setExpectedPropertiesOfType (String aPattern) {
    	String[] extractTypeAndProperties = aPattern.split(">") ;
		String aType = extractTypeAndProperties[0];
		String[] aProperties = extractTypeAndProperties[1].split("|");
		typeToProperty.put(aType, aProperties);
	}  
	
	/*
	 * @StructurePatternNames.LinePattern> int X | int Y | int Width |int Height, 
	 * @StructurePatternNames.OvalPatetrn> X:int | Y:int | Width:int | Height: int
	 */
	public void setExpectedProperties (String[] aPattern) {
		
	}
//	public void visitType(DetailAST ast) {
//    	super.visitType(ast);
//	}
//	public void doVisitToken(DetailAST ast) {
//		// System.out.println("Check called:" + MSG_KEY);
//		switch (ast.getType()) {
//		case TokenTypes.PACKAGE_DEF:
//			visitPackage(ast);
//			return;
//		case TokenTypes.CLASS_DEF:
//			visitType(ast);
//			return;
//		case TokenTypes.INTERFACE_DEF:
//			visitType(ast);
//			return;
//		default:
//			System.err.println("Unexpected token");
//		}
//	}
	

//	public static Boolean matchType (Set<String> aSpecifiedTypes, STType anSTType) {
//		for (String aSpecifiedType:aSpecifiedTypes) {
//				checkTagsOfType(aSpecifiedType, anSTType);
//		}
//	}
	public static Boolean matchProperties (String[] aSpecifiedProperties, Map<String, PropertyInfo>  aPropertyInfos) {
		for (String aSpecifiedProperty: aSpecifiedProperties) {
			String[] aPropertyAndType = aSpecifiedProperty.split(" *");
			String aType = aPropertyAndType[0];
			String aProperty = aPropertyAndType[1];
			if (!matchProperty(aType, aProperty, aPropertyInfos))
				return false;
		}
		return true;
	}
	public static Boolean matchProperty(String aSpecifiedType, String aSpecifiedPoperty,  Map<String, PropertyInfo>  aPropertyInfos) {
		for (String aProperty:aPropertyInfos.keySet()) {
			if (aSpecifiedPoperty.equalsIgnoreCase(aProperty))
				return aSpecifiedType.equalsIgnoreCase(aPropertyInfos.get(aProperty).getGetter().getReturnType());
			else
				continue;
		}
		return false;
		
	}
	public Boolean doPendingCheck(DetailAST anAST, DetailAST aTree) {
		 STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName (
				 getName(getEnclosingClassDeclaration(aTree)));
		 String aSpecifiedType = findMatchingType(typeToProperty.keySet(), anSTType);
		 if (aSpecifiedType == null)
			 return true; // the constraint does not apply to us		 
		 
		 Map<String, PropertyInfo> aPropertyInfos =  anSTType.getPropertyInfos();
		 String[] aSpecifiedProperties = typeToProperty.get(aSpecifiedType);
		 return matchProperties(aSpecifiedProperties, aPropertyInfos);		 
	 }
	@Override
	protected String msgKey() {
		// TODO Auto-generated method stub
		return MSG_KEY;
	}
	public void finishTree(DetailAST ast) {		
//		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(fullTypeName);
//		for (STMethod aMethod: anSTType.getMethods()) {
//			visitMethod(anSTType, aMethod);
//		}
		maybeAddToPendingTypeChecks(ast);
		super.finishTree(ast);
    	
    }
}
