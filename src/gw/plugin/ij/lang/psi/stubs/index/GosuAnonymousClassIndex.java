package gw.plugin.ij.lang.psi.stubs.index;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuAnonymousClassDefinition;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuAnonymousClassIndex extends StringStubIndexExtension<GosuAnonymousClassDefinition>
{
  public static final StubIndexKey<String, GosuAnonymousClassDefinition> KEY = StubIndexKey.createIndexKey( "gosu.anonymous.class" );

  public StubIndexKey<String, GosuAnonymousClassDefinition> getKey()
  {
    return KEY;
  }
}
