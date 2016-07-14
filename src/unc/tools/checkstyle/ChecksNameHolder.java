package unc.tools.checkstyle;

import java.util.HashMap;
import java.util.Map;

import unc.cs.checks.STBuilderCheck;
import unc.tools.checkstyle.ProjectDirectoryHolder;


public class ChecksNameHolder {
	static Map<String, String> projectToChecksName = new HashMap<>();
	
	public static String getChecksName() {
		String aProjectDirectory = ProjectDirectoryHolder.getCurrentProjectDirectory();
		String aChecksName = projectToChecksName.get(aProjectDirectory);
		if (aChecksName == null) {
			aChecksName = STBuilderCheck.getChecksName();
			projectToChecksName.put(aProjectDirectory, aChecksName);
		}
		return aChecksName;
	}

	
}
