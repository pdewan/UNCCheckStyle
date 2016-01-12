package unc.cs.symbolTable;

import java.util.List;
import java.util.Set;

public interface STMethod extends STNameable{
	String PARAMETER_SEPARATOR = ";";
	String getDeclaringClass();
	String getName();
	String[] getParameterTypes();
	String getReturnType();
	boolean isPublic();
	boolean isVisible();
	STNameable[] getTags();
	boolean assignsToGlobal();
//	String[][] methodsCalled();
	CallInfo[] getMethodsCalled();
	boolean isProcedure();
	boolean isSetter();
	boolean isGetter();
	boolean isInit();
	String getSignature();
	boolean isInstance();
	boolean isParsedMethod();
	STNameable[] getComputedTags();
	STType getDeclaringSTType();
	void addCaller(STMethod aMethod);
	public Set<STMethod> getAllCalledMethods();
	public Set<STMethod> getAllCallingMethods() ;
	public Set<STMethod> getAllInternallyCalledMethods() ;
	public Set<STMethod> getAllInternallyCallingMethods() ;
	Set<STMethod> getCallingMethods();
	Set<STMethod> getInternallyCallingMethods();
	Boolean callsInternally(STMethod anSTMethod);
	Boolean calls(STMethod anSTMethod);
	boolean isConstructor();
	void setDeclaringSTType(STType declaringSTType);
	List<STMethod> getLocalMethodsCalled();
	void fillLocalCallClosure(List<STMethod> aList);
	List<STMethod> getAllMethodsCalled();
	void fillAllCallClosure(List<STMethod> aList);
	List<STMethod> getAllCallClosure();
	List<STMethod> getLocalCallClosure();
	List<STNameable> getTypesInstantiated();
	Boolean instantiatesType(String aShortOrLongName);
	String[] getParameterNames();
}
