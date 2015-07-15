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


public  class VectorPatternCheck extends ExpectedSignaturesCheck {
	
	public static final String VECTOR_SPECIFICATION = "@VECTOR_PATTERN" + TYPE_SEPARATOR + "elementAt:int->$T | size:->int ";
    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
//    public static final String MSG_KEY = "listPattern";
   
    public VectorPatternCheck() {
		super.setExpectedSignaturesOfType(VECTOR_SPECIFICATION);
	}
    
//	@Override
//	protected String msgKey() {
//		return MSG_KEY;
//	}
	
	
}
