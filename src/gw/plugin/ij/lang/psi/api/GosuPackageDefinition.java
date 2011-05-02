package gw.plugin.ij.lang.psi.api;

import gw.plugin.ij.lang.psi.api.statements.IGosuStatement;
import gw.plugin.ij.lang.psi.api.types.GosuCodeReferenceElement;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public interface GosuPackageDefinition extends IGosuStatement
{
  String getPackageName();

  GosuCodeReferenceElement getPackageReference();
}
