package unc.cs.checks;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import unc.cs.symbolTable.AnSTMethod;
import unc.cs.symbolTable.AnSTType;
import unc.cs.symbolTable.CallInfo;
import unc.cs.symbolTable.STType;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;


public class LegalInitCallCheck extends InitCallCheck {

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY = "legalInitCall";
  

    @Override
	protected String msgKey() {
		return MSG_KEY;
	}

    
	@Override
	// "fails" if an init call in an init or constructor or external call
	protected Boolean check(STType aCallingType, DetailAST ast,
			String aShortMethodName, String aLongMethodName, CallInfo aCallInfo) {
		return  ! (
				AnSTMethod.isInit(aShortMethodName) && (
				currentMethodIsConstructor // any call allowed from constructor
				|| AnSTMethod.isInit(currentMethodName) // ditto for init			
				|| isExternalCall(aLongMethodName))); // anExternal call
				
	}
   
}
