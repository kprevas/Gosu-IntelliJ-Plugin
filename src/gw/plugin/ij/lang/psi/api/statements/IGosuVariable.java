package gw.plugin.ij.lang.psi.api.statements;

import com.intellij.psi.PsiStatement;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiVariable;
import com.intellij.util.IncorrectOperationException;
import gw.plugin.ij.lang.psi.GosuNamedElement;
import gw.plugin.ij.lang.psi.api.expressions.IGosuExpression;
import gw.plugin.ij.lang.psi.api.types.GosuTypeElement;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public interface IGosuVariable extends PsiVariable, GosuNamedElement, PsiStatement
{
  IGosuVariable[] EMPTY_ARRAY = new IGosuVariable[0];

  IGosuExpression getInitializerGosu();

  void setType( PsiType type ) throws IncorrectOperationException;

  GosuTypeElement getTypeElementGosu();

}
