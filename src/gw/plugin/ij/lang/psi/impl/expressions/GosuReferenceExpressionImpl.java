package gw.plugin.ij.lang.psi.impl.expressions;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiCallExpression;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.infos.CandidateInfo;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import gw.lang.parser.IParsedElement;
import gw.lang.reflect.gs.IGosuClass;
import gw.plugin.ij.codeInsight.GosuTargetElementEvaluator;
import gw.plugin.ij.lang.parser.GosuCompositeElement;
import gw.plugin.ij.lang.psi.api.GosuResolveResult;
import gw.plugin.ij.lang.psi.api.expressions.IGosuReferenceExpression;
import gw.plugin.ij.lang.psi.impl.GosuPsiElementImpl;
import gw.plugin.ij.lang.psi.impl.GosuResolveResultImpl;
import gw.plugin.ij.lang.psi.util.GosuPsiParseUtil;
import org.jetbrains.annotations.NotNull;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public abstract class GosuReferenceExpressionImpl<T extends IParsedElement> extends GosuPsiElementImpl<T> implements IGosuReferenceExpression
{
  public GosuReferenceExpressionImpl( GosuCompositeElement<T> node )
  {
    super( node );
  }

  public PsiReference getReference()
  {
    return this;
  }

  public String getReferenceName()
  {
    PsiElement nameElement = getReferenceNameElement();
    if( nameElement != null )
    {
      return nameElement.getText();
    }
    return null;
  }

  abstract public PsiElement getReferenceNameElement();

  public PsiElement getElement()
  {
    return this;
  }

  public TextRange getRangeInElement()
  {
    final PsiElement refNameElement = getReferenceNameElement();
    if( refNameElement != null )
    {
      final int offsetInParent = refNameElement.getStartOffsetInParent();
      return new TextRange( offsetInParent, offsetInParent + refNameElement.getTextLength() );
    }
    return new TextRange( 0, getTextLength() );
  }

  abstract public PsiElement resolve();

  protected PsiElement resolveType( String strFullName )
  {
    if( strFullName != null )
    {
      final JavaPsiFacade facade = JavaPsiFacade.getInstance( getProject() );
//      if( getContext() != null )
//      {
//        return facade.getResolveHelper().resolveReferencedClass( strFullName, getContext() );
//      }
//      else
//      {
      return facade.findClass( strFullName, getResolveScope() );
//      }
    }
    else
    {
      return null;
    }
  }

  protected PsiElement resolveField( String strField )
  {
    return resolveField( strField, getContext() );
  }

  protected PsiElement resolveField( String strField, IGosuClass gsClass )
  {
    return resolveField( strField, resolveType( gsClass.getName() ) );
  }

  protected PsiElement resolveField( String strField, String strFqn )
  {
    return resolveField( strField, resolveType( strFqn ) );
  }

  protected PsiElement resolveField( String strField, PsiElement context )
  {

    if( strField != null )
    {
      final JavaPsiFacade facade = JavaPsiFacade.getInstance( getProject() );
      if( getContext() != null )
      {
        return facade.getResolveHelper().resolveReferencedVariable( strField, context );
      }
      else
      {
        return null;
      }
    }
    else
    {
      return null;
    }
  }

  protected CandidateInfo[] resolveMethod( PsiCallExpression callExpr )
  {
    JavaPsiFacade facade = JavaPsiFacade.getInstance( getProject() );
    return facade.getResolveHelper().getReferencedMethodCandidates( callExpr, false );
  }

  protected PsiElement handleElementRenameInner( String newElementName ) throws IncorrectOperationException
  {
    PsiElement nameElement = getReferenceNameElement();
    if( nameElement != null )
    {
      ASTNode node = nameElement.getNode();
      ASTNode newNameNode = GosuPsiParseUtil.createReferenceNameFromText(this, newElementName ).getNode();
      assert newNameNode != null && node != null;
      node.getTreeParent().replaceChild( node, newNameNode );
    }

    return this;
  }

  public PsiElement handleElementRename( String newElementName ) throws IncorrectOperationException
  {
    return handleElementRenameInner( newElementName );
  }

  public PsiElement bindToElement( PsiElement element ) throws IncorrectOperationException
  {
    throw new UnsupportedOperationException( "Men at work" );
  }

  @NotNull
  public String getCanonicalText()
  {
    return getRangeInElement().substring( getElement().getText() );
  }

  public boolean isReferenceTo( PsiElement element )
  {
    return getManager().areElementsEquivalent( element, GosuTargetElementEvaluator.correctSearchTargets( resolve() ) );
  }

  @NotNull
  public Object[] getVariants()
  {
    return ArrayUtil.EMPTY_OBJECT_ARRAY;
  }

  public boolean isSoft()
  {
    return false;
  }

  @NotNull
  public GosuResolveResult[] multiResolve( boolean incomplete )
  {
    //## todo: actual multiResolve impl
    return new GosuResolveResult[]{new GosuResolveResultImpl( resolve(), !incomplete )};
  }
}
