package test.version1;

import java.awt.Component;

public abstract class TestSuperClass extends Component{
//	static int superGlobal;
	static final int superConstant = 0;
	static final int superConstant2 = superConstant;

	protected int superGlobal;
	public TestSuperClass() {
		System.out.println(superConstant);
	}
	protected void superMethod() {
		superGlobal = superConstant2;
		superGlobal = superGlobal++;
		
	}

}
