package gw.plugin.ij;

import com.intellij.codeInsight.template.FileTypeBasedContextType;

/**
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuProgramLiveTemplateContextType extends FileTypeBasedContextType {

  protected GosuProgramLiveTemplateContextType() {
    super("GOSU PROGRAM", "Gosu Program", GosuProgramFileType.instance());
  }

}
