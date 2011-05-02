package gw.plugin.ij.lang.psi.api.statements.typedef;

import com.intellij.psi.PsiMember;
import gw.plugin.ij.lang.psi.IGosuPsiElement;
import gw.plugin.ij.lang.psi.api.auxilary.IGosuModifierList;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public interface IGosuMember extends PsiMember, IGosuPsiElement
{
  public static final IGosuMember[] EMPTY_ARRAY = new IGosuMember[0];

  IGosuModifierList getModifierList();
}
