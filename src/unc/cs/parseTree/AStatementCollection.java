package unc.cs.parseTree;

import java.util.ArrayList;
import java.util.List;

public class AStatementCollection extends AStatement implements StatementCollection {
	List<Statement> statements;
	

	public AStatementCollection(List<Statement> statements) {
		super();
		this.statements = statements;
	}


	@Override
	public List<Statement> getStatements() {
		return statements;
	}

	

}
