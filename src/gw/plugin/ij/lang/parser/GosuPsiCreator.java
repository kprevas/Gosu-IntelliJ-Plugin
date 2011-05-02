package gw.plugin.ij.lang.parser;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.file.PsiPackageImpl;
import com.intellij.psi.tree.IElementType;
import gw.lang.parser.IExpansionPropertyInfo;
import gw.lang.parser.expressions.IIdentifierExpression;
import gw.lang.parser.expressions.IMemberAccessExpression;
import gw.lang.parser.expressions.IVarStatement;
import gw.lang.reflect.INamespaceType;
import gw.lang.reflect.IPropertyInfo;
import gw.lang.reflect.TypeSystem;
import gw.lang.reflect.gs.IGosuClass;
import gw.lang.reflect.gs.IGosuVarPropertyInfo;
import gw.lang.reflect.java.IJavaFieldPropertyInfo;
import gw.lang.reflect.module.IModule;
import gw.plugin.ij.activator.Activator;
import gw.plugin.ij.lang.psi.impl.expressions.GosuPropertyMemberAccessExpressionImpl;
import gw.plugin.ij.lang.psi.impl.expressions.GosuIdentifierExpressionImpl;
import gw.plugin.ij.lang.psi.impl.expressions.GosuFieldMemberAccessExpressionImpl;
import gw.plugin.ij.lang.psi.impl.expressions.GosuNameInDeclarationImpl;
import gw.plugin.ij.lang.psi.impl.expressions.GosuTypeLiteralImpl;
import gw.plugin.ij.lang.psi.impl.statements.GosuFieldImpl;
import gw.plugin.ij.lang.psi.impl.statements.GosuNotAStatementImpl;
import gw.plugin.ij.lang.psi.impl.statements.GosuPackageDefinitionImpl;
import gw.plugin.ij.lang.psi.impl.statements.GosuStatementListImpl;
import gw.plugin.ij.lang.psi.impl.statements.GosuUsesStatementImpl;
import gw.plugin.ij.lang.psi.impl.statements.GosuUsesStatementListImpl;
import gw.plugin.ij.lang.psi.impl.statements.GosuVariableImpl;
import gw.plugin.ij.lang.psi.impl.statements.params.GosuParameterImpl;
import gw.plugin.ij.lang.psi.impl.statements.params.GosuParameterListImpl;
import gw.plugin.ij.lang.psi.impl.statements.typedef.GosuClassDefinitionImpl;
import gw.plugin.ij.lang.psi.impl.statements.typedef.GosuExtendsClauseImpl;
import gw.plugin.ij.lang.psi.impl.statements.typedef.GosuImplementsClauseImpl;
import gw.plugin.ij.lang.psi.impl.statements.typedef.GosuInterfaceDefinitionImpl;
import gw.plugin.ij.lang.psi.impl.statements.typedef.members.GosuMethodImpl;
import gw.plugin.ij.lang.psi.impl.types.GosuTypeParameterListImpl;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuPsiCreator extends GosuElementTypes
{
  /**
   * Creates Gosu PSI element by given AST node
   *
   * @return Respective PSI element
   */
  public static PsiElement createElement( final ASTNode rawNode )
  {
    GosuCompositeElement node = null;
    if( rawNode instanceof GosuCompositeElement )
    {
      node = (GosuCompositeElement)rawNode;
    }

    IElementType elem = node.getElementType();
    IGosuClass gsClass = node.getParsedElement().getGosuClass();
    IModule mod;
    if( gsClass == null )
    {
      mod = Activator.getUberModule();
    }
    else
    {
      mod = gsClass.getTypeLoader().getModule();
    }
    TypeSystem.getExecutionEnvironment().pushModule( mod );
    try
    {
  //    if( elem instanceof IGosuDocElementType )
  //    {
  //      return GosuDocPsiCreator.createElement( node );
  //    }

  //    //Identifiers & literal
  //    if( elem.equals( LITERAL ) )
  //    {
  //      return new GosuLiteralImpl( node );
  //    }
  //    if( elem.equals( LABEL ) )
  //    {
  //      return new GosuLabelImpl( node );
  //    }
  ////    if (elem.equals(IDENTIFIER)) return new GosuIdentifierImpl(node);
  //    //Lists, maps etc...
  //    if( elem.equals( LIST_OR_MAP ) )
  //    {
  //      return new GosuListOrMapImpl( node );
  //    }
  //    if( elem.equals( ANNOTATION ) )
  //    {
  //      return new GosuAnnotationImpl( node );
  //    }
  //    if( elem.equals( ANNOTATION_ARGUMENTS ) )
  //    {
  //      return new GosuAnnotationArgumentListImpl( node );
  //    }
  //    if( elem.equals( ANNOTATION_ARRAY_INITIALIZER ) )
  //    {
  //      return new GosuAnnotationArrrayInitializerImpl( node );
  //    }
  //    if( elem.equals( ANNOTATION_MEMBER_VALUE_PAIR ) )
  //    {
  //      return new GosuAnnotationNameValuePairImpl( node );
  //    }
  //    if( elem.equals( ANNOTATION_MEMBER_VALUE_PAIRS ) )
  //    {
  //      return new GosuAnnotationNameValuePairsImpl( node );
  //    }
  //
  //    if( elem.equals( DEFAULT_ANNOTATION_VALUE ) )
  //    {
  //      return new GosuDefaultAnnotationValueImpl( node );
  //    }
  //
  //    //throws
  //    if( elem.equals( THROW_CLAUSE ) )
  //    {
  //      return new GosuThrowsClauseImpl( node );
  //    }
  //
  //    // Imports
  //    if( elem.equals( IMPORT_STATEMENT ) )
  //    {
  //      return new GosuImportStatementImpl( node );
  //    }

      // Packaging
      if( elem.equals( ELEM_TYPE_NamespaceStatement ) )
      {
        return new GosuPackageDefinitionImpl( node );
      }

  //    //statements
  //    if( elem.equals( LABELED_STATEMENT ) )
  //    {
  //      return new GosuLabeledStatementImpl( node );
  //    }
  //    if( elem.equals( IF_STATEMENT ) )
  //    {
  //      return new GosuIfStatementImpl( node );
  //    }
  //    if( elem.equals( FOR_STATEMENT ) )
  //    {
  //      return new GosuForStatementImpl( node );
  //    }
  //    if( elem.equals( FOR_IN_CLAUSE ) )
  //    {
  //      return new GosuForInClauseImpl( node );
  //    }
  //    if( elem.equals( FOR_TRADITIONAL_CLAUSE ) )
  //    {
  //      return new GosuTraditionalForClauseImpl( node );
  //    }
  //    if( elem.equals( WHILE_STATEMENT ) )
  //    {
  //      return new GosuWhileStatementImpl( node );
  //    }
  //    if( elem.equals( TRY_BLOCK_STATEMENT ) )
  //    {
  //      return new GosuTryCatchStatementImpl( node );
  //    }
  //    if( elem.equals( CATCH_CLAUSE ) )
  //    {
  //      return new GosuCatchClauseImpl( node );
  //    }
  //    if( elem.equals( FINALLY_CLAUSE ) )
  //    {
  //      return new GosuFinallyClauseImpl( node );
  //    }
  //    if( elem.equals( SYNCHRONIZED_STATEMENT ) )
  //    {
  //      return new GosuSynchronizedStatementImpl( node );
  //    }
  //    if( elem.equals( SWITCH_STATEMENT ) )
  //    {
  //      return new GosuSwitchStatementImpl( node );
  //    }
  //    if( elem.equals( CASE_LABEL ) )
  //    {
  //      return new GosuCaseLabelImpl( node );
  //    }
  //    if( elem.equals( CASE_SECTION ) )
  //    {
  //      return new GosuCaseSectionImpl( node );
  //    }
  //    if( elem.equals( VARIABLE_DEFINITION ) || elem.equals( VARIABLE_DEFINITION_ERROR ) )
  //    {
  //      return new GosuVariableDeclarationImpl( node );
  //    }
  //    if( elem.equals( MULTIPLE_VARIABLE_DEFINITION ) )
  //    {
  //      return new GosuMultipleVariableDeclarationImpl( node );
  //    }
  //    if( elem.equals( TUPLE_DECLARATION ) || elem.equals( TUPLE_ERROR ) )
  //    {
  //      return new GosuTupleDeclarationImpl( node );
  //    }
  //    if( elem.equals( TUPLE_EXPRESSION ) )
  //    {
  //      return new GosuTupleExpressionImpl( node );
  //    }
  //    if( elem.equals( VARIABLE ) )
  //    {
  //      return new GosuVariableImpl( node );
  //    }

//    // Imports
    if( elem.equals( ELEM_TYPE_UsesStatement ) )
    {
      return new GosuUsesStatementImpl( node );
    }
    if( elem.equals( ELEM_TYPE_UsesStatementList ) )
    {
      return new GosuUsesStatementListImpl( node );
    }
      if( elem.equals( FIELD ) )
      {
        return new GosuFieldImpl( node );
      }
  //    if( elem.equals( CLASS_INITIALIZER ) )
  //    {
  //      return new GosuClassInitializerImpl( node );
  //    }

      //type definitions
      if( elem.equals( ELEM_TYPE_ClassStatement ) )
      {
        return new GosuClassDefinitionImpl( node );
      }
      if( elem.equals( INTERFACE_DEFINITION ) )
      {
        return new GosuInterfaceDefinitionImpl( node );
      }
  //    if( elem.equals( ENUM_DEFINITION ) )
  //    {
  //      return new GosuEnumTypeDefinitionImpl( node );
  //    }
  //    if( elem.equals( ANNOTATION_DEFINITION ) )
  //    {
  //      return new GosuAnnotationTypeDefinitionImpl( node );
  //    }
  //    if( elem.equals( ANNOTATION_METHOD ) )
  //    {
  //      return new GosuAnnotationMethodImpl( node );
  //    }
  //
  //    if( elem.equals( REFERENCE_ELEMENT ) )
  //    {
  //      return new GosuCodeReferenceElementImpl( node );
  //    }
  //    if( elem.equals( CLASS_TYPE_ELEMENT ) )
  //    {
  //      return new GosuClassTypeElementImpl( node );
  //    }

      //clauses
      if( elem.equals( IMPLEMENTS_CLAUSE ) )
      {
        return new GosuImplementsClauseImpl( node );
      }
      if( elem.equals( EXTENDS_CLAUSE ) )
      {
        return new GosuExtendsClauseImpl( node );
      }

      //bodies
  //    if( elem.equals( ENUM_BODY ) )
  //    {
  //      return new GosuEnumDefinitionBodyImpl( node );
  //    }
  //    if( elem.equals( CLOSABLE_BLOCK ) )
  //    {
  //      return new GosuClosableBlockImpl( node );
  //    }
  //    if( elem.equals( OPEN_BLOCK ) )
  //    {
  //      return new GosuOpenBlockImpl( node );
  //    }
  //    if( elem.equals( BLOCK_STATEMENT ) )
  //    {
  //      return new GosuBlockStatementImpl( node );
  //    }
  //    if( elem.equals( EXPLICIT_CONSTRUCTOR ) )
  //    {
  //      return new GosuConstructorInvocationImpl( node );
  //    }

  //    //enum
  //    if( elem.equals( ENUM_CONSTANTS ) )
  //    {
  //      return new GosuEnumConstantListImpl( node );
  //    }
  //    if( elem.equals( ENUM_CONSTANT ) )
  //    {
  //      return new GosuEnumConstantImpl( node );
  //    }
  //
  //    //members
  //    if( elem.equals( CONSTRUCTOR_DEFINITION ) )
  //    {
  //      return new GosuConstructorImpl( node );
  //    }
      if( elem.equals( METHOD_DEFINITION ) )
      {
        return new GosuMethodImpl( node );
      }
      if( elem.equals( ELEM_TYPE_NameInDeclaration ) )
      {
        return new GosuNameInDeclarationImpl( node );
      }

      //parameters
      if( elem.equals( PARAMETERS_LIST ) )
      {
        return new GosuParameterListImpl( node );
      }
      if( elem.equals( ELEM_TYPE_ParameterDeclaration ) )
      {
        return new GosuParameterImpl( node );
      }
  //
  //    //type parameters
  //    if( elem.equals( TYPE_ARGUMENT ) )
  //    {
  //      return new GosuWildcardTypeArgumentImpl( node );
  //    }
  //    if( elem.equals( TYPE_ARGUMENTS ) )
  //    {
  //      return new GosuTypeArgumentListImpl( node );
  //    }


      if( elem.equals( TYPE_PARAMETER_LIST ) )
      {
        return new GosuTypeParameterListImpl( node );
      }
  //    if( elem.equals( TYPE_PARAMETER ) )
  //    {
  //      return new GosuTypeParameterImpl( node );
  //    }
  //    if( elem.equals( TYPE_PARAMETER_EXTENDS_BOUND_LIST ) )
  //    {
  //      return new GosuTypeParameterParameterExtendsListImpl( node );
  //    }
  //
  //    //Branch statements
  //    if( elem.equals( RETURN_STATEMENT ) )
  //    {
  //      return new GosuReturnStatementImpl( node );
  //    }
  //    if( elem.equals( THROW_STATEMENT ) )
  //    {
  //      return new GosuThrowStatementImpl( node );
  //    }
  //    if( elem.equals( ASSERT_STATEMENT ) )
  //    {
  //      return new GosuAssertStatementImpl( node );
  //    }
  //    if( elem.equals( BREAK_STATEMENT ) )
  //    {
  //      return new GosuBreakStatementImpl( node );
  //    }
  //    if( elem.equals( CONTINUE_STATEMENT ) )
  //    {
  //      return new GosuContinueStatementImpl( node );
  //    }
  //
  //    //expressions
  //    if( elem.equals( CALL_EXPRESSION ) )
  //    {
  //      return new GosuApplicationStatementImpl( node );
  //    }
  //    if( elem.equals( COMMAND_ARGUMENTS ) )
  //    {
  //      return new GosuCommandArgumentListImpl( node );
  //    }
  //    if( elem.equals( CONDITIONAL_EXPRESSION ) )
  //    {
  //      return new GosuConditionalExprImpl( node );
  //    }
  //    if( elem.equals( ELVIS_EXPRESSION ) )
  //    {
  //      return new GosuElvisExprImpl( node );
  //    }
  //    if( elem.equals( ASSIGNMENT_EXPRESSION ) )
  //    {
  //      return new GosuAssignmentExpressionImpl( node );
  //    }
  //    if( elem.equals( LOGICAL_OR_EXPRESSION ) )
  //    {
  //      return new GosuLogicalOrExpressionImpl( node );
  //    }
  //    if( elem.equals( LOGICAL_AND_EXPRESSION ) )
  //    {
  //      return new GosuLogicalAndExpressionImpl( node );
  //    }
  //    if( elem.equals( EXCLUSIVE_OR_EXPRESSION ) )
  //    {
  //      return new GosuExclusiveOrExpressionImpl( node );
  //    }
  //    if( elem.equals( INCLUSIVE_OR_EXPRESSION ) )
  //    {
  //      return new GosuInclusiveOrExpressionImpl( node );
  //    }
  //    if( elem.equals( AND_EXPRESSION ) )
  //    {
  //      return new GosuAndExpressionImpl( node );
  //    }
  //    if( elem.equals( REGEX_MATCH_EXPRESSION ) )
  //    {
  //      return new GosuRegexMatchExpressionImpl( node );
  //    }
  //    if( elem.equals( REGEX_FIND_EXPRESSION ) )
  //    {
  //      return new GosuRegexFindExpressionImpl( node );
  //    }
  //    if( elem.equals( EQUALITY_EXPRESSION ) )
  //    {
  //      return new GosuEqualityExpressionImpl( node );
  //    }
  //    if( elem.equals( RELATIONAL_EXPRESSION ) )
  //    {
  //      return new GosuRelationalExpressionImpl( node );
  //    }
  //    if( elem.equals( SHIFT_EXPRESSION ) )
  //    {
  //      return new GosuShiftExpressionImpl( node );
  //    }
  //    if( elem.equals( RANGE_EXPRESSION ) )
  //    {
  //      return new GosuRangeExpressionImpl( node );
  //    }
  //    if( elem.equals( COMPOSITE_SHIFT_SIGN ) )
  //    {
  //      return new GosuOperationSignImpl( node );
  //    }
  //    if( elem.equals( ADDITIVE_EXPRESSION ) )
  //    {
  //      return new GosuAdditiveExpressionImpl( node );
  //    }
  //    if( elem.equals( MULTIPLICATIVE_EXPRESSION ) )
  //    {
  //      return new GosuMultiplicativeExpressionImpl( node );
  //    }
  //    if( elem.equals( POWER_EXPRESSION ) )
  //    {
  //      return new GosuPowerExpressionImpl( node );
  //    }
  //    if( elem.equals( POWER_EXPRESSION_SIMPLE ) )
  //    {
  //      return new GosuPowerExpressionImpl( node );
  //    }
  //    if( elem.equals( UNARY_EXPRESSION ) )
  //    {
  //      return new GosuUnaryExpressionImpl( node );
  //    }
  //    if( elem.equals( POSTFIX_EXPRESSION ) )
  //    {
  //      return new GosuPostfixExprImpl( node );
  //    }
  //    if( elem.equals( CAST_EXPRESSION ) )
  //    {
  //      return new GosuTypeCastExpressionImpl( node );
  //    }
  //    if( elem.equals( SAFE_CAST_EXPRESSION ) )
  //    {
  //      return new GosuSafeCastExpressionImpl( node );
  //    }
  //    if( elem.equals( INSTANCEOF_EXPRESSION ) )
  //    {
  //      return new GosuInstanceofExpressionImpl( node );
  //    }
  //    if( elem.equals( BUILT_IN_TYPE_EXPRESSION ) )
  //    {
  //      return new GosuBuiltinTypeClassExpressionImpl( node );
  //    }
  //    if( elem.equals( ARRAY_TYPE ) )
  //    {
  //      return new GosuArrayTypeElementImpl( node );
  //    }
  //    if( elem.equals( BUILT_IN_TYPE ) )
  //    {
  //      return new GosuBuiltInTypeElementImpl( node );
  //    }
  //    if( elem.equals( GSTRING ) )
  //    {
  //      return new GosuStringImpl( node );
  //    }
  //    if( elem.equals( REGEX ) )
  //    {
  //      return new GosuRegexImpl( node );
  //    }
  //    if( elem.equals( GSTRING_INJECTION ) )
  //    {
  //      return new GosuStringInjectionImpl( node );
  //    }
  //    if( elem.equals( REFERENCE_EXPRESSION ) )
  //    {
  //      return new GosuReferenceExpressionImpl( node );
  //    }
  //    if( elem.equals( THIS_REFERENCE_EXPRESSION ) )
  //    {
  //      return new GosuThisReferenceExpressionImpl( node );
  //    }
  //    if( elem.equals( SUPER_REFERENCE_EXPRESSION ) )
  //    {
  //      return new GosuSuperReferenceExpressionImpl( node );
  //    }
  //    if( elem.equals( PARENTHESIZED_EXPRESSION ) )
  //    {
  //      return new GosuParenthesizedExpressionImpl( node );
  //    }
  //    if( elem.equals( NEW_EXPRESSION ) )
  //    {
  //      return new GosuNewExpressionImpl( node );
  //    }
  //    if( elem.equals( ANONYMOUS_CLASS_DEFINITION ) )
  //    {
  //      return new GosuAnonymousClassDefinitionImpl( node );
  //    }
  //    if( elem.equals( ARRAY_DECLARATOR ) )
  //    {
  //      return new GosuArrayDeclarationImpl( node );
  //    }
  //
  //    //Paths
  //    if( elem.equals( PATH_PROPERTY ) )
  //    {
  //      return new GosuPropertySelectorImpl( node );
  //    }
  //    if( elem.equals( PATH_PROPERTY_REFERENCE ) )
  //    {
  //      return new GosuPropertySelectionImpl( node );
  //    }
  //    if( elem.equals( PATH_METHOD_CALL ) )
  //    {
  //      return new GosuMethodCallExpressionImpl( node );
  //    }
  //    if( elem.equals( PATH_INDEX_PROPERTY ) )
  //    {
  //      return new GosuIndexPropertyImpl( node );
  //    }
  //
  //    // Arguments
  //    if( elem.equals( ARGUMENTS ) )
  //    {
  //      return new GosuArgumentListImpl( node );
  //    }
  //    if( elem.equals( ARGUMENT ) )
  //    {
  //      return new GosuNamedArgumentImpl( node );
  //    }
  //    if( elem.equals( ARGUMENT_LABEL ) )
  //    {
  //      return new GosuArgumentLabelImpl( node );
  //    }
  //
  //    if( elem.equals( BALANCED_BRACKETS ) )
  //    {
  //      return new GosuBalancedBracketsImpl( node );
  //    }

      if( elem.equals( ELEM_TYPE_TypeLiteral ) )
      {
        return new GosuTypeLiteralImpl( node );
      }
      if( elem.equals( ELEM_TYPE_TypeParameterListClause ) )
      {
        return new GosuTypeParameterListImpl( node );
      }
      if( elem.equals( ELEM_TYPE_VarStatement ) )
      {
        return new GosuVariableImpl<IVarStatement>( node );
      }
      if( elem.equals( ELEM_TYPE_IIdentifierExpression ) )
      {
        if( ((IIdentifierExpression)node.getParsedElement()).getType() instanceof INamespaceType )
        {
           new GosuUnhandledPsiElement( node );
          //## todo:
          //return new GosuPackageExpressionImpl( node );
        }
        else
        {
          return new GosuIdentifierExpressionImpl( node );
        }
      }
      if( elem.equals( ELEM_TYPE_MemberAccess ) )
      {
        IMemberAccessExpression expr = (IMemberAccessExpression)node.getParsedElement();
        if( expr.getType() instanceof INamespaceType )
        {
          return new GosuFieldMemberAccessExpressionImpl( node, expr.getMemberName() );
        }
        try
        {
          IPropertyInfo pi = expr.getPropertyInfo();
          if( pi instanceof IExpansionPropertyInfo )
          {
            throw new UnsupportedOperationException( "Men at work" );

            //## todo:
            //MemberExpansionAccess expr = MemberExpansionAccess.wrap( _expr() );
            // node.setParsedElement( expr );
            //return new GosuMemberExpansionAccessExpressionImpl( node );
          }
          else if( pi instanceof IJavaFieldPropertyInfo ||
                   pi instanceof IGosuVarPropertyInfo )
          {
            String strVarName;
            if( pi instanceof IGosuVarPropertyInfo )
            {
              strVarName = pi.getName();
            }
            else
            {
              strVarName = expr.getMemberName();
            }
            return new GosuFieldMemberAccessExpressionImpl( node, strVarName );
          }
          else
          {
            return new GosuPropertyMemberAccessExpressionImpl( node );
          }
        }
        catch( RuntimeException e )
        {
          //## todo: this is a hack for now, probably should not be calling getPropertyInfo() in here, all the info we need should be available statically on the MemberAccess, what asshole wrote that code anyway?
          //## maybe check for ParseException "No property descriptor found for propert xxx..." and if there don't call getPropertyInfo()?
          return new GosuFieldMemberAccessExpressionImpl( node, expr.getMemberName() );
        }
      }
      if( elem.equals( ELEM_TYPE_ClassDeclaration ) )
      {
        return new GosuNameInDeclarationImpl( node );
      }
      if( elem.equals( ELEM_TYPE_StatementList ) )
      {
        return new GosuStatementListImpl( node );
      }

      if( elem.equals( ELEM_TYPE_NotAStatement ) )
      {
        return new GosuNotAStatementImpl( node );
      }

      return new GosuUnhandledPsiElement( node );
    }
    finally
    {
      TypeSystem.getExecutionEnvironment().popModule( mod );
    }
  }

}
