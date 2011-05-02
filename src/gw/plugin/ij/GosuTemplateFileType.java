package gw.plugin.ij;

import com.intellij.openapi.fileTypes.LanguageFileType;

import javax.swing.*;

import org.jetbrains.annotations.NotNull;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuTemplateFileType extends LanguageFileType
{
  public static final Icon TEMPLATE_LOGO = GosuIcons.FILE_TEMP;

  public static final String EXT = "gst";

  private static GosuTemplateFileType INSTANCE = new GosuTemplateFileType();

  public static GosuTemplateFileType instance()
  {
    return INSTANCE;
  }

  private GosuTemplateFileType()
  {
    super( GosuLanguage.instance() );
  }

  @NotNull
  public String getName()
  {
    return "Gosu Tempalte";
  }

  @NotNull
  public String getDescription()
  {
    return "Gosu template source files";
  }

  @NotNull
  public String getDefaultExtension()
  {
    return EXT;
  }

  public Icon getIcon()
  {
    return TEMPLATE_LOGO;
  }

  public boolean isJVMDebuggingSupported()
  {
    return true;
  }
}