package unc.tools.checkstyle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

public class GenerateDictionary {
	public static final String UNIX_DICTIONARY_NAME = "american-english-extended.txt";
//	public static final String GENERATED_ARRAY_FILE_NAME = "UnixDictionaryArray.txt";
	public static final String GENERATED_CLASS_PACKAGE_NAME = "unc.cs.tools.checkstyle.dictionary";

	public static final String GENERATED_CLASS_NAME_PREFIX = "Dictionary";
	public static final int NUM_FILES = 'z' - 'a' + 1;


	public static final String GENERATED_CLASS_A2J_NAME = "UnixDictionaryA2J";
	public static final String GENERATED_CLASS_K2Z_NAME = "UnixDictionaryK2Z";
	public static final String GENERATED_CLASS_S_OVERFLOW_NAME = "DictionarySPlus";
	public static final String GENERATED_CLASS_S_OVERFLOW_FILE_NAME = "src/" + GENERATED_CLASS_PACKAGE_NAME.replace(".", "/") + "/" + GENERATED_CLASS_S_OVERFLOW_NAME  + ".java";


	public static final String GENERATED_CLASS_FILE_NAME_PREFIX = "src/" + GENERATED_CLASS_PACKAGE_NAME.replace(".", "/") + "/";


	public static final String GENERATED_CLASS_A2J_FILE_NAME = "src/" + GENERATED_CLASS_PACKAGE_NAME.replace(".", "/") + "/" + GENERATED_CLASS_A2J_NAME + ".java";
	public static final String GENERATED_CLASS_K2Z_FILE_NAME = "src/" + GENERATED_CLASS_PACKAGE_NAME.replace(".", "/") + "/" + GENERATED_CLASS_K2Z_NAME + ".java";

	private static final String  GENERATED_CLASS_PREFIX =
			"package " + GENERATED_CLASS_PACKAGE_NAME + " ;" + "\n" +
			"public class " + GENERATED_CLASS_A2J_NAME + " {" + "\n" +
			"public static String[] dictionary =" + "\n" +
			 "\t" + "{";
	private static final String  GENERATED_CLASS_PREFIX_1 =
			"package " + GENERATED_CLASS_PACKAGE_NAME + " ;" + "\n" +
			"public class " ;
	private static final String  GENERATED_CLASS_PREFIX_2 =
			 " {" + "\n" +
			"public static String[] dictionary =" + "\n" +
			 "\t" + "{";
	private static final String GENERATED_CLASS_SUFFIX =
			"\t};\n" +
			"}";
//	generatedPrintStream.println("}");
			
//	private static final String CLASS_HEADER_LINE = "public class " + GENERATED_CLASS_A2J_NAME + " {";


	public static boolean isIdentifier(String name) {
	   

	    for (int i = 0; i < name.length(); i++) {
	    	char c = name.charAt(i);
	        if(!Character.isLetter(c) && 
	        	!Character.isDigit(c) &&
	        	! (c == '_')
	        	
	        		) {
	            return false;
	        }
	    }

	    return true;
	}
	public static void main(String[] args) {
		try {
		String[] classNames = new String[NUM_FILES];
		String[] files = new String[NUM_FILES];
		PrintStream[] printStreams = new PrintStream[NUM_FILES];
		PrintStream sOverflowStream = new PrintStream(GENERATED_CLASS_S_OVERFLOW_FILE_NAME);
		sOverflowStream.println(GENERATED_CLASS_PREFIX_1 + GENERATED_CLASS_S_OVERFLOW_NAME + GENERATED_CLASS_PREFIX_2 );
		
//			StringBuilder contentBuilder = new StringBuilder();
			
			for (int i= 0; i < NUM_FILES; i++) {
				char c = (char ) ('A' + i);
				classNames[i] = GENERATED_CLASS_NAME_PREFIX + c ;
				files[i] = GENERATED_CLASS_FILE_NAME_PREFIX + classNames[i] + ".java";
				printStreams[i] = new PrintStream(new File(files[i] ));
				String aGeneratedClassPrefix = GENERATED_CLASS_PREFIX_1 + classNames[i] + GENERATED_CLASS_PREFIX_2 ;
				printStreams[i].println(aGeneratedClassPrefix);
			}
			
//			PrintStream generatedPrintStreamAToJ = new PrintStream(new File(GENERATED_CLASS_A2J_FILE_NAME));
//			PrintStream generatedPrintStreamKToZ = new PrintStream(new File(GENERATED_CLASS_K2Z_FILE_NAME));
//			generatedPrintStreamAToJ.println(GENERATED_CLASS_PREFIX);
//			generatedPrintStreamKToZ.println(GENERATED_CLASS_PREFIX);
//			generatedPrintStreamAToJ.println(PACKAGE_LINE);
//			generatedPrintStreamAToJ.println(CLASS_HEADER_LINE);
//			generatedPrintStreamAToJ.println("public static String[] dictionary =");
//			generatedPrintStreamAToJ.println("\t" + "{");

			

//			package unc.tools.checkstyle;

//			public class UnixDictionary {
//			 public static String[] dictionary =
//			generatedPrintStream.println("{");
			try (BufferedReader br = new BufferedReader(new FileReader(UNIX_DICTIONARY_NAME))) {

				String sCurrentLine;
				while ((sCurrentLine = br.readLine()) != null) {
					if (!isIdentifier(sCurrentLine)) {
						continue;
					}
					String aLowerCaseIdentifier = sCurrentLine.toLowerCase();
					String aNextLine = "\t \"" + sCurrentLine + "\",";
					int aStartChar = aLowerCaseIdentifier.charAt(0);
					int anIndex = aStartChar- 'a';
					if (aStartChar == 's' && aLowerCaseIdentifier.length() > 1 && aLowerCaseIdentifier.charAt(1) > 'j' ) {
						sOverflowStream.println(aNextLine);
					} else {
						printStreams[anIndex].println(aNextLine);
					}
//		
//					PrintStream aPrintStream = aLowerCaseIdentifier.charAt(0) < 'k'?generatedPrintStreamAToJ:generatedPrintStreamKToZ;
//					aPrintStream.println(aNextLine);
					
				}
			}
			for (int i= 0; i < NUM_FILES; i++) {
				
				printStreams[i].println(GENERATED_CLASS_SUFFIX);
				printStreams[i].close();
				
			}
			sOverflowStream.println(GENERATED_CLASS_SUFFIX);
			sOverflowStream.close();
//			generatedPrintStreamAToJ.println(GENERATED_CLASS_SUFFIX);
//			generatedPrintStreamKToZ.println(GENERATED_CLASS_SUFFIX);
////			generatedPrintStream.println("\t};");
////			generatedPrintStream.println("}");
//
//			generatedPrintStreamAToJ.close();
//			generatedPrintStreamKToZ.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
