package gw.plugin.ij.lang.psi.api.statements.params;

import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiParameter;
import gw.plugin.ij.lang.psi.api.expressions.IGosuExpression;
import gw.plugin.ij.lang.psi.api.statements.IGosuVariable;
import gw.plugin.ij.lang.psi.api.types.GosuTypeElement;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public interface IGosuParameter extends PsiParameter, IGosuVariable
{
  public static final IGosuParameter[] EMPTY_ARRAY = new IGosuParameter[0];

  GosuTypeElement getTypeElementGosu();

  IGosuExpression getDefaultInitializer();

  PsiModifierList getModifierList();

  boolean isOptional();
}
