package gw.plugin.ij.lang.psi.impl.statements.typedef;

import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.IncorrectOperationException;
import gw.lang.parser.IExpression;
import gw.plugin.ij.lang.parser.GosuCompositeElement;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuReferenceList;
import gw.plugin.ij.lang.psi.api.types.GosuCodeReferenceElement;
import gw.plugin.ij.lang.psi.impl.GosuBaseElementImpl;
import gw.plugin.ij.lang.psi.impl.GosuClassReferenceType;
import gw.plugin.ij.lang.psi.stubs.GosuReferenceListStub;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public abstract class GosuReferenceListImpl extends GosuBaseElementImpl<IExpression, GosuReferenceListStub>
  implements StubBasedPsiElement<GosuReferenceListStub>, GosuReferenceList
{

  private PsiClassType[] cachedTypes = null;

  public GosuReferenceListImpl( GosuCompositeElement node )
  {
    super( node );
  }

  public GosuReferenceListImpl( final GosuReferenceListStub stub, IStubElementType elementType )
  {
    super( stub, elementType );
  }

  @NotNull
  public GosuCodeReferenceElement[] getReferenceElements()
  {
    return findChildrenByClass( GosuCodeReferenceElement.class );
  }

  @NotNull
  public PsiClassType[] getReferenceTypes()
  {
    if( cachedTypes == null || !isValid() )
    {
      final ArrayList<PsiClassType> types = new ArrayList<PsiClassType>();
      for( GosuCodeReferenceElement ref : getReferenceElements() )
      {
        types.add( new GosuClassReferenceType( ref ) );
      }
      cachedTypes = types.toArray( new PsiClassType[types.size()] );
    }
    return cachedTypes;
  }

  @Override
  public void subtreeChanged()
  {
    cachedTypes = null;
  }

  @Override
  public PsiElement add( @NotNull PsiElement element ) throws IncorrectOperationException
  {
    throw new UnsupportedOperationException(  );
//    if( element instanceof GosuCodeReferenceElement && findChildByClass( GosuCodeReferenceElement.class ) != null )
//    {
//      PsiElement lastChild = getLastChild();
//      lastChild = PsiUtil.skipWhitespaces( lastChild, false );
//      if( !lastChild.getNode().getElementType().equals( GosuTokenTypes.mCOMMA ) )
//      {
//        getNode().addLeaf( GosuTokenTypes.mCOMMA, ",", null );
//      }
//      return super.add( element );
//    }
//    else
//    {
//      return super.add( element );
//    }
  }
}
