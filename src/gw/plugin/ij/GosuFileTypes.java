package gw.plugin.ij;

import com.intellij.openapi.fileTypes.FileType;

import java.util.ArrayList;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuFileTypes
{
  public static final ArrayList<FileType> TYPES = new ArrayList<FileType>();
  static
  {
    TYPES.add( GosuClassFileType.instance() );
    TYPES.add( GosuProgramFileType.instance() );
//    TYPES.add( GosuTemplateFileType.instance() );
//    TYPES.add( GosuEnhancementFileType.instance() );
    TYPES.trimToSize();
  }
}
