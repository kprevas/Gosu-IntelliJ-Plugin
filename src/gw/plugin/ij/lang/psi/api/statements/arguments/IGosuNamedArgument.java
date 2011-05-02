package gw.plugin.ij.lang.psi.api.statements.arguments;

import gw.plugin.ij.lang.psi.IGosuPsiElement;
import gw.plugin.ij.lang.psi.api.expressions.IGosuExpression;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public interface IGosuNamedArgument extends IGosuPsiElement
{
  IGosuNamedArgument[] EMPTY_ARRAY = new IGosuNamedArgument[0];

  IGosuExpression getExpression();

  String getLabelName();
}
