package gw.plugin.ij.lang.psi.impl.expressions;

import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.tree.IElementType;
import gw.lang.parser.expressions.INameInDeclaration;
import gw.plugin.ij.lang.parser.GosuCompositeElement;
import gw.plugin.ij.lang.psi.impl.GosuElementVisitor;
import gw.plugin.ij.lang.psi.impl.GosuPsiElementImpl;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuNameInDeclarationImpl extends GosuPsiElementImpl<INameInDeclaration> implements PsiIdentifier
{
  public GosuNameInDeclarationImpl( GosuCompositeElement<INameInDeclaration> node )
  {
    super(node);
  }

  public void accept( GosuElementVisitor visitor )
  {
    visitor.visitNameInDeclaration(this);
  }

  public String getName()
  {
    return getParsedElement().getName();
  }

  @Override
  public IElementType getTokenType()
  {
    return getNode().getElementType();
  }
}
