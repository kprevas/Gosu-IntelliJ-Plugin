package gw.plugin.ij.lang.psi.api.statements.typedef;

import com.intellij.psi.PsiClassType;
import com.intellij.psi.StubBasedPsiElement;
import gw.plugin.ij.lang.psi.IGosuPsiElement;
import gw.plugin.ij.lang.psi.api.types.GosuCodeReferenceElement;
import gw.plugin.ij.lang.psi.stubs.GosuReferenceListStub;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public interface GosuReferenceList extends IGosuPsiElement, StubBasedPsiElement<GosuReferenceListStub>
{
  GosuCodeReferenceElement[] getReferenceElements();

  PsiClassType[] getReferenceTypes();
}
