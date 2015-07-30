package unc.cs.checks;

import java.util.Hashtable;
import java.util.Map;

import unc.cs.symbolTable.PropertyInfo;
import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.SymbolTableFactory;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class MinComponentObjectsCheck extends ComprehensiveVisitCheck {
	public static final String MSG_KEY = "minComponentObjects";
//	protected Map<String, Integer> typeToInt = new Hashtable<>();

	protected int minObjectComponents = 1;
	@Override
	public int[] getDefaultTokens() {
		return new int[] {
						TokenTypes.PACKAGE_DEF, 
						TokenTypes.CLASS_DEF,  
						TokenTypes.INTERFACE_DEF, 
//						TokenTypes.TYPE_ARGUMENTS,
//						TokenTypes.TYPE_PARAMETERS,
//						TokenTypes.VARIABLE_DEF,
//						TokenTypes.PARAMETER_DEF,
//						TokenTypes.METHOD_DEF, 
//						TokenTypes.CTOR_DEF,
//						TokenTypes.IMPORT, TokenTypes.STATIC_IMPORT,
//						TokenTypes.LCURLY,
//						TokenTypes.RCURLY,
//						TokenTypes.METHOD_CALL,
//						TokenTypes.IDENT,
						};
	}
	
//	public Boolean doPendingCheck(DetailAST anAST, DetailAST aTree) {
//		Boolean retVal = super.doPendingCheck(anAST, aTree);
//		if (retVal == null) return retVal;
//		
//		
//	}
	
	
//	protected void setTypeValue(String newVal) {
//		String[] aTypeAndValue = newVal.split (">");
//		String aType;
//		String aValueString = "";
//		if (aTypeAndValue.length == 1) {
////			aType = "*";
//			aValueString = aTypeAndValue[0];
//			
//		} else if (aTypeAndValue.length == 2){
//			aType = aTypeAndValue[0];
//			aValueString = aTypeAndValue[1];
//			try {				
//				int aValue = Integer.parseInt(aValueString);
//				typeToInt.put(aType, aValue);
//
//			} catch (Exception e) {
//				System.out.println ("Did not get int component value");
//				e.printStackTrace();
//			}
//		}
//		
//		int aValue = 1;
//		
//		
//	}
	
//	protected void setMinObjectComponentsOfType(String newVal) {
//		String[] aTypeAndValue = newVal.split (">");
//		String aType;
//		String aValueString = "";
//		if (aTypeAndValue.length == 1) {
////			aType = "*";
//			aValueString = aTypeAndValue[0];
//			
//		} else if (aTypeAndValue.length == 2){
//			aType = aTypeAndValue[0];
//			aValueString = aTypeAndValue[1];
//			try {				
//				int aValue = Integer.parseInt(aValueString);
//				typeToInt.put(aType, aValue);
//
//			} catch (Exception e) {
//				System.out.println ("Did not get int component value");
//				e.printStackTrace();
//			}
//		}
//		
//		int aValue = 1;
//		
//		
//	}
	
	public void setMinObjectComponents(String[] newVal) {
		for (String aString:newVal) {
			setIntValueOfType(aString);
		}
//		minObjectComponents = newVal;
	}

	@Override
	protected String msgKey() {
		// TODO Auto-generated method stub
		return MSG_KEY;
	}
	public Boolean doPendingCheck(DetailAST anAST, DetailAST aTree) {
		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
				.getSTClassByShortName(
						getName(getEnclosingTypeDeclaration(aTree)));
		if (anSTType.isEnum())
			return true;
		String aType = findMatchingType(typeToInt.keySet(), anSTType);
		Integer aMinComponents;
		if (aType != null)
		   aMinComponents = getInt(aType);
		else
			aMinComponents = minObjectComponents;
		Map<String, PropertyInfo> aPropertyInfos = anSTType.getPropertyInfos();
		if (aPropertyInfos == null) {
			return null;
		}
		int aNumObjectComponents = 0;
		for (String aPropertyName:aPropertyInfos.keySet()) {
			String aTypeName = aPropertyInfos.get(aPropertyName).getType();
			if (!primitiveTypesSet.contains(aTypeName))
				aNumObjectComponents++;
		}
		if (aNumObjectComponents >= aMinComponents) {
			return true;
		}
		
		
		String aSourceName = shortFileName(astToFileContents.get(aTree)
				.getFilename());
		if (aTree == currentTree) {
			DetailAST aTypeTree = getEnclosingTypeDeclaration(aTree);
			DetailAST aNameAST = getNameAST(aTypeTree);
			

			log (aNameAST.getLineNo(), msgKey(), aNumObjectComponents, aMinComponents, aSourceName );
		} else {
			log (0, msgKey(), aNumObjectComponents, aMinComponents, aSourceName );
		}
		
		return false;
		
	}

    public void doFinishTree(DetailAST ast) {
		
		maybeAddToPendingTypeChecks(ast);
		super.doFinishTree(ast);

	}

	
}
