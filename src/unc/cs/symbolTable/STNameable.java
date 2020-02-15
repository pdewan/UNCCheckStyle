package unc.cs.symbolTable;

import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

public interface STNameable extends STElement {
	String getName();

	Object getData();

	int getNumReferences();
	Set<DetailAST> getReferences();
//	void setNumReferences(int numReferences);

//	String[] getComponents();


}
