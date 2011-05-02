package gw.plugin.ij.lang.parser;

import com.intellij.psi.impl.source.tree.CompositeElement;
import com.intellij.psi.tree.IElementType;
import gw.lang.parser.IToken;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuTokenElement extends CompositeElement
{
  private IToken _token;


  public GosuTokenElement( IElementType elemType, IToken token )
  {
    super( elemType );
    _token = token;
  }

  public IToken getToken()
  {
    return _token;
  }
}
