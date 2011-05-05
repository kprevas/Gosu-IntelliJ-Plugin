package gw.plugin.ij.view;

import com.intellij.lang.Language;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.FileViewProviderFactory;
import com.intellij.psi.PsiManager;

public class GosuFileViewProviderFactory implements FileViewProviderFactory {
  public GosuFileViewProviderFactory() {
    System.out.println();
  }

  @Override
  public FileViewProvider createFileViewProvider(VirtualFile file, Language language, PsiManager manager, boolean physical) {
    return new GosuFileViewProvider(manager, file, physical, language);
  }
}
