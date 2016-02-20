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


public  class ListPatternCheck extends ExpectedSignaturesCheck {
	
//	public static final String LIST_SPECIFICATION = "@LIST_PATTERN" + TYPE_SEPARATOR + "get:int->$T | size:->int ";
	public static final String LIST_SPECIFICATION = "@LIST_PATTERN" + TYPE_SEPARATOR + "get:int->$T " + BASIC_SET_MEMBER_SEPARATOR + " size:->int ";

	/**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
//    public static final String MSG_KEY = "listPattern";
   
    public ListPatternCheck() {
		super.setExpectedSignaturesOfType(LIST_SPECIFICATION);
	}
    
//	@Override
//	protected String msgKey() {
//		return MSG_KEY;
//	}
	
	
}
