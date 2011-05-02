package gw.plugin.ij.lang.psi.api.statements;

import com.intellij.psi.PsiField;
import com.intellij.psi.StubBasedPsiElement;
import gw.plugin.ij.lang.psi.api.statements.typedef.IGosuMember;
import gw.plugin.ij.lang.psi.stubs.GosuFieldStub;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public interface IGosuField extends IGosuVariable, IGosuMember, PsiField, StubBasedPsiElement<GosuFieldStub>
{
  IGosuField[] EMPTY_ARRAY = new IGosuField[0];
}
