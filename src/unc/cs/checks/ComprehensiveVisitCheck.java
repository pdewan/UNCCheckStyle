package unc.cs.checks;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sun.management.jmxremote.ConnectorBootstrap.PropertyNames;
import unc.cs.symbolTable.AnSTType;
import unc.cs.symbolTable.AnSTMethod;
import unc.cs.symbolTable.AnSTNameable;
import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.STMethod;
import unc.cs.symbolTable.STNameable;
import unc.cs.symbolTable.SymbolTableFactory;

import com.puppycrawl.tools.checkstyle.api.AnnotationUtility;
import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.checks.CheckUtils;

public abstract class ComprehensiveVisitCheck extends TypeVisitedCheck{
	
//	public static final String MSG_KEY = "stBuilder";
	
	protected boolean isInterface;
	protected boolean isElaboration;
	protected String superClass;
	protected String[] interfaces;
	protected boolean currentMethodIsConstructor;
	protected String currentMethodName;
	protected String currentMethodType;
	protected DetailAST currentMethodAST;
	protected boolean currentMethodIsPublic;
	protected boolean currentMethodIsVisible;;
	protected List<String> currentMethodParameterTypes = new ArrayList();
	protected List<STNameable> imports = new ArrayList();
	protected List<STNameable> propertyNames;
	protected List<STNameable> editablePropertyNames;
	protected List<STNameable> tags= new ArrayList();
	

	protected STNameable structurePattern;

	
	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.PACKAGE_DEF, TokenTypes.CLASS_DEF,  
						TokenTypes.INTERFACE_DEF, 
						TokenTypes.TYPE_ARGUMENTS,
						TokenTypes.TYPE_PARAMETERS,
						TokenTypes.METHOD_DEF, 
						TokenTypes.CTOR_DEF,
						TokenTypes.IMPORT, TokenTypes.STATIC_IMPORT,
						TokenTypes.PARAMETER_DEF };
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
    public static List<STNameable> getArrayLiterals (DetailAST parentOfArrayInitializer) {
    	List<STNameable> result = new ArrayList<>();
    	final DetailAST arrayInit =
    			parentOfArrayInitializer.findFirstToken(TokenTypes.ANNOTATION_ARRAY_INIT);
		 if (arrayInit == null)
			 return result;
		 DetailAST anArrayElementExpression = arrayInit.findFirstToken(TokenTypes.EXPR);
		 
		 while (anArrayElementExpression != null) {
			 DetailAST anArrayElement = anArrayElementExpression.getFirstChild();
			 result.add(new AnSTNameable(anArrayElement, anArrayElement.getText()));
			 if (anArrayElementExpression.getNextSibling() == null)
				 break;
			 anArrayElementExpression = anArrayElementExpression.getNextSibling().getNextSibling();
		 }
		 return result;
    }

     protected List emptyArrayList = new ArrayList();
    public void maybeVisitPropertyNames(DetailAST ast) {  
    	// not putting dependency on OE
		DetailAST annotationAST = AnnotationUtility.getAnnotation(ast, "PropertyNames");		
		if (annotationAST == null) {
			propertyNames = emptyArrayList;
			return;
		}
		propertyNames = getArrayLiterals(annotationAST);
    }
    public void maybeVisitEditablePropertyNames(DetailAST ast) {  
    	DetailAST annotationAST = AnnotationUtility.getAnnotation(ast, "EditablePropertyNames");		
		if (annotationAST == null) {
			editablePropertyNames = emptyArrayList;
			return;
		}
		editablePropertyNames = getArrayLiterals(annotationAST);
    }
    public void maybeVisitStructurePattern(DetailAST ast) {  
    	DetailAST annotationAST = AnnotationUtility.getAnnotation(ast, "StructurePattern");		
		if (annotationAST == null)
			return;
		DetailAST expressionAST = annotationAST.findFirstToken(TokenTypes.EXPR);
		DetailAST actualParamAST = expressionAST.getFirstChild();
		FullIdent actualParamIDent = FullIdent.createFullIdent(actualParamAST);
		structurePattern = new AnSTNameable(actualParamAST, actualParamIDent.getText());		
    }
    public void maybeVisitVisible(DetailAST ast) {  
    	currentMethodIsVisible = true;
    	DetailAST annotationAST = AnnotationUtility.getAnnotation(ast, "Visible");		
		if (annotationAST == null)
			return;
		DetailAST expressionAST = annotationAST.findFirstToken(TokenTypes.EXPR);
		DetailAST actualParamAST = expressionAST.getFirstChild();
		FullIdent actualParamIDent = FullIdent.createFullIdent(actualParamAST);
		currentMethodIsVisible = !"false".equals(actualParamIDent.getText());
    }
    public void maybeVisitTags(DetailAST ast) {  
    	DetailAST annotationAST = AnnotationUtility.getAnnotation(ast, "Tags");		
		if (annotationAST == null) {
			tags = emptyArrayList;
			return;
		}
		tags = getArrayLiterals(annotationAST);
    }
    
    public void visitType(DetailAST ast) {  
    	super.visitType(ast);
		maybeVisitStructurePattern(ast);
		maybeVisitPropertyNames(ast);
		maybeVisitEditablePropertyNames(ast);
		maybeVisitTags(ast);
//		FullIdent aFullIdent = CheckUtils.createFullType(ast);
//		typeName = aFullIdent.getText();
    }
  protected abstract void processPreviousMethodData();

    
//    protected void processPreviousMethodData() {
//    	if (currentMethodName != null) {
//    		String[] aParameterTypes = currentMethodParameterTypes.toArray(new String[0]);
//    		STMethod anSTMethod = new AnSTMethod(
//    				currentMethodAST, 
//    				currentMethodName, 
//    				typeName,
//    				aParameterTypes, 
//    				currentMethodIsPublic,     				
//    				currentMethodType,
//    				currentMethodIsVisible);
//    		stMethods.add(anSTMethod);
//    	}
//    	
//    }
  public void visitMethod(DetailAST methodDef) {
  	processPreviousMethodData();
  	currentMethodIsConstructor = false;
  	visitMethodOrConstructor(methodDef);
  }
   public void visitConstructor(DetailAST methodDef) {
	  	processPreviousMethodData();
	  	currentMethodIsConstructor = true;
	  	visitMethodOrConstructor(methodDef);
   }

		
    public void visitMethodOrConstructor(DetailAST methodDef) {
//    	if (currentMethodName != null) {
//    		String[] aParameterTypes = currentMethodParameterTypes.toArray(new String[0]);
//    		STMethod anSTMethod = new AnSTMethod(
//    				currentMethodAST, 
//    				currentMethodName, 
//    				typeName,
//    				aParameterTypes, 
//    				currentMethodIsPublic,     				
//    				currentMethodType,
//    				currentMethodIsVisible);
//    		stMethods.add(anSTMethod);
//    	}    
    	processPreviousMethodData();
    	currentMethodParameterTypes.clear();    	
    	DetailAST aMethodNameAST = methodDef.findFirstToken(TokenTypes.IDENT);
    	currentMethodName = aMethodNameAST.getText();
    	currentMethodIsPublic = ComprehensiveVisitCheck.isPublicInstanceMethod(methodDef);
    	if (!currentMethodIsConstructor) {
    	DetailAST typeDef = methodDef.findFirstToken(TokenTypes.TYPE);
    	FullIdent aTypeFullIdent = FullIdent.createFullIdent(typeDef.getFirstChild());
    	currentMethodType = aTypeFullIdent.getText();
        }
    	currentMethodAST = methodDef;
    	maybeVisitVisible(methodDef);    	
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
    public void visitTypeParameters(DetailAST typeParameters) {
    	isGeneric = true;

    }
    public void visitTypeArguments(DetailAST typeParameters) {
    	isElaboration = true;

    }
	public void visitClass(DetailAST ast) {
		visitType(ast);
		String[] superTypes = getSuperTypes(ast);
		if (superTypes.length == 0)
			superClass = null;
		else
			superClass = superTypes[0];
		interfaces = getInterfaces(ast);
		isInterface = false;
	}
	 public void visitInterface(DetailAST ast) {
	    	visitType(ast);
	    	interfaces = getSuperTypes(ast);
			isInterface = true;
	 }
	 public void visitImport(DetailAST ast) {
		 FullIdent anImport = FullIdent.createFullIdentBelow(ast);
		 STNameable anSTNameable = new AnSTNameable(ast, anImport.getText());
		 imports.add(anSTNameable);
	 }
	 public void visitStaticImport(DetailAST ast) {
		 DetailAST anImportAST = ast.getFirstChild().getNextSibling();
		 FullIdent anImport = FullIdent.createFullIdent(
	                anImportAST);
		 STNameable anSTNameable = new AnSTNameable(ast, anImport.getText());
		 imports.add(anSTNameable);
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
		case TokenTypes.CTOR_DEF:
			visitConstructor(ast);
			return;
		case TokenTypes.PARAMETER_DEF:
			visitParamDef(ast);
			return;
		case TokenTypes.TYPE_PARAMETERS:
			visitTypeParameters(ast);
			return;		
		case TokenTypes.TYPE_ARGUMENTS:
			visitTypeArguments(ast);
			return;
		case TokenTypes.IMPORT:
			visitImport(ast);
			return;
		case TokenTypes.STATIC_IMPORT:
			visitStaticImport(ast);
			return;
		default:
			System.err.println("Unexpected token");
		}
		
	}
	 @Override
	    public void beginTree(DetailAST ast) {  
		 super.beginTree(ast);
		 	currentMethodName = null;
		 	typeName = null;
		 	isInterface = false;
		 	isGeneric = false;
		 	isElaboration = false;
//		 	stMethods.clear();
		 	imports.clear();
	    }
	  protected abstract void processMethodAndClassData();
	 


	    @Override
	    public void finishTree(DetailAST ast) {
	    	processMethodAndClassData();
	    }

		public static boolean isPublicInstanceMethod(DetailAST methodOrVariableDef) {
			boolean foundPublic = false;
			final DetailAST modifiersAst = methodOrVariableDef
					.findFirstToken(TokenTypes.MODIFIERS);
			if (modifiersAst.getFirstChild() != null) {
		
				for (DetailAST modifier = modifiersAst.getFirstChild(); modifier != null; modifier = modifier
						.getNextSibling()) {
					// System.out.println("Checking modifier:" + modifier);
					if (modifier.getType() == TokenTypes.LITERAL_STATIC) {
						// System.out.println("Not instance");
						return false;
					}
					if (modifier.getType() == TokenTypes.LITERAL_PUBLIC) {
						foundPublic = true;
					}
		
				}
			}
		
			return foundPublic;
		}

}
