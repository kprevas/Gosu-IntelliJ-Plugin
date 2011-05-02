package gw.plugin.ij.lang.psi.stubs;

import com.intellij.psi.stubs.NamedStub;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuMethod;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public interface GosuMethodStub extends NamedStub<GosuMethod>
{
  String[] getAnnotations();

  String[] getNamedParameters();
}
