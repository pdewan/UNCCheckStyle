package unc.cs.symbolTable;
import java.util.ArrayList;
import java.util.List;

import unc.cs.checks.TypeDefinedCheck;
import unc.cs.checks.ClassHasOneInterfaceCheck;
import unc.cs.symbolTable.SymbolTableFactory;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.checks.CheckUtils;

public class STBuilderCheck extends TypeDefinedCheck{
	
	public static final String MSG_KEY = "stBuilder";
	protected String typeName;
	protected boolean isInterface;
	protected String superClass;
	protected String[] interfaces;
	String currentMethodName;
	boolean currentMethodIsPublic;
	
	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.PACKAGE_DEF, TokenTypes.CLASS_DEF,  
						TokenTypes.INTERFACE_DEF, TokenTypes.METHOD_DEF };
	}
   
    public static String[] getInterfaces(DetailAST aClassDef) {
    	List<String> anInterfaces = new ArrayList();
    	String[] emptyArray = {};
    	int numInterfaces = 0;
		DetailAST implementsClause = aClassDef
				.findFirstToken(TokenTypes.IMPLEMENTS_CLAUSE);

		if (implementsClause == null)
			return emptyArray;
		DetailAST anImplementedInterface = implementsClause
				.findFirstToken(TokenTypes.IDENT);
		while (anImplementedInterface != null) {
			if (anImplementedInterface.getType() == TokenTypes.IDENT)
				anInterfaces.add(anImplementedInterface.getText());
			anImplementedInterface = anImplementedInterface.getNextSibling();
		}
		return (String[]) anInterfaces.toArray(emptyArray);
    	
    }
    
    public static String[] getSuperTypes(DetailAST aClassDef) {
    	List<String> aSuperTypes = new ArrayList();
    	String[] emptyArray = {};
    	int numInterfaces = 0;
		DetailAST extendsClause = aClassDef
				.findFirstToken(TokenTypes.EXTENDS_CLAUSE);

		if (extendsClause == null)
			return emptyArray;
		DetailAST anExtendedType = extendsClause
				.findFirstToken(TokenTypes.IDENT);
		while (anExtendedType != null) {
			if (anExtendedType.getType() == TokenTypes.IDENT)
				aSuperTypes.add(anExtendedType.getText());
			anExtendedType = anExtendedType.getNextSibling();
		}
		return (String[]) aSuperTypes.toArray(emptyArray);
		
    }
    
    public void visitType(DetailAST ast) {  
//    	
//		DetailAST aClassNameAST = ast.findFirstToken(TokenTypes.IDENT);
//		String aShortName = aClassNameAST.getText();
//		String aFullName = packageName + "." + aShortName;
//		typeName = aFullName;	
		FullIdent aFullIdent = CheckUtils.createFullType(ast);
		typeName = aFullIdent.getText();
    }
		
    public void visitMethod(DetailAST methodDef) {
    	DetailAST aMethodNameAST = methodDef.findFirstToken(TokenTypes.IDENT);
    	currentMethodName = aMethodNameAST.getText();
    	currentMethodIsPublic = ClassHasOneInterfaceCheck.isPublicInstanceMethod(methodDef);
		DetailAST aMethodParametersAST = methodDef.findFirstToken(TokenTypes.);
	}
	public void visitClass(DetailAST ast) {
		visitType(ast);
		String[] superTypes = getSuperTypes(ast);
		if (superTypes.length == 0)
			superClass = null;
		else
			superClass = superTypes[0];
		isInterface = false;
	}
	 public void visitInterface(DetailAST ast) {
	    	visitType(ast);
	    	interfaces = getSuperTypes(ast);
			isInterface = true;
		}
    public void visitMethod(DetailAST ast) {
		
	} 
	public void visitToken(DetailAST ast) {
//    	System.out.println("Check called:" + MSG_KEY);
		switch (ast.getType()) {
		case TokenTypes.PACKAGE_DEF: 
			visitPackage(ast);
			return;
		case TokenTypes.CLASS_DEF:
			visitClass(ast);
			return;
		case TokenTypes.INTERFACE_DEF:
			visitInterface(ast);
			return;
		case TokenTypes.METHOD_DEF:
			visitMethod(ast);
			return;
		default:
			System.err.println("Unexpected token");
		}
	
		
		
	}
}
