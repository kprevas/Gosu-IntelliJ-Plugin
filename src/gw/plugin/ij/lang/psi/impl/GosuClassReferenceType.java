package gw.plugin.ij.lang.psi.impl;

import com.intellij.pom.java.LanguageLevel;
import com.intellij.psi.JavaPsiFacade;
import gw.plugin.ij.lang.psi.api.GosuResolveResult;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiNameHelper;
import com.intellij.psi.PsiSubstitutor;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeParameter;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.search.GlobalSearchScope;
import gw.plugin.ij.lang.psi.api.expressions.IGosuReferenceExpression;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuClassReferenceType extends PsiClassType
{
  private final IGosuReferenceExpression myReferenceElement;

  public GosuClassReferenceType( IGosuReferenceExpression referenceElement )
  {
    super( LanguageLevel.JDK_1_5 );
    myReferenceElement = referenceElement;
  }

  public GosuClassReferenceType( IGosuReferenceExpression referenceElement, LanguageLevel languageLevel )
  {
    super( languageLevel );
    myReferenceElement = referenceElement;
  }

  @Nullable
  public PsiClass resolve()
  {
    PsiElement psiElement = myReferenceElement.resolve();
    return (PsiClass) psiElement instanceof PsiClass ? (PsiClass)psiElement : null;
    //## todo: use this
//    ResolveResult[] results = multiResolve();
//    if( results.length == 1 )
//    {
//      PsiElement only = results[0].getElement();
//      return only instanceof PsiClass ? (PsiClass)only : null;
//    }
//
//    return null;
  }

  //reference resolve is cached
  private GosuResolveResult[] multiResolve()
  {
    return (GosuResolveResult[])myReferenceElement.multiResolve( false );
  }

  public String getClassName()
  {
    return myReferenceElement.getReferenceName();
  }

  @NotNull
  public PsiType[] getParameters()
  {
    return myReferenceElement.getTypeArguments();
  }

  @NotNull
  public ClassResolveResult resolveGenerics()
  {
    return new ClassResolveResult()
    {
      public PsiClass getElement()
      {
        return resolve();
      }

      public PsiSubstitutor getSubstitutor()
      {
        final GosuResolveResult[] results = multiResolve();
        if( results.length != 1 )
        {
          return PsiSubstitutor.UNKNOWN;
        }
        return results[0].getSubstitutor();
      }

      public boolean isPackagePrefixPackageReference()
      {
        return false;
      }

      public boolean isAccessible()
      {
        final GosuResolveResult[] results = multiResolve();
        for( GosuResolveResult result : results )
        {
          if( result.isAccessible() )
          {
            return true;
          }
        }
        return false;
      }

      public boolean isStaticsScopeCorrect()
      {
        return true; //TODO
      }

      public PsiElement getCurrentFileResolveScope()
      {
        return null; //TODO???
      }

      public boolean isValidResult()
      {
        return isStaticsScopeCorrect() && isAccessible();
      }
    };
  }

  @NotNull
  public PsiClassType rawType()
  {
    final PsiClass clazz = resolve();
    if( clazz != null )
    {
      final PsiElementFactory factory = JavaPsiFacade.getInstance( clazz.getProject() ).getElementFactory();
      return factory.createType( clazz, factory.createRawSubstitutor( clazz ) );
    }

    return this;
  }

  public String getPresentableText()
  {
    return PsiNameHelper.getPresentableText( myReferenceElement.getReferenceName(), myReferenceElement.getTypeArguments() );
  }

  @Nullable
  public String getCanonicalText()
  {
    PsiClass resolved = resolve();
    if( resolved == null )
    {
      return null;
    }
    if( resolved instanceof PsiTypeParameter )
    {
      return resolved.getName();
    }
    final String qName = resolved.getQualifiedName();
    if( isRaw() )
    {
      return qName;
    }

    final PsiType[] typeArgs = myReferenceElement.getTypeArguments();
    if( typeArgs.length == 0 )
    {
      return qName;
    }

    StringBuilder builder = new StringBuilder();
    builder.append( qName ).append( "<" );
    for( int i = 0; i < typeArgs.length; i++ )
    {
      if( i > 0 )
      {
        builder.append( ", " );
      }
      final String typeArgCanonical = typeArgs[i].getCanonicalText();
      if( typeArgCanonical != null )
      {
        builder.append( typeArgCanonical );
      }
      else
      {
        return null;
      }
    }
    builder.append( ">" );
    return builder.toString();
  }

  public String getInternalCanonicalText()
  {
    return getCanonicalText();
  }

  public boolean isValid()
  {
    return myReferenceElement.isValid();
  }

  public boolean equalsToText( @NonNls String text )
  {
    return text.endsWith( getPresentableText() ) && //optimization
           text.equals( getCanonicalText() );
  }

  @NotNull
  public GlobalSearchScope getResolveScope()
  {
    return myReferenceElement.getResolveScope();
  }

  @NotNull
  public LanguageLevel getLanguageLevel()
  {
    return myLanguageLevel;
  }

  public PsiClassType setLanguageLevel( final LanguageLevel languageLevel )
  {
    return new GosuClassReferenceType( myReferenceElement, languageLevel );
  }
}
