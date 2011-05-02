package gw.plugin.ij.lang.psi.stubs.elements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;
import gw.plugin.ij.lang.parser.GosuCompositeElement;
import gw.plugin.ij.lang.psi.GosuStubElementType;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuMethod;
import gw.plugin.ij.lang.psi.impl.statements.typedef.members.GosuMethodImpl;
import gw.plugin.ij.lang.psi.stubs.GosuMethodStub;
import gw.plugin.ij.lang.psi.stubs.GosuStubUtils;
import gw.plugin.ij.lang.psi.stubs.impl.GosuMethodStubImpl;
import gw.plugin.ij.lang.psi.stubs.index.GosuAnnotatedMemberIndex;
import gw.plugin.ij.lang.psi.stubs.index.GosuMethodNameIndex;

import java.io.IOException;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuMethodElementType extends GosuStubElementType<GosuMethodStub, GosuMethod>
{
  public GosuMethodElementType()
  {
    super( "method definition" );
  }

  public GosuMethod createElement( ASTNode node )
  {
    return new GosuMethodImpl( (GosuCompositeElement)node );
  }

  public GosuMethod createPsi( GosuMethodStub stub )
  {
    return new GosuMethodImpl( stub );
  }

  public GosuMethodStub createStub( GosuMethod psi, StubElement parentStub )
  {

    return new GosuMethodStubImpl( parentStub, StringRef.fromString( psi.getName() ), GosuTypeDefinitionElementType.getAnnotationNames( psi ),
                                 psi.getNamedParametersArray() );
  }

  public void serialize( GosuMethodStub stub, StubOutputStream dataStream ) throws IOException
  {
    dataStream.writeName( stub.getName() );
    GosuStubUtils.writeStringArray( dataStream, stub.getAnnotations() );
    GosuStubUtils.writeStringArray( dataStream, stub.getNamedParameters() );
  }

  public GosuMethodStub deserialize( StubInputStream dataStream, StubElement parentStub ) throws IOException
  {
    StringRef ref = dataStream.readName();
    final String[] annNames = GosuStubUtils.readStringArray( dataStream );
    String[] namedParameters = GosuStubUtils.readStringArray( dataStream );
    return new GosuMethodStubImpl( parentStub, ref, annNames, namedParameters );
  }

  public void indexStub( GosuMethodStub stub, IndexSink sink )
  {
    String name = stub.getName();
    if( name != null )
    {
      sink.occurrence( GosuMethodNameIndex.KEY, name );
    }
    for( String annName : stub.getAnnotations() )
    {
      if( annName != null )
      {
        sink.occurrence( GosuAnnotatedMemberIndex.KEY, annName );
      }
    }
  }
}
