package gw.plugin.ij.lang.psi.stubs.elements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import gw.lang.parser.statements.IClassStatement;
import gw.plugin.ij.lang.parser.GosuCompositeElement;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuInterfaceDefinition;
import gw.plugin.ij.lang.psi.impl.statements.typedef.GosuInterfaceDefinitionImpl;
import gw.plugin.ij.lang.psi.stubs.GosuTypeDefinitionStub;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuInterfaceDefinitionElementType extends GosuTypeDefinitionElementType<GosuInterfaceDefinition>
{
  public GosuInterfaceDefinition createPsi( GosuTypeDefinitionStub stub )
  {
    return new GosuInterfaceDefinitionImpl( stub );
  }

  public GosuInterfaceDefinitionElementType()
  {
    super( "interface definition" );
  }

  public PsiElement createElement( ASTNode node )
  {
    return new GosuInterfaceDefinitionImpl( (GosuCompositeElement<IClassStatement>)node );
  }
}