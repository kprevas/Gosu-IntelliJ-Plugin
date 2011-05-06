package gw.plugin.ij.lang.psi.impl.statements;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeElement;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import gw.lang.parser.IParsedElementWithAtLeastOneDeclaration;
import gw.plugin.ij.GosuIcons;
import gw.plugin.ij.lang.parser.GosuCompositeElement;
import gw.plugin.ij.lang.parser.GosuElementTypes;
import gw.plugin.ij.lang.psi.api.auxilary.IGosuModifierList;
import gw.plugin.ij.lang.psi.api.expressions.IGosuExpression;
import gw.plugin.ij.lang.psi.api.statements.IGosuVariable;
import gw.plugin.ij.lang.psi.api.types.GosuTypeElement;
import gw.plugin.ij.lang.psi.api.util.GosuVariableOwner;
import gw.plugin.ij.lang.psi.impl.GosuDeclaredElementImpl;
import gw.plugin.ij.lang.psi.impl.PsiImplUtil;
import gw.plugin.ij.lang.psi.impl.TypesUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public abstract class GosuVariableBaseImpl<E extends IParsedElementWithAtLeastOneDeclaration, T extends StubElement>
  extends GosuDeclaredElementImpl<E, T> implements IGosuVariable
{
  public static final Logger LOG = Logger.getInstance( "gw.plugin.ij.lang.psi.impl.statements.GosuVariableImpl" );

  public GosuVariableBaseImpl( GosuCompositeElement node )
  {
    super( node );
  }

  protected GosuVariableBaseImpl( final T stub, IStubElementType nodeType )
  {
    super( stub, nodeType );
  }

  @Nullable
  public PsiTypeElement getTypeElement()
  {
    return null;
  }

  @Nullable
  public PsiExpression getInitializer()
  {
    return null;
  }

  public boolean hasInitializer()
  {
    return false;
  }

  public void normalizeDeclaration() throws IncorrectOperationException
  {
  }

  @Nullable
  public Object computeConstantValue()
  {
    return null;
  }

  @Override
  public void delete() throws IncorrectOperationException
  {
    //## todo:
//    PsiElement parent = getParent();
//    PsiElement prev = PsiUtil.getPrevNonSpace( this );
//    PsiElement next = PsiUtil.getNextNonSpace( this );
//    ASTNode parentNode = parent.getNode();
//    assert parentNode != null;
//    super.delete();
//    if( prev != null && prev.getNode() != null && prev.getNode().getElementType() == GosuTokenTypes.mCOMMA )
//    {
//      prev.delete();
//    }
//    else if( next instanceof LeafPsiElement && next.getNode() != null && next.getNode().getElementType() == GosuTokenTypes.mCOMMA )
//    {
//      next.delete();
//    }
  }

  public String getElementToCompare()
  {
    return getName();
  }

  @NotNull
  public PsiType getType()
  {
    PsiType type = getDeclaredType();
    return type != null ? type : TypesUtil.getJavaLangObject( this );
  }

  @Nullable
  public GosuTypeElement getTypeElementGosu()
  {
    return (GosuTypeElement)findChildByType( GosuElementTypes.ELEM_TYPE_TypeLiteral );
  }

  @Nullable
  public PsiType getDeclaredType()
  {
    GosuTypeElement typeElement = getTypeElementGosu();
    if( typeElement != null )
    {
      return typeElement.getType();
    }

    return null;
  }

  public void setType( @Nullable PsiType type )
  {
    throw new UnsupportedOperationException( "Men at work" );
  }

  @NotNull
  public PsiIdentifier getNameIdentifierGosu()
  {
    //return (PsiIdentifier)findElement( this, GosuElementTypes.ELEM_TYPE_NameInDeclaration );
    PsiIdentifier id = findChildByClass( PsiIdentifier.class );
    if( id.getFirstChild() != null && id.getFirstChild() instanceof PsiIdentifier )
    {
      // Always return the leaf token node; we always want to patch in just the name and not mess with upper-level tree nodes
      id = (PsiIdentifier)id.getFirstChild();
    }
    return id;
  }

  @Nullable
  public IGosuExpression getInitializerGosu()
  {
    IGosuExpression[] childrenByClass = findChildrenByClass( IGosuExpression.class );
    if( childrenByClass.length != 0 )
    {
      return childrenByClass[childrenByClass.length-1];
    }
    return null;
  }

  public int getTextOffset()
  {
    return getNameIdentifierGosu().getTextRange().getStartOffset();
  }

  public PsiElement setName( @NonNls @NotNull String name ) throws IncorrectOperationException
  {
    com.intellij.psi.impl.PsiImplUtil.setName(getNameIdentifierGosu(), name);
    return this;
  }

  @NotNull
  public SearchScope getUseScope()
  {
    final GosuVariableOwner owner = PsiTreeUtil.getParentOfType( this, GosuVariableOwner.class );
    if( owner != null )
    {
      return new LocalSearchScope( owner );
    }
    return super.getUseScope();
  }

  @NotNull
  public String getName()
  {
    return PsiImplUtil.getName( this );
  }

  @Nullable
  public PsiIdentifier getNameIdentifier()
  {
    return getNameIdentifierGosu();
  }

  @Nullable
  public IGosuModifierList getModifierList()
  {
    return this;
  }

  @Nullable
  public Icon getIcon( int flags )
  {
    return GosuIcons.VARIABLE;
  }

  public PsiType getTypeNoResolve()
  {
    return getType();
  }
}
