package gw.plugin.ij.intentions;

import com.intellij.codeInsight.daemon.impl.quickfix.ImportClassFixBase;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import gw.plugin.ij.lang.psi.api.types.GosuCodeReferenceElement;

/**
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuAddImportAction extends ImportClassFixBase<GosuCodeReferenceElement> {
  public GosuAddImportAction(GosuCodeReferenceElement ref) {
    super(ref);
  }

  @Override
  protected String getReferenceName(GosuCodeReferenceElement reference) {
    return reference.getReferenceName();
  }

  @Override
  protected PsiElement getReferenceNameElement(GosuCodeReferenceElement reference) {
    return null;
  }

  @Override
  protected boolean hasTypeParameters(GosuCodeReferenceElement reference) {
    return reference.getTypeArguments().length > 0;
  }

  @Override
  protected boolean isAccessible(PsiClass aClass, GosuCodeReferenceElement reference) {
    return true;
  }

  @Override
  protected String getQualifiedName(GosuCodeReferenceElement reference) {
    return reference.getCanonicalText();
  }

  @Override
  protected boolean isQualified(GosuCodeReferenceElement reference) {
    return reference.getQualifier() != null;
  }

  @Override
  protected boolean hasUnresolvedImportWhichCanImport(PsiFile psiFile, String name) {
//    if (!(psiFile instanceof GosuFile)) return false;
//    final GosuUsesStatement[] importStatements = ((GosuFile)psiFile).getUsesStatements();
//    for (GosuUsesStatement importStatement : importStatements) {
//      final GosuCodeReferenceElement importReference = importStatement.getImportReference();
//      if (importReference == null || importReference.resolve() != null) {
//        continue;
//      }
//      return true;
//    }
    return false;
  }
}
