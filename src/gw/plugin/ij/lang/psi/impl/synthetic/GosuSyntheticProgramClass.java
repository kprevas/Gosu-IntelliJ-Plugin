package gw.plugin.ij.lang.psi.impl.synthetic;

import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.HierarchicalMethodSignature;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassInitializer;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiJavaToken;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiReferenceList;
import com.intellij.psi.PsiSubstitutor;
import com.intellij.psi.PsiTypeParameter;
import com.intellij.psi.PsiTypeParameterList;
import com.intellij.psi.ResolveState;
import com.intellij.psi.SyntheticElement;
import com.intellij.psi.impl.ElementBase;
import com.intellij.psi.impl.ElementPresentationUtil;
import com.intellij.psi.impl.InheritanceImplUtil;
import com.intellij.psi.impl.PsiClassImplUtil;
import com.intellij.psi.impl.light.LightElement;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.scope.NameHint;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.ui.RowIcon;
import com.intellij.util.IncorrectOperationException;
import gw.plugin.ij.GosuIcons;
import gw.plugin.ij.lang.psi.GosuNamedElement;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuMembersDeclaration;
import gw.plugin.ij.lang.psi.impl.GosuProgramFileImpl;
import gw.plugin.ij.lang.psi.impl.PsiImplUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuSyntheticProgramClass extends LightElement implements PsiClass, SyntheticElement
{
  private final GosuProgramFileImpl _file;

  public GosuSyntheticProgramClass( GosuProgramFileImpl file )
  {
    super( file.getManager(), file.getLanguage() );
    _file = file;
  }

  public String toString()
  {
    return "Script Class:" + getQualifiedName();
  }

  public String getText()
  {
    return "class " + getName() + " {}";
  }

  public void accept( @NotNull PsiElementVisitor visitor )
  {
    if( visitor instanceof JavaElementVisitor )
    {
      ((JavaElementVisitor)visitor).visitClass( this );
    }
  }

  public PsiElement copy()
  {
    return new GosuSyntheticProgramClass( _file );
  }

  public PsiFile getContainingFile()
  {
    return _file;
  }

  public TextRange getTextRange()
  {
    return _file.getTextRange();
  }

  public boolean isValid()
  {
    return _file.isValid();
  }

  @NotNull
  public String getQualifiedName()
  {
    final String packName = _file.getPackageName();
    if( packName.length() == 0 )
    {
      return getName();
    }
    else
    {
      return packName + "." + getName();
    }
  }

  public boolean isInterface()
  {
    return false;
  }

  public boolean isWritable()
  {
    return true;
  }

  public boolean isAnnotationType()
  {
    return false;
  }

  public boolean isEnum()
  {
    return false;
  }

  @Override
  public PsiElement add( @NotNull PsiElement element ) throws IncorrectOperationException
  {
    return _file.add( element );
  }

  @Override
  public PsiElement addAfter( @NotNull PsiElement element, PsiElement anchor ) throws IncorrectOperationException
  {
    return _file.addAfter( element, anchor );
  }

  @Override
  public PsiElement addBefore( @NotNull PsiElement element, PsiElement anchor ) throws IncorrectOperationException
  {
    return _file.addBefore( element, anchor );
  }

  public PsiReferenceList getExtendsList()
  {
    return null;
  }


  public PsiReferenceList getImplementsList()
  {
    return null;
  }

  @NotNull
  public PsiClassType[] getExtendsListTypes()
  {
    return PsiClassType.EMPTY_ARRAY;
  }

  @NotNull
  public PsiClassType[] getImplementsListTypes()
  {
    return PsiClassType.EMPTY_ARRAY;
  }

  public PsiClass getSuperClass()
  {
    return null;
  }

  public PsiClass[] getInterfaces()
  {
    return PsiClass.EMPTY_ARRAY;
  }

  @NotNull
  public PsiClass[] getSupers()
  {
    final PsiClass superClass = getSuperClass();
    if( superClass != null )
    {
      return new PsiClass[]{superClass};
    }
    else
    {
      return PsiClass.EMPTY_ARRAY;
    }
  }

  @NotNull
  public PsiClassType[] getSuperTypes()
  {
    return new PsiClassType[0];
  }

  public PsiClass getContainingClass()
  {
    return null;
  }

  @NotNull
  public Collection<HierarchicalMethodSignature> getVisibleSignatures()
  {
    return Collections.emptySet();
  }

  @NotNull
  public PsiField[] getFields()
  {
    return PsiField.EMPTY_ARRAY;
  }

  @NotNull
  public PsiMethod[] getMethods()
  {
    return _file.getTopLevelMethods();
  }

  @NotNull
  public PsiMethod[] getConstructors()
  {
    return PsiMethod.EMPTY_ARRAY;
  }

  @NotNull
  public PsiClass[] getInnerClasses()
  {
    return PsiClass.EMPTY_ARRAY;
  }

  @NotNull
  public PsiClassInitializer[] getInitializers()
  {
    return PsiClassInitializer.EMPTY_ARRAY;
  }

  @NotNull
  public PsiTypeParameter[] getTypeParameters()
  {
    return PsiTypeParameter.EMPTY_ARRAY;
  }

  @NotNull
  public PsiField[] getAllFields()
  {
    return PsiClassImplUtil.getAllFields( this );
  }

  @NotNull
  public PsiMethod[] getAllMethods()
  {
    return PsiClassImplUtil.getAllMethods( this );
  }

  @NotNull
  public PsiClass[] getAllInnerClasses()
  {
    return PsiClass.EMPTY_ARRAY;
  }

  public PsiField findFieldByName( String name, boolean checkBases )
  {
    return null;
  }

  public PsiMethod findMethodBySignature( PsiMethod patternMethod, boolean checkBases )
  {
    return PsiClassImplUtil.findMethodBySignature( this, patternMethod, checkBases );
  }

  @NotNull
  public PsiMethod[] findMethodsBySignature( PsiMethod patternMethod, boolean checkBases )
  {
    return PsiClassImplUtil.findMethodsBySignature( this, patternMethod, checkBases );
  }

  @NotNull
  public PsiMethod[] findMethodsByName( String name, boolean checkBases )
  {
    return PsiClassImplUtil.findMethodsByName( this, name, checkBases );
  }

  @NotNull
  public List<Pair<PsiMethod, PsiSubstitutor>> findMethodsAndTheirSubstitutorsByName( String name, boolean checkBases )
  {
    return new ArrayList<Pair<PsiMethod, PsiSubstitutor>>();
  }

  @NotNull
  public List<Pair<PsiMethod, PsiSubstitutor>> getAllMethodsAndTheirSubstitutors()
  {
    return new ArrayList<Pair<PsiMethod, PsiSubstitutor>>();
  }

  public PsiClass findInnerClassByName( String name, boolean checkBases )
  {
    return null;
  }

  public PsiTypeParameterList getTypeParameterList()
  {
    return null;
  }

  public boolean hasTypeParameters()
  {
    return false;
  }

  public PsiJavaToken getLBrace()
  {
    return null;
  }

  public PsiJavaToken getRBrace()
  {
    return null;
  }

  public PsiIdentifier getNameIdentifier()
  {
    return null;
  }

  // very special method!
  public PsiElement getScope()
  {
    return _file;
  }

  public boolean isInheritorDeep( PsiClass baseClass, PsiClass classToByPass )
  {
    return InheritanceImplUtil.isInheritorDeep( this, baseClass, classToByPass );
  }

  public boolean isInheritor( @NotNull PsiClass baseClass, boolean checkDeep )
  {
    return InheritanceImplUtil.isInheritor( this, baseClass, checkDeep );
  }

  @NotNull
  public String getName()
  {
    String name = _file.getName();
    int i = name.indexOf( '.' );
    return i > 0 ? name.substring( 0, i ) : name;
  }

  public PsiElement setName( @NotNull String name ) throws IncorrectOperationException
  {
    _file.setName( name + "." + _file.getViewProvider().getVirtualFile().getExtension() );
    return this;
  }

  public PsiModifierList getModifierList()
  {
    return null;
  }

  public boolean hasModifierProperty( @NotNull String name )
  {
    return PsiModifier.PUBLIC.equals( name );
  }

  public PsiDocComment getDocComment()
  {
    return null;
  }

  public boolean isDeprecated()
  {
    return false;
  }

  public boolean processDeclarations( @NotNull final PsiScopeProcessor processor,
                                      @NotNull ResolveState state,
                                      PsiElement lastParent,
                                      @NotNull PsiElement place )
  {
    for( GosuNamedElement defintion : _file.getTopLevelDefinitions() )
    {
      if( !(defintion instanceof PsiClass) )
      {
        if( !processElement( processor, defintion, state ) )
        {
          return false;
        }
      }
    }

//    final PsiClass scriptClass = getSuperClass();
//    //noinspection RedundantIfStatement
//    if( scriptClass != null && !scriptClass.processDeclarations( new BaseScopeProcessor()
//    {
//      public boolean execute( PsiElement element, ResolveState state )
//      {
//        return !(element instanceof PsiNamedElement) || processElement( processor, (PsiNamedElement)element, state );
//      }
//
//      @Override
//      public <T> T getHint( Key<T> hintKey )
//      {
//        return processor.getHint( hintKey );
//      }
//    }, state, lastParent, place ) )
//    {
//      return false;
//    }

    return true;
  }

  public static boolean processElement( PsiScopeProcessor processor, GosuNamedElement namedElement, ResolveState state )
  {
    NameHint nameHint = processor.getHint( NameHint.KEY );
    String name = nameHint == null ? null : nameHint.getName( state );
    if( name == null || name.equals( namedElement.getName() ) )
    {
      return processor.execute( namedElement, state );
    }

    return true;
  }

  @Override
  public PsiElement getContext()
  {
    return _file;
  }

  //default implementations of methods from NavigationItem
  public ItemPresentation getPresentation()
  {
    return new ItemPresentation()
    {
      public String getPresentableText()
      {
        return getName();
      }

      public String getLocationString()
      {
        final String packageName = _file.getPackageName();
        return "(groovy script" + (packageName.isEmpty() ? "" : ", " + packageName) + ")";
      }

      public TextAttributesKey getTextAttributesKey()
      {
        return null;
      }

      public Icon getIcon( boolean open )
      {
        return GosuIcons.FILE_CLASS;
        //## todo:
        //return GosuScriptClass.this.getIcon( ICON_FLAG_VISIBILITY | ICON_FLAG_READ_STATUS );
      }
    };
  }

  public PsiElement getOriginalElement()
  {
    return PsiImplUtil.getOriginalElement( this, _file );
  }

  @Nullable
  public Icon getIcon( int flags )
  {
    final Icon icon = _file.getIcon( flags );
    RowIcon baseIcon = ElementBase.createLayeredIcon( icon, 0 );
    return ElementPresentationUtil.addVisibilityIcon( this, flags, baseIcon );
  }

  public void checkDelete() throws IncorrectOperationException
  {
  }

  public void delete() throws IncorrectOperationException
  {
    _file.delete();
  }

  public <T extends GosuMembersDeclaration> T addMemberDeclaration( T decl, PsiElement anchorBefore ) throws IncorrectOperationException
  {
    throw new UnsupportedOperationException( "Men at work" );
    //return _file.addMemberDeclaration( decl, anchorBefore );
  }
}
