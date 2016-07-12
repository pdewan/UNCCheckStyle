package unc.tools.checkstyle;

import java.util.Set;

public interface CheckStyleLogManager {
	public  void newSequenceNumber (int aSequenceNumber, boolean isAutoBuild, Set<String> aFilesInLastPhase) ;
	public  void newLog (int aSequenceNumber, boolean isAutoBuild, Set<String> aFilesInLastPhase, String aFileName, int lineNo, int colNo, String key,
            Object... arg) ;
	public void maybeNewProjectDirectory(String aProjectDirectory, String aChecksName);
	void checkStyleStarted();
}
