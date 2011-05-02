package gw.plugin.ij.commenting;

import com.intellij.lang.CodeDocumentationAwareCommenter;
import com.intellij.psi.PsiComment;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.Nullable;

/**
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuCommenter implements CodeDocumentationAwareCommenter {
  public String getLineCommentPrefix() {
    return "//";
  }

  public String getBlockCommentPrefix() {
    return "/*";
  }

  public String getBlockCommentSuffix() {
    return "*/";
  }

  public String getCommentedBlockCommentPrefix() {
    return null;
  }

  public String getCommentedBlockCommentSuffix() {
    return null;
  }

  @Nullable
  public IElementType getLineCommentTokenType() {
    return null;
  }

  @Nullable
  public IElementType getBlockCommentTokenType() {
    return null;
  }

  @Nullable
  public IElementType getDocumentationCommentTokenType() {
    return null;
  }

  @Nullable
  public String getDocumentationCommentPrefix() {
    return "/**";
  }

  @Nullable
  public String getDocumentationCommentLinePrefix() {
    return "*";
  }

  @Nullable
  public String getDocumentationCommentSuffix() {
    return "*/";
  }

  public boolean isDocumentationComment(PsiComment element) {
    return element.getText().startsWith(getDocumentationCommentPrefix());
  }
}
