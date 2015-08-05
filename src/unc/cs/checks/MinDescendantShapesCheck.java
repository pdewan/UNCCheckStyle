package unc.cs.checks;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import unc.cs.symbolTable.PropertyInfo;
import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.SymbolTableFactory;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class MinDescendantShapesCheck extends DescendentPropertiesCheck {
	public static final String MSG_KEY = "minDescendantShapes";
//	protected Map<String, Integer> typeToInt = new Hashtable<>();

	protected int minShapeDescendents = 1;
	
//	public int[] getDefaultTokens() {
//		return new int[] {
//						TokenTypes.PACKAGE_DEF, 
//						TokenTypes.CLASS_DEF,  
//						TokenTypes.INTERFACE_DEF, 
////						TokenTypes.TYPE_ARGUMENTS,
////						TokenTypes.TYPE_PARAMETERS,
////						TokenTypes.VARIABLE_DEF,
////						TokenTypes.PARAMETER_DEF,
////						TokenTypes.METHOD_DEF, 
////						TokenTypes.CTOR_DEF,
////						TokenTypes.IMPORT, TokenTypes.STATIC_IMPORT,
////						TokenTypes.LCURLY,
////						TokenTypes.RCURLY,
////						TokenTypes.METHOD_CALL,
////						TokenTypes.IDENT,
//						};
//	}
	
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
	
	
	
	public void setMinShapeDescendants(String[] newVal) {
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
		
//		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
//				.getSTClassByShortName(
//						getName(getEnclosingTypeDeclaration(aTree)));
		STType anSTType = getSTType(aTree);

		if (anSTType.isEnum())
			return true;
		String aType = findMatchingType(typeToInt.keySet(), anSTType);
		Integer aMinDescendents;
		if (aType != null)
			aMinDescendents = getInt(aType);
		else
			aMinDescendents = minShapeDescendents;
		Boolean aDescendentsBuilt = super.doPendingCheck(anAST, aTree);
		if (aDescendentsBuilt == null)
			return null;
		int aNumDescendents = 0;
		for (String aKey:propertyToTypes.keySet()) {
			List<String> aTypes = propertyToTypes.get(aKey);
			if (isShape(aTypes))
				aNumDescendents++;
		}
		if (aNumDescendents >= minShapeDescendents) 
			return true;
				
		String aSourceName = shortFileName(astToFileContents.get(aTree)
				.getFilename());
		if (aTree == currentTree) {
			DetailAST aTypeTree = getEnclosingTypeDeclaration(aTree);
			DetailAST aNameAST = getNameAST(aTypeTree);
			

			log (aNameAST.getLineNo(), msgKey(), aNumDescendents, aMinDescendents, aSourceName );
		} else {
			log (0, msgKey(), aNumDescendents, aMinDescendents, aSourceName );
		}
		
		return false;
		
		
	}

//    public void doFinishTree(DetailAST ast) {
//		super.doFinishTree(ast);		
//		maybeAddToPendingTypeChecks(ast);
//
//	}

	
}
