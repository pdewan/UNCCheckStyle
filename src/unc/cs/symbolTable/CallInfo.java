package unc.cs.symbolTable;

import java.util.List;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

public interface CallInfo {

	public abstract String getCaller();
	boolean hasUnknownCalledType();

	public abstract String getCalledType();

	public abstract String getCallee();

	List<DetailAST> getActuals();

	String[] getNormalizedCall();

	List<String> getCallerParameterTypes();

	STMethod getCallingMethod();

	void setCallingMethod(STMethod anSTMethod);

	STType getCalledSTType();

	Set<STMethod> getMatchingCalledMethods();

	DetailAST getAST();

//	void setCalledType(String newVal);
	void setCalledSTType(STType newVal);

	void setCalledType(String newVal);
	String getCallingType();
	STMethod[] getCalledSTMethods();

}