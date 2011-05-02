package gw.plugin.ij.lang.psi;

import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiNamedElement;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public interface GosuNamedElement extends PsiNamedElement, IGosuPsiElement
{
  PsiIdentifier getNameIdentifierGosu();
}
