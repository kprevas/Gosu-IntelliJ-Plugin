package gw.plugin.ij;

import com.intellij.codeInsight.template.FileTypeBasedContextType;

/**
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuClassLiveTemplateContextType extends FileTypeBasedContextType {

  protected GosuClassLiveTemplateContextType() {
    super("GOSU CLASS", "Gosu Class", GosuClassFileType.instance());
  }

}
