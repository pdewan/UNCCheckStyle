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


public class AFileBasedCheckStyleLogFileManager extends ACheckStyleLogFileManager {
	protected String keyName(String aFileName, Object... arg) {
		return aFileName;
	}
	protected void batchMergeWithLastPhase(Set<String> aFilesInLastPhase) {
//		System.out.println("Merging with last phase");
		for (String aFileName:aFilesInLastPhase) { // only the files we received errors from
//		for (String aFileName:fileNameToMessages.keySet()) { 

			Set<String> anOriginalMessages = fileNameToMessages.get(aFileName);
			Set<String> aNewMessages = fileNameToLastPhaseMessages.get(aFileName);
			mergeWithLastPhase(aFileName, anOriginalMessages, aNewMessages);
		}
	}
}
