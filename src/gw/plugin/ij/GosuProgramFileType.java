package gw.plugin.ij;

import com.intellij.openapi.fileTypes.LanguageFileType;

import javax.swing.*;

import org.jetbrains.annotations.NotNull;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuProgramFileType extends LanguageFileType
{
  public static final Icon PROGRAM_LOGO = GosuIcons.FILE_PROG;

  public static final String EXT = "gsp";

  private static GosuProgramFileType INSTANCE = new GosuProgramFileType();

  public static GosuProgramFileType instance()
  {
    return INSTANCE;
  }

  private GosuProgramFileType()
  {
    super( GosuLanguage.instance() );
  }

  @NotNull
  public String getName()
  {
    return "Gosu Program";
  }

  @NotNull
  public String getDescription()
  {
    return "Gosu program source files";
  }

  @NotNull
  public String getDefaultExtension()
  {
    return EXT;
  }

  public Icon getIcon()
  {
    return PROGRAM_LOGO;
  }

  public boolean isJVMDebuggingSupported()
  {
    return true;
  }
}