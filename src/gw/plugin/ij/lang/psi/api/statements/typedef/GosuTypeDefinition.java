package gw.plugin.ij.lang.psi.api.statements.typedef;

import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiClass;
import com.intellij.util.ArrayFactory;
import gw.plugin.ij.lang.psi.GosuNamedElement;
import gw.plugin.ij.lang.psi.IGosuPsiElement;
import gw.plugin.ij.lang.psi.api.auxilary.IGosuModifierList;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public interface GosuTypeDefinition extends NavigatablePsiElement, PsiClass, IGosuPsiElement, GosuNamedElement, IGosuModifierList
{
  String DEFAULT_BASE_CLASS_NAME = "gosu.lang.GosuObject";

  GosuTypeDefinition[] EMPTY_ARRAY = new GosuTypeDefinition[0];

  ArrayFactory<GosuTypeDefinition> ARRAY_FACTORY =
    new ArrayFactory<GosuTypeDefinition>()
    {
      public GosuTypeDefinition[] create( int count )
      {
        return new GosuTypeDefinition[count];
      }
    };

  String[] getSuperClassNames();

  boolean isAnonymous();

  GosuReferenceList getExtendsClause();

  GosuReferenceList getImplementsClause();
}
