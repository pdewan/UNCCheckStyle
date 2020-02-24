package unc.cs.checks;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import unc.cs.symbolTable.STMethod;
import unc.cs.symbolTable.STType;

public class MethodAccessesGlobal extends  MissingMethodTextCheck {

	/**
	 * A key is pointing to the warning message text in "messages.properties"
	 * file.
	 */
	public static final String WARNING_MSG_KEY = "methodDoesNotAccessGlobal";
//	public static final String DIRECT_WARNING_MSG_KEY = "methodDoesNotDirectlyAccessGlobal";
	public static final String INFO_MSG_KEY = "methodAccessesGlobal";
	
//	protected String[] patterns;

	public int[] getDefaultTokens() {
		return new int[] {
				TokenTypes.CLASS_DEF, 
				TokenTypes.PACKAGE_DEF,
				TokenTypes.METHOD_DEF,
				TokenTypes.CTOR_DEF,
				};
	}
	protected  String msgKeyWarning() {
		return WARNING_MSG_KEY;
	} ;
	
	protected  String msgKeyInfo() {
		return INFO_MSG_KEY;
	}
	
	public void setExpectedReferences(String[] aPatterns) {
		super.setExpectedStrings(aPatterns);
	}

	
	/**
	 * For parsing, we can have overrident method.
	 * 
	 */
	protected Boolean matchMethodToString(STMethod aCallingMethod, String aSpecifiedText, Pattern aSpecifiedPattern) {
//		String aMethodText = toStringList(aCallingMethod.getAST());
		List<String> aGlobalsAccessed = aCallingMethod.getGlobalsAccessed();
		if (aGlobalsAccessed != null) {
			for (String aGlobalAccessed:aGlobalsAccessed) {
				if (aSpecifiedPattern.matcher(aGlobalAccessed).matches()) {
					return true;
				}
			}
		}
		Map<String, Set<DetailAST>> anUnknownsAccessed = aCallingMethod.getUnknownAccessed();
		if (anUnknownsAccessed != null) {
			for (String anUnknownAccessed:anUnknownsAccessed.keySet()) {
				if (aSpecifiedPattern.matcher(anUnknownAccessed).matches()) {
					return true;
				}
			}
		}
		return false; // assume no global table has to be checked

	}
	
}
