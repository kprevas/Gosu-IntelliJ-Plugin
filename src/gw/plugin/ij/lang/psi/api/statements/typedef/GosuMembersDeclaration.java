package gw.plugin.ij.lang.psi.api.statements.typedef;

import com.intellij.psi.PsiModifierListOwner;
import gw.plugin.ij.lang.psi.IGosuPsiElement;
import gw.plugin.ij.lang.psi.api.auxilary.IGosuModifierList;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public interface GosuMembersDeclaration extends IGosuPsiElement, PsiModifierListOwner
{
  GosuMembersDeclaration[] EMPTY_ARRAY = new GosuMembersDeclaration[0];

  IGosuMember[] getMembers();

  IGosuModifierList getModifierList();
}
