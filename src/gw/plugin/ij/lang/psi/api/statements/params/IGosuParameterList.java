package gw.plugin.ij.lang.psi.api.statements.params;

import com.intellij.psi.PsiModifierListOwner;
import com.intellij.psi.PsiParameterList;
import gw.plugin.ij.lang.psi.IGosuPsiElement;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public interface IGosuParameterList extends IGosuPsiElement, PsiParameterList, PsiModifierListOwner
{
  IGosuParameter[] getParameters();

  void addParameterToEnd( IGosuParameter parameter );

  void addParameterToHead( IGosuParameter parameter );

  int getParameterNumber( IGosuParameter parameter );
}
