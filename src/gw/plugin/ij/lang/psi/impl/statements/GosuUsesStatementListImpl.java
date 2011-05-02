package gw.plugin.ij.lang.psi.impl.statements;

import gw.lang.parser.statements.IUsesStatementList;
import gw.plugin.ij.lang.parser.GosuCompositeElement;
import gw.plugin.ij.lang.psi.api.statements.IGosuStatement;
import gw.plugin.ij.lang.psi.impl.GosuElementVisitor;
import gw.plugin.ij.lang.psi.impl.GosuPsiElementImpl;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuUsesStatementListImpl extends GosuPsiElementImpl<IUsesStatementList> implements IGosuStatement
{
  public GosuUsesStatementListImpl( GosuCompositeElement<IUsesStatementList> node )
  {
    super( node );
  }

  public void accept( GosuElementVisitor visitor )
  {
    visitor.visitUsesStatementList( this );
  }
}
