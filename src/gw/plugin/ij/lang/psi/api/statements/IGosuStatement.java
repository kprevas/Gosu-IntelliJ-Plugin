package gw.plugin.ij.lang.psi.api.statements;

import com.intellij.psi.PsiStatement;
import com.intellij.util.IncorrectOperationException;
import gw.plugin.ij.lang.psi.IGosuPsiElement;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public interface IGosuStatement extends IGosuPsiElement, PsiStatement
{
  public static final IGosuStatement[] EMPTY_ARRAY = new IGosuStatement[0];

  <T extends IGosuStatement> T replaceWithStatement( T statement );

  void removeStatement() throws IncorrectOperationException;
}
