package gw.plugin.ij.lang.psi.stubs;

import com.intellij.psi.PsiFile;
import com.intellij.psi.stubs.DefaultStubBuilder;
import com.intellij.psi.stubs.StubElement;
import gw.plugin.ij.lang.psi.GosuFile;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuFileStubBuilder extends DefaultStubBuilder {

  protected StubElement createStubForFile(final PsiFile file) {
    if (file instanceof GosuFile) {
      return new GosuFileStub((GosuFile) file);
    }

    return super.createStubForFile(file);
  }

}