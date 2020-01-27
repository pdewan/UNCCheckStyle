package unc.tools.checkstyle;

import com.puppycrawl.tools.checkstyle.Main;

public class CheckstyleMainCaller {
	static final String SOURCE = "C:\\Users\\dewan\\Downloads\\twitter-heron";
//	static final String SOURCE  = "C:\\Users\\dewan\\Downloads\\twitter-heron\\contrib\\bolts\\kafka\\src\\java\\org\\apache\\heron\\bolts\\kafka\\KafkaBolt.java";
	static final String CHECKSTYLE_CONFIGURATION = "unc_checks.xml";
	static final String[] ARGS = {"-c", CHECKSTYLE_CONFIGURATION, SOURCE}; 
	public static void main (String[] args) {
			Main.main(ARGS);
	}

}
