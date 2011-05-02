package gw.plugin.ij.lang.psi.impl.expressions;

import com.intellij.psi.JavaResolveResult;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiSubstitutor;
import gw.plugin.ij.lang.psi.IGosuPsiElement;
import gw.plugin.ij.lang.psi.api.GosuResolveResult;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
*/
class JavaToGosuResolveResult implements GosuResolveResult
{
  private final JavaResolveResult _r;

  public JavaToGosuResolveResult( JavaResolveResult r )
  {
    _r = r;
  }

  @Override
  public boolean isAccessible()
  {
    return _r.isAccessible();
  }

  @Override
  public boolean isStaticsScopeCorrect()
  {
    return _r.isStaticsScopeCorrect();
  }

  @Override
  public PsiElement getCurrentFileResolveScope()
  {
    return _r.getCurrentFileResolveScope();
  }

  @Override
  public boolean isStaticsOK()
  {
    return _r.isStaticsScopeCorrect();
  }

  @Override
  public IGosuPsiElement getCurrentFileResolveContext()
  {
    return (IGosuPsiElement)_r.getCurrentFileResolveScope();
  }

  @Override
  public PsiSubstitutor getSubstitutor()
  {
    return _r.getSubstitutor();
  }

  @Override
  public boolean isPackagePrefixPackageReference()
  {
    return _r.isPackagePrefixPackageReference();
  }

  @Override
  public PsiElement getElement()
  {
    return _r.getElement();
  }

  @Override
  public boolean isValidResult()
  {
    return _r.isValidResult();
  }
}
