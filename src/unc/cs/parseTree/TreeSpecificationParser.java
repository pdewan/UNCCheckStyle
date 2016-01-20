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
	 * <IF Statement> -> IF <Paren Expression> <Statement List> ELSE <Statement List>	 * 	 
	 */
	public static final String ASSIGN = "assign";
	public static final String IF = "if";
	public static final String ELSE = "else";
	public static final String CALL = "call";
	public static final String BODY = "body";
	public static final String RETURN = "return";
	public static final String SET_START = "\\[";
	public static final String SET_END = "\\]";
	public static final String SEQUENCE_START = "{";
	public static final String SEQUENCE_END = "}";
	public static final String L_PAREN = "\\(";
	public static final String R_PAREN = "\\)";
//	public static final String NODES_START = "(";
//	public static final String NODES_END = ")";
	
//	public static String normalizeSpecification (String aSpecification) {
//		String result = aSpecification.toLowerCase();
////		result.replaceAll("\\(", "\\(");
////		result.replaceAll("\\)", "\\)");
////		result.replaceAll("\\*", "\\*");
//		return result;
//	}
	public static CheckedNode parseConstruct(String aSpecification) {
		Scanner aScanner = new Scanner(aSpecification.toLowerCase());
		return parseNode(aScanner);
	}
	public static CheckedNode parseNodes(String aSpecification) {
		Scanner aScanner = new Scanner(aSpecification.toLowerCase());
		List<CheckedNode> aStatements = parseNodeList(aScanner, null);

		return new AnIndependentNodes(aStatements);
	}
	
	protected static CheckedNode parseNode(Scanner aScanner) {
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
		else if (nextToken.equalsIgnoreCase(BODY))
			return parseBody(aScanner);
		else if (nextToken.equals(SET_START))
			return parseStatementSet(aScanner);
		else if (nextToken.equals(SEQUENCE_START))
			return parseStatementSequence(aScanner);
		else if (nextToken.equals(L_PAREN))
			return parseParenthesizedExpression(aScanner);
		return AnyNode.getSingleton();
	}
	
	protected static CheckedNode parseAssign(Scanner aScanner) {
//		if (!aScanner.hasNext()) {
//			System.err.println (ASSIGN + " not followed by variable name");
//			return null;
//		}
		String variableName = aScanner.next();
		return new AnAssignOperation(variableName);
	}
	protected static CheckedNode parseParenthesizedExpression(Scanner aScanner) {
		List<CheckedNode> anExpressions = parseNodeList(aScanner, R_PAREN);
		return new AParenthizedExpressionSequence(anExpressions);
	}
	protected static CheckedNode parseCall(Scanner aScanner) {
//		if (!aScanner.hasNext()) {
//			System.err.println (ASSIGN + " not followed by variable name");
//			return null;
//		}
		String methodName = aScanner.next();
		return new ACallOperation( methodName);
	}
	protected static CheckedNode parseBody(Scanner aScanner) {

		String body = aScanner.next();
		return new ABody( body);
	}
	protected static CheckedNode parseReturn(Scanner aScanner) {
		return null;
//		return new AReturnOperation();
	}
	
	protected static CheckedNode parseStatementSet(Scanner aScanner) {
		List<CheckedNode> aStatements = parseNodeList(aScanner, SET_END);
		return new AStatementSet(aStatements);		
	}
	protected static CheckedNode parseStatementSequence(Scanner aScanner) {
		List<CheckedNode> aStatements = parseNodeList(aScanner, SEQUENCE_END);
		return new AStatementSequence(aStatements);		
	}
    protected static List<CheckedNode> parseNodeList(Scanner aScanner, String anEndDelimiter) {
    	List<CheckedNode> result = new ArrayList();
		while (aScanner.hasNext() && 
				(anEndDelimiter == null || 
				!aScanner.hasNext(anEndDelimiter))) {
			CheckedNode aStatement = parseNode(aScanner);
			result.add(aStatement);
		}
		if (anEndDelimiter != null)
		   aScanner.next();
		return result;		
	}
	
	protected static CheckedNode parseIF(Scanner aScanner) {		
//		aScanner.next(); // consume the left paren
//		CheckedNode expression = parseParenthesizedExpression(aScanner);
		String anExpression = aScanner.next();
		CheckedNode thenPart = parseNode(aScanner);
		CheckedNode elsePart = null;
		if (aScanner.hasNext(ELSE)) {
			aScanner.next();
			elsePart = parseNode(aScanner);
		}
		return new AnIFStatement(anExpression, thenPart, elsePart);
		
	}
	
	
	 protected static NodesCollection parseStatementNodes (Scanner aScanner) {
		
		 List<CheckedNode> aStatements = parseNodeList(aScanner, SEQUENCE_END);
			return new AnIndependentNodes(aStatements);	
	 }
	 

	 
	 public static void main (String[] args) {
//		 String testString = "{ assign x123 if { return } else { call foo } }";
		 String testString = "factorial#RETURN IF ( * ) RETURN ELSE CALL @Self%(.*)$1(.*)  CALL factorial:int->int%(.*)$1(.*) { RETURN } IF ( * ) { RETURN } ELSE { IF ( * ) { RETURN } RETURN CALL @Self } ";

		 String recursive = "{ if ( * ) { return } call @caller }";
		 
		 String[] aParameters = "CALL @Self%(.*)$1(.*)".split("%");

		 Scanner aScanner = new Scanner(testString);
		 while (aScanner.hasNext()) {
			 System.out.println (aScanner.next());			 
		 }
		 CheckedNode parsedStatement = parseConstruct(recursive);
		 System.out.println(parsedStatement);
		 
	 }


}
