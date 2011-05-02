package gw.plugin.ij;

import com.intellij.openapi.util.IconLoader;
import com.intellij.util.IconUtil;

import javax.swing.*;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public interface GosuIcons
{
  Icon FILE_CLASS = IconLoader.findIcon( "/gw/plugin/ij/FileClass.png" );
  Icon FILE_PROG = IconLoader.findIcon( "/gw/plugin/ij/FileProgram.png" );
  Icon FILE_ENH = IconLoader.findIcon( "/gw/plugin/ij/FileEnhancement.png" );
  Icon FILE_TEMP = IconLoader.findIcon( "/gw/plugin/ij/FileTemplate.png" );
  Icon FILE_RULE = IconLoader.findIcon( "/gw/plugin/ij/FileRule.png" );

  Icon FIELD = IconLoader.findIcon( "/gw/plugin/ij/field.png" );
  Icon METHOD = IconLoader.findIcon( "/gw/plugin/ij/method.png" );
  Icon PROPERTY = IconLoader.findIcon( "/gw/plugin/ij/property.png" );
  Icon VARIABLE = IconLoader.findIcon( "/gw/plugin/ij/variable.png" );
}
