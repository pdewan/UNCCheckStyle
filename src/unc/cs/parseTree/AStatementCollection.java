package unc.cs.parseTree;

import java.util.ArrayList;
import java.util.List;

import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class AStatementCollection extends AStatement implements StatementCollection {
	List<CheckedStatement> statements;
	

	public AStatementCollection(List<CheckedStatement> statements) {
		super(TokenTypes.LCURLY);
		this.statements = statements;
	}


	@Override
	public List<CheckedStatement> getStatements() {
		return statements;
	}

	

}
