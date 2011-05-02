package gw.plugin.ij.lang.psi.api.expressions;

import com.intellij.psi.PsiType;
import gw.plugin.ij.lang.psi.IGosuPsiElement;
import gw.plugin.ij.lang.psi.api.auxilary.annotation.GosuAnnotationMemberValue;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public interface IGosuExpression extends IGosuPsiElement, GosuAnnotationMemberValue
{
  IGosuExpression[] EMPTY_ARRAY = new IGosuExpression[0];

  PsiType getType();
}
