package unc.cs.checks;

import com.puppycrawl.tools.checkstyle.api.Check;

public abstract class UNCCheck extends Check{
	public final void extendibleLog(int line, String key, Object... args) {
		System.out.println("key:" + key);
        log(line, key, args);
    }


    public final void extendibleLog(int lineNo, int colNo, String key,
            Object... args) {
		System.out.println("key:" + key);

        log(lineNo, colNo, key, args);
    }

}
