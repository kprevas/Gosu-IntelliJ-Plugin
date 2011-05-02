
package gw.plugin.ij.lang.psi.stubs.index;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuMethod;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
` */
public class GosuMethodNameIndex extends StringStubIndexExtension<GosuMethod>
{
  public static final StubIndexKey<String, GosuMethod> KEY = StubIndexKey.createIndexKey( "Gosu.method.name" );

  public StubIndexKey<String, GosuMethod> getKey()
  {
    return KEY;
  }
}