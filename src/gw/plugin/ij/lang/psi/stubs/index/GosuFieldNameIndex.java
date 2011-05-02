package gw.plugin.ij.lang.psi.stubs.index;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import gw.plugin.ij.lang.psi.api.statements.IGosuField;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuFieldNameIndex extends StringStubIndexExtension<IGosuField>
{
  public static final StubIndexKey<String, IGosuField> KEY = StubIndexKey.createIndexKey( "gosu.field.name" );

  public StubIndexKey<String, IGosuField> getKey()
  {
    return KEY;
  }
}
