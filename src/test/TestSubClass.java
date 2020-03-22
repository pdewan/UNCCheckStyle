package test;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.PrintStream;

public abstract class TestSubClass extends TestSuperClass implements PropertyChangeListener{
//	TestSuperClass testSuperClass;
//	PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
//	private String aPrivate;
	static int staticVariable;
	public static final int subConstant = superConstant + superConstant2;
//	String aDefault;
//	protected int protectedP;
//	int p2;
//	int foo;
//	void f1() {
//		f2();
//	}
//	void f2() {
//		f3();
//	}
//	public int getBar() {
//		return foo;
//	}
//	int getComputedProperty() {
//		return protectedP + p2; 
//	}
//	protected String getAPrivate() {
//		return aPrivate;
//	}
//	void assertingMethod() {
//		int i = 0, j = 1;
//		assert i > j;
//		j = i > 0?i:j;
//		i = j < i?j:i;
//	}
//	void methodWithBlocks(int aParameter) {
//		 while ( aParameter > 3) {
//			 
//			int whileVar;
//			for (int i = 0; i < 2; i++) {
//				int forvar;
//				if (i > 3) {
//					int anIfVar = 3;
//				} else {
//					int anIfElseVar = 3;
//				}
//				
//			}
//		 }
//		
//	}
//	synchronized  public void f3() {
////		foo = 0;
//		
//		protectedP = 2 + superGlobal;
//		propertyChange(null);
//		propertyChangeSupport.addPropertyChangeListener(this);
//		f2();
//		
//	}
//	protected int getProtectedP() {
//		return protectedP ;
//	}
//	protected void setProtectedP(int newVal) {
////		super.superGlobal = 2;;
//		protectedP = newVal + subConstant;
////		p2 = newVal + 1;
////		System.out.println(aDefault);
//	}
//	public int getP() {
//		return 0;
//	}
//	public void setP(int newVal) {
//		try {
//		testSuperClass = null;
//		superGlobal = 3;
//		TestSuperClass.superGlobal = 4;
//		super.superGlobal =3;
//		Object a = this.testSuperClass;
//		Object b = this.TOP_ALIGNMENT;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
////		protectedP = newVal;
//	}
//	protected void subMethod() {
//		super.superMethod();
//		superMethod();
//		this.superMethod();
//		testSuperClass.superMethod();
//		
//	}
//	public PropertyChangeEvent getProppertyChangeEvent() {
//		return null;
//	}
//	int getPureExpression() {
//		return 0;
//	}
//	protected void blockUsing() {
//		for (int i = 1; i < 3; i++) {
//			
//		}
//	}
//	protected void exceptionCatch() {
//		try {
//			((Object) this).notify();
//		} catch (Exception e) {
//			
//			e.printStackTrace();
//		}
//	}
	public static PrintStream getOut() {
		return System.out;
	}
	public static PrintStream getOut2() {
		return getOut();
	}
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
//		getOut().println("foo");
//		getOut2().println("foo");
		superGlobal = 3;
		System.out.println(superGlobal + superConstant + subConstant);
		PrintStream aPrintStream = System.out;
		aPrintStream.println("foo");
		java.lang.Math.ceil(4.5);
		Math.ceil(4.5);
		((PrintStream) aPrintStream).println("bar");
		TestSubClass.getOut().println("foo");
		// TODO Auto-generated method stub
		
	}
	{
		staticVariable = subConstant + subConstant + subConstant;
	}

}
