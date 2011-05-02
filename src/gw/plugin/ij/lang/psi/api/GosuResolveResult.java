package gw.plugin.ij.lang.psi.api;

import com.intellij.psi.JavaResolveResult;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiSubstitutor;
import gw.plugin.ij.lang.psi.IGosuPsiElement;
import org.jetbrains.annotations.Nullable;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public interface GosuResolveResult extends JavaResolveResult
{
  public static final GosuResolveResult[] EMPTY_ARRAY = new GosuResolveResult[0];

  boolean isAccessible();

  boolean isStaticsOK();

  @Nullable
  IGosuPsiElement getCurrentFileResolveContext();

  PsiSubstitutor getSubstitutor();

  public static final GosuResolveResult EMPTY_RESULT =
    new GosuResolveResult()
    {
      public boolean isAccessible()
      {
        return false;
      }

      @Override
      public boolean isStaticsScopeCorrect()
      {
        return false;
      }

      @Override
      public PsiElement getCurrentFileResolveScope()
      {
        return null;
      }

      public IGosuPsiElement getCurrentFileResolveContext()
      {
        return null;
      }

      public boolean isStaticsOK()
      {
        return true;
      }

      public PsiSubstitutor getSubstitutor()
      {
        return PsiSubstitutor.EMPTY;
      }

      @Override
      public boolean isPackagePrefixPackageReference()
      {
        return false;
      }

      @Nullable
      public PsiElement getElement()
      {
        return null;
      }

      public boolean isValidResult()
      {
        return false;
      }
    };
}
