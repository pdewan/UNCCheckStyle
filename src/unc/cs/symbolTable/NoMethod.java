package unc.cs.symbolTable;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

public class NoMethod extends AnSTMethod implements STMethod{


	public NoMethod() {
		super(null, null, null, null, null, false, false, false, null, false, null, null, false, null,
				null, null, null, null, null, null);
	}
	@Override
	protected void introspect() {
		
	}
	static STMethod singleton = new NoMethod();
	public static STMethod getInstance() {
		return singleton;
	}

}
