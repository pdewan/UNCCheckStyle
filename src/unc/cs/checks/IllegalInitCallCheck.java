package unc.cs.checks;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import unc.cs.symbolTable.AnSTType;
import unc.cs.symbolTable.STType;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;


public class IllegalInitCallCheck extends MethodCallVisitedCheck {

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY = "illegalInitCall";
  

    @Override
	protected String msgKey() {
		return MSG_KEY;
	}
    @Override
	public int[] getDefaultTokens() {
		return new int[] {
						TokenTypes.PACKAGE_DEF, TokenTypes.CLASS_DEF,  
//						TokenTypes.INTERFACE_DEF, 
//						TokenTypes.TYPE_ARGUMENTS,
//						TokenTypes.TYPE_PARAMETERS,
						TokenTypes.METHOD_DEF, 
						TokenTypes.CTOR_DEF,
						TokenTypes.METHOD_CALL
//						TokenTypes.IMPORT, TokenTypes.STATIC_IMPORT,
//						TokenTypes.PARAMETER_DEF 
						};
	}
    public static boolean isExternalCall(String aLongMethodName) {
    	return aLongMethodName.indexOf(".") != -1 
				&& !aLongMethodName.startsWith("this");
    }
    
	@Override
	// succeeds if an init call
	protected boolean check(DetailAST ast, String aShortMethodName,
			String aLongMethodName) {
		return (
				!AnSTType.isInit(aShortMethodName)
				|| currentMethodIsConstructor // any call allowed from constructor
				|| AnSTType.isInit(currentMethodName) // ditto for init
				|| isExternalCall(aLongMethodName)); // anExternal call
	}
   
}
