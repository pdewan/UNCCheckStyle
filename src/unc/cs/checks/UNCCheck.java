package unc.cs.checks;

import java.util.HashMap;
import java.util.Map;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FileContents;

public abstract class UNCCheck extends Check{

	public final void extendibleLog(int line, String key, Object... args) {
		System.out.println("key:" + key);
        log(line, key, args);
    }
	
	public abstract void checkedVisitToken(DetailAST ast);
	
	public void visitToken(DetailAST ast) {
		try {
			checkedVisitToken(ast);
			
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
			
		}
	}



    public final void extendibleLog(int lineNo, int colNo, String key,
            Object... args) {
		System.out.println("key:" + key);

        log(lineNo, colNo, key, args);
    }
    protected  abstract String msgKey();

}
