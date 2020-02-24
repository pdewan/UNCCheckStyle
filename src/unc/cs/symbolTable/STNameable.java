package unc.cs.symbolTable;

import java.util.List;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.checks.naming.AccessModifier;

public interface STNameable extends STElement {
	String getName();

	Object getData();

	int getNumReferences();
	Set<DetailAST> getReferences();
//	void setNumReferences(int numReferences);

	AccessModifier getAccessModifier();
	List<AccessModifierUsage> getAccessModifiersUsed();

//	String[] getComponents();


}
