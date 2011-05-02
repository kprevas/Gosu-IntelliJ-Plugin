package gw.plugin.ij.lang.psi;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassOwner;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiImportHolder;
import com.intellij.psi.PsiModifierListOwner;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public interface GosuFileBase extends PsiFile, PsiClassOwner, PsiImportHolder, IGosuPsiElement, PsiModifierListOwner
{
  PsiClass getPsiClass();
}
