package gw.plugin.ij.lang.psi;

import gw.plugin.ij.lang.psi.impl.expressions.GosuTypeLiteralImpl;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public interface GosuFile extends GosuFileBase
{
  void addImport( GosuTypeLiteralImpl tl );
}
