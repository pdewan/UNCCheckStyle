package unc.cs.symbolTable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

public class AnSTTypeFromClass extends AnAbstractSTType implements STType {
	Class reflectedClass;
	public AnSTTypeFromClass(Class aClass) {
		super (null, aClass.getName());
		reflectedClass = aClass;
		Method[] aMethods = aClass.getDeclaredMethods();	
		declaredMethods = new STMethod[aMethods.length];
		for (int i = 0; i < declaredMethods.length; i++) {
			declaredMethods[i] = new AnSTMethodFromMethod(aMethods[i]);
		}
		Constructor[] aConstructors = aClass.getDeclaredConstructors();
		declaredConstructors = new STMethod[aConstructors.length];
		for (int i = 0; i < declaredConstructors.length; i++) {
			declaredConstructors[i] = new AnSTMethodFromConstructor(aConstructors[i]);
		}	
		packageName = aClass.getPackage().getName();
		Class[] anInterfaces = aClass.getInterfaces();
		declaredInterfaces = new AnSTNameable[anInterfaces.length];
		for (int i = 0; i < declaredInterfaces.length; i++) {
			declaredInterfaces[i] = new AnSTNameable(anInterfaces[i].getSimpleName());
		}
		Field[] aFields = aClass.getFields();
		declaredFields = new STNameable[aFields.length];
		for (int i = 0; i < declaredFields.length; i++) {
			declaredFields[i] = new AnSTNameable(aFields[i].getName());
		}
		Class aSuperClass = aClass.getSuperclass();
		if (aSuperClass != null)
		   superClass = new AnSTNameable(aSuperClass.getSimpleName());
		
	}
	
	
	

//	@Override
//	public String getName() {
//		return reflectedClass.getName();
//	}

	@Override
	public Object getData() {
		return null;
	}

//	@Override
//	public DetailAST getAST() {
//		return null;
//	}

//	@Override
//	public STMethod[] getDeclaredMethods() {
//		return null;
//	}
//
//	@Override
//	public STMethod[] getMethods() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public STNameable[] getInterfaces() {
//		// TODO Auto-generated method stub
//		return null;
//	}

//	@Override
//	public STMethod getMethod(String aName, String[] aParameterTypes) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public String getPackage() {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public boolean isInterface() {
		// TODO Auto-generated method stub
		return reflectedClass.isInterface();
	}

//	@Override
//	public STNameable getSuperClass() {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public STNameable getStructurePatternName() {
		// TODO Auto-generated method stub
		return null;
	}
	static STNameable[] emptyNemable = new AnSTNameable[0];

	@Override
	public STNameable[] getDeclaredPropertyNames() {
		return emptyNemable;
	}

	@Override
	public STNameable[] getDeclaredEditablePropertyNames() {
		return emptyNemable;
	}

	@Override
	public STNameable[] getTags() {
		return emptyNemable;
	}

	
	@Override
	public STNameable[] getImports() {
		// TODO Auto-generated method stub
		return emptyNemable;
	}

	

	@Override
	public STNameable[] getPropertyNames() {
		// TODO Auto-generated method stub
		return emptyNemable;
	}

	
//
//
//	@Override
//	public List<String> signaturesCommonWith(String aTypeName) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public List<String> getSubTypes() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public List<String> getPeerTypes() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public List<STNameable> superTypesInCommonWith(String anOtherType) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public List<STNameable> superTypesInCommonWith(STType anOtherType) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public List<String> namesOfSuperTypesInCommonWith(String anOtherType) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public List<String> getSignatures() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
	@Override
	public void findDelegateTypes() {
		
	}
//
//	@Override
//	public List<STNameable> getAllInterfaces() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public Boolean isSubtypeOf(String aName) {
//		// TODO Auto-generated method stub
//		return null;
//	}

//	@Override
//	public Boolean isDelegate(String aName) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public boolean isParsedClass() {
		return false;
	}
	static Set<String> emptySet = new HashSet();
	@Override
	public Set<String> getDelegates() {
		// TODO Auto-generated method stub
		return emptySet;
	}
    @Override
	public boolean waitForSuperTypeToBeBuilt() {
		return false;
	}




	@Override
	public boolean isEnum() {
		return reflectedClass.isEnum();
	}


//	@Override
//	public Map<String, PropertyInfo> getDeclaredPropertyInfos() {
//		return null;
//	}


//
//
//	@Override
//	public Set<String> getDelegates() {
//		// TODO Auto-generated method stub
//		return null;
//	}

}
