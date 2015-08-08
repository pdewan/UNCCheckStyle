package unc.cs.checks;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import unc.cs.symbolTable.AnSTNameable;
import unc.cs.symbolTable.STNameable;
import unc.cs.symbolTable.STType;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class StaticVariableCheck extends ComprehensiveVisitCheck {
	
	public static final String MSG_KEY = "staticVariable";	

//    private int max = 30;

    public StaticVariableCheck() {
    	
    }
    public int[] getDefaultTokens() {
        return new int[] { TokenTypes.CLASS_DEF, TokenTypes.PACKAGE_DEF,
        		TokenTypes.VARIABLE_DEF };
    }
    
	protected Set<String> singletonTypes = new HashSet();


	public void setSingletonTypes(String[] newVal) {
		singletonTypes =  new HashSet(Arrays.asList(newVal));
	}

    @Override
	protected String msgKey() {
		// TODO Auto-generated method stub
		return MSG_KEY;
	}
    protected Boolean isNotFinalNotSingletonStaticWithInstanceMethods(DetailAST ast, DetailAST aTreeAST) {
    	STType anSTType = getSTType(aTreeAST);
		if (!isStaticAndNotFinal(ast))
			return true;
		List<String> aSignatures = anSTType.getInstanceSignatures();
		if (aSignatures == null)
			return null;
		if (aSignatures.size() == 0) // only static methods, then it is ok
			return true;
		if (singletonTypes.size() == 0)
			return false;
		 final DetailAST aTypeParent = ast.findFirstToken(TokenTypes.TYPE);
			FullIdent aTypeIdent = FullIdent.createFullIdentBelow(aTypeParent);

//	 		final DetailAST anIdentifier = ast.findFirstToken(TokenTypes.IDENT);
		STNameable anSTNameable = new AnSTNameable(aTypeParent.getFirstChild(), aTypeIdent.getText());
		STNameable[] aTags = {anSTNameable};
		return (matchesSomeSpecificationTags(Arrays.asList(aTags), singletonTypes));
    }
    public Boolean doPendingCheck(DetailAST ast, DetailAST aTreeAST) {
    	Boolean retVal = isNotFinalNotSingletonStaticWithInstanceMethods(ast, aTreeAST);
    	if (retVal != null && !retVal)
    		log(ast, getName(ast));
    	return retVal;
//		STType anSTType = getSTType(aTreeAST);
//		if (!isStaticAndNotFinal(ast))
//			return true;
//		List<String> aSignatures = anSTType.getInstanceSignatures();
//		if (aSignatures == null)
//			return null;
//		if (aSignatures.size() == 0) // only static methods, then it is ok
//			return true;
//		if (singletonTypes.size() == 0)
//			return false;
//		 final DetailAST aType = ast.findFirstToken(TokenTypes.TYPE);
//	 		final DetailAST anIdentifier = ast.findFirstToken(TokenTypes.IDENT);
//		STNameable anSTNameable = new AnSTNameable(aType, aType.getText());
//		STNameable[] aTags = {anSTNameable};
//		return (matchesSomeSpecificationTags(Arrays.asList(aTags), singletonTypes));
		
	}
    @Override
	public void visitVariableDef(DetailAST ast) {
    	maybeAddToPendingTypeChecks(ast);
//		if (!checkIncludeExcludeTagsOfCurrentType())
//		return;
//		if (isStaticAndNotFinal(ast)) {
////			 log(ast.getLineNo(), ast.getColumnNo(),					 
////					 msgKey(), getName(ast));
//			 log(ast, getName(ast));
//		}
		
	}

    
    
//	@Override
//	public void doVisitToken(DetailAST ast) {
////			System.out.println("Check called:" + MSG_KEY);
//			switch (ast.getType()) {
//			
//			case TokenTypes.CLASS_DEF:
//				visitType(ast);
//				return;
//			
//			
//			case TokenTypes.VARIABLE_DEF:
//				visitVariableDef(ast);
//				return;
//			
//			default:
//				System.err.println("Unexpected token");
//			}
//			
//		}
	
}
