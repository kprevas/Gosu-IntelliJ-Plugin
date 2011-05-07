package gw.plugin.ij.lang.psi.impl.expressions;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiType;
import gw.lang.parser.IBlockClass;
import gw.lang.parser.ICapturedSymbol;
import gw.lang.parser.IDynamicPropertySymbol;
import gw.lang.parser.IDynamicSymbol;
import gw.lang.parser.IReducedSymbol;
import gw.lang.parser.ISymbol;
import gw.lang.parser.Keyword;
import gw.lang.parser.expressions.IIdentifierExpression;
import gw.lang.reflect.IType;
import gw.lang.reflect.gs.ICompilableType;
import gw.lang.reflect.gs.IGosuClass;
import gw.lang.reflect.gs.IGosuProgram;
import gw.plugin.ij.lang.parser.GosuCompositeElement;
import gw.plugin.ij.lang.psi.api.types.GosuCodeReferenceElement;
import gw.plugin.ij.lang.psi.api.types.GosuTypeArgumentList;
import gw.plugin.ij.lang.psi.api.types.GosuTypeElement;
import gw.plugin.ij.lang.psi.util.TypesUtil;


/**
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuIdentifierExpressionImpl extends GosuReferenceExpressionImpl<IIdentifierExpression> implements GosuCodeReferenceElement, GosuTypeElement
{
  public GosuIdentifierExpressionImpl( GosuCompositeElement<IIdentifierExpression> node )
  {
    super( node );
  }

  public PsiElement getReferenceNameElement()
  {
    // Return the psi identifier *token*

    PsiElement child = this;
    while( child.getFirstChild() != null )
    {
      child = child.getFirstChild();
    }
    if( !(child instanceof PsiIdentifier) )
    {
      throw new IllegalStateException( "Expected PsiIdentifier, but found: " + child.getClass().getSimpleName() );
    }
    return child;
  }

  @Override
  public PsiType getType()
  {
    return TypesUtil.createType( getTypeName(), this );
  }

  private String getTypeName()
  {
    if( getTypeReferenced().isParameterizedType() )
    {
      return getTypeReferenced().getGenericType().getName();
    }
    return getTypeReferenced().getName();
  }

  private IType getTypeReferenced()
  {
    return getParsedElement().getType();
  }

  @Override
  public GosuCodeReferenceElement getQualifier()
  {
    return null;
  }

  @Override
  public void setQualifier( GosuCodeReferenceElement newQualifier )
  {
    throw new UnsupportedOperationException( "Men at work" );
  }

  @Override
  public PsiType[] getTypeArguments()
  {
    return new PsiType[0];
  }

  public GosuTypeArgumentList getTypeArgumentList()
  {
    return null;
  }

  @Override
  public PsiElement resolve()
  {
    ISymbol symbol = getParsedElement().getSymbol();
    IGosuClass gsClass = getParsedElement().getGosuClass();
    if( (Keyword.KW_this.equals( symbol.getName() ) ||
         Keyword.KW_super.equals( symbol.getName() )) &&
        // 'this' must be an external symbol when in a program e.g., studio debugger expression
        (!(gsClass instanceof IGosuProgram) || gsClass.isAnonymous()) )
    {
      if( gsClass instanceof IBlockClass )
      {
        while( gsClass instanceof IBlockClass )
        {
          gsClass = (IGosuClass)gsClass.getEnclosingType();
        }
      }
      return resolveType( gsClass.getName() );
    }
    else if( Keyword.KW_outer.equals( symbol.getName() ) )
    {
      // 'outer'
      return resolveOuter( gsClass );
    }
    else
    {
      return resolveSymbol( symbol, gsClass );
    }
  }

  public PsiElement resolveSymbol( IReducedSymbol symbol, IGosuClass gsClass )
  {
    IType type = symbol.getType();
    Class symClass = symbol.getClass();
    if( gsClass.getExternalSymbol( getReferenceName() ) != null )
    {
      // Currently we do not attempt to link to decl source of external symbols

      return null;
    }
    if( IDynamicSymbol.class.isAssignableFrom( symClass ) )
    {
      // Instance or Static field

      if( !symbol.isStatic() )
      {
        if( isMemberOnEnclosingType( symbol, gsClass ) )
        {
          // Instance field from 'outer'
          return resolveField( getReferenceName(), (IGosuClass)symbol.getGosuClass().getEnclosingType() );
        }
        else
        {
          // Instance field from 'this'
          return resolveField( getReferenceName(), symbol.getGosuClass() );
        }
      }
      else
      {
        // Static field
        return resolveField( getReferenceName(), symbol.getGosuClass() );
      }
    }
    else if( ICapturedSymbol.class.isAssignableFrom( symClass ) )
    {
      return resolveCapture( getReferenceName(), gsClass );
    }
    else if( symbol.getIndex() >= 0 )
    {
      // Local var

      if( symbol.isValueBoxed() )
      {
        // Local var is captured in an anonymous inner class.
        // Symbol's value maintained as a one elem array of symbol's type.
        return resolveCapture( getReferenceName(), gsClass );
      }
      else
      {
        // Simple local var
        return resloveLocal( getReferenceName() );
      }
    }
    else if( IDynamicPropertySymbol.class.isAssignableFrom( symClass ) )
    {
      throw new UnsupportedOperationException( "Men at work" );
    }
    return null;
  }

  private PsiElement resloveLocal( String strName )
  {
    return resolveField( strName );
  }

  private PsiElement resolveCapture( String strName, IGosuClass gsClass )
  {
    return resolveField( strName, gsClass );
  }

  private PsiElement resolveField( String strField, ICompilableType gsClass )
  {
    return resolveField( strField, gsClass );
  }

  protected PsiElement resolveOuter( IGosuClass gsClass )
  {
    while( gsClass instanceof IBlockClass )
    {
      gsClass = (IGosuClass)gsClass.getEnclosingType();
    }
    return resolveType( gsClass.getEnclosingType().getName() );
  }

  protected boolean isMemberOnEnclosingType( IReducedSymbol symbol, IGosuClass gsClass )
  {
    if( !gsClass.isStatic() && gsClass.getEnclosingType() != null )
    {
      return false;
    }

    // If the symbol is on this class, or any ancestors, it's not enclosed
    //noinspection SuspiciousMethodCalls
    IType symbolClass = maybeUnwrapProxy( symbol.getGosuClass() );
    if( gsClass.getAllTypesInHierarchy().contains( symbolClass ) )
    {
      return false;
    }

    ICompilableType enclosingClass = gsClass.getEnclosingType();
    while( enclosingClass != null )
    {
      //noinspection SuspiciousMethodCalls
      if( enclosingClass.getAllTypesInHierarchy().contains( symbolClass ) )
      {
        return true;
      }
      enclosingClass = enclosingClass.getEnclosingType();
    }

    return false;
  }

  private static IType maybeUnwrapProxy( IType type )
  {
    if( type != null && type.isParameterizedType() )
    {
      type = type.getGenericType();
    }
    return type == null ? null : IGosuClass.ProxyUtil.getProxiedType( type );
  }
}
