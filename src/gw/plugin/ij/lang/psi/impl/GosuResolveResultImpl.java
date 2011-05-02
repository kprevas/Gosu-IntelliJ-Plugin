package gw.plugin.ij.lang.psi.impl;

import gw.plugin.ij.lang.psi.api.GosuResolveResult;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiSubstitutor;
import gw.plugin.ij.lang.psi.IGosuPsiElement;
import org.jetbrains.annotations.Nullable;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuResolveResultImpl implements GosuResolveResult
{
  private PsiElement myElement;
  private boolean myIsAccessible;
  private boolean myIsStaticsOK;
  private PsiSubstitutor mySubstitutor;

  private IGosuPsiElement myCurrentFileResolveContext;

  public GosuResolveResultImpl( PsiElement element, boolean isAccessible )
  {
    this( element, null, PsiSubstitutor.EMPTY, isAccessible, true );
  }

  public GosuResolveResultImpl( PsiElement element,
                                IGosuPsiElement context,
                                PsiSubstitutor substitutor,
                                boolean isAccessible,
                                boolean staticsOK )
  {
    myCurrentFileResolveContext = context;
    myElement = element; //element instanceof PsiClass ? GosuClassSubstitutor.getSubstitutedClass( (PsiClass)element ) : element;
    myIsAccessible = isAccessible;
    mySubstitutor = substitutor;
    myIsStaticsOK = staticsOK;
  }

  public PsiSubstitutor getSubstitutor()
  {
    return mySubstitutor;
  }

  public boolean isPackagePrefixPackageReference()
  {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public boolean isAccessible()
  {
    return myIsAccessible;
  }

  @Override
  public boolean isStaticsOK()
  {
    return myIsStaticsOK;
  }

  public boolean isStaticsScopeCorrect()
  {
    return myIsStaticsOK;
  }

  public PsiElement getCurrentFileResolveScope()
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Nullable
  public PsiElement getElement()
  {
    return myElement;
  }

  public boolean isValidResult()
  {
    return isAccessible();
  }

  public boolean equals( Object o )
  {
    if( this == o )
    {
      return true;
    }
    if( o == null || getClass() != o.getClass() )
    {
      return false;
    }

    GosuResolveResultImpl that = (GosuResolveResultImpl)o;

    return myIsAccessible == that.myIsAccessible &&
           myElement.getManager().areElementsEquivalent( myElement, that.myElement );

  }

  public int hashCode()
  {
    int result = 0;
    if( myElement instanceof PsiNamedElement )
    {
      String name = ((PsiNamedElement)myElement).getName();
      if( name != null )
      {
        result = name.hashCode();
      }
    }
    result = 31 * result + (myIsAccessible ? 1 : 0);
    return result;
  }

  public IGosuPsiElement getCurrentFileResolveContext()
  {
    return myCurrentFileResolveContext;
  }
}
