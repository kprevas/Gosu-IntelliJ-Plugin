package gw.plugin.ij.lang.psi.api.types;

import com.intellij.psi.PsiTypeParameter;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuTypeDefinition;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public interface GosuTypeParameter extends GosuTypeDefinition, PsiTypeParameter
{
  public static final GosuTypeParameter[] EMPTY_ARRAY = new GosuTypeParameter[0];
}
