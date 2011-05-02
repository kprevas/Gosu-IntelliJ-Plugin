package gw.plugin.ij.lang.parser;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import gw.plugin.ij.lang.GosuElementType;
import gw.plugin.ij.lang.GosuTokenTypes;
import gw.plugin.ij.lang.psi.GosuStubElementType;
import gw.plugin.ij.lang.psi.api.statements.IGosuField;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuClassDefinition;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuEnumConstant;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuExtendsClause;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuImplementsClause;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuInterfaceDefinition;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuMethod;
import gw.plugin.ij.lang.psi.stubs.GosuFieldStub;
import gw.plugin.ij.lang.psi.stubs.GosuMethodStub;
import gw.plugin.ij.lang.psi.stubs.GosuReferenceListStub;
import gw.plugin.ij.lang.psi.stubs.GosuTypeDefinitionStub;
import gw.plugin.ij.lang.psi.stubs.elements.GosuClassDefinitionElementType;
import gw.plugin.ij.lang.psi.stubs.elements.GosuEnumConstantElementType;
import gw.plugin.ij.lang.psi.stubs.elements.GosuExtendsClauseElementType;
import gw.plugin.ij.lang.psi.stubs.elements.GosuFieldElementType;
import gw.plugin.ij.lang.psi.stubs.elements.GosuImplementsClauseElementType;
import gw.plugin.ij.lang.psi.stubs.elements.GosuInterfaceDefinitionElementType;
import gw.plugin.ij.lang.psi.stubs.elements.GosuMethodElementType;

/**
 * Utility interface that contains all Gosu non-token element types
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuElementTypes extends GosuTokenTypes
{
  /*
  Stub elements
   */
  public static GosuStubElementType<GosuTypeDefinitionStub, GosuClassDefinition> ELEM_TYPE_ClassStatement = new GosuClassDefinitionElementType();
  public static GosuStubElementType<GosuTypeDefinitionStub, GosuInterfaceDefinition> INTERFACE_DEFINITION = new GosuInterfaceDefinitionElementType();
//## todo:  public GosuStubElementType<GosuTypeDefinitionStub, GosuEnumTypeDefinition> ENUM_DEFINITION = new GosuEnumDefinitionElementType();
//## todo:  public GosuStubElementType<GosuTypeDefinitionStub, GosuAnnotationTypeDefinition> ANNOTATION_DEFINITION = new GosuAnnotationDefinitionElementType();
//## todo:  public GosuStubElementType<GosuTypeDefinitionStub, GosuAnonymousClassDefinition> ANONYMOUS_CLASS_DEFINITION = new GosuAnonymousClassDefinitionElementType();

  public static TokenSet TYPE_DEFINITION_TYPES =
    TokenSet.create(
      ELEM_TYPE_ClassStatement,
      INTERFACE_DEFINITION
      //## todo:  ENUM_DEFINITION,
      //## todo:  ANNOTATION_DEFINITION
    );

  public static GosuStubElementType<GosuFieldStub, GosuEnumConstant> ENUM_CONSTANT = new GosuEnumConstantElementType();
  public static GosuStubElementType<GosuFieldStub, IGosuField> FIELD = new GosuFieldElementType();
  public static GosuStubElementType<GosuMethodStub, GosuMethod> METHOD_DEFINITION = new GosuMethodElementType();
  //## todo:  public GosuStubElementType<GosuAnnotationMethodStub, GosuAnnotationMethod> ANNOTATION_METHOD = new GosuAnnotationMethodElementType();

  public static GosuStubElementType<GosuReferenceListStub, GosuImplementsClause> IMPLEMENTS_CLAUSE = new GosuImplementsClauseElementType();
  public static GosuStubElementType<GosuReferenceListStub, GosuExtendsClause> EXTENDS_CLAUSE = new GosuExtendsClauseElementType();

  public static GosuElementType PARAMETERS_LIST = new GosuElementType( "parameters list" );

  public static GosuElementType TYPE_PARAMETER_LIST = new GosuElementType( "type parameter list" );

  public static GosuElementType REFERENCE_ELEMENT = new GosuElementType( "reference element" );

  public static GosuElementType ELEM_TYPE_NamespaceStatement = new GosuElementType( "NamespaceStatement" );
  public static GosuElementType ELEM_TYPE_ClassDeclaration = new GosuElementType( "ClassDeclaration" );
  public static GosuElementType ELEM_TYPE_FunctionStatement = new GosuElementType( "FunctionStatement" );
  public static GosuElementType ELEM_TYPE_PropertyStatement = new GosuElementType( "PropertyStatement" );
  public static GosuElementType ELEM_TYPE_ConstructorStatement = new GosuElementType( "ConstructorStatement" );
  public static GosuElementType ELEM_TYPE_StatementList = new GosuElementType( "StatementList" );
  public static GosuElementType ELEM_TYPE_NameInDeclaration = new GosuElementType( "NameInDeclaration" );
  public static GosuElementType ELEM_TYPE_VarStatement = new GosuElementType( "VarStatement" );
  public static GosuElementType ELEM_TYPE_AssignmentStatement = new GosuElementType( "AssignmentStatement" );
  public static GosuElementType ELEM_TYPE_MemberAssignmentStatement = new GosuElementType( "MemberAssignmentStatement" );
  public static GosuElementType ELEM_TYPE_ArrayAssignmentStatement = new GosuElementType( "ArrayAssignmentStatement" );
  public static GosuElementType ELEM_TYPE_MapAssignmentStatement = new GosuElementType( "MapAssignmentStatement" );
  public static GosuElementType ELEM_TYPE_MethodCallStatement = new GosuElementType( "MethodCallStatement" );
  public static GosuElementType ELEM_TYPE_BlockInvocationStatement = new GosuElementType( "BlockInvocationStatement" );
  public static GosuElementType ELEM_TYPE_BeanMethodCallStatement = new GosuElementType( "BeanMethodCallStatement" );
  public static GosuElementType ELEM_TYPE_ReturnStatement = new GosuElementType( "ReturnStatement" );
  public static GosuElementType ELEM_TYPE_BreakStatement = new GosuElementType( "BreakStatement" );
  public static GosuElementType ELEM_TYPE_ContinueStatement = new GosuElementType( "ContinueStatement" );
  public static GosuElementType ELEM_TYPE_IfStatement = new GosuElementType( "IfStatement" );
  public static GosuElementType ELEM_TYPE_WhileStatement = new GosuElementType( "WhileStatement" );
  public static GosuElementType ELEM_TYPE_DoWhileStatement = new GosuElementType( "DoWhileStatement" );
  public static GosuElementType ELEM_TYPE_ForEachStatement = new GosuElementType( "ForEachStatement" );
  public static GosuElementType ELEM_TYPE_SwitchStatement = new GosuElementType( "SwitchStatement" );
  public static GosuElementType ELEM_TYPE_TryCatchFinallyStatement = new GosuElementType( "TryCatchFinallyStatement" );
  public static GosuElementType ELEM_TYPE_ThrowStatement = new GosuElementType( "ThrowStatement" );
  public static GosuElementType ELEM_TYPE_UsingStatement = new GosuElementType( "UsingStatement" );
  public static GosuElementType ELEM_TYPE_EvalStatement = new GosuElementType( "EvalStatement" );
  public static GosuElementType ELEM_TYPE_SyntheticFunctionStatement = new GosuElementType( "SyntheticFunctionStatement" );
  public static GosuElementType ELEM_TYPE_SyntheticMemberAccessStatement = new GosuElementType( "SyntheticMemberAccessStatement" );
  public static GosuElementType ELEM_TYPE_NoOpStatement = new GosuElementType( "NoOpStatement" );
  public static GosuElementType ELEM_TYPE_ClasspathStatement = new GosuElementType( "ClasspathStatement" );
  public static GosuElementType ELEM_TYPE_UsesStatementList = new GosuElementType( "UsesStatementList" );
  public static GosuElementType ELEM_TYPE_UsesStatement = new GosuElementType( "UsesStatement" );
  public static GosuElementType ELEM_TYPE_IIdentifierExpression = new GosuElementType( "IIdentifierExpression" );
  public static GosuElementType ELEM_TYPE_ITypeAsExpression = new GosuElementType( "ITypeAsExpression" );
  public static GosuElementType ELEM_TYPE_TypeIsExpression = new GosuElementType( "TypeIsExpression" );
  public static GosuElementType ELEM_TYPE_ITypeOfExpression = new GosuElementType( "ITypeOfExpression" );
  public static GosuElementType ELEM_TYPE_StaticTypeOfExpression = new GosuElementType( "StaticTypeOfExpression" );
  public static GosuElementType ELEM_TYPE_StringLiteral = new GosuElementType( "StringLiteral" );
  public static GosuElementType ELEM_TYPE_CharLiteral = new GosuElementType( "CharLiteral" );
  public static GosuElementType ELEM_TYPE_NumericLiteral = new GosuElementType( "NumericLiteral" );
  public static GosuElementType ELEM_TYPE_TypeLiteral = new GosuElementType( "TypeLiteral" );
  public static GosuElementType ELEM_TYPE_TypeParameterListClause = new GosuElementType( "TypeParameterListClause" );
  public static GosuElementType ELEM_TYPE_BooleanLiteral = new GosuElementType( "BooleanLiteral" );
  public static GosuElementType ELEM_TYPE_DefaultArgLiteral = new GosuElementType( "DefaultArgLiteral" );
  public static GosuElementType ELEM_TYPE_UnaryExpression = new GosuElementType( "UnaryExpression" );
  public static GosuElementType ELEM_TYPE_UnaryNotPlusMinusExpression = new GosuElementType( "UnaryNotPlusMinusExpression" );
  public static GosuElementType ELEM_TYPE_EqualityExpression = new GosuElementType( "EqualityExpression" );
  public static GosuElementType ELEM_TYPE_IdentityExpression = new GosuElementType( "IdentityExpression" );
  public static GosuElementType ELEM_TYPE_RelationalExpression = new GosuElementType( "RelationalExpression" );
  public static GosuElementType ELEM_TYPE_ConditionalOrExpression = new GosuElementType( "ConditionalOrExpression" );
  public static GosuElementType ELEM_TYPE_ConditionalAndExpression = new GosuElementType( "ConditionalAndExpression" );
  public static GosuElementType ELEM_TYPE_AdditiveExpression = new GosuElementType( "AdditiveExpression" );
  public static GosuElementType ELEM_TYPE_MultiplicativeExpression = new GosuElementType( "MultiplicativeExpression" );
  public static GosuElementType ELEM_TYPE_BitshiftExpression = new GosuElementType( "BitshiftExpression" );
  public static GosuElementType ELEM_TYPE_BitwiseOrExpression = new GosuElementType( "BitwiseOrExpression" );
  public static GosuElementType ELEM_TYPE_BitwiseXorExpression = new GosuElementType( "BitwiseXorExpression" );
  public static GosuElementType ELEM_TYPE_BitwiseAndExpression = new GosuElementType( "BitwiseAndExpression" );
  public static GosuElementType ELEM_TYPE_BeanMethodCallExpression = new GosuElementType( "BeanMethodCallExpression" );
  public static GosuElementType ELEM_TYPE_MethodCallExpression = new GosuElementType( "MethodCallExpression" );
  public static GosuElementType ELEM_TYPE_MemberExpansionAccess = new GosuElementType( "MemberExpansionAccess" );
  public static GosuElementType ELEM_TYPE_MemberAccess = new GosuElementType( "MemberAccess" );
  public static GosuElementType ELEM_TYPE_NewExpression = new GosuElementType( "NewExpression" );
  public static GosuElementType ELEM_TYPE_EvalExpression = new GosuElementType( "EvalExpression" );
  public static GosuElementType ELEM_TYPE_QueryExpression = new GosuElementType( "QueryExpression" );
  public static GosuElementType ELEM_TYPE_ConditionalTernaryExpression = new GosuElementType( "ConditionalTernaryExpression" );
  public static GosuElementType ELEM_TYPE_ArrayAccess = new GosuElementType( "ArrayAccess" );
  public static GosuElementType ELEM_TYPE_MapAccess = new GosuElementType( "MapAccess" );
  public static GosuElementType ELEM_TYPE_IntervalExpression = new GosuElementType( "IntervalExpression" );
  public static GosuElementType ELEM_TYPE_ParenthesizedExpression = new GosuElementType( "ParenthesizedExpression" );
  public static GosuElementType ELEM_TYPE_NullExpression = new GosuElementType( "NullExpression" );
  public static GosuElementType ELEM_TYPE_BlockExpression = new GosuElementType( "BlockExpression" );
  public static GosuElementType ELEM_TYPE_ObjectLiteralExpression = new GosuElementType( "ObjectLiteralExpression" );
  public static GosuElementType ELEM_TYPE_TemplateStringLiteral = new GosuElementType( "TemplateStringLiteral" );
  public static GosuElementType ELEM_TYPE_ExistsExpression = new GosuElementType( "ExistsExpression" );
  public static GosuElementType ELEM_TYPE_BlockInvocation = new GosuElementType( "BlockInvocation" );
  public static GosuElementType ELEM_TYPE_FeatureLiteral = new GosuElementType( "FeatureLiteral" );
  public static GosuElementType ELEM_TYPE_ParameterDeclaration = new GosuElementType( "ParameterDeclaration" );
  public static GosuElementType ELEM_TYPE_CollectionInitializerExpression = new GosuElementType( "CollectionInitializerExpression" );
  public static GosuElementType ELEM_TYPE_NotAStatement = new GosuElementType( "NotAStatement" );
  public static GosuElementType ELEM_TYPE_LocalVarDeclaration = new GosuElementType( "LocalVarDeclaration" );
  public static GosuElementType ELEM_TYPE_NotAWordExpression = new GosuElementType( "NotAWordExpression" );
}