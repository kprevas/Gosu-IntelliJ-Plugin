package gw.plugin.ij.view;

import com.intellij.lang.Language;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.SingleRootFileViewProvider;

public class GosuFileViewProvider extends SingleRootFileViewProvider {

  public GosuFileViewProvider(@org.jetbrains.annotations.NotNull PsiManager manager, @org.jetbrains.annotations.NotNull VirtualFile file) {
    super(manager, file);
  }

  public GosuFileViewProvider(@org.jetbrains.annotations.NotNull PsiManager manager, @org.jetbrains.annotations.NotNull VirtualFile virtualFile, boolean physical) {
    super(manager, virtualFile, physical);
  }

  protected GosuFileViewProvider(@org.jetbrains.annotations.NotNull PsiManager manager, @org.jetbrains.annotations.NotNull VirtualFile virtualFile, boolean physical, @org.jetbrains.annotations.NotNull Language language) {
    super(manager, virtualFile, physical, language);
  }

  public boolean supportsIncrementalReparse(final Language rootLanguage) {
    return true;
  }

}
