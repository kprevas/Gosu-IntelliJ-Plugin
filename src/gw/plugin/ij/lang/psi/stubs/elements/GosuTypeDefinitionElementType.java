package gw.plugin.ij.lang.psi.stubs.elements;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiModifierListOwner;
import com.intellij.psi.PsiNameHelper;
import com.intellij.psi.impl.java.stubs.index.JavaFullClassNameIndex;
import com.intellij.psi.impl.java.stubs.index.JavaShortClassNameIndex;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.ArrayUtil;
import com.intellij.util.containers.CollectionFactory;
import com.intellij.util.io.StringRef;
import gw.plugin.ij.lang.psi.GosuStubElementType;
import gw.plugin.ij.lang.psi.api.auxilary.IGosuModifierList;
import gw.plugin.ij.lang.psi.api.auxilary.annotation.GosuAnnotation;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuTypeDefinition;
import gw.plugin.ij.lang.psi.api.types.GosuCodeReferenceElement;
import gw.plugin.ij.lang.psi.stubs.GosuTypeDefinitionStub;
import gw.plugin.ij.lang.psi.stubs.impl.GosuTypeDefinitionStubImpl;
import gw.plugin.ij.lang.psi.stubs.index.GosuAnnotatedMemberIndex;
import gw.plugin.ij.lang.psi.stubs.index.GosuAnonymousClassIndex;
import gw.plugin.ij.lang.psi.stubs.index.GosuFullClassNameIndex;

import java.io.IOException;
import java.util.List;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public abstract class GosuTypeDefinitionElementType<TypeDef extends GosuTypeDefinition>
  extends GosuStubElementType<GosuTypeDefinitionStub, TypeDef>
{
  public GosuTypeDefinitionElementType( String debugName )
  {
    super( debugName );
  }

  public GosuTypeDefinitionStub createStub( TypeDef psi, StubElement parentStub )
  {
    String[] superClassNames = psi.getSuperClassNames();
    final byte flags = GosuTypeDefinitionStubImpl.buildFlags( psi );
    return new GosuTypeDefinitionStubImpl( parentStub, psi.getName(), superClassNames, this, psi.getQualifiedName(), getAnnotationNames( psi ),
                                         flags );
  }

  public static String[] getAnnotationNames( PsiModifierListOwner psi )
  {
    List<String> annoNames = CollectionFactory.arrayList();
    final PsiModifierList modifierList = psi.getModifierList();
    if( modifierList instanceof IGosuModifierList )
    {
      for( GosuAnnotation annotation : ((IGosuModifierList)modifierList).getAnnotations() )
      {
        final GosuCodeReferenceElement element = annotation.getClassReference();
        if( element != null )
        {
          final String annoShortName = StringUtil.getShortName( element.getText() ).trim();
          if( StringUtil.isNotEmpty( annoShortName ) )
          {
            annoNames.add( annoShortName );
          }
        }
      }
    }
    return ArrayUtil.toStringArray( annoNames );
  }

  public void serialize( GosuTypeDefinitionStub stub, StubOutputStream dataStream ) throws IOException
  {
    dataStream.writeName( stub.getName() );
    dataStream.writeName( stub.getQualifiedName() );
    dataStream.writeByte( stub.getFlags() );
    writeStringArray( dataStream, stub.getSuperClassNames() );
    writeStringArray( dataStream, stub.getAnnotations() );
  }

  private static void writeStringArray( StubOutputStream dataStream, String[] names ) throws IOException
  {
    dataStream.writeByte( names.length );
    for( String name : names )
    {
      dataStream.writeName( name );
    }
  }

  public GosuTypeDefinitionStub deserialize( StubInputStream dataStream, StubElement parentStub ) throws IOException
  {
    String name = StringRef.toString( dataStream.readName() );
    String qname = StringRef.toString( dataStream.readName() );
    byte flags = dataStream.readByte();
    String[] superClasses = readStringArray( dataStream );
    String[] annos = readStringArray( dataStream );
    return new GosuTypeDefinitionStubImpl( parentStub, name, superClasses, this, qname, annos, flags );
  }

  private static String[] readStringArray( StubInputStream dataStream ) throws IOException
  {
    byte supersNumber = dataStream.readByte();
    String[] superClasses = new String[supersNumber];
    for( int i = 0; i < supersNumber; i++ )
    {
      superClasses[i] = StringRef.toString( dataStream.readName() );
    }
    return superClasses;
  }

  public void indexStub( GosuTypeDefinitionStub stub, IndexSink sink )
  {
    if( stub.isAnonymous() )
    {
      final String[] classNames = stub.getSuperClassNames();
      if( classNames.length != 1 )
      {
        return;
      }
      final String baseClassName = classNames[0];
      if( baseClassName != null )
      {
        final String shortName = PsiNameHelper.getShortClassName( baseClassName );
        sink.occurrence( GosuAnonymousClassIndex.KEY, shortName );
      }
    }
    else
    {
      String shortName = stub.getName();
      if( shortName != null )
      {
        sink.occurrence( JavaShortClassNameIndex.KEY, shortName );
      }
      final String fqn = stub.getQualifiedName();
      if( fqn != null )
      {
        sink.occurrence( GosuFullClassNameIndex.KEY, fqn.hashCode() );
        sink.occurrence( JavaFullClassNameIndex.KEY, fqn.hashCode() );
      }
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
