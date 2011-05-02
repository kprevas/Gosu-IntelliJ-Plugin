package gw.plugin.ij.lang.psi.impl;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiInvalidElementAccessException;
import com.intellij.psi.impl.CheckUtil;
import com.intellij.psi.impl.source.SourceTreeToPsiMap;
import com.intellij.psi.impl.source.tree.ChangeUtil;
import com.intellij.psi.impl.source.tree.CompositeElement;
import com.intellij.psi.impl.source.tree.TreeElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.IncorrectOperationException;
import gw.lang.parser.IParsedElement;
import gw.plugin.ij.lang.TokenSets;
import gw.plugin.ij.lang.parser.GosuCompositeElement;
import gw.plugin.ij.lang.psi.IGosuPsiElement;
import gw.plugin.ij.lang.psi.api.statements.IGosuStatement;

import java.util.Iterator;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuBaseElementImpl<E extends IParsedElement, T extends StubElement> extends StubBasedPsiElementBase<T> implements IGosuPsiElement
{
  public GosuBaseElementImpl( GosuCompositeElement<E> node )
  {
    super( node );
  }

  protected GosuBaseElementImpl( final T stub, IStubElementType nodeType )
  {
    super( stub, nodeType );
  }

  public PsiElement findElement( PsiElement parent, IElementType elemType )
  {
    if( parent.getNode().getElementType() == elemType )
    {
      return parent;
    }
    PsiElement child = parent.getFirstChild();
    while( child != null )
    {
      PsiElement elem = findElement( child, elemType );
      if( elem != null )
      {
        return elem;
      }
      child = child.getNextSibling();
    }
    return null;
  }

  public void removeElements( PsiElement[] elements ) throws IncorrectOperationException
  {
    ASTNode parentNode = getNode();
    for( PsiElement element : elements )
    {
      if( element.isValid() )
      {
        ASTNode node = element.getNode();
        if( node == null || node.getTreeParent() != parentNode )
        {
          throw new IncorrectOperationException();
        }
        parentNode.removeChild( node );
      }
    }
  }

  public void removeStatement() throws IncorrectOperationException
  {
    if( getParent() == null ||
        getParent().getNode() == null )
    {
      throw new IncorrectOperationException();
    }
    ASTNode parentNode = getParent().getNode();
    ASTNode prevNode = getNode().getTreePrev();
    parentNode.removeChild( this.getNode() );
    if( prevNode != null && TokenSets.SEPARATORS.contains( prevNode.getElementType() ) )
    {
      parentNode.removeChild( prevNode );
    }
  }

  @Override
  public PsiElement getParent()
  {
    return getParentByStub();
  }

  public <T extends IGosuStatement> T replaceWithStatement( T newStmt )
  {
    PsiElement parent = getParent();
    if( parent == null )
    {
      throw new PsiInvalidElementAccessException( this );
    }
    return (T)replace( newStmt );
  }

  public <T extends IGosuPsiElement> Iterable<T> childrenOfType( final TokenSet tokSet )
  {
    return new Iterable<T>()
    {

      public Iterator<T> iterator()
      {
        return new Iterator<T>()
        {
          private ASTNode findChild( ASTNode child )
          {
            if( child == null )
            {
              return null;
            }

            if( tokSet.contains( child.getElementType() ) )
            {
              return child;
            }

            return findChild( child.getTreeNext() );
          }

          PsiElement first = getFirstChild();

          ASTNode n = first == null ? null : findChild( first.getNode() );

          public boolean hasNext()
          {
            return n != null;
          }

          public T next()
          {
            if( n == null )
            {
              return null;
            }
            else
            {
              final ASTNode res = n;
              n = findChild( n.getTreeNext() );
              return (T)res.getPsi();
            }
          }

          public void remove()
          {
          }
        };
      }
    };
  }

  public void accept( GosuElementVisitor visitor )
  {
    visitor.visitElement( this );
  }

  public void acceptChildren( GosuElementVisitor visitor )
  {
    PsiElement child = getFirstChild();
    while( child != null )
    {
      if( child instanceof IGosuPsiElement )
      {
        ((IGosuPsiElement)child).accept( visitor );
      }

      child = child.getNextSibling();
    }
  }

  public PsiElement replace( PsiElement newElement ) throws IncorrectOperationException
  {
    CompositeElement treeElement = calcTreeElement();
    assert treeElement.getTreeParent() != null;
    CheckUtil.checkWritable( this );
    TreeElement elementCopy = ChangeUtil.copyToElement( newElement );
    treeElement.getTreeParent().replaceChildInternal( treeElement, elementCopy );
    elementCopy = ChangeUtil.decodeInformation( elementCopy );
    return SourceTreeToPsiMap.treeElementToPsi( elementCopy );
  }

  protected CompositeElement calcTreeElement()
  {
    return getNode();
  }

  public GosuCompositeElement<E> getNode()
  {
    //noinspection unchecked
    return (GosuCompositeElement<E>)super.getNode();
  }

  public E getParsedElement()
  {
    return getNode().getParsedElement();
  }

  public String toString()
  {
    return getNode().getParsedElement().getClass().getSimpleName();
  }
}
