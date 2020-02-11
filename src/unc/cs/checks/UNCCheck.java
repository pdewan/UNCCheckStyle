package unc.cs.checks;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;

import unc.tools.checkstyle.AConsentFormVetoer;
import unc.tools.checkstyle.CheckStyleLogManager;
import unc.tools.checkstyle.CheckStyleLogManagerFactory;
import unc.tools.checkstyle.ProjectSTBuilderHolder;
import unc.tools.checkstyle.ProjectDirectoryHolder;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FileContents;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public abstract class UNCCheck extends Check {
	public static long NEW_CHEKCKS_THRESHOLD = 6000; // 1 seconds between
														// checks
	public static final String INFO = "info";
	public static final String WARNING = "warning";
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
//	protected static String projectDirectory;
	protected String currentFile;
	public static String checkDirectory;
	protected static String consentFileName;
	protected static boolean consentFormSigned;
	protected static boolean consentFormShown;
	static Integer sequenceNumber;
	static Integer numFilesInLastPhase;
	static Set<String> filesInCurrentPhase = new HashSet();
	static Set<String> allFilesInProject = new HashSet();
	static Set<String> filesInLastPhase = new HashSet();
	 boolean isAutoBuild;
	protected  boolean checkOnBuild = false;
	boolean visitedTree = true;
	boolean newSequenceNumber;

	public UNCCheck() {
		initCheck();
	}
	protected void initCheck() {
		findConsole();
	}
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
	
	protected void resetProject() {
		lastExecutionTime = 0;
		currentFile = null;
		checkDirectory = null;
		String consentFileName = null;
		consentFormSigned  = false;
		consentFormShown = false;
		sequenceNumber = null;
		numFilesInLastPhase = null;
		filesInCurrentPhase.clear();
		allFilesInProject.clear();
		filesInLastPhase.clear();
//		projectDirectory = null;
		ProjectDirectoryHolder.setCurrentProjectDirectory(null);
		checkDirectory = null;
		consentFileName = null;
		
	}
	protected void newProjectDirectory(String aNewProjectDirectory) {
		resetProject();
//		projectDirectory = aNewProjectDirectory;
		ProjectDirectoryHolder.setCurrentProjectDirectory(aNewProjectDirectory);

		checkDirectory = aNewProjectDirectory + "/"
				+ AConsentFormVetoer.LOG_DIRECTORY;
		consentFileName = checkDirectory + "/"
				+ AConsentFormVetoer.CONSENT_FILE_NAME;
		CheckStyleLogManagerFactory.getOrCreateCheckStyleLogManager()
				.maybeNewProjectDirectory(aNewProjectDirectory,
//						STBuilderCheck.getChecksName()
						ProjectSTBuilderHolder.getSTBuilder().getChecksName()
						);
	}

	protected boolean maybeSaveProjectDirectory(String aFileName) {
		// if (projectDirectory != null)
		// return;
		int anIndex = aFileName.indexOf("src");
		if (anIndex < 0) {
			return false;
		}
		String aNewProjectDirectory = aFileName.substring(0, anIndex - 1);
		if (aNewProjectDirectory.equals(ProjectDirectoryHolder.getCurrentProjectDirectory())) {
			return false;
		}
		newProjectDirectory(aNewProjectDirectory);
		return true;
//		projectDirectory = aNewProjectDirectory;
//		checkDirectory = projectDirectory + "/"
//				+ AConsentFormVetoer.LOG_DIRECTORY;
//		consentFileName = checkDirectory + "/"
//				+ AConsentFormVetoer.CONSENT_FILE_NAME;
//		CheckStyleLogManagerFactory.getOrCreateCheckStyleLogManager()
//				.maybeNewProjectDirectory(projectDirectory,
//						STBuilderCheck.getChecksName());

	}

	protected void saveFileName(String aFileName) {
		int anIndex = aFileName.indexOf("src");
		if (anIndex < 0) {
			return;
		}
		currentFile = aFileName.substring(anIndex + "src".length() + 1);
		filesInCurrentPhase.add(currentFile);
		allFilesInProject.add(currentFile);
//		System.out.println("added file:" + currentFile);
//		if (filesInCurrentPhase.size() > 0)
//		System.out.println ("num files in current phase:" + filesInCurrentPhase.size());
	}

	protected void maybeAskForConsent() {
//		System.out.println("Ask for consent: Checking if in plugin:" + notInPlugIn);
		if (notInPlugIn || isAutoBuild)
			return; // we are grading in server
		if (consentFormShown)
			return;
		consentFormSigned = AConsentFormVetoer
				.checkConstentForm(consentFileName);
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
//		} catch (Exception e) {
		} catch (Throwable e) {

			notInPlugIn = true;
			return null;
		}
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++)
			if (name.equals(existing[i].getName()))
				return (MessageConsole) existing[i];
		// no console found, so create a new one
		MessageConsole myConsole = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[] { myConsole });
		return myConsole;
	}
	
	protected void newSequenceNumber() {
		
		filesInLastPhase = filesInCurrentPhase;
//		Set<String> aFilesNotInLastPhase = new HashSet(allFilesInProject);
//		aFilesNotInLastPhase.removeAll(filesInLastPhase);
//		if (aFilesNotInLastPhase.size() > 0) {
//			System.err.println ("Missing files:" + aFilesNotInLastPhase);
//			System.err.println("Actual files:" + filesInLastPhase);
//		}
		filesInCurrentPhase = new HashSet();
//		System.out.println("Rsetting current phase files:");
		if (sequenceNumber == null) {
			sequenceNumber = 0;
		} else {
			sequenceNumber++;
		}
		CheckStyleLogManagerFactory.getOrCreateCheckStyleLogManager()
				.newSequenceNumber(sequenceNumber, isAutoBuild,
						filesInLastPhase);
		// System.out.println ("New set of checks at:" + new
		// Date(aCurrentExecutionTime));

		// else {
		// // filesInCurrentPhase;
		// }
	}

	public final void extendibleLog(int line, String key, Object... args) {
		if (isDeferLogging()) {
			log.add(new ALogObject(line, -1, key, args));
		} else {
			// System.out.println("key:" + key);
			try {
				log(line, key, args);
				CheckStyleLogManagerFactory.getOrCreateCheckStyleLogManager()
						.newLog(sequenceNumber, isAutoBuild, filesInLastPhase,
								currentFile, 0, 0, key, args);

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
	protected boolean notInPlugIn() {
		return notInPlugIn;
	}
	protected boolean isAutoBuild() {
		if (notInPlugIn())
			return false; // 
		Throwable aDummy = new Throwable();
		StackTraceElement[] anElements = aDummy.getStackTrace();
		StackTraceElement aSecondLastElement = anElements[anElements.length - 2];
		return aSecondLastElement.toString().contains("Build");

	}
	boolean isNewSequenceNumber() {
		return newSequenceNumber;
	}
	protected void maybeNewSequenceNumber() {
		long aCurrentExecutionTime = System.currentTimeMillis();
//		isAutoBuild = isAutoBuild();
		long aTimeDelta = aCurrentExecutionTime - lastExecutionTime;
		if (
		// isAutoBuild ||
		aTimeDelta > NEW_CHEKCKS_THRESHOLD) {
//			System.out.println("Time delta:" + aTimeDelta);
			newSequenceNumber = true;
			newSequenceNumber();

		} else {
			newSequenceNumber = false;
		}
		
		lastExecutionTime = aCurrentExecutionTime;
	}
	public void beginTree(DetailAST ast) {
//		if (vetoChecks())
//			return;
		try {
//			System.out.println ("begin tree:" + this);

//			maybeAskForConsent();
//			if (vetoChecks())
//				return;
//			maybeNewSequenceNumber();
//			long aCurrentExecutionTime = System.currentTimeMillis();
////			isAutoBuild = isAutoBuild();
//			long aTimeDelta = aCurrentExecutionTime - lastExecutionTime;
//			if (
//			// isAutoBuild ||
//			aTimeDelta > NEW_CHEKCKS_THRESHOLD) {
//				System.out.println("Time delta:" + aTimeDelta);
//				newSequenceNumber();
//
//			}
//			
//			lastExecutionTime = aCurrentExecutionTime;
			isAutoBuild = isAutoBuild();

			if (isAutoBuild && !isCheckOnBuild()) {
				visitedTree = false;
//				System.out.println ("Auto build and not check on build:");
				return;
			}
//			String aFileName = getFileContents().getFilename();

//			if (!isAutoBuild) {
//				maybeAskForConsent();
//			}
//			if (!isAutoBuild 
//					&& vetoChecks()) // should this not call maybeAskForConsent
//				return;
//			visitedTree = true;
//			isPackageInfo = false;
			String aFileName = getFileContents().getFilename();
//			if (aFileName.contains("ndo")) {
//				System.out.println ("found undo");
//			}
//			if (aFileName.contains("main")) {
//				System.out.println ("Found main");
//			}
			if (!fileNameCheck(aFileName))
				return;
			if (aFileName.endsWith("package-info.java")) {
				isPackageInfo = true;
				return;
			}
			isPackageInfo = false;


			// checkAndFileDescription = "Check:" + this + " ast:" + ast + " " +
			// getFileContents().getFilename();
			checkAndFileDescription = "Check:" + this + " ast:" + ast + " "
					+ aFileName;

			maybeSaveProjectDirectory(aFileName);
			if (!isAutoBuild) {
				maybeAskForConsent();
			}
			if (!isAutoBuild 
					&& vetoChecks()) // should this not call maybeAskForConsent
				return;
			visitedTree = true;
			
//			long aCurrentExecutionTime = System.currentTimeMillis();
//			isAutoBuild = isAutoBuild();
//			long aTimeDelta = aCurrentExecutionTime - lastExecutionTime;
//			if (
//					// isAutoBuild ||
//					aTimeDelta > NEW_CHEKCKS_THRESHOLD) {
//						System.out.println("Time delta:" + aTimeDelta);
//						newSequenceNumber();
//
//					}
//					
//					lastExecutionTime = aCurrentExecutionTime;
			
//			maybeAskForConsent();
//			if (vetoChecks())
//				return;
			maybeNewSequenceNumber(); // which should go first?
			// This line gives null pointer exception in Ram's code
			CheckStyleLogManagerFactory.getOrCreateCheckStyleLogManager()
					.maybeNewProjectDirectory(ProjectDirectoryHolder.getCurrentProjectDirectory(),
//							STBuilderCheck.getChecksName()
							ProjectSTBuilderHolder.getSTBuilder().getChecksName()
							);
			saveFileName(aFileName);
//			maybeNewSequenceNumber(); // which should go first?

//			maybeNewSequenceNumber();

			// System.out.println ("begin tree called from:" + this + " ast:" +
			// ast + " " + getFileContents().getFilename());
			// if (ast.getType() == TokenTypes.LITERAL_NEW) {
			// System.out.println ("found new");
			// }
			doBeginTree(ast);
			// System.out.println ("Begin tree ended from:" + this + " ast:" +
			// ast + " " + getFileContents().getFilename());
//			lastExecutionTime = aCurrentExecutionTime;

		} catch (RuntimeException e) {
			e.printStackTrace();
			errorOccurred = true;

			log(ast, ERROR_KEY, "Begin tree:" + checkAndFileDescription + " "
					+ e.getMessage());
			consoleOut(e.getMessage() + " Description:"
					+ checkAndFileDescription + "Stack:\n"
					+ toString(e.getStackTrace()));

			// throw e;

		}

	}

	public static String toString(StackTraceElement[] aStackTrace) {
		StringBuffer result = new StringBuffer();
		for (StackTraceElement anElement : aStackTrace) {
			result.append(anElement.toString() + "\n");
		}
		return result.toString();
	}

	public void finishTree(DetailAST ast) {
		if (vetoChecks() 
				|| !visitedTree)
			return;
		try {
			// System.out.println ("finish tree called from:" + this + " ast:" +
			// ast + " " + getFileContents().getFilename());
			// findConsole().newMessageStream().println("finish tree called from:"
			// + this + " ast:" + ast + " " + getFileContents().getFilename());

			// if (ast.getType() == TokenTypes.LITERAL_NEW) {
			// System.out.println ("found new");
			// }
			if (isPackageInfo) {
				isPackageInfo = false;
				return;
			}
			doFinishTree(ast);
			// System.out.println ("Check ended from:" + this + " ast:" + ast +
			// " " + getFileContents().getFilename());

		} catch (RuntimeException e) {
			System.err.println("Description:" + checkAndFileDescription);

			e.printStackTrace();
			errorOccurred = true;
			log(ast, ERROR_KEY, "Finish tree:" + checkAndFileDescription + " "
					+ e.getStackTrace());
			consoleOut(e.getMessage() + " Description:"
					+ checkAndFileDescription + "Stack:\n"
					+ toString(e.getStackTrace()));

			// throw e;

		}
	}

	public void leaveToken(DetailAST ast) {
		if (vetoChecks() 
				|| !visitedTree)
			return;
		try {
			doLeaveToken(ast);
		} catch (Exception e) {
			e.printStackTrace();
			errorOccurred = true;

			// findConsole().newMessageStream().println("Stack:" +
			// e.getStackTrace());
			log(ast, ERROR_KEY, "Visit token:" + checkAndFileDescription + " "
					+ e.getMessage());
			consoleOut(e.getMessage() + " Description:"
					+ checkAndFileDescription + "Stack:\n"
					+ toString(e.getStackTrace()));
		}
	}
	protected boolean errorCheck() {
		return !errorOccurred;
	}
	protected boolean consentCheck() {
		return (consentFormShown && !consentFormSigned);
	}
	protected boolean vetoChecks() {
		return errorCheck() && consentCheck();
//		return errorOccurred || 
//				(consentFormShown && !consentFormSigned);
	}
	// file set check gets all files
	protected boolean fileNameCheck(String aFileName) {
		return aFileName.endsWith(".java");
	}

	public void visitToken(DetailAST ast) {
		// if (errorOccurred)
		if (vetoChecks()
				|| !visitedTree)

			return;
		try {
			if (isPackageInfo)
				return;
			// System.out.println ("Check called from:" + this + " ast:" + ast +
			// " " + getFileContents().getFilename());
			// if (ast.getType() == TokenTypes.LITERAL_NEW) {
			// System.out.println ("found new");
			// }
			doVisitToken(ast);
			// System.out.println ("Check ended from:" + this + " ast:" + ast +
			// " " + getFileContents().getFilename());

		} catch (RuntimeException e) {
			e.printStackTrace();
			errorOccurred = true;

			// findConsole().newMessageStream().println("Stack:" +
			// e.getStackTrace());
			log(ast, ERROR_KEY, "Visit token:" + checkAndFileDescription + " "
					+ e.getMessage());
			consoleOut(e.getMessage() + " Description:"
					+ checkAndFileDescription + "Stack:\n"
					+ toString(e.getStackTrace()));

			// throw e;

		}
	}

	protected boolean isDeferLogging() {
//		System.out.println ("temp defer logging");
//		return false;
		return deferLogging;
	}

	protected void deferLogging() {
		if (notInPlugIn())
			return;
		deferLogging = true;
	}

	protected void flushLogAndResumeLogging() {
		deferLogging = false;
		for (LogObject aLogObject : log) {
			extendibleLog(aLogObject.getLine(), aLogObject.getColumn(),
					aLogObject.getKey(), aLogObject.getArgs());
		}
		log.clear();
	}

	protected void clearLogAndResumeLogging() {
		deferLogging = false;
		log.clear();

	}

	public final void extendibleLog(int lineNo, int colNo, String key,
			Object... args) {
		// System.out.println("key:" + key);
		if (colNo < 0) {
			extendibleLog(lineNo, key, args);
			return;
		}
		if (isDeferLogging()) {
			log.add(new ALogObject(lineNo, colNo, key, args));
		} else {

			log(lineNo, colNo, key, args);
			CheckStyleLogManagerFactory.getOrCreateCheckStyleLogManager()
					.newLog(sequenceNumber, isAutoBuild, filesInLastPhase,
							currentFile, lineNo, colNo, key, args);
		}
	}
	//make this per check?
		public void setCheckOnBuild(boolean newValue) {
			checkOnBuild = newValue;
		}
		
		public  boolean isCheckOnBuild() {
			return checkOnBuild;
		}
	protected abstract String msgKey();
	
	protected  String msgKeyWarning() {
		return msgKey();
	}
	protected  String msgKeyInfo() {
		return msgKey();
	}
	// public static boolean isPublicAndInstance(DetailAST methodOrVariableDef)
	// {
	// return isPublic(methodOrVariableDef)
	// && ! isStatic(methodOrVariableDef);
	// }
	// public static boolean isPublic(DetailAST methodOrVariableDef) {
	// return methodOrVariableDef.branchContains(TokenTypes.LITERAL_PUBLIC);
	//
	// }
	// public static boolean isStatic(DetailAST methodOrVariableDef) {
	// return methodOrVariableDef.branchContains(TokenTypes.LITERAL_STATIC);
	//
	// }
	// public static boolean isFinal(DetailAST methodOrVariableDef) {
	// return methodOrVariableDef.branchContains(TokenTypes.FINAL);
	// }
	// public static boolean isStaticAndNotFinal(DetailAST methodOrVariableDef)
	// {
	// return isStatic (methodOrVariableDef)
	// && ! isFinal(methodOrVariableDef);
	// }
   protected boolean isInfo() {
	   return INFO.equals(getSeverity());
   }
}
