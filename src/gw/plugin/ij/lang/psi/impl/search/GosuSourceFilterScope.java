package gw.plugin.ij.lang.psi.impl.search;

import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.DelegatingGlobalSearchScope;
import com.intellij.psi.search.GlobalSearchScope;
import gw.plugin.ij.GosuClassFileType;
import org.jetbrains.annotations.NotNull;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuSourceFilterScope extends DelegatingGlobalSearchScope
{
  private final ProjectFileIndex myIndex;

  public GosuSourceFilterScope( @NotNull final GlobalSearchScope delegate )
  {
    super( delegate, "gosu.sourceFilter" );
    myIndex = ProjectRootManager.getInstance( getProject() ).getFileIndex();
  }

  public boolean contains( final VirtualFile file )
  {
    return super.contains( file ) &&
           myIndex.isInSourceContent( file ) &&
           GosuClassFileType.instance() == file.getFileType();
  }
}
