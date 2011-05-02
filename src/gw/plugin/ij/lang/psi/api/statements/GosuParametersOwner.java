package gw.plugin.ij.lang.psi.api.statements;

import gw.plugin.ij.lang.psi.IGosuPsiElement;
import gw.plugin.ij.lang.psi.api.statements.params.IGosuParameter;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public interface GosuParametersOwner extends IGosuPsiElement
{
  IGosuParameter[] getParameters();
}
