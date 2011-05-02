package gw.plugin.ij.lang.psi.api.types;

import com.intellij.psi.PsiTypeParameterList;
import gw.plugin.ij.lang.psi.IGosuPsiElement;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public interface GosuTypeParameterList extends IGosuPsiElement, PsiTypeParameterList
{
  GosuTypeParameter[] getTypeParameters();
}
