package gw.plugin.ij.lang.psi.api.util;

import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import gw.plugin.ij.lang.psi.IGosuPsiElement;
import gw.plugin.ij.lang.psi.api.statements.IGosuStatement;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public interface GosuStatementOwner extends IGosuPsiElement
{
  IGosuStatement addStatementBefore( IGosuStatement statement, IGosuStatement anchor ) throws IncorrectOperationException;

  void removeElements( PsiElement[] elements ) throws IncorrectOperationException;

}
