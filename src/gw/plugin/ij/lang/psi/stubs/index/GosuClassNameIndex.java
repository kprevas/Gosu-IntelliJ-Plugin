package gw.plugin.ij.lang.psi.stubs.index;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import gw.plugin.ij.lang.psi.GosuFile;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuClassNameIndex extends StringStubIndexExtension<GosuFile>
{
  public static final StubIndexKey<String, GosuFile> KEY = StubIndexKey.createIndexKey( "gosu.class" );

  public StubIndexKey<String, GosuFile> getKey()
  {
    return KEY;
  }
}
