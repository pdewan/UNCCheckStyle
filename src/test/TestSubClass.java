package test;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class TestSubClass extends TestSuperClass implements PropertyChangeListener{
	PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	private String aPrivate;
	String aDefault;
	protected int protectedP;
	int p2;
	int foo;
	void f1() {
		f2();
	}
	void f2() {
		f3();
	}
	public int getBar() {
		return foo;
	}
	int getComputedProperty() {
		return protectedP + p2; 
	}
	protected String getAPrivate() {
		return aPrivate;
	}
	void assertingMethod() {
		int i = 0, j = 1;
		assert i > j;
		j = i > 0?i:j;
		i = j < i?j:i;
	}
	void methodWithBlocks(int aParameter) {
		 while ( aParameter > 3) {
			 
			int whileVar;
			for (int i = 0; i < 2; i++) {
				int forvar;
				if (i > 3) {
					int anIfVar = 3;
				} else {
					int anIfElseVar = 3;
				}
				
			}
		 }
		
	}
	synchronized  public void f3() {
//		foo = 0;
		
		protectedP = 2 + superGlobal;
		propertyChange(null);
		propertyChangeSupport.addPropertyChangeListener(this);
		f2();
		
	}
	protected int getProtectedP() {
		return protectedP;
	}
	private void setProtectedP(int newVal) {
		super.superGlobal = 2;;
		protectedP = newVal;
		p2 = newVal + 1;
		System.out.println(aDefault);
	}
	public int getP() {
		return 0;
	}
	public void setP(int newVal) {
		superGlobal = 3;
		protectedP = newVal;
	}
	public PropertyChangeEvent getProppertyChangeEvent() {
		return null;
	}
	int getPureExpression() {
		return 0;
	}
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub
		
	}

}
