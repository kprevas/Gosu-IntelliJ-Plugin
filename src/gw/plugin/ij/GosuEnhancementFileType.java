package gw.plugin.ij;

import com.intellij.openapi.fileTypes.LanguageFileType;

import javax.swing.*;

import org.jetbrains.annotations.NotNull;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuEnhancementFileType extends LanguageFileType
{
  public static final Icon ENHANCEMENT_LOGO = GosuIcons.FILE_ENH;

  public static final String EXT = "gsx";

  private static GosuEnhancementFileType INSTANCE = new GosuEnhancementFileType();

  public static GosuEnhancementFileType instance()
  {
    return INSTANCE;
  }

  private GosuEnhancementFileType()
  {
    super( GosuLanguage.instance() );
  }

  @NotNull
  public String getName()
  {
    return "Gosu Enhancement";
  }

  @NotNull
  public String getDescription()
  {
    return "Gosu enhancement source files";
  }

  @NotNull
  public String getDefaultExtension()
  {
    return EXT;
  }

  public Icon getIcon()
  {
    return ENHANCEMENT_LOGO;
  }

  public boolean isJVMDebuggingSupported()
  {
    return true;
  }
}