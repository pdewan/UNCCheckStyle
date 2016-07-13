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
	protected void logDeletedMessages (Set<String> aDeletedMessages) {
		for (String aDeletedMessage:aDeletedMessages) {
			appendLine(toString(false, lastSequenceNumber + lastReadSequenceNumber + 1, aDeletedMessage));
		}
		
	}

	protected void mergeWithLastPhase(Set<String> anOriginalMessages, Set<String> aNewMessages) {
		if (anOriginalMessages == null)
			return;
		Set<String> aDeletedMessages = new HashSet(anOriginalMessages);	
		if (aNewMessages != null) {
			aDeletedMessages.removeAll(aNewMessages);
			anOriginalMessages.retainAll(aNewMessages);
		} else {
			anOriginalMessages.clear(); // we are deleting all messages
		}
		logDeletedMessages(aDeletedMessages);
		
	}

	protected void mergeWithLastPhase() {
		for (String aFileName:fileNameToMessages.keySet()) {
			Set<String> anOriginalMessages = fileNameToMessages.get(aFileName);
			Set<String> aNewMessages = fileNameToLastPhaseMessages.get(aFileName);
			mergeWithLastPhase(anOriginalMessages, aNewMessages);
		}
	}
	protected void incermentalMergeWithLastPhase(Set<String> aFilesInLastPhase) {
		System.out.println("Merging with last phase");
		for (String aFileName:aFilesInLastPhase) { // only the files we received errors from
			Set<String> anOriginalMessages = fileNameToMessages.get(aFileName);
			Set<String> aNewMessages = fileNameToLastPhaseMessages.get(aFileName);
			mergeWithLastPhase(anOriginalMessages, aNewMessages);
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
		incermentalMergeWithLastPhase(aFilesInLastPhase);
		}
		wasLastPhaseAutoBuild = anIsAutoBuild;
		fileNameToLastPhaseMessages.clear();
		
		
	}
	@Override
	public void newSequenceNumber(int aSequenceNumber, boolean isAutoBuild,
			Set<String> aFilesInLastPhase) {
		System.out.println("New sequence number:" + aSequenceNumber + "isAuroBuild:" + isAutoBuild + " num files" + aFilesInLastPhase.size());
		maybeProcessLastPhase(aSequenceNumber, isAutoBuild, aFilesInLastPhase);

	
		
	}
	protected void processNewMessage (int aSequenceNumber, boolean isAutoBuild, Set<String> aFilesInLastPhase, String aFileName, String aMessage) {
//		maybeProcessLastPhase(aSequenceNumber, isAutoBuild, aFilesInLastPhase);
		Set<String> aLastMessages = fileNameToLastPhaseMessages.get(aFileName);
		if (aLastMessages == null) {
			aLastMessages = new HashSet();
			fileNameToLastPhaseMessages.put(aFileName, aLastMessages);
	    }
		aLastMessages.add(aMessage);
		if (!isNewMessage(aFileName, aMessage)) {
			return;
		}
		appendLine(toString(true, lastSequenceNumber + lastReadSequenceNumber + 1, aMessage));

		Set<String> aMessages = fileNameToMessages.get(aFileName);
		if (aMessages == null) {
			aMessages = new HashSet();
			fileNameToMessages.put(aFileName, aMessages);
	    }
		
		aMessages.add(aMessage);
//		Set<String> aLastMessages = fileNameToLastPhaseMessages.get(aFileName);
//		if (aLastMessages == null) {
//			aLastMessages = new HashSet();
//			fileNameToLastPhaseMessages.put(aFileName, aLastMessages);
//	    }
//		aLastMessages.add(aMessage);
	}
	public void newLog (int aSequenceNumber, boolean isAutoBuild, Set<String> aFilesInLastPhase, String aFileName, int lineNo, int colNo, String key,
            Object... arg) {
		
		String aMessage = toMessage(aFileName, key, arg);
//		if (!isNewMessage(aFileName, aMessage)) {
//			return;
//		}
//		appendLine(toString(true, aSequenceNumber, aMessage));
		processNewMessage(aSequenceNumber, isAutoBuild, aFilesInLastPhase, aFileName, aMessage);
		
//        if (aSequenceNumber == lastSequenceNumber) {
//			
//		}
		
	}
	public void readLog (boolean isAddition, int aSequenceNumber,  String aFileName, String key,
            Object... arg) {
		String aMessage = toMessage(aFileName, key, arg);
		Set<String> anExistingMessages = fileNameToMessages.get(aFileName);
		if (anExistingMessages == null) {
			anExistingMessages = new HashSet();
			fileNameToMessages.put(aFileName, anExistingMessages);
		}
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
	public static String toString (boolean isAddition, int aSequenceNumber, String aMessage) {
		Date aDate = new Date(System.currentTimeMillis());
		stringBuilder.setLength(0);
		stringBuilder.append("" + aSequenceNumber);
		stringBuilder.append("," + aDate.toString());
		stringBuilder.append("," + isAddition);
		stringBuilder.append("," + aMessage);
//		stringBuilder.append ("," + aFileName);
//		stringBuilder.append ("," + key);
//		for (Object anArg:arg){
//			stringBuilder.append ("," + arg);
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
		messageBuilder.append (aFileName);
		messageBuilder.append ("," + key);
		for (Object anArg:anArgs){
			if (anArg == null)
				continue;
			String anArgString = anArg.toString();
			if (
					anArgString.contains(key) || 
					anArgString.startsWith("(") ||
					aFileName.contains(anArgString)
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
			 readLog(anIsAddition, aSequenceNumber, aFileName, aKey, anArgs);
			 
		 }
	 }
	void appendLine(String aLine) {
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
	@Override
	public void maybeNewProjectDirectory(String aProjectDirectory, String aChecksName) {
		if (aProjectDirectory.equals(projectDirectry))
			return;
		reset();
		projectDirectry = aProjectDirectory;
//		if (logFileName == null)
		logFileName = aProjectDirectory + "/" + AConsentFormVetoer.LOG_DIRECTORY + "/" + aChecksName + ".csv";
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
	
	
	 
//	 public static void main (String[] args) {
//		 String[] anArray = {"hello", "goodbye"};
//		 System.out.println(Arrays.toString(anArray));
//	 }
}
