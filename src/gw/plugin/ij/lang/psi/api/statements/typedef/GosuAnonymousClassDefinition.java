package gw.plugin.ij.lang.psi.api.statements.typedef;

import com.intellij.psi.PsiAnonymousClass;
import com.intellij.psi.StubBasedPsiElement;
import gw.plugin.ij.lang.psi.api.types.GosuTypeParameterListOwner;
import gw.plugin.ij.lang.psi.stubs.GosuTypeDefinitionStub;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public interface GosuAnonymousClassDefinition
  extends GosuTypeDefinition, GosuTypeParameterListOwner, PsiAnonymousClass, StubBasedPsiElement<GosuTypeDefinitionStub>
{
//  GosuCodeReferenceElement getBaseClassReferenceGosuoovy();
//  IGosuArgumentList getArgumentListGosuoovy();
}
