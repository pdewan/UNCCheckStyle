package unc.cs.checks;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import unc.cs.symbolTable.PropertyInfo;
import unc.cs.symbolTable.STMethod;
import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.SymbolTableFactory;

public class ExpectedGettersCheck extends BeanTypedPropertiesCheck {
	public static final String MSG_KEY = "expectedGetters";
	@Override
	protected String msgKey() {
		// TODO Auto-generated method stub
		return MSG_KEY;
	}




	public Boolean matchType(String aSpecifiedType, String aProperty,
			Map<String, PropertyInfo> aPropertyInfos) {

		return matchGetter(aSpecifiedType, aProperty, aPropertyInfos);

	}


}
