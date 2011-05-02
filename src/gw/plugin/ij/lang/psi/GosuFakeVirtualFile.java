package gw.plugin.ij.lang.psi;

import com.intellij.testFramework.LightVirtualFile;
import gw.plugin.ij.GosuLanguage;

/**
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuFakeVirtualFile extends LightVirtualFile
{
  public GosuFakeVirtualFile( String src, String name )
  {
    super( name, GosuLanguage.instance(), src );
  }
}
