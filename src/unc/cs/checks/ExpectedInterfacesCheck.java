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

public  class ExpectedInterfacesCheck extends ExpectedTypesCheck {
	public static final String MSG_KEY = "expectedInterfaces";
	@Override
	public int[] getDefaultTokens() {
		return new int[] {
				TokenTypes.CLASS_DEF,
				};
	}
	
	public void setExpectedInterfaces(String[] aSpecifications) {
//		super.setExpectedStrings(aPatterns);

		super.setExpectedStrings(aSpecifications);

	}
	

	
	protected List<STNameable> getTypes(STType anSTType) {
		return anSTType.getAllInterfaces();
	}
	@Override
	protected
	boolean doCheck(STType anSTType) {
		return !anSTType.isInterface() && !anSTType.isEnum() && !anSTType.isAnnotation();
	}
	@Override
	protected String msgKey() {
		return MSG_KEY;
	}
//	@Override
//	protected Boolean processStrings(DetailAST anAST, DetailAST aTree, STType anSTType, String aSpecifiedType, String[] aStrings) {
//		Map<String, PropertyInfo> aPropertyInfos = anSTType.getPropertyInfos();
//
//		if (aPropertyInfos == null)
//			return null;
//
//		Map<String, PropertyInfo> anUmmatchedPropertyInfos = new HashMap(aPropertyInfos);
//		return matchProperties(aStrings, anUmmatchedPropertyInfos, aTree);
//	}
}
