package test;

import java.awt.Component;

public abstract class TestSuperClass extends Component{
//	static int superGlobal;
	protected int superGlobal;
	protected void superMethod() {
		superGlobal = 0;
	}

}
