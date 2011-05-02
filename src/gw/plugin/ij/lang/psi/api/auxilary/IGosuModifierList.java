package gw.plugin.ij.lang.psi.api.auxilary;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.StubBasedPsiElement;
import gw.plugin.ij.lang.psi.IGosuPsiElement;
import gw.plugin.ij.lang.psi.api.auxilary.annotation.GosuAnnotation;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public interface IGosuModifierList extends IGosuPsiElement, PsiModifierList
{
  PsiElement[] getModifiers();

  boolean hasExplicitVisibilityModifiers();

  GosuAnnotation[] getAnnotations();
}
