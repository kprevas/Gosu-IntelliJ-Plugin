package gw.plugin.ij;

import com.intellij.codeInsight.template.FileTypeBasedContextType;

/**
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuTemplateContextType extends FileTypeBasedContextType {

  protected GosuTemplateContextType() {
    super("GOSU", "Gosu", GosuClassFileType.instance());
  }

}
