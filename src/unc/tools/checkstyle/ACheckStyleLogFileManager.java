package unc.tools.checkstyle;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ACheckStyleLogFileManager implements CheckStyleLogManager {
	protected String logFileName;
	protected Map<String, Set<String>> fileNameToMessages = new HashMap();
	
	public  void newLog (String aFileName, int lineNo, int colNo, String key,
            Object... arg) {
		
	}

}
