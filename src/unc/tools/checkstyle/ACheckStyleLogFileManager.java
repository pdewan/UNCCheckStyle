package unc.tools.checkstyle;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class ACheckStyleLogFileManager implements CheckStyleLogManager {
	public static final String SUBMISSION_FOLDER_NAME = "Submission attachment(s)";
	protected  PrintWriter out = null;
	protected  BufferedWriter bufWriter;
	protected String logFileName;
	protected Map<String, Set<String>> fileNameToMessages = new HashMap();
	int  lastSequenceNumber = -1;
	int filesWithLastSequenceNumber;
	protected boolean wasLastPhaseAutoBuild = true;
	protected Map<String, Set<String>> fileNameToLastPhaseMessages = new HashMap(); // for garbage collection
	protected String projectDirectry = null;
	protected int lastReadSequenceNumber = -1;// we will add 1 + current sequence number to it
	
	
	protected boolean isNewMessage (String aFileName, String aMessage) {
		Set<String> aMessages = fileNameToMessages.get(aFileName);
//		if (aMessages == null) {
//			aMessages = new HashSet();
//			fileNameToMessages.put(aFileName, aMessages);
//		}
		return aMessages == null || !aMessages.contains(aMessage);
		
	}
	protected boolean wasPreviousPhaseCheckAll(boolean isAutoBuild,int aFilesInLastPhase) {
		int aNumTotalFiles = fileNameToMessages.size();
//		int aNumLastPhaseFiles = fileNameToLastPhaseMessages.size();
		return aNumTotalFiles == aFilesInLastPhase || 
				aFilesInLastPhase > 1; // check all will have more than one file
		
	}
	protected void logDeletedMessages (String aFileName, Set<String> aDeletedMessages) {
		for (String aDeletedMessage:aDeletedMessages) {
			appendLine(toString(false, lastSequenceNumber + lastReadSequenceNumber + 1, aFileName, aDeletedMessage));
//			System.out.println("Deleting:" + aDeletedMessage + " with key" + aFileName );
		}
		
	}

	protected void mergeWithLastPhase(String aFileName,Set<String> anOriginalMessages, Set<String> aNewMessages) {
		if (anOriginalMessages == null)
			return;
//		if (aFileName.contains("Illegal")) {
//			System.out.println (" origina:" + anOriginalMessages);
//			System.out.println (" new:" + aNewMessages);
//		}
		Set<String> aDeletedMessages = new HashSet(anOriginalMessages);	
		if (aNewMessages != null) {
			aDeletedMessages.removeAll(aNewMessages);
			anOriginalMessages.retainAll(aNewMessages);
		} else {
			anOriginalMessages.clear(); // we are deleting all messages
		}
		logDeletedMessages(aFileName, aDeletedMessages);
		
	}

	protected void mergeWithLastPhase() {
		for (String aFileName:fileNameToMessages.keySet()) {
			Set<String> anOriginalMessages = fileNameToMessages.get(aFileName);
			Set<String> aNewMessages = fileNameToLastPhaseMessages.get(aFileName);
			mergeWithLastPhase(aFileName,anOriginalMessages, aNewMessages);
		}
	}
	protected void batchMergeWithLastPhase(Set<String> aFilesInLastPhase) {
//		System.out.println("Merging with last phase");
		if (aFilesInLastPhase.size() < fileNameToMessages.size()) {
//			for (String aKeyName:fileNameToMessages.keySet()) {
//				boolean foundKey = false;
//				for (String aFileName:aFilesInLastPhase) {
//					if (aFileName.contains(aKeyName)) {
//						foundKey = true;
//						break;
//					}
//				}
//				if (!foundKey) {
//					System.err.println ("Not found key:" + aKeyName);
//					System.out.println("Messages with key:" + fileNameToMessages.get(aKeyName));
//				}
//			}
			Set<String> missingFiles = new HashSet(fileNameToMessages.keySet());
			missingFiles.removeAll(aFilesInLastPhase);			
			System.out.println("files in last phase: " + aFilesInLastPhase.size() + " < files with messages " +  fileNameToMessages.size() + ", not doing merge");
			System.out.println ("Missing files:" + missingFiles);
//			return;
		}
//		int aNumFilesInLastPhase = aFilesInLastPhase.size();
		// total files visited should be at least as many as total number of erroneous files
		// sometimes checkstyle has two fewer files
//		if (aNumFilesInLastPhase < fileNameToLastPhaseMessages.size() - 2) {
//			System.err.println("Few files in last phase:" + aNumFilesInLastPhase);
//			return;
//		}
//		for (String aFileName:aFilesInLastPhase) { // only the files we received errors from
		for (String aFileName:fileNameToMessages.keySet()) { 
			
			Set<String> anOriginalMessages = fileNameToMessages.get(aFileName);
			Set<String> aNewMessages = fileNameToLastPhaseMessages.get(aFileName);
			mergeWithLastPhase(aFileName, anOriginalMessages, aNewMessages);
		}
	}
	
	protected void incrementalMergeWithLastPhase(Set<String> aFilesInLastPhase) {
		System.out.println("Merging with last phase");

		for (String aFileName:aFilesInLastPhase) { // only the files we received errors from
//		for (String aFileName:fileNameToMessages.keySet()) { 
			
			Set<String> anOriginalMessages = fileNameToMessages.get(aFileName);
			Set<String> aNewMessages = fileNameToLastPhaseMessages.get(aFileName);
			mergeWithLastPhase(aFileName, anOriginalMessages, aNewMessages);
		}
	}
	protected void maybeProcessLastPhase(int aSequenceNumber, boolean anIsAutoBuild, Set<String> aFilesInLastPhase) {
		if (lastSequenceNumber == aSequenceNumber)
			return;
		
//		System.out.println (" new check phase with files in last phase:" + aFilesInLastPhase);
		lastSequenceNumber = aSequenceNumber;

//		if (wasPreviousPhaseCheckAll(anIsAutoBuild, aFilesInLastPhase)) {
//			System.out.println ("Previous command was  check all command:");
//			mergeWithLastPhase();
//		}
		if (!wasLastPhaseAutoBuild) {
//			int numFilesInLastPhase = aFilesInLastPhase.size();
//			int numTotalFiles = fileNameToMessages.size();
//			if (numFilesInLastPhase == 0 || numTotalFiles == 0)
//				return;
//			double aFractionInNewPhase
//			if (numFiles)

		batchMergeWithLastPhase(aFilesInLastPhase);
		}
		wasLastPhaseAutoBuild = anIsAutoBuild;
//		System.out.println("lasy phase was built auto?" + wasLastPhaseAutoBuild);
		fileNameToLastPhaseMessages.clear();
		
		
	}
	@Override
	public void newSequenceNumber(int aSequenceNumber, boolean isAutoBuild,
			Set<String> aFilesInLastPhase) {
//		System.out.println("New sequence number:" + aSequenceNumber + "isAuroBuild:" + isAutoBuild + " num files" + aFilesInLastPhase.size());
		maybeProcessLastPhase(aSequenceNumber, isAutoBuild, aFilesInLastPhase);

	
		
	}
	protected void processNewMessage (int aSequenceNumber, boolean isAutoBuild, Set<String> aFilesInLastPhase, String aFileName, String aKeyName, String aMessage) {
//		maybeProcessLastPhase(aSequenceNumber, isAutoBuild, aFilesInLastPhase);
		Set<String> aLastMessages = fileNameToLastPhaseMessages.get(aKeyName);
		if (aLastMessages == null) {
			aLastMessages = new HashSet();
			fileNameToLastPhaseMessages.put(aKeyName, aLastMessages);
	    }
		aLastMessages.add(aMessage);
		if (!isNewMessage(aKeyName, aMessage)) {
			return;
		}
		appendLine(toString(true, lastSequenceNumber + lastReadSequenceNumber + 1, aFileName, aMessage));

		Set<String> aMessages = fileNameToMessages.get(aKeyName);
		if (aMessages == null) {
			aMessages = new HashSet();
			fileNameToMessages.put(aKeyName, aMessages);
	    }
//		System.out.println (aMessage + " not found for key:" + aKeyName + ":");
		aMessages.add(aMessage);
//		Set<String> aLastMessages = fileNameToLastPhaseMessages.get(aFileName);
//		if (aLastMessages == null) {
//			aLastMessages = new HashSet();
//			fileNameToLastPhaseMessages.put(aFileName, aLastMessages);
//	    }
//		aLastMessages.add(aMessage);
	}
//	protected String keyName(String aFileName, Object... arg) {
//		if (arg == null)
//			return aFileName;
//		Object classNameArg = null;
//		if (arg.length > 2) {
//			classNameArg = arg[2];
//		} else if (arg.length  > 1) {
//			classNameArg = arg[1]; // The line number got deleted
//		}
//		if (classNameArg == null)
//			return aFileName;
//		return classNameArg.toString();
////		try {
////		return arg != null 
////				&& arg.length > 2 
////				&& arg[2] != null
////				?arg[2].toString():
////					aFileName;
////		} catch (NullPointerException e) {
////			e.printStackTrace();
////			return aFileName;
////		}
//	}
//	protected String keyNameLiveMessage(String aFileName, Object... arg) {
//		if (arg == null)
//			return aFileName;
//		Object classNameArg = null;
//		try {
//		return arg != null 
//				&& arg.length > 2 
//				&& arg[2] != null
//				?arg[2].toString():
//					aFileName;
//		} catch (NullPointerException e) {
//			e.printStackTrace();
//			return aFileName;
//		}
//	}
	protected String keyName(boolean isLive, String aFileName, Object... arg) {
		String retVal = null;
		int anArgIndex = isLive?2:0;
		if (arg == null)
			return aFileName;
		Object classNameArg = null;
		try {
		retVal = arg != null 
				&& arg.length > anArgIndex 
				&& arg[anArgIndex] != null
				?arg[anArgIndex].toString():
					aFileName;
		} catch (NullPointerException e) {
			e.printStackTrace();
			retVal = aFileName;
		}
		if (retVal.isEmpty() || retVal.equals(" ")) {
			System.err.println(" Empty key:" +  retVal);
		}
		return retVal;
	}
//	protected String keyNameLoggedMessage(String aFileName, Object... arg) {
//		if (arg == null)
//			return aFileName;
//		Object classNameArg = null;
//		try {
//		return arg != null 
//				&& arg.length > 1 
//				&& arg[1] != null
//				?arg[1].toString():
//					aFileName;
//		} catch (NullPointerException e) {
//			e.printStackTrace();
//			return aFileName;
//		}
//	}
	
	public void newLog (int aSequenceNumber, boolean isAutoBuild, Set<String> aFilesInLastPhase, String aFileName, int lineNo, int colNo, String key,
            Object... arg) {
		
		String aMessage = toMessage(aFileName, key, arg);

//		String aKeyName = arg.length > 2?arg[2].toString():aFileName;
//		processNewMessage(aSequenceNumber, isAutoBuild, aFilesInLastPhase, aFileName, aMessage);
		processNewMessage(aSequenceNumber, isAutoBuild, aFilesInLastPhase, 
				aFileName,
				keyName(true, aFileName, arg),
				aMessage);

		
	}
	public void readLog (boolean isAddition, int aSequenceNumber,  String aFileName, String key,
            Object... arg) {
		String aMessage = toMessage(aFileName, key, arg);
//		Set<String> anExistingMessages = fileNameToMessages.get(aFileName);
//		Set<String> anExistingMessages = fileNameToMessages.get(keyName(false, aFileName, arg));
		Set<String> anExistingMessages = fileNameToMessages.get(aFileName);


		if (anExistingMessages == null) {
			anExistingMessages = new HashSet();
			fileNameToMessages.put(aFileName, anExistingMessages);
		}
//		System.out.println ("reading message" + aMessage + " for key:" + aFileName);
		if (isAddition) {
			anExistingMessages.add(aMessage);
		} else {
			boolean retVal = anExistingMessages.remove(aMessage);
			if (!retVal) {
				System.err.println("Log inconsistency, not in log: " + aMessage);
			}
		}
		lastReadSequenceNumber = aSequenceNumber;
		
	}
	
	static StringBuilder stringBuilder = new StringBuilder();
	public static String toString (boolean isAddition, int aSequenceNumber, String aFileName, String aMessage) {
		Date aDate = new Date(System.currentTimeMillis());
		stringBuilder.setLength(0);
		stringBuilder.append("" + aSequenceNumber);
		stringBuilder.append("," + aDate.toString());
		stringBuilder.append("," + isAddition);
		stringBuilder.append ("," + aFileName);
//		messageBuilder.append ("," + key);
		stringBuilder.append("," + aMessage);
//		stringBuilder.append ("," + aFileName);
//		stringBuilder.append ("," + key);
//		for (Object anArg:arg){
//			stringBuilder.append ("," + arg);
//		}
//		if (!isAddition) {
//			System.out.println ("Logging deleted message:" + stringBuilder.toString());
//		} else {
//			System.out.println ("Logging inserted nessage:" + stringBuilder.toString());
//		}
		return stringBuilder.toString();
		
	}
	
//	public static String toString (boolean isAddition, int aSequenceNumber, String aFileName,  String key,
//            Object... arg) {
//		Date aDate = new Date(System.currentTimeMillis());
//		stringBuilder.setLength(0);
//		stringBuilder.append("" + aSequenceNumber);
//		stringBuilder.append(aDate.toString());
//		stringBuilder.append("," + toMessage(aFileName, key, arg));
////		stringBuilder.append ("," + aFileName);
////		stringBuilder.append ("," + key);
////		for (Object anArg:arg){
////			stringBuilder.append ("," + arg);
////		}
//		return stringBuilder.toString();
//		
//	}
	static StringBuilder messageBuilder = new StringBuilder();

	public static String toMessage (String aFileName,  String key,
            Object... anArgs) {
		messageBuilder.setLength(0);
//		messageBuilder.append (aFileName);
//		messageBuilder.append ("," + key);
		messageBuilder.append (key);

		for (Object anArg:anArgs){
			if (anArg == null)
				continue;
			String anArgString = anArg.toString();
			if (
					anArgString.contains(key) || 
					anArgString.startsWith("(") 
//					||
//					aFileName.contains(anArgString)
					)
					
				continue;
				
			messageBuilder.append ("," + anArg);
		}
		return messageBuilder.toString();
		
	}
//	public static String toMessage (String aFileName,  String key,
//            Object... anArgs) {
//		messageBuilder.setLength(0);
//		messageBuilder.append (aFileName);
//		messageBuilder.append ("," + key);
//		for (Object anArg:anArgs){
//			if (anArg == null)
//				continue;
//			String anArgString = anArg.toString();
//			if (
//					anArgString.contains(key) || 
//					anArgString.startsWith("(") ||
//					aFileName.contains(anArgString)
//					)
//					
//				continue;
//				
//			messageBuilder.append ("," + anArg);
//		}
//		return messageBuilder.toString();
//		
//	}
	public static int SEQUENCE_NUMBER_INDEX = 0;
	public static int DATE_INDEX = 1;
	public static int IS_ADDITION_INDEX = 2;
	public static int FILE_NAME_INDEX = 3;
	public static int KEY_INDEX = 4;
	public static int ARGS_INDEX = 5;


	protected  void readLogFile() {
		 List<String> aLines = toTextLines(logFileName);
		 for (String aLine:aLines) {
			 String[] aParts = aLine.split(",");
			 if (aParts.length < ARGS_INDEX) {
				 continue; // perhaps a blank line
			 }
			 int aSequenceNumber = Integer.parseInt(aParts[SEQUENCE_NUMBER_INDEX]);
			 Date aDate = new Date( aParts[DATE_INDEX]);
			 Boolean anIsAddition = Boolean.parseBoolean(aParts[IS_ADDITION_INDEX]);
			 String aFileName = aParts[FILE_NAME_INDEX];
			 String aKey = aParts[KEY_INDEX];
			 String[] anArgs = new String[aParts.length - ARGS_INDEX];
			 for (int i = ARGS_INDEX; i < aParts.length; i++) {
				 anArgs[i - ARGS_INDEX] = aParts[i];
			 }
			 readLog(anIsAddition, aSequenceNumber, keyName(false, aFileName, anArgs), aKey, anArgs);
			 
		 }
	 }
	void appendLine(String aLine) {
//		System.out.println("logging:" + aLine);
		maybeCreateOrLoadAppendableFile(logFileName);
		out.println(aLine);
		out.flush();
	}
	
	
	 void maybeCreateOrLoadAppendableFile(String aFileName) {
		 if (out != null && bufWriter != null) {
			 return;
		 }
	        String aFullFileName = aFileName;
	        File aFile = new File(aFullFileName);
	        boolean aNewFile = !aFile.exists();
	        if (aNewFile) {
	        	aFile.getParentFile().mkdirs();
	        }
	        try {
	            bufWriter
	                    = Files.newBufferedWriter(
	                            Paths.get(aFullFileName),
	                            Charset.forName("UTF8"),
	                            StandardOpenOption.WRITE,
	                            StandardOpenOption.APPEND,
	                            StandardOpenOption.CREATE);
	            out = new PrintWriter(bufWriter, true);
	            
	        } catch (IOException e) {
	            e.printStackTrace();
	            //Oh, no! Failed to create PrintWriter
	        }


	    }
	 
	 
	 
	 public static List<String> toTextLines(String aFileName) {
		 List<String> result = new ArrayList();
		 File aFile = new File(aFileName);
		 if (!aFile.exists())
			 return result;
		 try (BufferedReader br = new BufferedReader(new FileReader(aFileName))) {
			    String line;
			    while ((line = br.readLine()) != null) {
			       result.add(line);
			    }
		} catch (IOException e) {
			e.printStackTrace();
			return result;
		}
		 return result;
	 
	 }
	@Override
	public void checkStyleStarted() {
		projectDirectry = null;
	}
	protected static String logDirectory(String aProjectDirectory) {
		int aSubmisssionAttachmentIndex = aProjectDirectory.indexOf(SUBMISSION_FOLDER_NAME);
		if (aSubmisssionAttachmentIndex < 0) {
			return aProjectDirectory;
		}
		return aProjectDirectory.substring(0, aSubmisssionAttachmentIndex + SUBMISSION_FOLDER_NAME.length() + 1); // get the last slash also
	}
	@Override
	public void maybeNewProjectDirectory(String aProjectDirectory, String aChecksName) {
		if (aProjectDirectory.equals(projectDirectry))
			return;
		reset();
		projectDirectry = aProjectDirectory;
//		if (logFileName == null)
		logFileName = logDirectory(aProjectDirectory) + "/" + AConsentFormVetoer.LOG_DIRECTORY + "/" + aChecksName + ".csv";
//		out = null;
//		bufWriter = null;
//		mergeWithLastPhase();
//		fileNameToLastPhaseMessages.clear();
//		fileNameToMessages.clear();
//		reset();
		readLogFile();
	}
	
	protected void reset() {
		if (out != null)
		out.close();
		if (bufWriter != null) {
		try {
			bufWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		out = null;
		bufWriter = null;
//		mergeWithLastPhase();
		fileNameToLastPhaseMessages.clear();
		fileNameToMessages.clear();
		lastSequenceNumber = -1;
		filesWithLastSequenceNumber = 0;
		wasLastPhaseAutoBuild = true;
		lastReadSequenceNumber = -1;// we will add 1 + current sequence number to it
		
		
	}
	
	
	 
	 public static void main (String[] args) {
		 String aLogDirectory = logDirectory("D:/dewan_backup/Java/Grader/Test Data/Comp401F16/Assignment1/All, Correct (acorrect)/Submission attachment(s)/Assignment1/Assignment1/");
		 aLogDirectory = logDirectory("D:/dewan_backup/Java/Grader/Test Data/Comp401F16/Assignment1/");
	 
	 }
}
