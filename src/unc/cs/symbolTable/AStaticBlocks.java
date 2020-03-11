package unc.cs.symbolTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

public class AStaticBlocks extends AnSTMethod{

	public AStaticBlocks(String aName) {
		super(aName);
		localMethodsCalled = new ArrayList();
		localCallClosure = new ArrayList();

		allMethodsCalled = new ArrayList();
		allCallClosure = new ArrayList();
		typesInstantiated = new ArrayList();;
//		protected List<String> globalsAssigned;
//		protected List<String> globalsAccessed;
		globalsAssignedMap = new HashMap();
		globalsAccessedMap = new HashMap();
		unknownAccessed = new HashMap();


//		protected List<String> unknownAccessed;

//		protected List<String> unknownAssigned;
		unknownAssigned = new HashMap();
		unknownsWithShortNames = new HashSet();



		localSTVariables = new ArrayList();

		
		 asserts = new ArrayList();
	}

}
