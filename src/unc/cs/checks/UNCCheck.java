package unc.cs.checks;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;

import unc.tools.checkstyle.AConsentFormVetoer;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FileContents;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public abstract class UNCCheck extends Check{
	public static long NEW_CHEKCKS_THRESHOLD = 60000; // a minute
	protected boolean isPackageInfo = false;
	protected String checkAndFileDescription = "";
	protected static boolean errorOccurred;
	public static final String ERROR_KEY = "checkStyleError";
	public static final String CONSOLE_NAME = "UNCChecks";
	protected MessageConsole console;	
	protected boolean notInPlugIn;
	protected boolean deferLogging;
	protected List<LogObject> log = new ArrayList();
	protected static long lastExecutionTime; // for all checks
	protected static String projectDirectory;
	protected String currentFile;
	public static String checkDirectory;
	protected static String consentFileName;
	protected static boolean consentFormSigned;
	protected static boolean consentFormShown;
	Integer sequenceNumber;
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
	  protected void maybeSaveProjectDirectory(String aFileName) {
		  if (projectDirectory != null)
			  return;
		  int anIndex = aFileName.indexOf("src");
		  if (anIndex < 0) {
			  return;
		  }
		  projectDirectory = aFileName.substring(0, anIndex - 1);
		  checkDirectory = projectDirectory+ "/" + AConsentFormVetoer.LOG_DIRECTORY;
		  consentFileName = checkDirectory + "/" + AConsentFormVetoer.CONSENT_FILE_NAME;
		  
		  
	  }
	  protected void saveFileName(String aFileName) {
		  int anIndex = aFileName.indexOf("src");
		  if (anIndex < 0) {
			  return;
		  }
		  currentFile = aFileName.substring(anIndex + "src".length() + 1);
	  }
	  protected void maybeAskForConsent() {
		  if (consentFormShown)
			  return;
		  consentFormSigned = AConsentFormVetoer.checkConstentForm(consentFileName);
		  consentFormShown = true;
			  
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
		if (isDeferLogging()) {
    		log.add(new ALogObject(line, -1, key, args));
    	} else {	
//		System.out.println("key:" + key);
		try {
        log(line, key, args);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	}
    }
	
	protected abstract void doVisitToken(DetailAST ast);
	protected abstract void doLeaveToken(DetailAST ast);

	public void doFinishTree(DetailAST ast) {
		
	}
    public void doBeginTree(DetailAST ast) {
		
	} 
    
   
	
    public void beginTree(DetailAST ast) { 
    	if (vetoChecks())
    		return;
    	try {
    		long aCurrentExecutionTime = System.currentTimeMillis();
    		if (aCurrentExecutionTime - lastExecutionTime > NEW_CHEKCKS_THRESHOLD) {
    			if (sequenceNumber == null) {
    				sequenceNumber = 0;
    			} else {
    				sequenceNumber++;
    			}
    			System.out.println ("New set of checks at:" + new Date(aCurrentExecutionTime));
    		}
    		isPackageInfo = false;
    		String aFileName = getFileContents().getFilename();
    		if (aFileName.endsWith("package-info.java")) {
    			isPackageInfo = true;
    			return;
    		}
    		
    		
//    		checkAndFileDescription = "Check:" + this + " ast:" + ast + " " + getFileContents().getFilename();
    		checkAndFileDescription = "Check:" + this + " ast:" + ast + " " + aFileName;

    		maybeSaveProjectDirectory(aFileName);
    		maybeAskForConsent();
    		if (vetoChecks())
    			return;
//			System.out.println ("begin tree called from:" + this + " ast:" + ast + " " + getFileContents().getFilename());
//			if (ast.getType() == TokenTypes.LITERAL_NEW) {
//				System.out.println ("found new");
//			}
			doBeginTree(ast);
//			System.out.println ("Begin tree ended from:" + this + " ast:" + ast + " " + getFileContents().getFilename());
			lastExecutionTime = aCurrentExecutionTime;
			
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
		if (vetoChecks())
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
	public void leaveToken(DetailAST ast) {
		if (vetoChecks())
    		return;
		try {
		doLeaveToken(ast);
		} catch (Exception e) {
			e.printStackTrace();
			errorOccurred = true;

//			findConsole().newMessageStream().println("Stack:" + e.getStackTrace());
			log(ast, ERROR_KEY, "Visit token:" + checkAndFileDescription + " " + e.getMessage());
			consoleOut(e.getMessage() + " Description:" + checkAndFileDescription + "Stack:\n" + toString(e.getStackTrace()));
		}
	}

	protected boolean vetoChecks() {
		return errorOccurred || 
				(consentFormShown && !consentFormSigned);
	}
	public void visitToken(DetailAST ast) {
//		if (errorOccurred)
		if (vetoChecks())

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
	
	protected boolean isDeferLogging() {
		return deferLogging;
	}

	protected void deferLogging() {
		deferLogging = true;
	}
	protected void flushLogAndResumeLogging() {
		deferLogging = false;
		for (LogObject aLogObject:log) {
			extendibleLog(aLogObject.getLine(),  aLogObject.getColumn(),  aLogObject.getKey(), aLogObject.getArgs());
		}
		log.clear();
	}
	protected void clearLogAndResumeLogging() {
		deferLogging = false;
		log.clear();
		
	}


    public final void extendibleLog(int lineNo, int colNo, String key,
            Object... args) {
//		System.out.println("key:" + key);
    	if (colNo < 0) {
    		extendibleLog(lineNo, key, args);
    		return;
    	}
    	if (isDeferLogging()) {
    		log.add(new ALogObject(lineNo, colNo, key, args));
    	} else {	
				
				
        log(lineNo, colNo, key, args);
    	}
    }
    protected  abstract String msgKey();
//    public static boolean isPublicAndInstance(DetailAST methodOrVariableDef) {
//		return isPublic(methodOrVariableDef) 
//				&& ! isStatic(methodOrVariableDef);
//	}
//	public static boolean isPublic(DetailAST methodOrVariableDef) {
//		return methodOrVariableDef.branchContains(TokenTypes.LITERAL_PUBLIC);
//				
//	}
//	public static boolean isStatic(DetailAST methodOrVariableDef) {
//		return methodOrVariableDef.branchContains(TokenTypes.LITERAL_STATIC);
//				
//	}
//	public static boolean isFinal(DetailAST methodOrVariableDef) {
//		return methodOrVariableDef.branchContains(TokenTypes.FINAL);				
//	}
//	public static boolean isStaticAndNotFinal(DetailAST methodOrVariableDef) {
//		return isStatic (methodOrVariableDef)
//				&& ! isFinal(methodOrVariableDef);
//	}
	

}
