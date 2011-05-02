package gw.plugin.ij.lang.psi.impl.statements.params;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiType;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import gw.lang.parser.expressions.IParameterDeclaration;
import gw.plugin.ij.lang.parser.GosuCompositeElement;
import gw.plugin.ij.lang.psi.api.auxilary.IGosuModifierList;
import gw.plugin.ij.lang.psi.api.auxilary.annotation.GosuAnnotation;
import gw.plugin.ij.lang.psi.api.expressions.IGosuExpression;
import gw.plugin.ij.lang.psi.api.statements.GosuParametersOwner;
import gw.plugin.ij.lang.psi.api.statements.params.IGosuParameter;
import gw.plugin.ij.lang.psi.api.types.GosuTypeElement;
import gw.plugin.ij.lang.psi.impl.GosuElementVisitor;
import gw.plugin.ij.lang.psi.impl.statements.GosuVariableImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuParameterImpl extends GosuVariableImpl<IParameterDeclaration> implements IGosuParameter
{
  public GosuParameterImpl( GosuCompositeElement<IParameterDeclaration> node )
  {
    super( node );
  }

  public void accept( GosuElementVisitor visitor )
  {
    visitor.visitParameter( this );
  }

  public String toString()
  {
    return "Parameter";
  }

  public void setType( @Nullable PsiType type )
  {
    throw new UnsupportedOperationException( "Men at work" );
//    final GosuTypeElement typeElement = getTypeElementGosu();
//    if( type == null )
//    {
//      if( typeElement != null )
//      {
//        typeElement.delete();
//      }
//      return;
//    }
//
//    GosuTypeElement newTypeElement;
//    try
//    {
//      newTypeElement = GosuPsiElementFactory.getInstance( getProject() ).createTypeElement( type );
//    }
//    catch( IncorrectOperationException e )
//    {
//      LOG.error( e );
//      return;
//    }
//
//    if( typeElement == null )
//    {
//      final IGosuModifierList modifierList = getModifierList();
//      newTypeElement = (GosuTypeElement)addAfter( newTypeElement, modifierList );
//    }
//    else
//    {
//      newTypeElement = (GosuTypeElement)typeElement.replace( newTypeElement );
//    }
//
//    PsiUtil.shortenReferences( newTypeElement );
  }

  @Nullable
  public GosuTypeElement getTypeElementGosu()
  {
    return findChildByClass( GosuTypeElement.class );
  }

  @Nullable
  public IGosuExpression getDefaultInitializer()
  {
    return findChildByClass( IGosuExpression.class );
  }

  public boolean isOptional()
  {
    return getDefaultInitializer() != null;
  }

  @NotNull
  public SearchScope getUseScope()
  {
    PsiElement scope = getDeclarationScope();
//## todo:
//    if( scope instanceof GosuDocCommentOwner )
//    {
//      GosuDocCommentOwner owner = (GosuDocCommentOwner)scope;
//      final GosuDocComment comment = owner.getDocComment();
//      if( comment != null )
//      {
//        return new LocalSearchScope( new PsiElement[]{scope, comment} );
//      }
//    }

    return new LocalSearchScope( scope );
  }

  @NotNull
  public String getName()
  {
    return getNameIdentifierGosu().getText();
  }

  public int getTextOffset()
  {
    return getNameIdentifierGosu().getTextRange().getStartOffset();
  }

  @NotNull
  public IGosuModifierList getModifierList()
  {
    return this;
  }

  @NotNull
  public PsiElement getDeclarationScope()
  {
    final GosuParametersOwner owner = PsiTreeUtil.getParentOfType( this, GosuParametersOwner.class );
    assert owner != null;
    return owner;
  }

  public boolean isVarArgs()
  {
    return false;
  }

  @NotNull
  public GosuAnnotation[] getAnnotations()
  {
    return GosuAnnotation.EMPTY_ARRAY;
  }

}
