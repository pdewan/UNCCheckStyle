package test;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class TestClass1 implements PropertyChangeListener{
	PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	private String aPrivate;
	String aDefault;
	protected int p;
	int foo;
	void f1() {
		f2();
	}
	void f2() {
		f3();
	}
	synchronized  public void f3() {
		foo = 0;
		p = 2;
		propertyChange(null);
		propertyChangeSupport.addPropertyChangeListener(this);
		f2();
	}
	protected int getProtectedP() {
		return foo + p;
	}
	private void setProtectedP(int newVal) {
		foo = newVal;
		System.out.println(aDefault);
	}
	public int getP() {
		return 0;
	}
	public void setP(int newVal) {
		p = newVal;
	}
	public PropertyChangeEvent getP2() {
		return null;
	}
	int getNonPublic() {
		return 0;
	}
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub
		
	}

}
