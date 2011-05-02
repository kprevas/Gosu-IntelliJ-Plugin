package gw.plugin.ij.lang.parser;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import gw.lang.parser.IParsedElement;
import gw.plugin.ij.lang.psi.IGosuPsiElement;
import gw.plugin.ij.lang.psi.impl.GosuElementVisitor;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuUnhandledPsiElement extends ASTWrapperPsiElement implements IGosuPsiElement
{
  public GosuUnhandledPsiElement( GosuCompositeElement node )
  {
    super( node );
  }

  public IParsedElement getParsedElement()
  {
    return ((GosuCompositeElement)getNode()).getParsedElement();
  }

  public String toString()
  {
    return "[raw] " + getParsedElement().getClass().getSimpleName();
  }

  public void accept( GosuElementVisitor visitor )
  {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public void acceptChildren( GosuElementVisitor visitor )
  {
    //To change body of implemented methods use File | Settings | File Templates.
  }
}
