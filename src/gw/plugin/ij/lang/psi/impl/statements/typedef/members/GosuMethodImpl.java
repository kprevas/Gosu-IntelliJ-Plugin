package gw.plugin.ij.lang.psi.impl.statements.typedef.members;

import com.intellij.psi.StubBasedPsiElement;
import gw.plugin.ij.lang.parser.GosuCompositeElement;
import gw.plugin.ij.lang.parser.GosuElementTypes;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuMethod;
import gw.plugin.ij.lang.psi.stubs.GosuMethodStub;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuMethodImpl extends GosuMethodBaseImpl<GosuMethodStub> implements GosuMethod, StubBasedPsiElement<GosuMethodStub>
{
  public GosuMethodImpl( GosuCompositeElement node )
  {
    super( node );
  }

  public GosuMethodImpl( GosuMethodStub stub )
  {
    super( stub, GosuElementTypes.METHOD_DEFINITION );
  }

  @Override
  public String[] getNamedParametersArray()
  {
    final GosuMethodStub stub = getStub();
    if( stub != null )
    {
      return stub.getNamedParameters();
    }
    return super.getNamedParametersArray();
  }

  @Override
  public String getName()
  {
    final GosuMethodStub stub = getStub();
    if( stub != null )
    {
      return stub.getName();
    }
    return super.getName();
  }
}