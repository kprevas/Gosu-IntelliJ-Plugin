package gw.plugin.ij.lang.psi.api.statements.typedef;

import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import com.intellij.util.ArrayFactory;
import gw.plugin.ij.lang.psi.GosuNamedElement;
import gw.plugin.ij.lang.psi.api.auxilary.IGosuModifierList;
import gw.plugin.ij.lang.psi.api.statements.GosuParametersOwner;
import gw.plugin.ij.lang.psi.api.statements.params.IGosuParameterList;
import gw.plugin.ij.lang.psi.api.types.GosuTypeElement;
import gw.plugin.ij.lang.psi.api.types.GosuTypeParameterListOwner;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public interface GosuMethod extends GosuMembersDeclaration, PsiMethod, IGosuModifierList, GosuNamedElement, IGosuMember,
  GosuParametersOwner, GosuTypeParameterListOwner
{
  GosuMethod[] EMPTY_ARRAY = new GosuMethod[0];
  Key<Boolean> BUILDER_METHOD = Key.create( "BUILDER_METHOD" );
  ArrayFactory<GosuMethod> ARRAY_FACTORY = new ArrayFactory<GosuMethod>()
  {
    public GosuMethod[] create( int count )
    {
      return new GosuMethod[count];
    }
  };

  GosuTypeElement getReturnTypeElementGosu();

  /**
   * @return the static return type, which will appear in the compiled Gosu class
   */
  PsiType getReturnType();

  GosuTypeElement setReturnType( PsiType newReturnType );

  String getName();

  IGosuParameterList getParameterList();

  IGosuModifierList getModifierList();

  String[] getNamedParametersArray();
}
