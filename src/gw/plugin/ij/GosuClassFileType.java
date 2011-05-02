package gw.plugin.ij;

import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import gw.plugin.ij.highlighter.GosuEditorHighlighter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuClassFileType extends LanguageFileType
{
  public static final Icon CLASS_LOGO = GosuIcons.FILE_CLASS;

  public static final String EXT = "gs";

  private static GosuClassFileType INSTANCE = new GosuClassFileType();

  public static GosuClassFileType instance()
  {
    return INSTANCE;
  }

  private GosuClassFileType()
  {
    super( GosuLanguage.instance() );
  }

  @NotNull
  public String getName()
  {
    return "Gosu Class";
  }

  @NotNull
  public String getDescription()
  {
    return "Gosu class source files";
  }

  @NotNull
  public String getDefaultExtension()
  {
    return EXT;
  }

  public Icon getIcon()
  {
    return CLASS_LOGO;
  }

  public boolean isJVMDebuggingSupported()
  {
    return true;
  }

  public EditorHighlighter getEditorHighlighter( @Nullable Project project,
                                                 @Nullable VirtualFile virtualFile,
                                                 @NotNull EditorColorsScheme colors )
  {
    return new GosuEditorHighlighter( colors, project, virtualFile );
  }

}