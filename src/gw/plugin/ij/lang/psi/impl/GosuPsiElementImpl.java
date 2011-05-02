package gw.plugin.ij.lang.psi.impl;

import com.intellij.psi.stubs.StubElement;
import gw.lang.parser.IParsedElement;
import gw.plugin.ij.lang.parser.GosuCompositeElement;
import gw.plugin.ij.lang.psi.IGosuPsiElement;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public abstract class GosuPsiElementImpl<T extends IParsedElement> extends GosuBaseElementImpl<T, StubElement> implements IGosuPsiElement
{
  public GosuPsiElementImpl( GosuCompositeElement<T> node )
  {
    super( node );
  }
}
