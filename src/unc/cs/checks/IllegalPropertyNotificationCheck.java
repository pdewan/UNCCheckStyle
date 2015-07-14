package unc.cs.checks;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import unc.cs.symbolTable.CallInfo;
import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.SymbolTableFactory;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;


public  class IllegalPropertyNotificationCheck extends MethodCallVisitedCheck {
	

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY = "illegalPropertyNotification";
    public static final String MSG_KEY_2 = "illegalPropertySpecifier";
    public static final String PROPERTY_CHANGE = "PropertyChangeEvent";
    public static final String FIRE_PROPERTY = "firePropertyChange";
   
    @Override
	public int[] getDefaultTokens() {
		return new int[] {
				 TokenTypes.PACKAGE_DEF,
				TokenTypes.CLASS_DEF,
				// TokenTypes.INTERFACE_DEF,
				// TokenTypes.TYPE_ARGUMENTS,
				// TokenTypes.TYPE_PARAMETERS,
				TokenTypes.VARIABLE_DEF, TokenTypes.PARAMETER_DEF,
				TokenTypes.METHOD_DEF, TokenTypes.CTOR_DEF,
				// TokenTypes.IMPORT, TokenTypes.STATIC_IMPORT,
				// TokenTypes.PARAMETER_DEF,
				// TokenTypes.LCURLY,
				// TokenTypes.RCURLY,
				TokenTypes.CTOR_CALL,
				TokenTypes.LITERAL_NEW,
				TokenTypes.METHOD_CALL };

	}
    protected Boolean processPropertyChange(List<DetailAST> aParameters) {
    	if (aParameters.size() < 2)
    		return false;
    	DetailAST aPropertyExpression = aParameters.get(1);
    	DetailAST aPropertySpecifier = getLastDescendent(aPropertyExpression);
    	String aPropertySpecifierText = aPropertySpecifier.getText();

    	if (aPropertySpecifier.getType() != TokenTypes.STRING_LITERAL) {
    		log ( aPropertySpecifier.getLineNo(),MSG_KEY_2, aPropertySpecifierText);
    		return true; // do not wnat super class to give error with msgKey();
    	}
    	String aPropertyName = maybeStripQuotes(aPropertySpecifierText);
    	STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(fullTypeName);
    	if (anSTType == null) return null;
    	return anSTType.hasActualProperty(aPropertyName);    	
    }

	@Override
	protected Boolean check(DetailAST aCalledMethodAST, String aShortMethodName, String aLongMethodName, CallInfo aCallInfo) {

		if (aCallInfo.getCalleee().equals(PROPERTY_CHANGE))
			return processPropertyChange(aCallInfo.getActuals());
		return true;
	}
	public void doVisitToken(DetailAST ast) {
		super.doVisitToken(ast);
	}
	@Override
	protected String msgKey() {
		return MSG_KEY;
	}
	
	
}
