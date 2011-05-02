package gw.plugin.ij;

import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.vfs.VirtualFile;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuProblemFileHighlightFilter implements Condition<VirtualFile>
{
  public boolean value( VirtualFile virtualFile )
  {
    return GosuFileTypes.TYPES.contains( FileTypeManager.getInstance().getFileTypeByFile( virtualFile ) );
  }
}
