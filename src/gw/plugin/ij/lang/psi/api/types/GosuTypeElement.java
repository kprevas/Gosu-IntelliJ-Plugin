package gw.plugin.ij.lang.psi.api.types;

import com.intellij.psi.PsiType;
import gw.plugin.ij.lang.psi.IGosuPsiElement;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public interface GosuTypeElement extends IGosuPsiElement
{
  PsiType getType();
}
