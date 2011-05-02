package gw.plugin.ij.lang.psi;

import com.intellij.psi.PsiElement;
import gw.plugin.ij.lang.psi.impl.GosuElementVisitor;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public interface IGosuPsiElement extends PsiElement
{
  void accept( GosuElementVisitor visitor );

  void acceptChildren( GosuElementVisitor visitor );
}
