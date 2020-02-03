package test;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class TestClass1 implements PropertyChangeListener{
	PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	void f1() {
		f2();
	}
	void f2() {
		f3();
	}
	void f3() {
		propertyChange(null);
		propertyChangeSupport.addPropertyChangeListener(this);
		f2();
	}
	public int getP() {
		return 0;
	}
	public void setP(int newVal) {
		
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
