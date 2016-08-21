package unc.tools.checkstyle;

import java.io.File;

public class ANonCachingTreeWalker extends AnExtendibleTreeWalker{
	// always process file
	@Override
	protected boolean ignoreCache(File file) {
		return true;
	}


}
