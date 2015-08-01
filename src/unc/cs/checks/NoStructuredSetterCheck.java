package unc.cs.checks;

import java.util.List;
import java.util.Map;

import unc.cs.symbolTable.AnSTMethodFromMethod;
import unc.cs.symbolTable.PropertyInfo;
import unc.cs.symbolTable.STNameable;
import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.SymbolTableFactory;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class NoStructuredSetterCheck extends ComprehensiveVisitCheck {
	public static final String MSG_KEY = "noStructuredSetter";

	
	@Override
	public int[] getDefaultTokens() {
		return new int[] { TokenTypes.PACKAGE_DEF, 
				TokenTypes.CLASS_DEF,
				 TokenTypes.INTERFACE_DEF,
//				 TokenTypes.ENUM_DEF
				
		};
	}

	@Override
	protected String msgKey() {
		return MSG_KEY;
	}

	@Override
	public void doVisitToken(DetailAST ast) {
		super.doVisitToken(ast);
		
	}
public void doFinishTree(DetailAST ast) {
		
		maybeAddToPendingTypeChecks(ast);
		super.doFinishTree(ast);

	}
	@Override
	public Boolean doPendingCheck(DetailAST anAST, DetailAST aTree) {
		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(fullTypeName);
		if (anSTType == null) // we did not  visit the type
			return true;
		if (anSTType.isEnum())
			return true;
		Map<String, PropertyInfo> aPropertyInfos = anSTType.getPropertyInfos();
		if (aPropertyInfos == null)
			return null;
		Boolean retVal = true;
		for (String aPropertyName:aPropertyInfos.keySet()) {
			PropertyInfo aPropertyInfo = aPropertyInfos.get(aPropertyName);
			String aType = aPropertyInfo.getType();
			STNameable aSetter = aPropertyInfo.getSetter();
			if (aSetter instanceof AnSTMethodFromMethod) continue;// external class
			if (isOEAtomic(aType) || aSetter == null) continue;
			DetailAST aSetterAST = aSetter.getAST();
			log(aSetterAST.getLineNo(), msgKey(), aPropertyName, aType);
			retVal = false;
		}
		return retVal;		
		
	}

	
}
