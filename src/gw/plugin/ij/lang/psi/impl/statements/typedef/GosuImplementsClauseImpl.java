package gw.plugin.ij.lang.psi.impl.statements.typedef;

import gw.lang.parser.statements.IInterfacesClause;
import gw.plugin.ij.lang.parser.GosuCompositeElement;
import gw.plugin.ij.lang.parser.GosuElementTypes;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuImplementsClause;
import gw.plugin.ij.lang.psi.impl.GosuElementVisitor;
import gw.plugin.ij.lang.psi.stubs.GosuReferenceListStub;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuImplementsClauseImpl extends GosuReferenceListImpl implements GosuImplementsClause
{

  public GosuImplementsClauseImpl( GosuCompositeElement<IInterfacesClause> node )
  {
    super( node );
  }

  public GosuImplementsClauseImpl( final GosuReferenceListStub stub )
  {
    super( stub, GosuElementTypes.IMPLEMENTS_CLAUSE );
  }

  public void accept( GosuElementVisitor visitor )
  {
    visitor.visitImplementsClause( this );
  }
}
