package gw.plugin.ij.lang.psi.impl.statements.typedef;

import gw.lang.parser.statements.IClassStatement;
import gw.plugin.ij.lang.parser.GosuCompositeElement;
import gw.plugin.ij.lang.parser.GosuElementTypes;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuInterfaceDefinition;
import gw.plugin.ij.lang.psi.stubs.GosuTypeDefinitionStub;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuInterfaceDefinitionImpl extends GosuTypeDefinitionImpl implements GosuInterfaceDefinition
{
  public GosuInterfaceDefinitionImpl( GosuTypeDefinitionStub stub )
  {
    super( stub, GosuElementTypes.INTERFACE_DEFINITION );
  }

  public GosuInterfaceDefinitionImpl( GosuCompositeElement<IClassStatement> node )
  {
    super( node );
  }

  public boolean isInterface()
  {
    return true;
  }
}