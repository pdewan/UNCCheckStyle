package unc.cs.checks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import unc.cs.symbolTable.SymbolTableFactory;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class InterfaceDefinedCheck extends TypeDefinedCheck{
	public static final String MSG_KEY = "interfaceDefined";

	public InterfaceDefinedCheck() {
		
	}
	@Override
	public int[] getDefaultTokens() {
		return new int[] {
				TokenTypes.PACKAGE_DEF,
				TokenTypes.INTERFACE_DEF,
//				TokenTypes.ANNOTATION,
				};
	}

	@Override
	protected String msgKey() {
		// TODO Auto-generated method stub
		return MSG_KEY;
	}
	
}
