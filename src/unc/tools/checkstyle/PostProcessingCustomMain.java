package unc.tools.checkstyle;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.NonExitingMain;
import com.puppycrawl.tools.checkstyle.Main;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;

import unc.cs.checks.STBuilderCheck;
import unc.cs.checks.TagBasedCheck;
import unc.cs.symbolTable.CallInfo;
import unc.cs.symbolTable.STMethod;
import unc.cs.symbolTable.STNameable;
import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.SymbolTable;
import unc.cs.symbolTable.SymbolTableFactory;

public class PostProcessingCustomMain extends PostProcessingMain {

//static final String SOURCE = "C:\\Users\\dewan\\Downloads\\twitter-heron";
//static final String SOURCE = "D:\\dewan_backup\\Java\\NIOTrickOrTreat\\src\\mapreduce";
//static final String SOURCE = "D:\\dewan_backup\\Java\\NIOTrickOrTreat\\src\\mapreduce\\ATokenCountingModel.java";

static final String SOURCE = "D:\\dewan_backup\\Java\\UNCCheckStyle\\src\\test";

//static final String SOURCE  = "C:\\Users\\dewan\\Downloads\\twitter-heron\\contrib\\bolts\\kafka\\src\\java\\org\\apache\\heron\\bolts\\kafka\\KafkaBolt.java";
static final String CHECKSTYLE_CONFIGURATION = "unc_checks.xml";
static final String[] ARGS = {"-c", CHECKSTYLE_CONFIGURATION, SOURCE};
	public static void main (String[] args) {
		setPrintOnlyTaggedClasses(true);
		PostProcessingMain.main(ARGS);
//		    Main.main(ARGS);

//			try {
//				NonExitingMain.main(ARGS);
//				initGlobals();
//				processTypes(sTTypes);
//			} catch (UnsupportedEncodingException | FileNotFoundException | CheckstyleException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		
			
	}

}
