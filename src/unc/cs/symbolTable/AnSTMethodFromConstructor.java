package unc.cs.symbolTable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

public class AnSTMethodFromConstructor extends AnAbstractSTMethod implements STMethod{
	Constructor constructor;
	static STNameable[] emptyArray = {};

	
	public AnSTMethodFromConstructor(Constructor aMethod) {
		super(null, aMethod.getName());
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
	public CallInfo[] getMethodsCalled() {
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
	List<STMethod> emptyMethodList = new ArrayList();
	@Override
	public List<STMethod> getLocalMethodsCalled() {
		return emptyMethodList;
	}
	@Override
	public void fillLocalCallClosure(List<STMethod> aList) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public List<STMethod> getAllMethodsCalled() {
		// TODO Auto-generated method stub
		return emptyMethodList;
	}
	@Override
	public void fillAllCallClosure(List<STMethod> aList) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public List<STMethod> getAllCallClosure() {
		return emptyMethodList;
	}
	@Override
	public List<STMethod> getLocalCallClosure() {
		return emptyMethodList;
	}
	@Override
	public Boolean instantiatesType(String aShortOrLongName) {
		return false;
	}
	
	

}
