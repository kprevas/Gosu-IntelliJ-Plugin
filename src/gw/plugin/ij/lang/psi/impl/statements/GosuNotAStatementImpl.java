package gw.plugin.ij.lang.psi.impl.statements;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.psi.PsiErrorElement;
import gw.plugin.ij.lang.parser.GosuCompositeElement;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuNotAStatementImpl extends ASTWrapperPsiElement
{
  public GosuNotAStatementImpl( GosuCompositeElement node )
  {
    super( node );
  }
}
