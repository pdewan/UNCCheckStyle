package unc.cs.checks;


import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import unc.cs.symbolTable.AnSTType;
import unc.cs.symbolTable.STType;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;


public class IllegalComponentInstantiation extends ComprehensiveVisitCheck {

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY = "illegalComponentInstantiation";
    Map<DetailAST, List<DetailAST>> astToPendingTypeInstantiations = new HashMap();
  

    @Override
	protected String msgKey() {
		return MSG_KEY;
	}
    @Override
	public int[] getDefaultTokens() {
		return new int[] {
						TokenTypes.PACKAGE_DEF, TokenTypes.CLASS_DEF,  
//						TokenTypes.INTERFACE_DEF, 
						TokenTypes.TYPE_ARGUMENTS,
//						TokenTypes.TYPE_PARAMETERS,
						TokenTypes.METHOD_DEF, 
						TokenTypes.CTOR_DEF,
						TokenTypes.LITERAL_NEW
//						TokenTypes.METHOD_CALL
//						TokenTypes.IMPORT, TokenTypes.STATIC_IMPORT,
//						TokenTypes.PARAMETER_DEF 
						};
	}
   
    void visitInstantiation(DetailAST ast) {
    	
    	
    }
    boolean checkInstantiation(DetailAST ast) {
    	DetailAST classAST = ast.getNextSibling(); 
    	return (currentMethodIsConstructor || AnSTType.isInit(currentMethodName));
    	
    }
    public void visitToken(DetailAST ast) {	
		
		if (ast.getType() == TokenTypes.LITERAL_NEW)
			visitInstantiation(ast);
		else 
			super.visitToken(ast);
    }
   
}
