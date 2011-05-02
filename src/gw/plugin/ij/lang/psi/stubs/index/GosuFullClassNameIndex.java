package gw.plugin.ij.lang.psi.stubs.index;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.IntStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import gw.plugin.ij.lang.psi.GosuFile;
import gw.plugin.ij.lang.psi.impl.search.GosuSourceFilterScope;

import java.util.Collection;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuFullClassNameIndex extends IntStubIndexExtension<GosuFile>
{
  public static final StubIndexKey<Integer, GosuFile> KEY = StubIndexKey.createIndexKey( "gosu.fqn" );

  private static final GosuFullClassNameIndex ourInstance = new GosuFullClassNameIndex();

  public static GosuFullClassNameIndex getInstance()
  {
    return ourInstance;
  }

  @Override
  public int getVersion()
  {
    return super.getVersion() + 1;
  }

  public StubIndexKey<Integer, GosuFile> getKey()
  {
    return KEY;
  }

  public Collection<GosuFile> get( final Integer integer, final Project project, final GlobalSearchScope scope )
  {
    return super.get( integer, project, new GosuSourceFilterScope( scope ) );
  }
}