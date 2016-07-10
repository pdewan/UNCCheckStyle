package unc.tools.checkstyle;

public class CheckStyleLogManagerFactory {
	static CheckStyleLogManager manager;
	public static CheckStyleLogManager getOrCreateCheckStyleLogManager() {
		if (manager == null) {
			manager = new ACheckStyleLogFileManager();
		}
		return manager;
	}
	
	public static void setCheckStyleLogManager(CheckStyleLogManager newVal) {
		manager = newVal;
	}

}
