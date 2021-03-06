package gw.plugin.ij.lang.psi.impl.expressions;

import com.intellij.lang.ASTNode;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.TreeElement;
import com.intellij.util.IncorrectOperationException;
import gw.lang.parser.IExpression;
import gw.lang.parser.expressions.ITypeLiteralExpression;
import gw.lang.reflect.IType;
import gw.plugin.ij.lang.parser.GosuCompositeElement;
import gw.plugin.ij.lang.parser.GosuElementTypes;
import gw.plugin.ij.lang.parser.GosuUnhandledPsiElement;
import gw.plugin.ij.lang.psi.GosuFile;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuClassDefinition;
import gw.plugin.ij.lang.psi.api.types.GosuCodeReferenceElement;
import gw.plugin.ij.lang.psi.api.types.GosuTypeArgumentList;
import gw.plugin.ij.lang.psi.api.types.GosuTypeElement;
import gw.plugin.ij.lang.psi.impl.GosuProgramFileImpl;
import gw.plugin.ij.lang.psi.impl.statements.typedef.GosuClassDefinitionImpl;
import gw.plugin.ij.lang.psi.util.GosuPsiParseUtil;
import gw.plugin.ij.lang.psi.util.TypesUtil;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuTypeLiteralImpl extends GosuReferenceExpressionImpl<ITypeLiteralExpression> implements GosuCodeReferenceElement, GosuTypeElement
{
  public GosuTypeLiteralImpl( GosuCompositeElement<ITypeLiteralExpression> node )
  {
    super( node );
  }

  public PsiElement getReferenceNameElement()
  {
    return getFirstChild();
  }

  @Override
  public PsiElement resolve()
  {
    String strFullName = getTypeName();
    if( strFullName != null )
    {
      final JavaPsiFacade facade = JavaPsiFacade.getInstance( getProject() );
//      if( getContext() != null )
//      {
//        return facade.getResolveHelper().resolveReferencedClass( strFullName, getContext() );
//      }
//      else
//      {
        return facade.findClass( strFullName, getResolveScope() );
//      }
    }
    else
    {
      return null;
    }
  }

  @Override
  public PsiType getType()
  {
    return TypesUtil.createType( getTypeName(), this );
  }

  private String getTypeName()
  {
    if( getTypeReferenced().isParameterizedType() )
    {
      return getTypeReferenced().getGenericType().getName();
    }
    return getTypeReferenced().getName();
  }

  public PsiElement bindToElement( PsiElement element ) throws IncorrectOperationException
  {
    String text = this.getText();
    if (text.contains(".")) {
      text = ((GosuClassDefinition) element).getQualifiedName();
    } else {
      text = ((GosuClassDefinition) element).getName();
    }
    GosuProgramFileImpl psiFile = GosuPsiParseUtil.createVirtualProgramFile(this, "null typeis " + text);
    ASTNode returnAST = psiFile.getNode().findChildByType(GosuElementTypes.ELEM_TYPE_ReturnStatement);
    ASTNode newNode = returnAST.getPsi().getChildren()[0].getChildren()[0].getChildren()[1].getNode();
    getNode().getTreeParent().replaceChildInternal(this.getNode(), (TreeElement)newNode);

//    if( element instanceof PsiQualifiedNamedElement )
//    {
//      PsiElement elt = GosuPsiParseUtil.parseExpression( ((PsiQualifiedNamedElement)element).getQualifiedName(), getManager() );
//      if( elt instanceof GosuTypeLiteralImpl )
//      {
//        GosuTypeLiteralImpl tl = (GosuTypeLiteralImpl)elt;
//        GosuFile file = (GosuFile)getContainingFile();
//        file.addImport(tl);
//        GosuCompositeElement<ITypeLiteralExpression> ast = tl.getNode();
//        ast.removeRange(ast.getFirstChildNode(), ast.getLastChildNode());
//        replace( tl );
//        return tl;
//      }
//    }
    return this;
  }

  private IType getTypeReferenced()
  {
    return getParsedElement().getType().getType();
  }

  @Override
  public GosuCodeReferenceElement getQualifier()
  {
    IExpression packageExpression = getParsedElement().getPackageExpression();
    if( packageExpression != null )
    {
      // todo fill out ast
      if(getFirstChild() instanceof GosuUnhandledPsiElement) {
        System.err.println("ERROR: Package element AST is not complete, code completion not available yet.");
        return null;
      }
      return (GosuCodeReferenceElement)getFirstChild();
    }
    return null;
  }
  @Override
  public void setQualifier( GosuCodeReferenceElement newQualifier )
  {
    throw new UnsupportedOperationException( "Men at work" );
  }

  @Override
  public PsiType[] getTypeArguments()
  {
    IType type = getTypeReferenced();
    if( type.isParameterizedType() )
    {
      PsiType[] psiTypes = new PsiType[type.getTypeParameters().length];
      for( IType typeParam : type.getTypeParameters() )
      {
        if( typeParam.getGenericType() != null )
        {
          TypesUtil.createType( typeParam.getGenericType().getName(), this );
        }
      }
    }
    return new PsiType[0];
  }

  @Override
  public GosuTypeArgumentList getTypeArgumentList()
  {
    return findChildByClass( GosuTypeArgumentList.class );
  }
}
