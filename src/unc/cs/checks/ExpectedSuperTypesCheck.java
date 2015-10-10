package unc.cs.checks;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import unc.cs.symbolTable.AnSTMethod;
import unc.cs.symbolTable.PropertyInfo;
import unc.cs.symbolTable.STMethod;
import unc.cs.symbolTable.STNameable;
import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.SymbolTableFactory;

public  class ExpectedSuperTypesCheck extends ExpectedTypesCheck {
	public static final String MSG_KEY = "expectedSuperTypes";
	@Override
	public int[] getDefaultTokens() {
		return new int[] {
				TokenTypes.CLASS_DEF,
				TokenTypes.INTERFACE_DEF
				};
	}
	public void setExpectedSuperTypes(String[] aSpecifications) {
		setExpectedTypes(aSpecifications);

	}
	
	protected List<STNameable> getTypes(STType anSTType) {
		return anSTType.getAllSuperTypes();
	}

	@Override
	boolean doCheck(STType anSTType) {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	protected String msgKey() {
		return MSG_KEY;
	}


}
