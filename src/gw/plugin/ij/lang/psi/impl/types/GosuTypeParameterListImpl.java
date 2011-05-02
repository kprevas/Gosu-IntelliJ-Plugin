package gw.plugin.ij.lang.psi.impl.types;

import com.intellij.psi.PsiTypeParameter;
import gw.lang.parser.expressions.ITypeParameterListClause;
import gw.plugin.ij.lang.parser.GosuCompositeElement;
import gw.plugin.ij.lang.psi.api.types.GosuTypeParameter;
import gw.plugin.ij.lang.psi.api.types.GosuTypeParameterList;
import gw.plugin.ij.lang.psi.impl.GosuPsiElementImpl;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuTypeParameterListImpl extends GosuPsiElementImpl<ITypeParameterListClause> implements GosuTypeParameterList
{
  public GosuTypeParameterListImpl( GosuCompositeElement<ITypeParameterListClause> node )
  {
    super( node );
  }

  public GosuTypeParameter[] getTypeParameters()
  {
    return findChildrenByClass( GosuTypeParameter.class );
  }

  public int getTypeParameterIndex( PsiTypeParameter typeParameter )
  {
    final GosuTypeParameter[] typeParameters = getTypeParameters();
    for( int i = 0; i < typeParameters.length; i++ )
    {
      if( typeParameters[i].equals( typeParameter ) )
      {
        return i;
      }
    }

    return -1;
  }
}
