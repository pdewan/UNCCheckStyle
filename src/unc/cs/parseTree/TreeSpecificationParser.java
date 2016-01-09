package unc.cs.parseTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class TreeSpecificationParser {
	/*
	 * <StatementNodes> -> <Statement>*
	 * 
	 * <Statement> -> <StatementList> | <Assign Statement> | <If Statement> | <Call Statement> | <Return Statement>
	 * <StatementSequence> -> {<Statement>;*}
	 * <StatementSet> -> [Statement*]
	 * <Assign Statement> -> ASSIGN <String>
	 * <Call Statement> -> Call <String>
	 * <IF Statement> -> IF <Statement List> ELSE <Statement List>	 * 	 
	 */
	public static final String ASSIGN = "assign";
	public static final String IF = "if";
	public static final String ELSE = "else";
	public static final String CALL = "call";
	public static final String RETURN = "return";
	public static final String SET_START = "[";
	public static final String SET_END = "]";
	public static final String SEQUENCE_START = "{";
	public static final String SEQUENCE_END = "}";
//	public static final String NODES_START = "(";
//	public static final String NODES_END = ")";
	
	
	public static CheckedStatement parseStatement(String aSpecification) {
		Scanner aScanner = new Scanner(aSpecification.toLowerCase());
		return parseStatement(aScanner);
	}
	public static CheckedStatement parseNodes(String aSpecification) {
		Scanner aScanner = new Scanner(aSpecification.toLowerCase());
		List<CheckedStatement> aStatements = parseStatementList(aScanner, null);

		return new AStatementNodes(aStatements);
	}
	
	protected static CheckedStatement parseStatement(Scanner aScanner) {
//		if (!aScanner.hasNext()) { 
//			System.err.println ("Premature end of stream:");
//			return null;
//		}
		String nextToken = aScanner.next();
		if (nextToken.equalsIgnoreCase(ASSIGN))
			return parseAssign(aScanner);
		else if (nextToken.equalsIgnoreCase(RETURN))
			return parseReturn(aScanner);
		else if (nextToken.equalsIgnoreCase(IF))
			return parseIF(aScanner);
		else if (nextToken.equalsIgnoreCase(CALL))
			return parseCall(aScanner);
		else if (nextToken.equals(SET_START))
			return parseStatementSet(aScanner);
		else if (nextToken.equals(SEQUENCE_START))
			return parseStatementSequence(aScanner);
		return null;
	}
	
	protected static CheckedStatement parseAssign(Scanner aScanner) {
//		if (!aScanner.hasNext()) {
//			System.err.println (ASSIGN + " not followed by variable name");
//			return null;
//		}
		String variableName = aScanner.next();
		return new AnAssignOperation(variableName);
	}
	protected static CheckedStatement parseCall(Scanner aScanner) {
//		if (!aScanner.hasNext()) {
//			System.err.println (ASSIGN + " not followed by variable name");
//			return null;
//		}
		String methodName = aScanner.next();
		return new ACallOperation(TokenTypes.METHOD_CALL, methodName);
	}
	protected static CheckedStatement parseReturn(Scanner aScanner) {
		
		return new AReturnOperation(TokenTypes.LITERAL_RETURN);
	}
	
	protected static CheckedStatement parseStatementSet(Scanner aScanner) {
		List<CheckedStatement> aStatements = parseStatementList(aScanner, SET_END);
		return new AStatementSet(aStatements);		
	}
	protected static CheckedStatement parseStatementSequence(Scanner aScanner) {
		List<CheckedStatement> aStatements = parseStatementList(aScanner, SEQUENCE_END);
		return new AStatementSequence(aStatements);		
	}
    protected static List<CheckedStatement> parseStatementList(Scanner aScanner, String anEndDelimiter) {
    	List<CheckedStatement> result = new ArrayList();
		while (aScanner.hasNext() && (anEndDelimiter == null || !aScanner.hasNext(anEndDelimiter))) {
			CheckedStatement aStatement = parseStatement(aScanner);
			result.add(aStatement);
		}
		if (anEndDelimiter != null)
		aScanner.next();
		return result;		
	}
	
	protected static CheckedStatement parseIF(Scanner aScanner) {
		CheckedStatement thenPart = parseStatement(aScanner);
		CheckedStatement elsePart = null;
		if (aScanner.hasNext(ELSE)) {
			aScanner.next();
			elsePart = parseStatement(aScanner);
		}
		return new AnIFStatement(thenPart, elsePart);
		
	}
	
	
	 protected static StatementCollection parseStatementNodes (Scanner aScanner) {
		
		 List<CheckedStatement> aStatements = parseStatementList(aScanner, SEQUENCE_END);
			return new AStatementNodes(aStatements);	
	 }
	 

	 
	 public static void main (String[] args) {
//		 String testString = "{ assign x123 if { return } else { call foo } }";
		 String recursive = "{ if { return } call @caller }";

//		 Scanner aScanner = new Scanner(testString);
//		 while (aScanner.hasNext()) {
//			 System.out.println (aScanner.next());			 
//		 }
		 CheckedStatement parsedStatement = parseStatement(recursive);
		 System.out.println(parsedStatement);
		 
	 }


}
