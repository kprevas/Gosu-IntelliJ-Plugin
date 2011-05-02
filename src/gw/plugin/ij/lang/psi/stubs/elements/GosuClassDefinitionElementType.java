package gw.plugin.ij.lang.psi.stubs.elements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import gw.lang.parser.statements.IClassStatement;
import gw.plugin.ij.lang.parser.GosuCompositeElement;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuClassDefinition;
import gw.plugin.ij.lang.psi.impl.statements.typedef.GosuClassDefinitionImpl;
import gw.plugin.ij.lang.psi.stubs.GosuTypeDefinitionStub;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuClassDefinitionElementType extends GosuTypeDefinitionElementType<GosuClassDefinition>
{
  public GosuClassDefinition createPsi( GosuTypeDefinitionStub stub )
  {
    return new GosuClassDefinitionImpl( stub );
  }

  public GosuClassDefinitionElementType()
  {
    super( "class definition" );
  }

  public PsiElement createElement( ASTNode node )
  {
    return new GosuClassDefinitionImpl( (GosuCompositeElement<IClassStatement>)node );
  }
}
