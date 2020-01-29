package unc.cs.checks;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import unc.cs.symbolTable.AnSTNameable;
import unc.cs.symbolTable.STNameable;
import unc.cs.symbolTable.STType;
import unc.tools.checkstyle.ProjectSTBuilderHolder;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException;

public abstract class TypeVisitedCheck extends UNCCheck {
	public static final String MSG_KEY = "typeDefined";	
	public static final String DEFAULT_PACKAGE = "default"; 
	protected String packageName;
	protected String fullTypeName, shortTypeName;
	protected DetailAST typeAST;
	protected DetailAST typeNameAST;
	protected STNameable typeNameable;
	protected Stack<String> fullTypeNameStack = new Stack();
	protected Stack<String> shortTypeNameStack = new Stack();
	protected Stack<DetailAST> typeNameASTStack = new Stack();
	protected Stack<STNameable> typeNameableStack = new Stack();
	protected Stack<DetailAST> typeASTStack = new Stack();
	protected Stack<Boolean> isGenericStack = new Stack();


//	protected STNameable packageNameable;
	protected Boolean isGeneric;
	@Override
    public void doBeginTree(DetailAST ast) {
        packageName = DEFAULT_PACKAGE;
    } 
//	@Override
//	public int[] getDefaultTokens() {
//		return new int[] {TokenTypes.PACKAGE_DEF, TokenTypes.CLASS_DEF,  
//						TokenTypes.INTERFACE_DEF, TokenTypes.METHOD_DEF, TokenTypes.PARAMETER_DEF };
//	}
	protected void resetProject() {
		super.resetProject();
		fullTypeName = null;
		shortTypeName = null;
		typeAST = null;
		typeNameAST = null;
		typeNameable = null;
		fullTypeNameStack.clear();
		shortTypeNameStack.clear();
		typeNameASTStack.clear();
		typeNameableStack.clear();;
		typeASTStack.clear();
		isGenericStack.clear();
		
	}
	
	public boolean maybeVisitPackage(DetailAST ast) {
		if (ast.getType() == TokenTypes.PACKAGE_DEF) {
//			packageName = ast.findFirstToken(TokenTypes.IDENT).getText();
//			System.out.println("found package:" + packageName);	
			visitPackage(ast);
			return true;
		}
		return false;
	}
	 public void visitType(DetailAST ast) { 
		 visitTypeMinimal(ast);

//	    	typeAST = ast;
//	    	typeNameAST = ast.findFirstToken(TokenTypes.IDENT);
//	    	isGeneric =  (typeNameAST.getNextSibling().getType() == TokenTypes.TYPE_PARAMETERS);
//			 shortTypeName = typeNameAST.getText();
//			 fullTypeName = packageName + "." + shortTypeName;
//			 typeNameable = new AnSTNameable(typeNameAST, fullTypeName);
	 }
	 public void leaveType(DetailAST ast) {
		 leaveTypeMinimal(ast);
	 }
	 protected void visitTypeMinimal(DetailAST ast) { 
//		 DetailAST generic = ast.getFirstChild().getNextSibling().getNextSibling().getNextSibling();
//		 generic = ast.findFirstToken(TokenTypes.TYPE_PARAMETERS);
//		 if (generic.getType() == TokenTypes.TYPE_PARAMETERS) {
//			 int i = 0;
//		 }
//	    	
//			maybeVisitPackage(ast);
	    	typeAST = ast;
	    	typeNameAST = ast.findFirstToken(TokenTypes.IDENT);
	    	isGeneric =  (typeNameAST.getNextSibling().getType() == TokenTypes.TYPE_PARAMETERS);
			 shortTypeName = typeNameAST.getText();
//			 if (shortTypeName.contains("Bridge")) {
//				 System.out.println ("found outer class");
//			 }
//			 if (shortTypeName.contains("ListImp")) {
//				 System.out.println("inner interface");
//			 }
			 fullTypeName = packageName + "." + shortTypeName;
			 
			 typeNameable = new AnSTNameable(typeNameAST, fullTypeName);
		if (ProjectSTBuilderHolder.getSTBuilder().getVisitInnerClasses()) {
	 
		    typeASTStack.push(typeAST);
		    typeNameASTStack.push(typeNameAST);
		    isGenericStack.push(isGeneric);
		    shortTypeNameStack.push(shortTypeName);		    
			 fullTypeNameStack.push(fullTypeName);
			 typeNameableStack.push(typeNameable);
		}

//			aFullTypeName = aFullName;
	 }
	 
	 public static <T> T myPop (Stack<T> aStack) {
		 if (aStack.isEmpty()) {
			 System.err.println ("empty stack!");
			 return null;
		 }
		 aStack.pop();
		 if (aStack.isEmpty())
			 return null;
		 return aStack.peek();
	 }
	 
	 // this may be danderous as have assumed one class per file so far
	 // and finishTree might assume these values to be set
	 // so not calling it
	 protected void leaveTypeMinimal (DetailAST ast) {
		 if (ProjectSTBuilderHolder.getSTBuilder().getVisitInnerClasses()) {
			 typeAST = myPop(typeASTStack);
//		 typeASTStack.pop();
//		 typeAST = typeASTStack.peek();
			 typeNameAST = myPop(typeNameASTStack);
//	    	typeNameASTStack.pop();
//	    	typeNameAST = typeNameASTStack.peek();
			 isGeneric = myPop(isGenericStack);
//	    	isGenericStack.pop();
//	    	isGeneric = isGenericStack.peek();
//	    	isGeneric =  (typeNameAST.getNextSibling().getType() == TokenTypes.TYPE_PARAMETERS);
			 shortTypeName = myPop(shortTypeNameStack);
//			 shortTypeNameStack.pop();
//			 shortTypeName = shortTypeNameStack.peek();
			fullTypeName = myPop(fullTypeNameStack);
//			fullTypeNameStack.pop();
//			fullTypeName = fullTypeNameStack.peek();
			typeNameable = myPop(typeNameableStack);
//			typeNameableStack.pop();
//			typeNameable = typeNameableStack.peek();
		 }
	 }
//	 public void visitEnum(DetailAST ast) {  
////	    	
////			maybeVisitPackage(ast);
//	    	typeAST = ast;
//	    	typeNameAST = ast.getNextSibling();
//	    	
//			 fullTypeName = packageName + "." + shortTypeName;
////			aFullTypeName = aFullName;
//	 }
	 protected static String getEnumName(DetailAST anEnum) {
	    	return getEnumNameAST(anEnum).getText();
	    }
	 protected static DetailAST getEnum(DetailAST anEnumDef) {
	    	return anEnumDef.getFirstChild().getNextSibling();

	 }
	    protected static DetailAST getEnumNameAST(DetailAST anEnumDef) {
	    	return getEnum(anEnumDef).getNextSibling();
	    }
	    public static String getPackageName (DetailAST ast) {
	    	FullIdent aFullIdent = FullIdent.createFullIdent(ast.getFirstChild().getNextSibling());
//			DetailAST anEnclosingType = TagBasedCheck.getEnclosingTypeDeclaration(ast);
//			packageName = getName(ast);
			return aFullIdent.getText();
	    }
	public void visitPackage(DetailAST ast) {
//		FullIdent aFullIdent = FullIdent.createFullIdent(ast.getFirstChild().getNextSibling());
//		packageName = aFullIdent.getText();
		packageName = getPackageName(ast);

	}
	protected void logType(DetailAST ast) {
//	    log(getNameAST(ast).getLineNo(), msgKey(), fullTypeName);
	    log(getNameAST(ast), fullTypeName);

    }
	// not a full name I assume
	public static String getName (DetailAST anAST) {
//		return anAST.findFirstToken(TokenTypes.IDENT).getText();
		if (anAST == null) {
//			System.err.println("null ast in get name, returning null name");
			return null;
		}
		if (anAST.getType() == TokenTypes.ENUM)
			return getEnumName(anAST);
			
		DetailAST aNameAST = anAST.findFirstToken(TokenTypes.IDENT);
//		if (aNameAST == null) {
//			System.err.println("no ident!");
//			return null;
//		}
		return aNameAST.getText();
		
//		return getNameAST(anAST).getText();

	}
//	  protected static String getEnumName(DetailAST anEnumDef) {
//	    	return anEnumDef.getNextSibling().toString();
//	    }
//	    
	
	public static DetailAST getNameAST (DetailAST anAST) {
		return anAST.findFirstToken(TokenTypes.IDENT);
		
	}
	public static String toShortTypeName (String aTypeName) {
		return TagBasedCheck.toShortTypeName(aTypeName);
	}
//	public static String toShortTypeName (String aTypeName) {
//		if (aTypeName == null)
//			return aTypeName;
//		int aDotIndex = aTypeName.lastIndexOf(".");
//		String aShortTypeName = aTypeName;
//		if (aDotIndex != -1)
//			aShortTypeName = aTypeName.substring(aDotIndex + 1);
//		return aShortTypeName;
//	}
	public static final Integer[] ignoreTokenTypesArray = {
		TokenTypes.MODIFIERS,
		TokenTypes.TYPE,
		TokenTypes.PARAMETERS,
//		TokenTypes.PARAMETER_DEF,
		TokenTypes.OBJBLOCK,
		TokenTypes.ELIST
	};
	public static final List<Integer> ignoreTokenTypesList =  Arrays.asList (ignoreTokenTypesArray);
	public static final Set<Integer> ignoreTokenTypesSet =  new HashSet(ignoreTokenTypesList);
  
	public static final Integer[] infixTokenTypesArray = {
		TokenTypes.ASSIGN,
		TokenTypes.EQUAL,
		TokenTypes.NOT_EQUAL,
		TokenTypes.PLUS,
		TokenTypes.PLUS_ASSIGN,
		TokenTypes.MINUS,
		TokenTypes.MINUS_ASSIGN,
		TokenTypes.DIV,
		TokenTypes.DIV_ASSIGN,
		TokenTypes.STAR,
		TokenTypes.STAR_ASSIGN,
		TokenTypes.BAND,
		TokenTypes.BAND_ASSIGN,
		TokenTypes.BOR,
		TokenTypes.BOR_ASSIGN,
		TokenTypes.LOR,
		TokenTypes.LAND,
		TokenTypes.SL_ASSIGN,
		TokenTypes.SR_ASSIGN,		
		TokenTypes.DOT,
		
	};
	public static final List<Integer> infixTokenTypesList =  Arrays.asList (infixTokenTypesArray);
	public static final Set<Integer> infixTokenTypesSet =  new HashSet(infixTokenTypesList);
	
	public static String toStringListPrefix(DetailAST anAST) {
		DetailAST t = anAST;
		String ts = "";
		boolean printSelf = !ignoreTokenTypesSet.contains(anAST.getType());
		// boolean printInfix =
		// printSelf?infixTokenTypesSet.contains(anAST.getType()):false;
		// boolean printPrefix = printSelf && !printInfix;

		// if (t.getFirstChild() != null) ts += " (";
		// if (printPrefix
		if (printSelf)
			ts += " " + t.getText();
		if (t.getFirstChild() != null) {
			ts += toStringListPrefix(t.getFirstChild());
		}
		// if (printInfix)
		// ts += " " + t.getText();
		// if (t.getFirstChild() != null) ts += " )";
		if (t.getNextSibling() != null) {
			ts += toStringListPrefix(t.getNextSibling());
		}
		return ts;
	}
	public static String toStringListPrefixOrInfix(DetailAST anAST) {
		String ts = "";
		boolean printSelf = !ignoreTokenTypesSet.contains(anAST.getType());
		 boolean printInfix =
		 printSelf?infixTokenTypesSet.contains(anAST.getType()):false;
		 boolean printPrefix = printSelf && !printInfix;

		// if (t.getFirstChild() != null) ts += " (";
		// if (printPrefix
		if (printPrefix)
			ts += " " + anAST.getText();
		if (anAST.getFirstChild() != null) {
			ts += toStringListPrefixOrInfix(anAST.getFirstChild());
			 if (printInfix) {
				 ts += " " + anAST.getText();
			 }
			 DetailAST child = anAST.getFirstChild().getNextSibling();
			 while (child != null) {
					ts += toStringListPrefixOrInfix(child);
					child = child.getNextSibling();

			 }
			
		}
		// if (printInfix)
		// ts += " " + t.getText();
		// if (t.getFirstChild() != null) ts += " )";
		
//		if (t.getNextSibling() != null) {
//			ts += toStringList(t.getNextSibling());
//		}
		return ts;
	}
	public static String toStringList(DetailAST anAST) {
		return toStringListPrefixOrInfix(anAST);
	}
	protected boolean getVisitInnerClasses() {
		return ProjectSTBuilderHolder.getSTBuilder().getVisitInnerClasses();
	}
	public static void trim (String[] aStrings) {
		for (int i = 0; i < aStrings.length; i++) {
			aStrings[i] = aStrings[i].trim();
		}
	}

}
