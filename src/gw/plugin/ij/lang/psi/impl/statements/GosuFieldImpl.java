package gw.plugin.ij.lang.psi.impl.statements;

import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.PsiImplUtil;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.IncorrectOperationException;
import gw.lang.parser.expressions.IVarStatement;
import gw.plugin.ij.GosuIcons;
import gw.plugin.ij.lang.parser.GosuCompositeElement;
import gw.plugin.ij.lang.parser.GosuElementTypes;
import gw.plugin.ij.lang.psi.GosuFileBase;
import gw.plugin.ij.lang.psi.api.auxilary.IGosuModifier;
import gw.plugin.ij.lang.psi.api.statements.IGosuField;
import gw.plugin.ij.lang.psi.impl.GosuElementVisitor;
import gw.plugin.ij.lang.psi.stubs.GosuFieldStub;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuFieldImpl extends GosuVariableBaseImpl<IVarStatement, GosuFieldStub> implements IGosuField
{
  public GosuFieldImpl( GosuCompositeElement node )
  {
    super( node );
  }

  public GosuFieldImpl( GosuFieldStub stub )
  {
    this( stub, GosuElementTypes.FIELD );
  }

  public GosuFieldImpl( GosuFieldStub stub, IStubElementType nodeType )
  {
    super( stub, nodeType );
  }

  public void accept( GosuElementVisitor visitor )
  {
    visitor.visitField( this );
  }

  public void setInitializer( @Nullable PsiExpression psiExpression ) throws IncorrectOperationException
  {
  }

  public boolean isDeprecated()
  {
    final GosuFieldStub stub = getStub();
    boolean byDocTag = stub == null ? PsiImplUtil.isDeprecatedByDocTag( this ) : stub.isDeprecatedByDocTag();
    if( byDocTag )
    {
      return true;
    }

    return PsiImplUtil.isDeprecatedByAnnotation( this );
  }

  public PsiClass getContainingClass()
  {
    PsiElement parent = getParent();
    if( parent instanceof PsiClass )
    {
      return (PsiClass)parent;
    }

    final PsiFile file = getContainingFile();
    if( file instanceof GosuFileBase )
    {
      return ((GosuFileBase)file).getPsiClass();
    }

    return null;
  }

  @Override
  public boolean hasModifierProperty( @NotNull @NonNls String modifier )
  {
    if( modifier.equals( IGosuModifier.PRIVATE ) )
    {
      // Gosu fields are private by default
      return !hasExplicitModifier( IGosuModifier.PUBLIC ) &&
             !hasExplicitModifier( IGosuModifier.PROTECTED ) &&
             !hasExplicitModifier( IGosuModifier.PACKAGE_LOCAL );
    }

    return super.hasModifierProperty( modifier );
  }

  public void clearCaches()
  {
  }

  @NotNull
  public SearchScope getUseScope()
  {
    return PsiImplUtil.getMemberUseScope( this );
  }

  @NotNull
  @Override
  public String getName()
  {
    final GosuFieldStub stub = getStub();
    if( stub != null )
    {
      return stub.getName();
    }
    return super.getName();
  }

  @Override
  public ItemPresentation getPresentation()
  {
    return new ItemPresentation()
    {
      public String getPresentableText()
      {
        return getName();
      }

      @Nullable
      public String getLocationString()
      {
        PsiClass clazz = getContainingClass();
        if( clazz == null )
        {
          return "";
        }
        String name = clazz.getQualifiedName();
        assert name != null;
        return "(in " + name + ")";
      }

      @Nullable
      public Icon getIcon( boolean open )
      {
        return GosuFieldImpl.this.getIcon( ICON_FLAG_VISIBILITY | ICON_FLAG_READ_STATUS );
      }

      @Nullable
      public TextAttributesKey getTextAttributesKey()
      {
        return null;
      }
    };
  }

  public PsiElement getOriginalElement()
  {
    final PsiClass containingClass = getContainingClass();
    if( containingClass == null )
    {
      return this;
    }
    PsiClass originalClass = (PsiClass)containingClass.getOriginalElement();
    PsiField originalField = originalClass.findFieldByName( getName(), false );
    return originalField != null ? originalField : this;
  }

  @Nullable
  public Icon getIcon( int flags )
  {
    return GosuIcons.FIELD;
  }

  public PsiDocComment getDocComment()
  {
    return null;
  }
}
