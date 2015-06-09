package unc.cs.checks;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sun.management.jmxremote.ConnectorBootstrap.PropertyNames;
import unc.cs.symbolTable.AnSTClass;
import unc.cs.symbolTable.AnSTMethod;
import unc.cs.symbolTable.STClass;
import unc.cs.symbolTable.STMethod;
import unc.cs.symbolTable.SymbolTableFactory;

import com.puppycrawl.tools.checkstyle.api.AnnotationUtility;
import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.checks.CheckUtils;

public class STBuilderCheck extends TypeDefinedCheck{
	
	public static final String MSG_KEY = "stBuilder";
	protected String typeName;
	protected DetailAST typeAST;
	protected boolean isInterface;
	protected String superClass;
	protected String[] interfaces;
	protected String currentMethodName;
	protected String currentMethodType;
	protected DetailAST currentMethodAST;
	protected boolean currentMethodIsPublic;
	protected List<String> currentMethodParameterTypes = new ArrayList();
	
	protected Map<String, String> propertyNameToType = new HashMap();
	protected List<String> propertyNames = new ArrayList();
	protected List<String> editablePropertyNames = new ArrayList();
	protected List<String> tags= new ArrayList();
	protected List<STMethod> stMethods = new ArrayList();

	
	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.PACKAGE_DEF, TokenTypes.CLASS_DEF,  
						TokenTypes.INTERFACE_DEF, TokenTypes.METHOD_DEF, TokenTypes.PARAMETER_DEF };
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
    public static List<String> getArrayLiterals (DetailAST parentOfArrayInitializer) {
    	List<String> result = new ArrayList<>();
    	final DetailAST arrayInit =
    			parentOfArrayInitializer.findFirstToken(TokenTypes.ANNOTATION_ARRAY_INIT);
		 if (arrayInit == null)
			 return result;
		 DetailAST anArrayElementExpression = arrayInit.findFirstToken(TokenTypes.EXPR);
		 
		 while (anArrayElementExpression != null) {
			 DetailAST anArrayElement = anArrayElementExpression.getFirstChild();
			 result.add(anArrayElement.getText());
			 if (anArrayElementExpression.getNextSibling() == null)
				 break;
			 anArrayElementExpression = anArrayElementExpression.getNextSibling().getNextSibling();
		 }
		 return result;
    }

    
    public void maybeVisitPropertyNames(DetailAST ast) {  
    	// not putting dependency on OE
		DetailAST annotationAST = AnnotationUtility.getAnnotation(ast, "PropertyNames");		
		if (annotationAST == null)
			return;
		propertyNames = getArrayLiterals(annotationAST);
    }
    public void maybeVisitEditablePropertyNames(DetailAST ast) {  
    	DetailAST annotationAST = AnnotationUtility.getAnnotation(ast, "EditablePropertyNames");		
		if (annotationAST == null)
			return;
		editablePropertyNames = getArrayLiterals(annotationAST);

    }
    public void maybeVisitTags(DetailAST ast) {  
    	DetailAST annotationAST = AnnotationUtility.getAnnotation(ast, "Tags");		
		if (annotationAST == null)
			return;
		tags = getArrayLiterals(annotationAST);
    }
    
    public void visitType(DetailAST ast) {  
//    	
		maybeVisitPackage(ast);
    	typeAST = ast;
    	DetailAST aClassNameAST = ast.findFirstToken(TokenTypes.IDENT);
		String aShortName = aClassNameAST.getText();
		String aFullName = packageName + "." + aShortName;
		typeName = aFullName;	
		maybeVisitPropertyNames(ast);
		maybeVisitEditablePropertyNames(ast);
		maybeVisitTags(ast);
//		FullIdent aFullIdent = CheckUtils.createFullType(ast);
//		typeName = aFullIdent.getText();
    }
		
    public void visitMethod(DetailAST methodDef) {
    	if (currentMethodName != null) {
    		String[] aParameterTypes = currentMethodParameterTypes.toArray(new String[0]);
    		STMethod anSTMethod = new AnSTMethod(
    				currentMethodAST, 
    				currentMethodName, 
    				typeName,
    				aParameterTypes, 
    				currentMethodIsPublic, 
    				currentMethodType);
    		stMethods.add(anSTMethod);
    	}    	
    	currentMethodParameterTypes.clear();    	
    	DetailAST aMethodNameAST = methodDef.findFirstToken(TokenTypes.IDENT);
    	currentMethodName = aMethodNameAST.getText();
    	currentMethodIsPublic = ClassHasOneInterfaceCheck.isPublicInstanceMethod(methodDef);
    	DetailAST typeDef = methodDef.findFirstToken(TokenTypes.TYPE);
    	FullIdent aTypeFullIdent = FullIdent.createFullIdent(typeDef.getFirstChild());
    	currentMethodType = aTypeFullIdent.getText();
    	currentMethodAST = methodDef;
    	
	}
    public void visitParamDef(DetailAST paramDef) {
    	final DetailAST grandParentAST = paramDef.getParent().getParent();
		if (!(grandParentAST.getType() == TokenTypes.METHOD_DEF))
			return;
		final DetailAST aType = paramDef.findFirstToken(TokenTypes.TYPE);
//		DetailAST aTypeNameAST = aType.findFirstToken(TokenTypes.IDENT);
//		String aTypeName = aTypeNameAST.getText();
		final DetailAST arrayDeclAST =
				aType.findFirstToken(TokenTypes.ARRAY_DECLARATOR);

		final FullIdent anIdentifierType = CheckUtils.createFullType(aType);
		String text = anIdentifierType.getText();
		if (arrayDeclAST != null)
			text = text + "[]";
		currentMethodParameterTypes.add(text);

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
		case TokenTypes.PARAMETER_DEF:
			visitParamDef(ast);
			return;
		default:
			System.err.println("Unexpected token");
		}
		
	}
	 @Override
	    public void beginTree(DetailAST ast) {    	
		 	currentMethodName = null;
		 	typeName = null;
		 	stMethods.clear();
	    }

	    @Override
	    public void finishTree(DetailAST ast) {
	    	
	    	STMethod[] aMethods = stMethods.toArray(new STMethod[0]);
	    	STClass anSTClass = new AnSTClass(
	    			typeAST, 
	    			typeName, 
	    			aMethods, 
	    			interfaces, 
	    			superClass, 
	    			packageName, 
	    			isInterface);
	    	SymbolTableFactory.getOrCreateSymbolTable().getTypeNameToSTClass().put(
	    			typeName, anSTClass);
//	        if (!defined) {
////	            log(ast.getLineNo(), MSG_KEY);
//	        }
	    }
}
