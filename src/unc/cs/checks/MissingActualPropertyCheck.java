package unc.cs.checks;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import unc.cs.symbolTable.CallInfo;
import unc.cs.symbolTable.STNameable;
import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.SymbolTableFactory;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;


public  class MissingActualPropertyCheck extends AbstractActualPropertyCheck {
	

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY = "missingActualProperty";
   
//    @Override
//	public int[] getDefaultTokens() {
//		return new int[] {
//				 TokenTypes.PACKAGE_DEF,
//				TokenTypes.CLASS_DEF,
//				 TokenTypes.INTERFACE_DEF,
//				// TokenTypes.TYPE_ARGUMENTS,
//				// TokenTypes.TYPE_PARAMETERS,
////				TokenTypes.VARIABLE_DEF, TokenTypes.PARAMETER_DEF,
////				TokenTypes.METHOD_DEF, TokenTypes.CTOR_DEF,
////				// TokenTypes.IMPORT, TokenTypes.STATIC_IMPORT,
////				// TokenTypes.PARAMETER_DEF,
////				// TokenTypes.LCURLY,
////				// TokenTypes.RCURLY,
////				TokenTypes.CTOR_CALL,
////				TokenTypes.LITERAL_NEW,
////				TokenTypes.METHOD_CALL };
//		};
//
//	}
//    protected void logPropertyNotFound(DetailAST aTreeAST, DetailAST anErroneousAST, String aProperty) {
//		String aSourceName = getShortFileName(aTreeAST);
//		 if (aTreeAST == currentTree) {
//
//			log(anErroneousAST.getLineNo(), anErroneousAST.getColumnNo(), msgKey(), aProperty, aSourceName);
//		} else {
//			log(0, msgKey(), aProperty,aSourceName);
//		}
//
//	}
//    public Boolean doPendingCheck(DetailAST ast, DetailAST aTreeAST) {
//    	String aTypeName = getName(getEnclosingTypeDeclaration(aTreeAST));
//		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aTypeName);
//		Boolean retVal = true;
//		if (anSTType == null)
//			return null;
//		STNameable[] anSTNameables = anSTType.getPropertyNames();
//		if (anSTNameables == null)
//			return null;
//		for (STNameable aNameable:anSTNameables) {
//			String aPropertyName = maybeStripQuotes(aNameable.getName());
//			Boolean hasProperty = anSTType.hasActualProperty(aPropertyName);
//			if (hasProperty == null) return null;
//			if (hasProperty) 
//				continue;
//			logPropertyNotFound(aTreeAST, aNameable.getAST(), aPropertyName);
//			retVal = false;
//			
//		}
//		return retVal;
//	}
//    public void doFinishTree(DetailAST ast) {
//		
//		maybeAddToPendingTypeChecks(ast);
//		super.doFinishTree(ast);
//
//	}
    @Override
    public Boolean checkActualProperty (STType anSTType, String aDeclarePropertyName ) {
    	return anSTType.hasActualProperty(aDeclarePropertyName);
    }
	@Override
	protected String msgKey() {
		return MSG_KEY;
	}
	
	
}
