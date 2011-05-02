package gw.plugin.ij.lang.psi.stubs.elements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.ArrayUtil;
import com.intellij.util.io.StringRef;
import gw.plugin.ij.lang.parser.GosuCompositeElement;
import gw.plugin.ij.lang.parser.GosuElementTypes;
import gw.plugin.ij.lang.psi.GosuStubElementType;
import gw.plugin.ij.lang.psi.api.statements.IGosuField;
import gw.plugin.ij.lang.psi.impl.statements.GosuFieldImpl;
import gw.plugin.ij.lang.psi.stubs.GosuFieldStub;
import gw.plugin.ij.lang.psi.stubs.GosuStubUtils;
import gw.plugin.ij.lang.psi.stubs.impl.GosuFieldStubImpl;
import gw.plugin.ij.lang.psi.stubs.index.GosuAnnotatedMemberIndex;
import gw.plugin.ij.lang.psi.stubs.index.GosuFieldNameIndex;

import java.io.IOException;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuFieldElementType extends GosuStubElementType<GosuFieldStub, IGosuField>
{
  public GosuFieldElementType()
  {
    super( "field" );
  }

  public PsiElement createElement( ASTNode node )
  {
    return new GosuFieldImpl( (GosuCompositeElement)node );
  }

  public IGosuField createPsi( GosuFieldStub stub )
  {
    return new GosuFieldImpl( stub );
  }

  public GosuFieldStub createStub( IGosuField psi, StubElement parentStub )
  {
    String[] annNames = GosuTypeDefinitionElementType.getAnnotationNames( psi );

    String[] namedParametersArray = ArrayUtil.EMPTY_STRING_ARRAY;

    //## todo:
//    if( psi instanceof GosuFieldImpl )
//    {
//      namedParametersArray = psi.getNamedParametersArray();
//    }

    return new GosuFieldStubImpl( parentStub, StringRef.fromString( psi.getName() ), annNames, namedParametersArray, GosuElementTypes.FIELD, GosuFieldStubImpl.buildFlags( psi ) );
  }

  public void serialize( GosuFieldStub stub, StubOutputStream dataStream ) throws IOException
  {
    serializeFieldStub( stub, dataStream );
  }

  public GosuFieldStub deserialize( StubInputStream dataStream, StubElement parentStub ) throws IOException
  {
    return deserializeFieldStub( dataStream, parentStub );
  }

  public void indexStub( GosuFieldStub stub, IndexSink sink )
  {
    indexFieldStub( stub, sink );
  }

  /*
   * ****************************************************************************************************************
   */

  static void serializeFieldStub( GosuFieldStub stub, StubOutputStream dataStream ) throws IOException
  {
    dataStream.writeName( stub.getName() );
    final String[] annotations = stub.getAnnotations();
    dataStream.writeByte( annotations.length );
    for( String s : annotations )
    {
      dataStream.writeName( s );
    }

    final String[] namedParameters = stub.getNamedParameters();
    GosuStubUtils.writeStringArray( dataStream, namedParameters );

    dataStream.writeByte( stub.getFlags() );
  }

  static GosuFieldStub deserializeFieldStub( StubInputStream dataStream, StubElement parentStub ) throws IOException
  {
    StringRef ref = dataStream.readName();
    final byte b = dataStream.readByte();
    final String[] annNames = new String[b];
    for( int i = 0; i < b; i++ )
    {
      annNames[i] = dataStream.readName().toString();
    }

    final String[] namedParameters = GosuStubUtils.readStringArray( dataStream );

    byte flags = dataStream.readByte();

    return new GosuFieldStubImpl( parentStub, ref, annNames, namedParameters, GosuFieldStubImpl.isEnumConstant( flags )
                                                                              ? GosuElementTypes.ENUM_CONSTANT
                                                                              : GosuElementTypes.FIELD,
                                  flags );
  }

  static void indexFieldStub( GosuFieldStub stub, IndexSink sink )
  {
    String name = stub.getName();
    if( name != null )
    {
      sink.occurrence( GosuFieldNameIndex.KEY, name );
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
