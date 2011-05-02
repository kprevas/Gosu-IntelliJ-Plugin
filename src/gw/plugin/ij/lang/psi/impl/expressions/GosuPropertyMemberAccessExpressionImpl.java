package gw.plugin.ij.lang.psi.impl.expressions;

import gw.plugin.ij.lang.psi.api.GosuResolveResult;
import com.intellij.psi.PsiCallExpression;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpressionList;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReferenceParameterList;
import com.intellij.psi.PsiType;
import gw.lang.parser.expressions.IMemberAccessExpression;
import gw.lang.reflect.IType;
import gw.plugin.ij.lang.GosuTokenTypes;
import gw.plugin.ij.lang.parser.GosuCompositeElement;
import gw.plugin.ij.lang.psi.api.types.GosuCodeReferenceElement;
import gw.plugin.ij.lang.psi.api.types.GosuTypeArgumentList;
import gw.plugin.ij.lang.psi.api.types.GosuTypeElement;
import gw.plugin.ij.lang.psi.util.TypesUtil;
import org.jetbrains.annotations.NotNull;


/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuPropertyMemberAccessExpressionImpl extends GosuReferenceExpressionImpl<IMemberAccessExpression> implements GosuCodeReferenceElement, GosuTypeElement //, PsiCallExpression
{
  public GosuPropertyMemberAccessExpressionImpl( GosuCompositeElement<IMemberAccessExpression> node )
  {
    super( node );
  }

  public PsiElement getReferenceNameElement()
  {
    return findLastChildByType( GosuTokenTypes.TT_IDENTIFIER );
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

  @NotNull
  public PsiReferenceParameterList getTypeArgumentList()
  {
    return null;
  }

  @Override
  public PsiType[] getTypeArguments()
  {
    return new PsiType[0];
  }

  @Override
  public GosuTypeArgumentList getTypeArgumentListGosu()
  {
    return null;
  }

  @Override
  public PsiElement resolve()
  {
    throw new UnsupportedOperationException( "Men at work" );
    //return resolveMethod( this );
  }

  public PsiExpressionList getArgumentList()
  {
    return null;
  }

  @Override
  public PsiReferenceParameterList getParameterList()
  {
    return null;
  }

  @Override
  public boolean isQualified()
  {
    return false;
  }

//  @Override
//  public PsiMethod resolveMethod()
//  {
//    return (PsiMethod)multiResolve( false )[0];
//  }
//
//  @NotNull
//  @Override
//  public GosuResolveResult[] multiResolve( boolean incomplete )
//  {
//    return resolveMethod( this );
//  }
//
//  @NotNull
//  @Override
//  public GosuResolveResult resolveMethodGenerics()
//  {
//    return null;
//  }
}
