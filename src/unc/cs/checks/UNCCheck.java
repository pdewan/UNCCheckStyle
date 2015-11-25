package unc.cs.checks;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FileContents;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public abstract class UNCCheck extends Check{
	protected boolean isPackageInfo = false;
	protected String checkAndFileDescription = "";
	protected static boolean errorOccurred;
	public static final String ERROR_KEY = "checkStyleError";
	public static final String CONSOLE_NAME = "UNCChecks";
	protected MessageConsole console;
	protected boolean notInPlugIn;
	  protected MessageConsole findConsole() {
		  if (notInPlugIn)
			  return null;
		  try {
		  if (console == null)
			  console = findConsole(CONSOLE_NAME);
		  return console;
		  } catch (Exception e) {
			  return null;
		  }
	  }
	  protected void consoleOut(String aString) {
		  if (notInPlugIn)
			  return;
		  findConsole().newMessageStream().println(aString);
	  }
	  protected MessageConsole findConsole(String name) {
		  try {
			  // see if we are executing in a plug in
			  Class.forName("org.eclipse.ui.console.ConsolePlugin");
		  } catch (Exception e){
			  notInPlugIn = true;
			  return null;
		  }
	      ConsolePlugin plugin = ConsolePlugin.getDefault();
	      IConsoleManager conMan = plugin.getConsoleManager();
	      IConsole[] existing = conMan.getConsoles();
	      for (int i = 0; i < existing.length; i++)
	         if (name.equals(existing[i].getName()))
	            return (MessageConsole) existing[i];
	      //no console found, so create a new one
	      MessageConsole myConsole = new MessageConsole(name, null);
	      conMan.addConsoles(new IConsole[]{myConsole});
	      return myConsole;
	   }

	public final void extendibleLog(int line, String key, Object... args) {
//		System.out.println("key:" + key);
		try {
        log(line, key, args);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	
	public abstract void doVisitToken(DetailAST ast);
	public void doFinishTree(DetailAST ast) {
		
	}
    public void doBeginTree(DetailAST ast) {
		
	} 
    
   
	
    public void beginTree(DetailAST ast) { 
    	if (errorOccurred)
    		return;
    	try {
    		isPackageInfo = false;
    		String aFileName = getFileContents().getFilename();
    		if (aFileName.endsWith("package-info.java")) {
    			isPackageInfo = true;
    			return;
    		}
    
    		checkAndFileDescription = "Check:" + this + " ast:" + ast + " " + getFileContents().getFilename();
//			System.out.println ("begin tree called from:" + this + " ast:" + ast + " " + getFileContents().getFilename());
//			if (ast.getType() == TokenTypes.LITERAL_NEW) {
//				System.out.println ("found new");
//			}
			doBeginTree(ast);
//			System.out.println ("Begin tree ended from:" + this + " ast:" + ast + " " + getFileContents().getFilename());

			
		} catch (RuntimeException e) {
			e.printStackTrace();
			errorOccurred = true;

			log(ast, ERROR_KEY, "Begin tree:" + checkAndFileDescription + " " + e.getMessage());
			consoleOut(e.getMessage() + " Description:" + checkAndFileDescription + "Stack:\n" + toString(e.getStackTrace()));

//			throw e;
			
		}
    	
    }

    public static String toString (StackTraceElement[] aStackTrace ) {
    	StringBuffer result = new StringBuffer();
    	for (StackTraceElement anElement:aStackTrace) {
    		result.append(anElement.toString() + "\n");
    	}
    	return result.toString();
    }

	public void finishTree(DetailAST ast) {
		if (errorOccurred)
    		return;
		try {
//			System.out.println ("finish tree called from:" + this + " ast:" + ast + " " + getFileContents().getFilename());
//			findConsole().newMessageStream().println("finish tree called from:" + this + " ast:" + ast + " " + getFileContents().getFilename());

//			if (ast.getType() == TokenTypes.LITERAL_NEW) {
//				System.out.println ("found new");
//			}
			if (isPackageInfo) {
				isPackageInfo = false;
				return;
			}
			doFinishTree(ast);
//			System.out.println ("Check ended from:" + this + " ast:" + ast + " " + getFileContents().getFilename());

			
		} catch (RuntimeException e) {
			System.out.println("Description:" + checkAndFileDescription);

			e.printStackTrace();
			errorOccurred = true;
			log(ast, ERROR_KEY, "Finish tree:" + checkAndFileDescription + " " + e.getStackTrace());
			consoleOut(e.getMessage() + " Description:" + checkAndFileDescription + "Stack:\n" + toString(e.getStackTrace()));

//			throw e;
			
		}
	}

	
	public void visitToken(DetailAST ast) {
		if (errorOccurred)
    		return;
		try {
			if (isPackageInfo)
				return;
//			System.out.println ("Check called from:" + this + " ast:" + ast + " " + getFileContents().getFilename());
//			if (ast.getType() == TokenTypes.LITERAL_NEW) {
//				System.out.println ("found new");
//			}
			doVisitToken(ast);
//			System.out.println ("Check ended from:" + this + " ast:" + ast + " " + getFileContents().getFilename());

			
		} catch (RuntimeException e) {
			e.printStackTrace();
			errorOccurred = true;

//			findConsole().newMessageStream().println("Stack:" + e.getStackTrace());
			log(ast, ERROR_KEY, "Visit token:" + checkAndFileDescription + " " + e.getMessage());
			consoleOut(e.getMessage() + " Description:" + checkAndFileDescription + "Stack:\n" + toString(e.getStackTrace()));


//			throw e;
			
		}
	}



    public final void extendibleLog(int lineNo, int colNo, String key,
            Object... args) {
//		System.out.println("key:" + key);

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
