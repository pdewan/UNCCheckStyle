package unc.cs.symbolTable;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.checks.naming.AccessModifier;

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
	CallInfo[] getCallInfoOfMethodsCalled();
	boolean isProcedure();
	boolean isPublicSetter();
	boolean isPublicGetter();
	boolean isSetter();
	boolean isGetter();
	boolean isInit();
	boolean isSynchronized();
	String getSignature();
	boolean isInstance();
	boolean isParsedMethod();
	STNameable[] getComputedTags();
	STType getDeclaringSTType();
	void setDeclaringSTType(STType declaringSTType);
	void addCaller(STMethod aMethod);
	public Set<STMethod> getAllDirectlyOrIndirectlyCalledMethods();
//	public Set<STMethod> getAllCallingMethods() ;
	public Set<STMethod> getAllInternallyDirectlyAndIndirectlyCalledMethods() ;
	public Set<STMethod> getAllInternallyCallingMethods() ;
	Set<STMethod> getCallingMethods();
	Set<STMethod> getInternallyCallingMethods();
	Boolean callsInternally(STMethod anSTMethod);
	Boolean calls(STMethod anSTMethod);
	boolean isConstructor();
	List<STMethod> getLocalMethodsCalled();
	void fillLocalCallClosure(List<STMethod> aList);
	List<STMethod> getAllMethodsCalled();
	void fillAllCallClosure(List<STMethod> aList);
	List<STMethod> getAllCallClosure();
	List<STMethod> getLocalCallClosure();
	List<STNameable> getTypesInstantiated();
	Boolean instantiatesType(String aShortOrLongName);
	String[] getParameterNames();
	List<String> getGlobalsAssigned();
	List<String> getGlobalsAccessed();
	Integer getAccessToken();
	AccessModifier getAccessModifier();
	boolean isAbstract();
	List<STVariable> getLocalVariables();
	List<STVariable> getParameters();
	int getNumberOfTernaryConditionals();
	List<STType> getAsserts();
	int getNumberOfAsserts();
//	void addFullNamesToUnknowns();
	Map<String, Set<DetailAST>> getUnknownAccessed();
	Map<String, Set<DetailAST>> getUnknownAssigned();
	void refreshUnknowns();
	boolean isIndirectMethodsNotFullProcessed();
	Set<STType> getCallingTypes();
	

}
