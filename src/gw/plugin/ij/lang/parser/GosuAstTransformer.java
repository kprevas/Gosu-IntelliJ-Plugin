package gw.plugin.ij.lang.parser;

import com.intellij.lang.ASTFactory;
import com.intellij.lang.ASTNode;
import com.intellij.psi.impl.source.tree.CompositeElement;
import com.intellij.psi.impl.source.tree.LazyParseableElement;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.util.containers.SortedList;
import gw.lang.parser.IDynamicFunctionSymbol;
import gw.lang.parser.IParseTree;
import gw.lang.parser.IParsedElement;
import gw.lang.parser.IProgramClassFunctionSymbol;
import gw.lang.parser.ISourceCodeTokenizer;
import gw.lang.parser.IStatement;
import gw.lang.parser.IToken;
import gw.lang.parser.expressions.*;
import gw.lang.parser.statements.IArrayAssignmentStatement;
import gw.lang.parser.statements.IAssignmentStatement;
import gw.lang.parser.statements.IBeanMethodCallStatement;
import gw.lang.parser.statements.IBlockInvocationStatement;
import gw.lang.parser.statements.IBreakStatement;
import gw.lang.parser.statements.IClassDeclaration;
import gw.lang.parser.statements.IClassFileStatement;
import gw.lang.parser.statements.IClassStatement;
import gw.lang.parser.statements.IClasspathStatement;
import gw.lang.parser.statements.IContinueStatement;
import gw.lang.parser.statements.IDoWhileStatement;
import gw.lang.parser.statements.IEvalStatement;
import gw.lang.parser.statements.IForEachStatement;
import gw.lang.parser.statements.IFunctionStatement;
import gw.lang.parser.statements.IIfStatement;
import gw.lang.parser.statements.IInterfacesClause;
import gw.lang.parser.statements.IMapAssignmentStatement;
import gw.lang.parser.statements.IMemberAssignmentStatement;
import gw.lang.parser.statements.IMethodCallStatement;
import gw.lang.parser.statements.INamespaceStatement;
import gw.lang.parser.statements.INoOpStatement;
import gw.lang.parser.statements.INotAStatement;
import gw.lang.parser.statements.IPropertyStatement;
import gw.lang.parser.statements.IReturnStatement;
import gw.lang.parser.statements.IStatementList;
import gw.lang.parser.statements.ISuperTypeClause;
import gw.lang.parser.statements.ISwitchStatement;
import gw.lang.parser.statements.ISyntheticFunctionStatement;
import gw.lang.parser.statements.ISyntheticMemberAccessStatement;
import gw.lang.parser.statements.IThrowStatement;
import gw.lang.parser.statements.ITryCatchFinallyStatement;
import gw.lang.parser.statements.IUsesStatement;
import gw.lang.parser.statements.IUsesStatementList;
import gw.lang.parser.statements.IUsingStatement;
import gw.lang.parser.statements.IWhileStatement;
import gw.lang.reflect.gs.IGosuClass;
import gw.lang.reflect.gs.IGosuProgram;
import gw.plugin.ij.lang.GosuTokenTypes;
import gw.plugin.ij.lang.psi.impl.expressions.GosuIdentifierImpl;

import java.util.Comparator;
import java.util.List;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuAstTransformer
{
  private static final GosuAstTransformer INSTANCE = new GosuAstTransformer();

  public static GosuAstTransformer instance()
  {
    return INSTANCE;
  }

  public ASTNode transform( ASTNode chameleon, IGosuClass gsClass )
  {
    LazyParseableElement rootFileNode = ASTFactory.lazy( (IFileElementType)chameleon.getElementType(), null );
    gsClass.isValid();
    IClassFileStatement cfs = gsClass.getClassStatement().getClassFileStatement();
    makeAstTree( rootFileNode, cfs );
    return rootFileNode;
  }

  public ASTNode transform( ASTNode chameleon, IGosuProgram prog )
  {
    LazyParseableElement rootFileNode = ASTFactory.lazy( (IFileElementType)chameleon.getElementType(), null );
    prog.isValid();
    IClassStatement cs = prog.getClassStatement();
    addTokens( cs, rootFileNode, null );

    IParsedElement after = null;
    for( Object toplevelThing : getOrderedTopLevelStatements( prog ) )
    {
      if( toplevelThing instanceof IParsedElement )
      {
        IParsedElement topLevelStmt = (IParsedElement)toplevelThing;
        rootFileNode.rawAddChildren( makeAstTree( rootFileNode, topLevelStmt ) );
        addTokens( cs, rootFileNode, topLevelStmt.getLocation() );
        after = topLevelStmt;
      }
      else
      {
        IToken token = (IToken)toplevelThing;
        addToken( rootFileNode, after == null ? null : after.getLocation(), token );
      }
    }

//    IParseTree parseTree = cfs.getLocation();
//    addTokens( cfs, rootFileNode, null );
//    for( IParseTree child : parseTree.getChildren() )
//    {
//      rootFileNode.rawAddChildren( makeAstTree( child ) );
//      addTokens( cfs, rootFileNode, child );
//    }

    return rootFileNode;
  }

  private List getOrderedTopLevelStatements( IGosuProgram gsProg )
  {
    @SuppressWarnings({"unchecked"})
    List elems = new SortedList(
      new Comparator()
      {
        public int compare( Object p1, Object p2 )
        {
          int iP1;
          if( p1 instanceof IParsedElement )
          {
            iP1 = ((IParsedElement)p1).getLocation().getOffset();
          }
          else
          {
            iP1 = ((IToken)p1).getTokenStart();
          }

          int iP2;
          if( p2 instanceof IParsedElement )
          {
            iP2 = ((IParsedElement)p2).getLocation().getOffset();
          }
          else
          {
            iP2 = ((IToken)p2).getTokenStart();
          }

          return iP1 - iP2;
        }
      } );
    for( IDynamicFunctionSymbol dfs : gsProg.getMemberFunctions() )
    {
      if( dfs instanceof IProgramClassFunctionSymbol )
      {
        if( dfs.getDisplayName().equals( "evaluate" ) )
        {
          IStatement body = (IStatement)dfs.getValueDirectly();
          if( body instanceof IStatementList )
          {
            for( IStatement stmt : ((IStatementList)body).getStatements() )
            {
              if( (!(stmt instanceof IReturnStatement) || stmt.getLocation() != null) && !(stmt instanceof INoOpStatement) )
              {
                elems.add( stmt );
              }
            }
            for( IToken token : body.getTokens() )
            {
              elems.add( token );
            }
          }
          else
          {
            elems.add( body );
          }
        }
      }
      else
      {
        elems.add( dfs.getDeclFunctionStmt() );
      }
    }
    for( IParseTree child : gsProg.getClassStatement().getLocation().getChildren() )
    {
      if( child.getParsedElement() instanceof IClassStatement ||
          child.getParsedElement() instanceof IUsesStatementList )
      {
        elems.add( child.getParsedElement() );
      }
    }

    return elems;
  }


//  private void transformTopLevel( LazyParseableElement rootFileNode, IStatement cfs )
//  {
//    IParseTree parseTree = cfs.getLocation();
//    addTokens( cfs, rootFileNode, null );
//    for( IParseTree child : parseTree.getChildren() )
//    {
//      rootFileNode.rawAddChildren( makeAstTree( child ) );
//      addTokens( cfs, rootFileNode, child );
//    }
//  }
//
//  private CompositeElement makeAstTree( IParseTree parseTree )
//  {
//    IParsedElement pe = parseTree.getParsedElement();
//    CompositeElement parent = makeAst( pe );
//    addTokens( pe, parent, null );
//    for( IParseTree child : parseTree.getChildren() )
//    {
//      if( pe instanceof IPropertyStatement )
//      {
//        pe = child.getParsedElement();
//        addTokens( pe, parent, null );
//        for( IParseTree c : child.getChildren() )
//        {
//          parent.rawAddChildren( makeAstTree( c ) );
//          addTokens( pe, parent, c );
//        }
//      }
//      else
//      {
//        parent.rawAddChildren( makeAstTree( child ) );
//        addTokens( pe, parent, child );
//      }
//    }
//    return parent;
//  }
//
  private CompositeElement makeAstTree( CompositeElement astParent, IParsedElement peChild )
  {
    CompositeElement astParentNew = makeAst( peChild );
    CompositeElement result = astParentNew;
    if( astParentNew == null )
    {
      astParentNew = astParent;
    }
    addTokens( peChild, astParentNew, null );
    for( IParseTree child : peChild.getLocation().getChildren() )
    {
      CompositeElement childNode = makeAstTree( astParentNew, child.getParsedElement() );
      if( childNode != null )
      {
        astParentNew.rawAddChildren( childNode );
      }
      addTokens( peChild, astParentNew, child );
    }
    return result;
  }

  private CompositeElement makeAst( IParsedElement pe )
  {
    CompositeElement node;
    if( pe instanceof INamespaceStatement )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_NamespaceStatement, pe );
    }
    else if( pe instanceof IClassFileStatement )
    {
      node = null;
    }
    else if( pe instanceof IClassStatement )
    {
      if( isProgram( pe ) )
      {
        return null;
      }
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_ClassStatement, pe );
    }
    else if( pe instanceof IClassDeclaration )
    {
      if( isProgram( pe ) )
      {
        return null;
      }
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_ClassDeclaration, pe );
    }
    else if( pe instanceof ISuperTypeClause )
    {
      if( isProgram( pe ) )
      {
        return null;
      }
      node = new GosuCompositeElement<ISuperTypeClause>( GosuElementTypes.EXTENDS_CLAUSE, (ISuperTypeClause)pe );
    }
    else if( pe instanceof IInterfacesClause )
    {
      if( isProgram( pe ) )
      {
        return null;
      }
      node = new GosuCompositeElement<IInterfacesClause>( GosuElementTypes.IMPLEMENTS_CLAUSE, (IInterfacesClause)pe );
    }
    else if( pe instanceof IFunctionStatement )
    {
      if( isProgram( pe ) &&
          ((IFunctionStatement)pe).getDynamicFunctionSymbol() instanceof IProgramClassFunctionSymbol )
      {
        return null;
      }

      node = new GosuCompositeElement<IFunctionStatement>( GosuElementTypes.METHOD_DEFINITION, (IFunctionStatement)pe );
    }
    else if( pe instanceof IPropertyStatement )
    {
      node = null;
    }
    else if( pe instanceof IParameterListClause )
    {
      node = new GosuCompositeElement<IParameterListClause>( GosuElementTypes.PARAMETERS_LIST, (IParameterListClause)pe );
    }
    else if( pe instanceof INameInDeclaration )
    {
      node = new GosuCompositeElement<INameInDeclaration>( GosuElementTypes.ELEM_TYPE_NameInDeclaration, (INameInDeclaration)pe );
    }
    else if( pe instanceof IStatementList )
    {
      if( isProgram( pe ) && pe.getParent() instanceof IFunctionStatement && ((IFunctionStatement)pe.getParent()).getDynamicFunctionSymbol().getDisplayName().equals( "evaluate" ) )
      {
        return null;
      }

      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_StatementList, pe );
    }
    else if( pe instanceof IVarStatement )
    {
      IVarStatement stmt = (IVarStatement)pe;
      if( stmt.isFieldDeclaration() )
      {
        node = new GosuCompositeElement<IVarStatement>( GosuElementTypes.FIELD, stmt );
      }
      else
      {
        node = new GosuCompositeElement<IVarStatement>( GosuElementTypes.ELEM_TYPE_VarStatement, stmt );
      }
    }
    else if( pe instanceof IAssignmentStatement )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_AssignmentStatement, pe );
    }
    else if( pe instanceof IMemberAssignmentStatement )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_MemberAssignmentStatement, pe );
    }
    else if( pe instanceof IArrayAssignmentStatement )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_ArrayAssignmentStatement, pe );
    }
    else if( pe instanceof IMapAssignmentStatement )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_MapAssignmentStatement, pe );
    }
    else if( pe instanceof IMethodCallStatement )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_MethodCallStatement, pe );
    }
    else if( pe instanceof IBlockInvocationStatement )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_BlockInvocationStatement, pe );
    }
    else if( pe instanceof IBeanMethodCallStatement )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_BeanMethodCallStatement, pe );
    }
    else if( pe instanceof IReturnStatement )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_ReturnStatement, pe );
    }
    else if( pe instanceof IBreakStatement )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_BreakStatement, pe );
    }
    else if( pe instanceof IContinueStatement )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_ContinueStatement, pe );
    }
    else if( pe instanceof IIfStatement )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_IfStatement, pe );
    }
    else if( pe instanceof IWhileStatement )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_WhileStatement, pe );
    }
    else if( pe instanceof IDoWhileStatement )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_DoWhileStatement, pe );
    }
    else if( pe instanceof IForEachStatement )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_ForEachStatement, pe );
    }
    else if( pe instanceof ISwitchStatement )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_SwitchStatement, pe );
    }
    else if( pe instanceof ITryCatchFinallyStatement )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_TryCatchFinallyStatement, pe );
    }
    else if( pe instanceof IThrowStatement )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_ThrowStatement, pe );
    }
    else if( pe instanceof IUsingStatement )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_UsingStatement, pe );
    }
    else if( pe instanceof IEvalStatement )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_EvalStatement, pe );
    }
    else if( pe instanceof ISyntheticFunctionStatement )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_SyntheticFunctionStatement, pe );
    }
    else if( pe instanceof ISyntheticMemberAccessStatement )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_SyntheticMemberAccessStatement, pe );
    }
    else if( pe instanceof INoOpStatement )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_NoOpStatement, pe );
    }
    else if( pe instanceof IClasspathStatement )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_ClasspathStatement, pe );
    }
    else if( pe instanceof IUsesStatementList )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_UsesStatementList, pe );
    }
    else if( pe instanceof IUsesStatement )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_UsesStatement, pe );
    }
    else if( pe instanceof IIdentifierExpression )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_IIdentifierExpression, pe );
    }
    else if( pe instanceof ITypeAsExpression )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_ITypeAsExpression, pe );
    }
    else if( pe instanceof ITypeIsExpression )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_TypeIsExpression, pe );
    }
    else if( pe instanceof ITypeOfExpression )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_ITypeOfExpression, pe );
    }
    else if( pe instanceof IStaticTypeOfExpression )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_StaticTypeOfExpression, pe );
    }
    else if( pe instanceof IStringLiteralExpression )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_StringLiteral, pe );
    }
    else if( pe instanceof ICharLiteralExpression )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_CharLiteral, pe );
    }
    else if( pe instanceof INumericLiteralExpression )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_NumericLiteral, pe );
    }
    else if( pe instanceof ITypeLiteralExpression )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_TypeLiteral, pe );
    }
    else if( pe instanceof ITypeParameterListClause )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_TypeParameterListClause, pe );
    }
    else if( pe instanceof IBooleanLiteralExpression )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_BooleanLiteral, pe );
    }
    else if( pe instanceof IUnaryExpression )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_UnaryExpression, pe );
    }
    else if( pe instanceof IUnaryNotPlusMinusExpression )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_UnaryNotPlusMinusExpression, pe );
    }
    else if( pe instanceof IEqualityExpression )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_EqualityExpression, pe );
    }
    else if( pe instanceof IIdentityExpression )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_IdentityExpression, pe );
    }
    else if( pe instanceof IRelationalExpression )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_RelationalExpression, pe );
    }
    else if( pe instanceof IConditionalOrExpression )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_ConditionalOrExpression, pe );
    }
    else if( pe instanceof IConditionalAndExpression )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_ConditionalAndExpression, pe );
    }
    else if( pe instanceof IAdditiveExpression )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_AdditiveExpression, pe );
    }
    else if( pe instanceof IMultiplicativeExpression )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_MultiplicativeExpression, pe );
    }
    else if( pe instanceof IBitshiftExpression )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_BitshiftExpression, pe );
    }
    else if( pe instanceof IBitwiseOrExpression )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_BitwiseOrExpression, pe );
    }
    else if( pe instanceof IBitwiseXorExpression )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_BitwiseXorExpression, pe );
    }
    else if( pe instanceof IBitwiseAndExpression )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_BitwiseAndExpression, pe );
    }
    else if( pe instanceof IBeanMethodCallExpression )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_BeanMethodCallExpression, pe );
    }
    else if( pe instanceof IMethodCallExpression )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_MethodCallExpression, pe );
    }
    else if( pe instanceof IMemberExpansionExpression )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_MemberExpansionAccess, pe );
    }
    else if( pe instanceof IMemberAccessExpression )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_MemberAccess, pe );
    }
    else if( pe instanceof INewExpression )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_NewExpression, pe );
    }
    else if( pe instanceof IEvalExpression )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_EvalExpression, pe );
    }
    else if( pe instanceof IQueryExpression )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_QueryExpression, pe );
    }
    else if( pe instanceof IConditionalTernaryExpression )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_ConditionalTernaryExpression, pe );
    }
    else if( pe instanceof IArrayAccessExpression )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_ArrayAccess, pe );
    }
    else if( pe instanceof IMapAccessExpression )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_MapAccess, pe );
    }
    else if( pe instanceof IIntervalExpression )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_IntervalExpression, pe );
    }
    else if( pe instanceof IParenthesizedExpression )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_ParenthesizedExpression, pe );
    }
    else if( pe instanceof INullExpression )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_NullExpression, pe );
    }
    else if( pe instanceof IBlockExpression )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_BlockExpression, pe );
    }
    else if( pe instanceof IObjectLiteralExpression )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_ObjectLiteralExpression, pe );
    }
    else if( pe instanceof ITemplateStringLiteral )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_TemplateStringLiteral, pe );
    }
    else if( pe instanceof IExistsExpression )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_ExistsExpression, pe );
    }
    else if( pe instanceof IBlockInvocation )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_BlockInvocation, pe );
    }
    else if( pe instanceof IFeatureLiteralExpression )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_FeatureLiteral, pe );
    }
    else if( pe instanceof IParameterDeclaration )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_ParameterDeclaration, pe );
    }
    else if( pe instanceof ICollectionInitializerExpression )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_CollectionInitializerExpression, pe );
    }
    else if( pe instanceof INotAStatement )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_NotAStatement, pe );
    }
    else if( pe instanceof INotAWordExpression )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_NotAWordExpression, pe );
    }
    else if( pe instanceof ILocalVarDeclaration )
    {
      node = new GosuCompositeElement( GosuElementTypes.ELEM_TYPE_LocalVarDeclaration, pe );
    }
    else
    {
      if( pe == null )
      {
        throw new IllegalStateException( "Null parsed element" );
      }
      throw new UnsupportedOperationException( "AST Transformer not yet implemented for: " + pe.getClass().getName() );
    }

    return node;
  }

  private boolean isProgram( IParsedElement pe )
  {
    return pe.getGosuClass() instanceof IGosuProgram;
  }

  private void addTokens( IParsedElement pe, CompositeElement node, IParseTree after )
  {
    for( IToken token : pe.getTokens() )
    {
      addToken( node, after, token );
    }
  }

  private void addToken( CompositeElement node, IParseTree after, IToken token )
  {
    if( after == token.getAfter() || isAncestor( after, token.getAfter() ) )
    {
      if( token.getType() == ISourceCodeTokenizer.TT_WHITESPACE )
      {
        node.rawAddChildren( ASTFactory.leaf( GosuTokenTypes.TT_WHITESPACE, token.getText() ) );
      }
      else if( token.getType() == ISourceCodeTokenizer.TT_EOF )
      {
        // skip
      }
      else if( token.getType() == ISourceCodeTokenizer.TT_WORD )
      {
        GosuIdentifierImpl ident = new GosuIdentifierImpl( GosuTokenTypes.getTypeFrom( token ), token.getText() );
        node.rawAddChildren( ident );
      }
      else
      {
        node.rawAddChildren( ASTFactory.leaf( GosuTokenTypes.getTypeFrom( token ), token.getText() ) );
      }
    }
  }

  private boolean isAncestor( IParseTree parent, IParseTree child )
  {
    if( child == null )
    {
      return false;
    }

    if( parent == child )
    {
      return true;
    }

    if( child == child.getParent() )
    {
      return false;
    }

    return isAncestor( parent, child.getParent() );
  }
}
