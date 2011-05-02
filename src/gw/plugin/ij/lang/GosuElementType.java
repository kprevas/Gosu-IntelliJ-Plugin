package gw.plugin.ij.lang;

import com.intellij.psi.tree.IElementType;
import gw.plugin.ij.GosuLanguage;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuElementType extends IElementType
{
  public GosuElementType( String strDebugName )
  {
    super( strDebugName, GosuLanguage.instance() );
  }
}
