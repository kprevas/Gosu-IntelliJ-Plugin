package gw.plugin.ij.lang.psi.impl.statements;

import com.intellij.psi.stubs.StubElement;
import gw.lang.parser.IStatement;
import gw.plugin.ij.lang.parser.GosuCompositeElement;
import gw.plugin.ij.lang.psi.api.statements.IGosuStatement;
import gw.plugin.ij.lang.psi.impl.GosuBaseElementImpl;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuBaseStatementImpl<E extends IStatement> extends GosuBaseElementImpl<E, StubElement> implements IGosuStatement
{
  public GosuBaseStatementImpl( GosuCompositeElement<E> node )
  {
    super( node );
  }
}
