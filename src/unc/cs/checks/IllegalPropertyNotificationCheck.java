package unc.cs.checks;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import unc.cs.symbolTable.AnAbstractSTType;
import unc.cs.symbolTable.CallInfo;
import unc.cs.symbolTable.PropertyInfo;
import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.SymbolTableFactory;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;


public  class IllegalPropertyNotificationCheck extends MethodCallVisitedCheck {
	

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String INVALID_KEY = "invalidPropertyNotification";
    public static final String MISSING_KEY = "missingPropertyNotification";
    public static final String STRUCTURED_KEY = "structuredPropertyNotification";
    public static final String PROPERTY_CHANGE = "PropertyChangeEvent";
    public static final String FIRE_PROPERTY = "firePropertyChange";
    
    
    
    List<String> excludeProperties = new ArrayList();
    
	protected Set<String> excludeStructuredTypes = new HashSet();

   
    @Override
	public int[] getDefaultTokens() {
    	// do we need all of this?
		return new int[] {
				 TokenTypes.PACKAGE_DEF,
				TokenTypes.CLASS_DEF,
				TokenTypes.ENUM_DEF,
//				TokenTypes.ANNOTATION,
				// TokenTypes.INTERFACE_DEF,
				// TokenTypes.TYPE_ARGUMENTS,
				// TokenTypes.TYPE_PARAMETERS,
				TokenTypes.VARIABLE_DEF, TokenTypes.PARAMETER_DEF,
				TokenTypes.METHOD_DEF, TokenTypes.CTOR_DEF,
				// TokenTypes.IMPORT, TokenTypes.STATIC_IMPORT,
				// TokenTypes.PARAMETER_DEF,
				// TokenTypes.LCURLY,
				// TokenTypes.RCURLY,
				TokenTypes.CTOR_CALL,
				TokenTypes.LITERAL_NEW,
				TokenTypes.METHOD_CALL };

	}
    public void setExcludeProperties(String[] aProperties) {
    	excludeProperties = Arrays.asList(aProperties);
    }
    protected Boolean processPropertyChange(List<DetailAST> aParameters) {
    	if (aParameters.size() < 2)
    		return true;
    	DetailAST aPropertyExpression = aParameters.get(1);
    	DetailAST aPropertySpecifier = getLastDescendent(aPropertyExpression);
    	String aPropertySpecifierText = aPropertySpecifier.getText();
    	DetailAST aTreeAST = getEnclosingTreeDeclaration(aPropertySpecifier);
    	DetailAST aTypeAST = getEnclosingTypeDeclaration(aPropertySpecifier);
    	String aTypeName = getName(aTypeAST);
    	
    	
    	if (aPropertySpecifier.getType() != TokenTypes.STRING_LITERAL) {
//    		log ( aPropertySpecifier.getLineNo(),MSG_KEY_2, aPropertySpecifierText);
    		log (INVALID_KEY, aPropertySpecifier, aTreeAST, aPropertySpecifierText );
    		return true; // do not wnat super class to give error with msgKey();
    	}
    	String aPropertyName = maybeStripQuotes(aPropertySpecifierText);
    	if (excludeProperties.contains(aPropertyName))
    			return true;
//    	STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(fullTypeName);
    	STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aTypeName);

    	if (anSTType == null) return null;
    	Map<String, PropertyInfo> aPropertyInfos = anSTType.getPropertyInfos();
		if (aPropertyInfos == null)
			return null; // should not happen
	
		PropertyInfo aPropertyInfo = AnAbstractSTType.getPropertyInfo(aPropertyName, aPropertyInfos);

		if (aPropertyInfo == null) {
			log (MISSING_KEY, aPropertySpecifier,  aTreeAST, aPropertySpecifierText);
    		return true; // do not want super class to give message
    	
		}
    	Boolean isStructuredProperty = isStructuredProperty(aPropertyInfo);
    	if (isStructuredProperty == null)
    		return null;
//    	Boolean hasProperty = anSTType.hasActualProperty(aPropertyName);
//    	if (hasProperty == null)
//    		return null;
//    	if (!hasProperty) {
////    		log ( aPropertySpecifier.getLineNo(),MSG_KEY, aPropertySpecifierText);
//    		log (aPropertySpecifier, aTreeAST, aPropertySpecifierText );
//
//    		return true; // do not want super class to give message
//    	}
    	if (isStructuredProperty) {
//    		log (aPropertySpecifier, aTreeAST, aPropertySpecifierText , STRUCTURED_KEY);
			log (STRUCTURED_KEY, aPropertySpecifier,  aTreeAST, aPropertySpecifierText);

    		return true;

    	}
    	
//    	return anSTType.hasActualProperty(aPropertyName);   
    	return true;
    	
    }

	@Override
	protected Boolean check(DetailAST aCalledMethodAST, String aShortMethodName, String aLongMethodName, CallInfo aCallInfo) {

		if (aCallInfo.getCalleee().equals(PROPERTY_CHANGE))
			return processPropertyChange(aCallInfo.getActuals());
		return true;
	}
	public void doVisitToken(DetailAST ast) {
		super.doVisitToken(ast);
	}
	@Override
	protected String msgKey() {
		return INVALID_KEY;
	}
	
	
}
