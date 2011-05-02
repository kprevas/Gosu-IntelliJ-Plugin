package gw.plugin.ij.lang.psi.api.types;

import gw.plugin.ij.lang.psi.api.expressions.IGosuReferenceExpression;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public interface GosuCodeReferenceElement extends IGosuReferenceExpression
{
  GosuCodeReferenceElement[] EMPTY_ARRAY = new GosuCodeReferenceElement[0];

  GosuCodeReferenceElement getQualifier();

  void setQualifier( GosuCodeReferenceElement newQualifier );
}
