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


public  class MapPatternCheck extends ExpectedSignaturesCheck {
	
	public static final String MAP_SPECIFICATION = "@MAP_PATTERN" + TYPE_SEPARATOR + "get:$K->$V | put:$K;$V->* ";
    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
//    public static final String MSG_KEY = "listPattern";
   
    public MapPatternCheck() {
		super.setExpectedSignaturesOfType(MAP_SPECIFICATION);
	}
    
//	@Override
//	protected String msgKey() {
//		return MSG_KEY;
//	}
	
	
}
