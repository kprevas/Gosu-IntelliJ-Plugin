package gw.plugin.ij.lang.psi.stubs.impl;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuReferenceList;
import gw.plugin.ij.lang.psi.stubs.GosuReferenceListStub;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuReferenceListStubImpl extends StubBase<GosuReferenceList> implements GosuReferenceListStub
{
  private final String[] myRefNames;

  public GosuReferenceListStubImpl( final StubElement parentStub, IStubElementType elemtType, final String[] refNames )
  {
    super( parentStub, elemtType );
    myRefNames = refNames;
  }

  public String[] getBaseClasses()
  {
    return myRefNames;
  }
}
