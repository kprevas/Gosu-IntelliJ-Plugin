package gw.plugin.ij.lang.psi.api.util;

import com.intellij.util.IncorrectOperationException;
import gw.plugin.ij.lang.psi.IGosuPsiElement;
import gw.plugin.ij.lang.psi.api.statements.IGosuStatement;
import gw.plugin.ij.lang.psi.api.statements.IGosuVariable;
import gw.plugin.ij.lang.psi.api.statements.IGosuVariable;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public interface GosuVariableOwner extends IGosuPsiElement
{

  /**
   * Removes variable from its owner.
   *
   * @param variable to remove
   *
   * @throws com.intellij.util.IncorrectOperationException
   *          in case the operation cannot be performed
   */
  void removeVariable( IGosuVariable variable );

  /**
   * Adds new variable declaration after anchor spectified. If anchor == null, adds variable at owner's first position
   *
   * @param variable variable to insert
   * @param anchor      Anchor after which new variable will be placed
   *
   * @return inserted variable
   *
   * @throws com.intellij.util.IncorrectOperationException
   *          in case the operation cannot be performed
   */
  IGosuVariable addVariableBefore( IGosuVariable variable, IGosuStatement anchor ) throws IncorrectOperationException;
}
