package gw.plugin.ij.lang.psi.api.expressions;

import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiQualifiedReference;
import com.intellij.psi.PsiType;
import gw.lang.parser.IParsedElement;
import gw.plugin.ij.lang.psi.IGosuPsiElement;
import gw.plugin.ij.lang.psi.api.types.GosuTypeArgumentList;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public interface IGosuReferenceExpression<T extends IParsedElement> extends IGosuExpression, IGosuPsiElement, PsiPolyVariantReference, PsiQualifiedReference
{
  PsiType[] getTypeArguments();

  GosuTypeArgumentList getTypeArgumentList();
}
