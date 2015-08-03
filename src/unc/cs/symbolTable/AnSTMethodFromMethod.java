package unc.cs.symbolTable;

import java.lang.reflect.Method;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

public class AnSTMethodFromMethod extends AnAbstractSTMethod implements STMethod{
	Method method;
	static STNameable[] emptyList = {};
	
	public AnSTMethodFromMethod(Method aMethod) {
		super(null, aMethod.getName());
		method = aMethod;	
		introspect();
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
		return method.getDeclaringClass().getName();
	}

//	@Override
//	public String getName() {
//		return method.getName();
//	}

	@Override
	public String[] getParameterTypes() {
		Class[] parameterTypes = method.getParameterTypes();
		String[] result = new String[parameterTypes.length];
		for (int i = 0; i < parameterTypes.length; i++) {
			result[i] = parameterTypes[i].getSimpleName();
		}
		return result;
	}

	@Override
	public String getReturnType() {
		// TODO Auto-generated method stub
		return method.getReturnType().getSimpleName();
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
		return emptyList;
	}

	@Override
	public boolean assignsToGlobal() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String[][] methodsCalled() {
		// TODO Auto-generated method stub
		return new String[0][0];
	}

	@Override
	public boolean isProcedure() {
		return method.getReturnType() == Void.TYPE;
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


}
