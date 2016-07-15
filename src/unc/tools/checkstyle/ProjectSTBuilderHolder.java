package unc.tools.checkstyle;

import java.util.HashMap;
import java.util.Map;

import unc.cs.checks.STBuilderCheck;
import unc.tools.checkstyle.ProjectDirectoryHolder;


public class ProjectSTBuilderHolder {
	static Map<String, STBuilderCheck> projectToChecksName = new HashMap<>();
	
	public static STBuilderCheck getSTBuilder() {
		String aProjectDirectory = ProjectDirectoryHolder.getCurrentProjectDirectory();
		STBuilderCheck aChecksName = projectToChecksName.get(aProjectDirectory);
		if (aChecksName == null) {
			aChecksName = STBuilderCheck.getLatestInstance();
			projectToChecksName.put(aProjectDirectory, aChecksName);
		}
		return aChecksName;
	}

	
}
