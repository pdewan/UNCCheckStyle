package unc.cs.symbolTable;

import java.util.List;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

public interface CallInfo {

	public abstract String getCaller();

	public abstract String getCalledType();

	public abstract String getCallee();

	List<DetailAST> getActuals();

	String[] getNormalizedCall();

	List<String> getCallerParameterTypes();

	STMethod getCallingMethod();

	void setCallingMethod(STMethod anSTMethod);

	STType getCalledSTType();

	List<STMethod> getMatchingCalledMethods();

}