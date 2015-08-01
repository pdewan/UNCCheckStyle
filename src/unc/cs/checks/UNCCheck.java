package unc.cs.checks;

import java.util.HashMap;
import java.util.Map;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FileContents;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public abstract class UNCCheck extends Check{
	protected boolean isPackageInfo = false;

	public final void extendibleLog(int line, String key, Object... args) {
		System.out.println("key:" + key);
        log(line, key, args);
    }
	
	public abstract void doVisitToken(DetailAST ast);
	public void doFinishTree(DetailAST ast) {
		
	}
    public void doBeginTree(DetailAST ast) {
		
	} 
	
    public void beginTree(DetailAST ast) {  
    	try {
    		isPackageInfo = false;
    		String aFileName = getFileContents().getFilename();
    		if (aFileName.endsWith("package-info.java")) {
    			isPackageInfo = true;
    			return;
    		}
			System.out.println ("begin tree called from:" + this + " ast:" + ast + " " + getFileContents().getFilename());
//			if (ast.getType() == TokenTypes.LITERAL_NEW) {
//				System.out.println ("found new");
//			}
			doBeginTree(ast);
			System.out.println ("Begin tree ended from:" + this + " ast:" + ast + " " + getFileContents().getFilename());

			
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
			
		}
    	
    }


	public void finishTree(DetailAST ast) {
		try {
			System.out.println ("finish tree called from:" + this + " ast:" + ast + " " + getFileContents().getFilename());
//			if (ast.getType() == TokenTypes.LITERAL_NEW) {
//				System.out.println ("found new");
//			}
			if (isPackageInfo)
				return;
			doFinishTree(ast);
			System.out.println ("Check ended from:" + this + " ast:" + ast + " " + getFileContents().getFilename());

			
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
			
		}
	}

	
	public void visitToken(DetailAST ast) {
		try {
			if (isPackageInfo)
				return;
			System.out.println ("Check called from:" + this + " ast:" + ast + " " + getFileContents().getFilename());
//			if (ast.getType() == TokenTypes.LITERAL_NEW) {
//				System.out.println ("found new");
//			}
			doVisitToken(ast);
			System.out.println ("Check ended from:" + this + " ast:" + ast + " " + getFileContents().getFilename());

			
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
			
		}
	}



    public final void extendibleLog(int lineNo, int colNo, String key,
            Object... args) {
		System.out.println("key:" + key);

        log(lineNo, colNo, key, args);
    }
    protected  abstract String msgKey();
    public static boolean isPublicAndInstance(DetailAST methodOrVariableDef) {
		return isPublic(methodOrVariableDef) 
				&& ! isStatic(methodOrVariableDef);
	}
	public static boolean isPublic(DetailAST methodOrVariableDef) {
		return methodOrVariableDef.branchContains(TokenTypes.LITERAL_PUBLIC);
				
	}
	public static boolean isStatic(DetailAST methodOrVariableDef) {
		return methodOrVariableDef.branchContains(TokenTypes.LITERAL_STATIC);
				
	}
	public static boolean isFinal(DetailAST methodOrVariableDef) {
		return methodOrVariableDef.branchContains(TokenTypes.FINAL);				
	}
	public static boolean isStaticAndNotFinal(DetailAST methodOrVariableDef) {
		return isStatic (methodOrVariableDef)
				&& ! isFinal(methodOrVariableDef);
	}
	

}
