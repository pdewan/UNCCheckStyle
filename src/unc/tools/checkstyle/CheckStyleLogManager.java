package unc.tools.checkstyle;

public interface CheckStyleLogManager {
	public  void newLog (String aFileName, int lineNo, int colNo, String key,
            Object... arg) ;
}
