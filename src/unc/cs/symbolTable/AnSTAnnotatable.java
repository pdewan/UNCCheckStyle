package unc.cs.symbolTable;

import java.util.List;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

public class AnSTAnnotatable extends AnSTNameable implements STAnnotatable{
	String[] actualParameters;
	
	public AnSTAnnotatable(DetailAST ast, String name, String[] actualParameters) {
		super(ast, name);
		this.actualParameters = actualParameters;
	}
	@Override
	public String[] getActualParameters() {
		// TODO Auto-generated method stub
		return actualParameters;
	}
	@Override
	public List<AccessModifierUsage> getAccessModifiersUsed() {
		// TODO Auto-generated method stub
		return null;
	}
	

}
