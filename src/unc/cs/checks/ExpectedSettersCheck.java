package unc.cs.checks;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import unc.cs.symbolTable.PropertyInfo;
import unc.cs.symbolTable.STMethod;
import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.SymbolTableFactory;

public class ExpectedSettersCheck extends BeanTypedPropertiesCheck {
	public static final String MSG_KEY = "expectedSetters";
	@Override
	protected String msgKey() {
		return MSG_KEY;
	}



//	public Boolean matchType(String aSpecifiedType, String aProperty,
//			Map<String, PropertyInfo> aPropertyInfos) {
//
//		return matchSetter(aSpecifiedType, aProperty, aPropertyInfos);
//
//	}



	@Override
	public Boolean matchType(String aSpecifiedType, PropertyInfo aProperty) {
		// TODO Auto-generated method stub
		return matchSetter(aSpecifiedType, aProperty);
	}

}
