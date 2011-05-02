package gw.plugin.ij.lang.psi.api.types;

import gw.plugin.ij.lang.psi.IGosuPsiElement;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public interface GosuTypeArgumentList extends IGosuPsiElement
{
  GosuTypeElement[] getTypeArgumentElements();
}
