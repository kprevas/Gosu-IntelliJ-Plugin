package gw.plugin.ij;

import com.intellij.ide.util.JavaUtil;
import com.intellij.ide.util.newProjectWizard.SourceRootFinder;
import com.intellij.openapi.util.Pair;

import java.io.File;
import java.util.List;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuSourceRootFinder implements SourceRootFinder
{
  public List<Pair<File, String>> findRoots( File dir )
  {
    return JavaUtil.suggestRoots( dir, GosuClassFileType.instance() );
  }

  public String getDescription()
  {
    return null;
  }

  public String getName()
  {
    return "Gosu";
  }
}
