package gw.plugin.ij.lang.psi.impl.statements.typedef;

import gw.lang.parser.statements.ISuperTypeClause;
import gw.plugin.ij.lang.parser.GosuCompositeElement;
import gw.plugin.ij.lang.parser.GosuElementTypes;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuExtendsClause;
import gw.plugin.ij.lang.psi.impl.GosuElementVisitor;
import gw.plugin.ij.lang.psi.stubs.GosuReferenceListStub;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuExtendsClauseImpl extends GosuReferenceListImpl implements GosuExtendsClause
{
  public GosuExtendsClauseImpl( GosuCompositeElement<ISuperTypeClause> node )
  {
    super( node );
  }

  public GosuExtendsClauseImpl( final GosuReferenceListStub stub )
  {
    super( stub, GosuElementTypes.EXTENDS_CLAUSE );
  }

  public void accept( GosuElementVisitor visitor )
  {
    visitor.visitExtendsClause( this );
  }
}
