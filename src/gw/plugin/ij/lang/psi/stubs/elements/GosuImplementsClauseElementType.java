package gw.plugin.ij.lang.psi.stubs.elements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import gw.lang.parser.statements.IInterfacesClause;
import gw.plugin.ij.lang.parser.GosuCompositeElement;
import gw.plugin.ij.lang.parser.GosuElementTypes;
import gw.plugin.ij.lang.psi.GosuStubElementType;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuImplementsClause;
import gw.plugin.ij.lang.psi.api.types.GosuCodeReferenceElement;
import gw.plugin.ij.lang.psi.impl.statements.typedef.GosuImplementsClauseImpl;
import gw.plugin.ij.lang.psi.stubs.GosuReferenceListStub;
import gw.plugin.ij.lang.psi.stubs.impl.GosuReferenceListStubImpl;
import gw.plugin.ij.lang.psi.stubs.index.GosuDirectInheritorsIndex;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuImplementsClauseElementType extends GosuStubElementType<GosuReferenceListStub, GosuImplementsClause>
{

  public GosuImplementsClauseElementType()
  {
    super( "implements clause" );
  }

  public GosuImplementsClause createElement( ASTNode node )
  {
    return new GosuImplementsClauseImpl( (GosuCompositeElement<IInterfacesClause>)node );
  }

  public GosuImplementsClause createPsi( GosuReferenceListStub stub )
  {
    return new GosuImplementsClauseImpl( stub );
  }

  public GosuReferenceListStub createStub( GosuImplementsClause psi, StubElement parentStub )
  {
    final GosuCodeReferenceElement[] elements = psi.getReferenceElements();
    String[] refNames = ContainerUtil.map( elements, new Function<GosuCodeReferenceElement, String>()
    {
      @Nullable
      public String fun( final GosuCodeReferenceElement element )
      {
        return element.getReferenceName();
      }
    }, new String[elements.length] );

    return new GosuReferenceListStubImpl( parentStub, GosuElementTypes.IMPLEMENTS_CLAUSE, refNames );
  }

  public void serialize( GosuReferenceListStub stub, StubOutputStream dataStream ) throws IOException
  {
    final String[] names = stub.getBaseClasses();
    dataStream.writeByte( names.length );
    for( String s : names )
    {
      dataStream.writeName( s );
    }
  }

  public GosuReferenceListStub deserialize( StubInputStream dataStream, StubElement parentStub ) throws IOException
  {
    final byte b = dataStream.readByte();
    final String[] names = new String[b];
    for( int i = 0; i < b; i++ )
    {
      names[i] = dataStream.readName().toString();
    }
    return new GosuReferenceListStubImpl( parentStub, GosuElementTypes.IMPLEMENTS_CLAUSE, names );
  }

  public void indexStub( GosuReferenceListStub stub, IndexSink sink )
  {
    for( String name : stub.getBaseClasses() )
    {
      if( name != null )
      {
        sink.occurrence( GosuDirectInheritorsIndex.KEY, name );
      }
    }
  }
}
