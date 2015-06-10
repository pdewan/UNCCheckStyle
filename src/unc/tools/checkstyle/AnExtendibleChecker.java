package unc.tools.checkstyle;


import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;

import java.io.File;
import java.util.List;


public class AnExtendibleChecker extends Checker {

	public AnExtendibleChecker() throws CheckstyleException {
		super();
	}
	@Override
	public int process(List<File> files) {
		return 0;
//		return process(files);		
	}

	

}
