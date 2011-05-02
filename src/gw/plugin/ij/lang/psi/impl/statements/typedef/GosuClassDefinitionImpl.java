package gw.plugin.ij.lang.psi.impl.statements.typedef;

import gw.lang.parser.statements.IClassStatement;
import gw.plugin.ij.lang.parser.GosuCompositeElement;
import gw.plugin.ij.lang.parser.GosuElementTypes;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuClassDefinition;
import gw.plugin.ij.lang.psi.stubs.GosuTypeDefinitionStub;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuClassDefinitionImpl extends GosuTypeDefinitionImpl implements GosuClassDefinition
{
  public GosuClassDefinitionImpl( GosuCompositeElement<IClassStatement> node )
  {
    super( node );
  }

  public GosuClassDefinitionImpl( final GosuTypeDefinitionStub stub )
  {
    super( stub, GosuElementTypes.ELEM_TYPE_ClassStatement );
  }
}
