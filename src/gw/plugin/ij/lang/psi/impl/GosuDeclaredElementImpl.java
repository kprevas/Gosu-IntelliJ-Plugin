package gw.plugin.ij.lang.psi.impl;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.IncorrectOperationException;
import gnu.trove.TObjectIntHashMap;
import gw.lang.parser.IParsedElement;
import gw.lang.reflect.Modifier;
import gw.plugin.ij.lang.TokenSets;
import gw.plugin.ij.lang.parser.GosuCompositeElement;
import gw.plugin.ij.lang.parser.GosuElementTypes;
import gw.plugin.ij.lang.psi.api.auxilary.IGosuModifier;
import gw.plugin.ij.lang.psi.api.auxilary.IGosuModifierList;
import gw.plugin.ij.lang.psi.api.auxilary.annotation.GosuAnnotation;
import gw.plugin.ij.lang.psi.api.statements.IGosuVariable;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuTypeDefinition;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuDeclaredElementImpl<E extends IParsedElement, T extends StubElement> extends GosuBaseElementImpl<E, T> implements IGosuModifierList
{
  public static final TObjectIntHashMap<String> NAME_TO_MODIFIER_FLAG_MAP = new TObjectIntHashMap<String>();

  static
  {
    NAME_TO_MODIFIER_FLAG_MAP.put( IGosuModifier.PUBLIC, Modifier.PUBLIC );
    NAME_TO_MODIFIER_FLAG_MAP.put( IGosuModifier.PROTECTED, Modifier.PROTECTED );
    NAME_TO_MODIFIER_FLAG_MAP.put( IGosuModifier.PRIVATE, Modifier.PRIVATE );
    NAME_TO_MODIFIER_FLAG_MAP.put( IGosuModifier.PACKAGE_LOCAL, Modifier.INTERNAL );
    NAME_TO_MODIFIER_FLAG_MAP.put( IGosuModifier.STATIC, Modifier.STATIC );
    NAME_TO_MODIFIER_FLAG_MAP.put( IGosuModifier.ABSTRACT, Modifier.ABSTRACT );
    NAME_TO_MODIFIER_FLAG_MAP.put( IGosuModifier.FINAL, Modifier.FINAL );
    NAME_TO_MODIFIER_FLAG_MAP.put( IGosuModifier.NATIVE, Modifier.NATIVE );
    NAME_TO_MODIFIER_FLAG_MAP.put( IGosuModifier.SYNCHRONIZED, Modifier.SYNCHRONIZED );
    NAME_TO_MODIFIER_FLAG_MAP.put( IGosuModifier.TRANSIENT, Modifier.TRANSIENT );
    NAME_TO_MODIFIER_FLAG_MAP.put( IGosuModifier.VOLATILE, Modifier.VOLATILE );
  }

  protected GosuDeclaredElementImpl( final T stub, IStubElementType nodeType )
  {
    super( stub, nodeType );
  }

  public GosuDeclaredElementImpl( GosuCompositeElement node )
  {
    super( node );
  }

  @NotNull
  public PsiElement[] getModifiers()
  {
    PsiElement[] modifiersKeywords = findChildrenByType( TokenSets.MODIFIERS, PsiElement.class );
    GosuAnnotation[] modifiersAnnotations = findChildrenByClass( GosuAnnotation.class );

    if( modifiersAnnotations.length == 0 )
    {
      return modifiersKeywords;
    }

    PsiElement[] res = new PsiElement[modifiersAnnotations.length + modifiersKeywords.length];

    int i = 0;
    for( PsiElement modifiersKeyword : modifiersKeywords )
    {
      res[i++] = modifiersKeyword;
    }
    for( GosuAnnotation modifiersAnnotation : modifiersAnnotations )
    {
      res[i++] = modifiersAnnotation;
    }

    return res;
  }

  public boolean hasExplicitVisibilityModifiers()
  {
    return findChildByType( TokenSets.VISIBILITY_MODIFIERS ) != null;
  }

  public boolean hasModifierProperty( @NotNull @NonNls String modifier )
  {
    final PsiElement parent = getParent();
    if( parent instanceof IGosuVariable &&
        parent.getParent() instanceof GosuTypeDefinition )
    {
      PsiElement pParent = parent.getParent().getParent();
      if( !hasExplicitVisibilityModifiers() )
      { //properties are backed by private fields
        if( pParent instanceof PsiClass && ((PsiClass)pParent).isInterface() )
        {
          if( modifier.equals( IGosuModifier.STATIC ) )
          {
            return true;
          }
          if( modifier.equals( IGosuModifier.FINAL ) )
          {
            return true;
          }
        }
        else
        {
          if( modifier.equals( IGosuModifier.PRIVATE ) )
          {
            return true;
          }
          if( modifier.equals( IGosuModifier.PROTECTED ) )
          {
            return false;
          }
          if( modifier.equals( IGosuModifier.PUBLIC ) )
          {
            return false;
          }
        }
      }
    }

    if( hasExplicitModifier( modifier ) )
    {
      return true;
    }

    if( modifier.equals( IGosuModifier.PUBLIC ) )
    {
      // Gosu type definitions and methods are public by default
      return !hasExplicitModifier( IGosuModifier.PRIVATE ) &&
             !hasExplicitModifier( IGosuModifier.PROTECTED ) &&
             !hasExplicitModifier( IGosuModifier.PACKAGE_LOCAL );
    }

    if( parent instanceof GosuTypeDefinition )
    {
      if( modifier.equals( IGosuModifier.STATIC ) )
      {
        final PsiClass containingClass = ((GosuTypeDefinition)parent).getContainingClass();
        return containingClass != null && containingClass.isInterface();
      }
      if( modifier.equals( IGosuModifier.ABSTRACT ) )
      {
        return ((GosuTypeDefinition)parent).isInterface();
      }
    }

    return false;
  }

  public boolean hasExplicitModifier( @NotNull @NonNls String name )
  {
    if( name.equals( IGosuModifier.PUBLIC ) )
    {
      return findChildByType( GosuElementTypes.TT_public ) != null;
    }
    if( name.equals( IGosuModifier.ABSTRACT ) )
    {
      return findChildByType( GosuElementTypes.TT_abstract ) != null;
    }
    if( name.equals( IGosuModifier.PRIVATE ) )
    {
      return findChildByType( GosuElementTypes.TT_private ) != null;
    }
    if( name.equals( IGosuModifier.PROTECTED ) )
    {
      return findChildByType( GosuElementTypes.TT_protected ) != null;
    }
    if( name.equals( IGosuModifier.PACKAGE_LOCAL ) )
    {
      return findChildByType( GosuElementTypes.TT_internal ) != null;
    }

    if( name.equals( IGosuModifier.STATIC ) )
    {
      return findChildByType( GosuElementTypes.TT_static ) != null;
    }
    if( name.equals( IGosuModifier.FINAL ) )
    {
      return findChildByType( GosuElementTypes.TT_final ) != null;
    }
    if( name.equals( IGosuModifier.TRANSIENT ) )
    {
      return findChildByType( GosuElementTypes.TT_transient ) != null;
    }
    return false;
  }

  public void setModifierProperty( @NotNull @NonNls String name, boolean doSet ) throws IncorrectOperationException
  {
    if( hasModifierProperty( name ) == doSet )
    {
      return;
    }

    if( doSet )
    {
      if( IGosuModifier.PRIVATE.equals( name ) ||
          IGosuModifier.PROTECTED.equals( name ) ||
          IGosuModifier.PUBLIC.equals( name ) ||
          IGosuModifier.PACKAGE_LOCAL.equals( name ) )
      {
        setModifierPropertyInternal( IGosuModifier.PUBLIC, false );
        setModifierPropertyInternal( IGosuModifier.PROTECTED, false );
        setModifierPropertyInternal( IGosuModifier.PRIVATE, false );
      }
    }
    setModifierPropertyInternal( name, doSet );
  }

  private void setModifierPropertyInternal( String name, boolean doSet )
  {
    throw new UnsupportedOperationException();
//    if( doSet )
//    {
//      final ASTNode modifierNode = GosuPsiElementFactory.getInstance( getProject() ).createModifierFromText( name ).getNode();
//      addInternal( modifierNode, modifierNode, null, null );
//    }
//    else
//    {
//      final PsiElement[] modifiers = findChildrenByType( TokenSets.MODIFIERS, PsiElement.class );
//      for( PsiElement modifier : modifiers )
//      {
//        if( name.equals( modifier.getText() ) )
//        {
//          deleteChildRange( modifier, modifier );
//          break;
//        }
//      }
//    }
  }

  public void checkSetModifierProperty( @NotNull @NonNls String name, boolean value ) throws IncorrectOperationException
  {
  }

  @NotNull
  public GosuAnnotation[] getAnnotations()
  {
    return findChildrenByClass( GosuAnnotation.class );
  }

  @NotNull
  public PsiAnnotation[] getApplicableAnnotations()
  {
    return getAnnotations();
  }

  @Nullable
  public PsiAnnotation findAnnotation( @NotNull @NonNls String qualifiedName )
  {
    PsiElement child = getFirstChild();
    while( child != null )
    {
      if( child instanceof PsiAnnotation && qualifiedName.equals( ((PsiAnnotation)child).getQualifiedName() ) )
      {
        return (PsiAnnotation)child;
      }
      child = child.getNextSibling();
    }
    return null;
  }

  @NotNull
  public GosuAnnotation addAnnotation( @NotNull @NonNls String qualifiedName )
  {
    throw new UnsupportedOperationException();
//    final PsiClass psiClass = JavaPsiFacade.getInstance( getProject() ).findClass( qualifiedName, getResolveScope() );
//    final GosuPsiElementFactory factory = GosuPsiElementFactory.getInstance( getProject() );
//    if( psiClass != null && psiClass.isAnnotationType() )
//    {
//      final GosuAnnotation annotation = (GosuAnnotation)addAfter( factory.createModifierFromText( "@xxx" ), null );
//      annotation.getClassReference().bindToElement( psiClass );
//      return annotation;
//    }
//
//    return (GosuAnnotation)addAfter( factory.createModifierFromText( "@" + qualifiedName ), null );
  }
}
