package gw.plugin.ij.lang.psi.stubs.index;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuReferenceList;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuDirectInheritorsIndex extends StringStubIndexExtension<GosuReferenceList>
{
  public static final StubIndexKey<String, GosuReferenceList> KEY = StubIndexKey.createIndexKey( "gosu.class.super" );

  public StubIndexKey<String, GosuReferenceList> getKey()
  {
    return KEY;
  }
}