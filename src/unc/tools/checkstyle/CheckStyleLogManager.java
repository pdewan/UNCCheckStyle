package unc.tools.checkstyle;

public interface CheckStyleLogManager {
	public  void newLog (int aSequenceNumber, String aFileName, int lineNo, int colNo, String key,
            Object... arg) ;
	public void maybeNewProjectDirectory(String aProjectDirectory, String aChecksName);
	void checkStyleStarted();
}
