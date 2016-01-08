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
	
	
	public static Statement parseStatement(String aSpecification) {
		Scanner aScanner = new Scanner(aSpecification.toLowerCase());
		return parseStatement(aScanner);
	}
	public static Statement parseNodes(String aSpecification) {
		Scanner aScanner = new Scanner(aSpecification.toLowerCase());
		List<Statement> aStatements = parseStatementList(aScanner, null);

		return new AStatementNodes(aStatements);
	}
	
	protected static Statement parseStatement(Scanner aScanner) {
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
	
	protected static Statement parseAssign(Scanner aScanner) {
//		if (!aScanner.hasNext()) {
//			System.err.println (ASSIGN + " not followed by variable name");
//			return null;
//		}
		String variableName = aScanner.next();
		return new AnAssignOperation(TokenTypes.ASSIGN, variableName);
	}
	protected static Statement parseCall(Scanner aScanner) {
//		if (!aScanner.hasNext()) {
//			System.err.println (ASSIGN + " not followed by variable name");
//			return null;
//		}
		String methodName = aScanner.next();
		return new ACallOperation(TokenTypes.METHOD_CALL, methodName);
	}
	protected static Statement parseReturn(Scanner aScanner) {
		
		return new AReturnOperation(TokenTypes.LITERAL_RETURN);
	}
	
	protected static Statement parseStatementSet(Scanner aScanner) {
		List<Statement> aStatements = parseStatementList(aScanner, SET_END);
		return new AStatementSet(aStatements);		
	}
	protected static Statement parseStatementSequence(Scanner aScanner) {
		List<Statement> aStatements = parseStatementList(aScanner, SEQUENCE_END);
		return new AStatementSequence(aStatements);		
	}
    protected static List<Statement> parseStatementList(Scanner aScanner, String anEndDelimiter) {
    	List<Statement> result = new ArrayList();
		while (!aScanner.hasNext(anEndDelimiter)) {
			Statement aStatement = parseStatement(aScanner);
			result.add(aStatement);
		}
		aScanner.next();
		return result;		
	}
	
	protected static Statement parseIF(Scanner aScanner) {
		Statement thenPart = parseStatement(aScanner);
		Statement elsePart = null;
		if (aScanner.hasNext(ELSE)) {
			aScanner.next();
			elsePart = parseStatement(aScanner);
		}
		return new AnIFStatement(TokenTypes.LITERAL_IF, thenPart, elsePart);
		
	}
	
	
	 protected static StatementCollection parseStatementNodes (Scanner aScanner) {
		
//		List<Statement> aStatements = parseStatementList(aSpecification, '[', ']');
		
		 return null;
	 }
	 
	 protected static List<Statement> parseStatementList (String aSpecification, char aStartDelimiter, char anEndDelimiter) {
//		 int beginIndex = aSpecification.indexOf(aStartDelimiter);
//		 if (beginIndex == -1) {
//			 System.err.println ("No star delimiter:" + aSpecification);
//		 }
//		 String aListSpecification = aSpecification.substring(beginIndex + 1, endIndex);
//		 return null;
		 return null;
	 }
	 
	 public static void main (String[] args) {
		 String testString = "{ assign x123 if { return } else { call foo } }";
//		 Scanner aScanner = new Scanner(testString);
//		 while (aScanner.hasNext()) {
//			 System.out.println (aScanner.next());			 
//		 }
		 Statement parsedStatement = parseStatement(testString);
		 
	 }


}
