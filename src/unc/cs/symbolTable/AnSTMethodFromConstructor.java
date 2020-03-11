package unc.cs.symbolTable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

import unc.cs.checks.ComprehensiveVisitCheck;

public class AnSTMethodFromConstructor extends AnAbstractSTMethod implements STMethod{
	Constructor constructor;
	static STNameable[] emptyArray = {};
	Integer accessToken;

	
	public AnSTMethodFromConstructor(Constructor aMethod) {
		super(null, aMethod.getName());
		accessToken = ComprehensiveVisitCheck.getAccessToken(aMethod);
		constructor = aMethod;		
	}
	@Override
	public boolean isParsedMethod() {
		return false;
	}
	
	@Override
	public Object getData() {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	public DetailAST getAST() {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public String getDeclaringClass() {
		return constructor.getDeclaringClass().getName();
	}

//	@Override
//	public String getName() {
//		return method.getName();
//	}

	@Override
	public String[] getParameterTypes() {
		Class[] parameterTypes = constructor.getParameterTypes();
		String[] result = new String[parameterTypes.length];
		for (int i = 0; i < parameterTypes.length; i++) {
			result[i] = parameterTypes[i].getSimpleName();
		}
		return result;
	}

	@Override
	public String getReturnType() {
		// TODO Auto-generated method stub
		return constructor.getDeclaringClass().getName();
	}

	@Override
	public boolean isPublic() {
		return true;
	}

	@Override
	public boolean isVisible() {
		return true;
	}

	@Override
	public STNameable[] getTags() {
		return emptyArray;
	}

	@Override
	public boolean assignsToGlobal() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public CallInfo[] getCallInfoOfMethodsCalled() {
		// TODO Auto-generated method stub
//		return new String[0][0];
		return new CallInfo[0];
	}

	@Override
	public boolean isProcedure() {
		return false;
	}

//	@Override
//	public boolean isSetter() {
//		return false;
//	}
//
//	@Override
//	public boolean isGetter() {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean isInit() {
//		// TODO Auto-generated method stub
//		return false;
//	}

//	@Override
//	public String getSignature() {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public boolean isInstance() {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public STNameable[] getComputedTags() {
		// TODO Auto-generated method stub
		return getTags();
	}
	@Override
	public boolean isConstructor() {
		// TODO Auto-generated method stub
		return true;
	}
//	@Override
//	public List<STMethod> getLocalMethodsCalled() {
//		// TODO Auto-generated method stub
//		return null;
//	}
	List emptyList = new ArrayList();
	@Override
	public List<STMethod> getLocalMethodsCalled() {
		return emptyList;
	}
	@Override
	public void fillLocalCallClosure(List<STMethod> aList) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public List<STMethod> getAllMethodsCalled() {
		// TODO Auto-generated method stub
		return emptyList;
	}
	@Override
	public void fillAllCallClosure(List<STMethod> aList) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public List<STMethod> getAllCallClosure() {
		return emptyList;
	}
	@Override
	public List<STMethod> getLocalCallClosure() {
		return emptyList;
	}
	@Override
	public Boolean instantiatesType(String aShortOrLongName) {
		return false;
	}
	@Override
	public String[] getParameterNames() {
		return null;
	}
	@Override
	public Set<String> getGlobalsAssigned() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Set<String> getGlobalsAccessed() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Integer getAccessToken() {
		return accessToken;
	}
	@Override
	public List<STVariable> getLocalVariables() {
		return emptyList;
	}
	@Override
	public List<STVariable> getParameters() {
		// TODO Auto-generated method stub
		return emptyList;
	}
	@Override
	public boolean isSynchronized() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public int getNumberOfTernaryConditionals() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public List<STType> getAsserts() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int getNumberOfAsserts() {
		// TODO Auto-generated method stub
		return 0;
	}
//	@Override
//	public void addFullNamesToUnknowns() {
//		// TODO Auto-generated method stub
//		
//	}
	@Override
	public Map<String, Set<DetailAST>> getUnknownAccessed() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Map<String, Set<DetailAST>> getUnknownAssigned() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void refreshUnknowns() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void processGlobals() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Map<String, Set<DetailAST>> getGlobalsAssignedMap() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Map<String, Set<DetailAST>> getGlobalsAccessedMap() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<STVariable> getParametersAssigned() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<STVariable> getLocalsAssigned() {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
