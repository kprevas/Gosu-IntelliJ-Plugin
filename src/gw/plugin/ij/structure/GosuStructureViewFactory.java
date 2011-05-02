package gw.plugin.ij.structure;

import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.TreeBasedStructureViewBuilder;
import com.intellij.ide.structureView.impl.java.JavaFileTreeModel;
import com.intellij.lang.PsiStructureViewFactory;
import com.intellij.psi.PsiFile;
import gw.plugin.ij.lang.psi.GosuFileBase;
import org.jetbrains.annotations.NotNull;

/**
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuStructureViewFactory implements PsiStructureViewFactory {

  public StructureViewBuilder getStructureViewBuilder(final PsiFile psiFile) {
    return new TreeBasedStructureViewBuilder() {
      @NotNull
      public StructureViewModel createStructureViewModel() {
        return new GosuStructureViewModel((GosuFileBase) psiFile);
      }
    };
  }

}