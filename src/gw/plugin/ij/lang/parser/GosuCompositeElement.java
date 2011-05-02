package gw.plugin.ij.lang.parser;

import com.intellij.psi.impl.source.tree.CompositeElement;
import com.intellij.psi.tree.IElementType;
import gw.lang.parser.IParsedElement;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuCompositeElement<T extends IParsedElement> extends CompositeElement
{
  private T _pe;


  public GosuCompositeElement( IElementType iElementType, T pe )
  {
    super( iElementType );
    _pe = pe;
  }

  public T getParsedElement()
  {
    return _pe;
  }
}
