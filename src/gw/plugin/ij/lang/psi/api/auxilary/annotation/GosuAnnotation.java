package gw.plugin.ij.lang.psi.api.auxilary.annotation;

import com.intellij.psi.PsiAnnotation;
import gw.plugin.ij.lang.psi.api.types.GosuCodeReferenceElement;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public interface GosuAnnotation extends PsiAnnotation
{
  public static final GosuAnnotation[] EMPTY_ARRAY = new GosuAnnotation[0];

  GosuCodeReferenceElement getClassReference();

  String getShortName();
}
