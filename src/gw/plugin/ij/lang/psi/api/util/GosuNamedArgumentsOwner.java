package gw.plugin.ij.lang.psi.api.util;

import gw.plugin.ij.lang.psi.api.statements.arguments.IGosuNamedArgument;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public interface GosuNamedArgumentsOwner
{
  IGosuNamedArgument[] getNamedArguments();

  IGosuNamedArgument findNamedArgument( String label );
}
