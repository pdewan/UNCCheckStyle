package unc.tools.checkstyle;

import java.util.HashMap;
import java.util.Map;

import unc.cs.checks.STBuilderCheck;
import unc.tools.checkstyle.ProjectDirectoryHolder;


public class ProjectSTBuilderHolder {
//	static Map<String, STBuilderCheck> projectToSTBuilder = new HashMap<>();
	//STBuolder is beingcreated on each run, so jjust return the latest one
	public static STBuilderCheck getSTBuilder() {
		return STBuilderCheck.getLatestInstance();
//		String aProjectDirectory = ProjectDirectoryHolder.getCurrentProjectDirectory();
//		STBuilderCheck anSTBuilder = projectToSTBuilder.get(aProjectDirectory);
//		if (anSTBuilder == null) {
//			anSTBuilder = STBuilderCheck.getLatestInstance();
//			projectToSTBuilder.put(aProjectDirectory, anSTBuilder);
//		}
//		return anSTBuilder;
	}
//	public static void refreshSTBuilder() {
//		String aProjectDirectory = ProjectDirectoryHolder.getCurrentProjectDirectory();
//		STBuilderCheck anSTBuilder = projectToSTBuilder.get(aProjectDirectory);
//	
//			anSTBuilder = STBuilderCheck.getLatestInstance();
//			projectToSTBuilder.put(aProjectDirectory, anSTBuilder);
//		
//	}

	
}
