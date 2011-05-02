package gw.plugin.ij.lang.psi.impl;

import gw.plugin.ij.lang.psi.GosuFileBase;
import gw.plugin.ij.lang.psi.IGosuPsiElement;
import gw.plugin.ij.lang.psi.api.GosuPackageDefinition;
import gw.plugin.ij.lang.psi.api.auxilary.IGosuModifierList;
import gw.plugin.ij.lang.psi.api.expressions.IGosuExpression;
import gw.plugin.ij.lang.psi.api.statements.IGosuField;
import gw.plugin.ij.lang.psi.api.statements.IGosuVariable;
import gw.plugin.ij.lang.psi.api.statements.params.IGosuParameter;
import gw.plugin.ij.lang.psi.api.statements.params.IGosuParameterList;
import gw.plugin.ij.lang.psi.api.statements.typedef.*;
import gw.plugin.ij.lang.psi.impl.expressions.GosuNameInDeclarationImpl;
import gw.plugin.ij.lang.psi.impl.statements.GosuUsesStatementImpl;
import gw.plugin.ij.lang.psi.impl.statements.GosuUsesStatementListImpl;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public abstract class GosuElementVisitor
{
  public void visitElement( IGosuPsiElement element )
  {
  }

  public void visitFile( GosuFileBase file )
  {
    visitElement( file );
  }

  public void visitField( IGosuField field )
  {
    visitVariable( field );
  }

  public void visitNameInDeclaration( GosuNameInDeclarationImpl name )
  {
    visitElement(name);
  }

  public void visitVariable( IGosuVariable variable )
  {
    visitElement(variable);
  }

  public void visitMethod( GosuMethod method )
  {
    visitElement( method );
  }

  public void visitExpression( IGosuExpression expression )
  {
    visitElement( expression );
  }

  public void visitParameterList( IGosuParameterList parameterList )
  {
    visitElement( parameterList );
  }

  public void visitParameter( IGosuParameter parameter )
  {
    visitVariable(parameter);
  }

  public void visitEnumConstant( GosuEnumConstant enumConstant )
  {
    visitField(enumConstant);
  }

  public void visitPackageDefinition( GosuPackageDefinition packageDefinition )
  {
    visitElement(packageDefinition);
  }

  public void visitTypeDefinition( GosuTypeDefinition typeDefinition )
  {
    visitElement(typeDefinition);
  }

  public void visitImplementsClause( GosuImplementsClause implementsClause )
  {
    visitElement( implementsClause );
  }

  public void visitExtendsClause( GosuExtendsClause extendsClause )
  {
    visitElement( extendsClause );
  }

  public void visitModifierList( IGosuModifierList modifierList )
  {
    visitElement( modifierList );
  }

  public void visitUsesStatement( GosuUsesStatementImpl gosuUsesStatement )
  {
    visitElement( gosuUsesStatement );
  }

  public void visitUsesStatementList( GosuUsesStatementListImpl gosuUsesStatement )
  {
    visitElement( gosuUsesStatement );
  }

//
//  public void visitStatement( IGosuStatement statement )
//  {
//    visitElement( statement );
//  }
//
//  public void visitClosure( GosuClosableBlock closure )
//  {
//    visitStatement( closure );
//  }
//
//  public void visitOpenBlock( GosuOpenBlock block )
//  {
//    visitElement( block );
//  }
//
//  public void visitEnumConstants( GosuEnumConstantList enumConstantsSection )
//  {
//    visitElement( enumConstantsSection );
//  }
//
//  public void visitEnumConstant( GosuEnumConstant enumConstant )
//  {
//    visitField( enumConstant );
//  }
//
//  public void visitImportStatement( GosuImportStatement importStatement )
//  {
//    visitElement( importStatement );
//  }
//
//  public void visitBreakStatement( GosuBreakStatement breakStatement )
//  {
//    visitStatement( breakStatement );
//  }
//
//  public void visitContinueStatement( GosuContinueStatement continueStatement )
//  {
//    visitStatement( continueStatement );
//  }
//
//  public void visitReturnStatement( GosuReturnStatement returnStatement )
//  {
//    visitStatement( returnStatement );
//  }
//
//  public void visitAssertStatement( GosuAssertStatement assertStatement )
//  {
//    visitStatement( assertStatement );
//  }
//
//  public void visitThrowStatement( GosuThrowStatement throwStatement )
//  {
//    visitStatement( throwStatement );
//  }
//
//  public void visitLabeledStatement( GosuLabeledStatement labeledStatement )
//  {
//    visitStatement( labeledStatement );
//  }
//
//  public void visitNewExpression( GosuNewExpression newExpression )
//  {
//    visitCallExpression( newExpression );
//  }
//
//  public void visitApplicationStatement( GosuApplicationStatement applicationStatement )
//  {
//    visitStatement( applicationStatement );
//  }
//
//  public void visitArrayDeclaration( GosuArrayDeclaration arrayDeclaration )
//  {
//    visitElement( arrayDeclaration );
//  }
//
//  public void visitCommandArguments( GosuCommandArgumentList argumentList )
//  {
//    visitArgumentList( argumentList );
//  }
//
//  public void visitConditionalExpression( GosuConditionalExpression expression )
//  {
//    visitExpression( expression );
//  }
//
//  public void visitAssignmentExpression( GosuAssignmentExpression expression )
//  {
//    visitExpression( expression );
//  }
//
//  public void visitBinaryExpression( GosuBinaryExpression expression )
//  {
//    visitExpression( expression );
//  }
//
//  public void visitUnaryExpression( GosuUnaryExpression expression )
//  {
//    visitExpression( expression );
//  }
//
//  public void visitPostfixExpression( GosuPostfixExpression expression )
//  {
//    visitUnaryExpression( expression );
//  }
//
//  public void visitRegexExpression( GosuRegex expression )
//  {
//    visitLiteralExpression( expression );
//  }
//
//  public void visitLiteralExpression( GosuLiteral literal )
//  {
//    visitExpression( literal );
//  }
//
//  public void visitGStringExpression( GosuString gstring )
//  {
//    visitExpression( gstring );
//  }
//
//  public void visitReferenceExpression( IGosuReferenceExpression referenceExpression )
//  {
//    visitExpression( referenceExpression );
//  }
//
//  public void visitThisExpression( GosuThisReferenceExpression thisExpression )
//  {
//    visitExpression( thisExpression );
//  }
//
//  public void visitSuperExpression( GosuSuperReferenceExpression superExpression )
//  {
//    visitExpression( superExpression );
//  }
//
//  public void visitCastExpression( GosuTypeCastExpression typeCastExpression )
//  {
//    visitExpression( typeCastExpression );
//  }
//
//  public void visitSafeCastExpression( GosuSafeCastExpression typeCastExpression )
//  {
//    visitExpression( typeCastExpression );
//  }
//
//  public void visitInstanceofExpression( GosuInstanceOfExpression expression )
//  {
//    visitExpression( expression );
//  }
//
//  public void visitBuiltinTypeClassExpression( GosuBuiltinTypeClassExpression expression )
//  {
//    visitExpression( expression );
//  }
//
//  public void visitParenthesizedExpression( GosuParenthesizedExpression expression )
//  {
//    visitExpression( expression );
//  }
//
//  public void visitPropertySelection( GosuPropertySelection expression )
//  {
//    visitExpression( expression );
//  }
//
//  public void visitPropertySelector( GosuPropertySelector selector )
//  {
//    visitElement( selector );
//  }
//
//  public void visitIndexProperty( GosuIndexProperty expression )
//  {
//    visitExpression( expression );
//  }
//
//  public void visitLabel( GosuLabel label )
//  {
//    visitElement( label );
//  }
//
//  public void visitArgumentList( IGosuArgumentList list )
//  {
//    visitElement( list );
//  }
//
//  public void visitNamedArgument( IGosuNamedArgument argument )
//  {
//    visitElement( argument );
//  }
//
//  public void visitArgumentLabel( GosuArgumentLabel argumentLabel )
//  {
//    visitElement( argumentLabel );
//  }
//
//  public void visitListOrMap( GosuListOrMap listOrMap )
//  {
//    visitExpression( listOrMap );
//  }
//
//  public void visitArrayTypeElement( GosuArrayTypeElement typeElement )
//  {
//    visitElement( typeElement );
//  }
//
//  public void visitBuiltinTypeElement( GosuBuiltInTypeElement typeElement )
//  {
//    visitElement( typeElement );
//  }
//
//  public void visitClassTypeElement( GosuClassTypeElement typeElement )
//  {
//    visitElement( typeElement );
//  }
//
//  public void visitCodeReferenceElement( GosuCodeReferenceElement refElement )
//  {
//    visitElement( refElement );
//  }
//
//
//
//  public void visitTypeArgumentList( GosuTypeArgumentList typeArgumentList )
//  {
//    visitElement( typeArgumentList );
//  }
//
//  public void visitWildcardTypeArgument( GosuWildcardTypeArgument wildcardTypeArgument )
//  {
//    visitElement( wildcardTypeArgument );
//  }
//
//  public void visitDefaultAnnotationMember( GosuAnnotationMethod annotationMethod )
//  {
//    visitElement( annotationMethod );
//  }
//
//  public void visitDefaultAnnotationValue( GosuDefaultAnnotationValue defaultAnnotationValue )
//  {
//    visitElement( defaultAnnotationValue );
//  }
//
//  public void visitMethod( GosuMethod method )
//  {
//    visitElement( method );
//  }
//
//  public void visitDocMethodReference( GosuDocMethodReference reference )
//  {
//    visitElement( reference );
//  }
//
//  public void visitDocFieldReference( GosuDocFieldReference reference )
//  {
//    visitElement( reference );
//  }
//
//  public void visitDocMethodParameterList( GosuDocMethodParams params )
//  {
//    visitElement( params );
//  }
//
//  public void visitDocMethodParameter( GosuDocMethodParameter parameter )
//  {
//    visitElement( parameter );
//  }
//
//  public void visitConstructorInvocation( GosuConstructorInvocation invocation )
//  {
//    visitElement( invocation );
//  }
//
//  public void visitThrowsClause( GosuThrowsClause throwsClause )
//  {
//    visitElement( throwsClause );
//  }
//
//  public void visitAnnotationArgumentList( GosuAnnotationArgumentList annotationArgumentList )
//  {
//    visitElement( annotationArgumentList );
//  }
//
//  public void visitAnnotationArrayInitializer( GosuAnnotationArrayInitializer arrayInitializer )
//  {
//    visitElement( arrayInitializer );
//  }
//
//  public void visitAnnotationNameValuePair( GosuAnnotationNameValuePair nameValuePair )
//  {
//    visitElement( nameValuePair );
//  }
//
//  public void visitAnnotationNameValuePairs( GosuAnnotationNameValuePairs nameValuePair )
//  {
//    visitElement( nameValuePair );
//  }
//
//  public void visitAnnotation( GosuAnnotation annotation )
//  {
//    visitElement( annotation );
//  }
//
//
//  public void visitEnumDefinitionBody( GosuEnumDefinitionBody enumDefinitionBody )
//  {
//    visitTypeDefinitionBody( enumDefinitionBody );
//  }
//
//  public void visitIfStatement( GosuIfStatement ifStatement )
//  {
//    visitStatement( ifStatement );
//  }
//
//  public void visitForStatement( GosuForStatement forStatement )
//  {
//    visitStatement( forStatement );
//  }
//
//  public void visitWhileStatement( GosuWhileStatement whileStatement )
//  {
//    visitStatement( whileStatement );
//  }
//
//  public void visitSwitchStatement( GosuSwitchStatement switchStatement )
//  {
//    visitStatement( switchStatement );
//  }
//
//  public void visitCaseSection( GosuCaseSection caseSection )
//  {
//    visitElement( caseSection );
//  }
//
//  public void visitCaseLabel( GosuCaseLabel caseLabel )
//  {
//    visitElement( caseLabel );
//  }
//
//  public void visitForInClause( GosuForInClause forInClause )
//  {
//    visitForClause( forInClause );
//  }
//
//  public void visitForClause( GosuForClause forClause )
//  {
//    visitElement( forClause );
//  }
//
//  public void visitTraditionalForClause( GosuTraditionalForClause forClause )
//  {
//    visitForClause( forClause );
//  }
//
//  public void visitTryStatement( GosuTryCatchStatement tryCatchStatement )
//  {
//    visitStatement( tryCatchStatement );
//  }
//
//  public void visitBlockStatement( GosuBlockStatement blockStatement )
//  {
//    visitStatement( blockStatement );
//  }
//
//  public void visitCatchClause( GosuCatchClause catchClause )
//  {
//    visitElement( catchClause );
//  }
//
//  public void visitDocComment( GosuDocComment comment )
//  {
//    visitElement( comment );
//  }
//
//  public void visitDocTag( GosuDocTag docTag )
//  {
//    visitElement( docTag );
//  }
//
//  public void visitFinallyClause( GosuFinallyClause catchClause )
//  {
//    visitElement( catchClause );
//  }
//
//  public void visitSynchronizedStatement( GosuSynchronizedStatement synchronizedStatement )
//  {
//    visitStatement( synchronizedStatement );
//  }
//
//  public void visitVariable( IGosuVariable variable )
//  {
//    visitElement( variable );
//  }
//

}
