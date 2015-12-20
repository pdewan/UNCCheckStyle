package unc.cs.symbolTable;

import java.util.List;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

public interface CallInfo {

	public abstract String getCaller();

	public abstract String getCalledType();

	public abstract String getCalleee();

	List<DetailAST> getActuals();

	String[] getNormalizedCall();

	List<String> getCallerParameterTypes();

}