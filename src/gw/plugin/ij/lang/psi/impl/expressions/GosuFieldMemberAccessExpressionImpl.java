package gw.plugin.ij.lang.psi.impl.expressions;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceParameterList;
import com.intellij.psi.PsiType;
import gw.lang.parser.IExpression;
import gw.lang.parser.expressions.IMemberAccessExpression;
import gw.lang.reflect.INamespaceType;
import gw.lang.reflect.IPropertyInfo;
import gw.lang.reflect.IType;
import gw.lang.reflect.gs.IGosuVarPropertyInfo;
import gw.plugin.ij.lang.GosuTokenTypes;
import gw.plugin.ij.lang.parser.GosuCompositeElement;
import gw.plugin.ij.lang.psi.api.types.GosuCodeReferenceElement;
import gw.plugin.ij.lang.psi.api.types.GosuTypeArgumentList;
import gw.plugin.ij.lang.psi.api.types.GosuTypeElement;
import gw.plugin.ij.lang.psi.util.TypesUtil;


/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuFieldMemberAccessExpressionImpl extends GosuReferenceExpressionImpl<IMemberAccessExpression> implements GosuCodeReferenceElement, GosuTypeElement
{
  private String _strVarName;

  public GosuFieldMemberAccessExpressionImpl( GosuCompositeElement<IMemberAccessExpression> node, String strVarName )
  {
    super( node );
    _strVarName = strVarName;
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
    return getFirstChild() instanceof GosuCodeReferenceElement ? (GosuCodeReferenceElement)getFirstChild() : null;
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

  @Override
  public GosuTypeArgumentList getTypeArgumentListGosu()
  {
    return null;
  }

  @Override
  public PsiElement resolve()
  {
    IExpression rootExpr = getParsedElement().getRootExpression();
    IType rootType = rootExpr.getType();
    rootType = rootType.isParameterizedType() ? rootType.getGenericType() : rootType;
    return resolveField( _strVarName, rootType.getName() );
  }

  @Override
  public PsiReferenceParameterList getParameterList()
  {
    return null;
  }

  @Override
  public boolean isQualified()
  {
    return getParent() instanceof GosuCodeReferenceElement;
  }
}
