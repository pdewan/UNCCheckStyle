package unc.cs.checks;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import unc.cs.symbolTable.AnSTMethod;
import unc.cs.symbolTable.CallInfo;
import unc.cs.symbolTable.PropertyInfo;
import unc.cs.symbolTable.STMethod;
import unc.cs.symbolTable.STNameable;
import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.SymbolTableFactory;

public  class ExpectedDeclaredSignaturesCheck extends ExpectedSignaturesCheck {
	public static final String MSG_KEY = "expectedDeclaredSignatures";

	@Override
	protected String msgKey() {
		return MSG_KEY;
	}
	@Override
	protected STMethod[] getMatchedMethods(STType anSTType) {
		return anSTType.getDeclaredMethods();
	}
}