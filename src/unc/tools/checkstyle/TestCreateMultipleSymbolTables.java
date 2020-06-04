package unc.tools.checkstyle;

public class TestCreateMultipleSymbolTables {
	static final String SOURCE1 = "src/test";
	static final String SOURCE2 = "src/test2";

	static final String CHECKSTYLE_CONFIGURATION = "unc_checks.xml";

	//static final String[] ARGS = {"-c", CHECKSTYLE_CONFIGURATION, "-f", "xml", SOURCE};
	static final String[] ARGS = {"-c", CHECKSTYLE_CONFIGURATION,  SOURCE1, SOURCE2};
	public static void main (String[] args) {
		
	}

}
