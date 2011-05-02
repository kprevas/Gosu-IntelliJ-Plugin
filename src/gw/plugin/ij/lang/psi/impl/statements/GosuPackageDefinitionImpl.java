package gw.plugin.ij.lang.psi.impl.statements;

import gw.lang.parser.statements.INamespaceStatement;
import gw.plugin.ij.lang.parser.GosuCompositeElement;
import gw.plugin.ij.lang.parser.GosuElementTypes;
import gw.plugin.ij.lang.psi.api.GosuPackageDefinition;
import gw.plugin.ij.lang.psi.api.types.GosuCodeReferenceElement;
import gw.plugin.ij.lang.psi.impl.GosuElementVisitor;
import gw.plugin.ij.lang.psi.impl.GosuPsiElementImpl;
import gw.plugin.ij.lang.psi.util.PsiUtil;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuPackageDefinitionImpl extends GosuPsiElementImpl<INamespaceStatement> implements GosuPackageDefinition
{
  public GosuPackageDefinitionImpl( GosuCompositeElement<INamespaceStatement> node )
  {
    super( node );
  }

  public void accept( GosuElementVisitor visitor )
  {
    visitor.visitPackageDefinition( this );
  }

  public String getPackageName()
  {
    GosuCodeReferenceElement ref = getPackageReference();
    if( ref == null )
    {
      return "";
    }
    return PsiUtil.getQualifiedReferenceText( ref );
  }

  public GosuCodeReferenceElement getPackageReference()
  {
    return (GosuCodeReferenceElement)findChildByType( GosuElementTypes.REFERENCE_ELEMENT );
  }
}
