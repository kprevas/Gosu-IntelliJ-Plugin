package gw.plugin.ij.lang.psi.stubs.elements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.ArrayUtil;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.io.StringRef;
import gw.plugin.ij.lang.parser.GosuCompositeElement;
import gw.plugin.ij.lang.parser.GosuElementTypes;
import gw.plugin.ij.lang.psi.GosuStubElementType;
import gw.plugin.ij.lang.psi.api.auxilary.IGosuModifierList;
import gw.plugin.ij.lang.psi.api.auxilary.annotation.GosuAnnotation;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuEnumConstant;
import gw.plugin.ij.lang.psi.api.types.GosuCodeReferenceElement;
import gw.plugin.ij.lang.psi.impl.statements.typedef.GosuEnumConstantImpl;
import gw.plugin.ij.lang.psi.stubs.GosuFieldStub;
import gw.plugin.ij.lang.psi.stubs.impl.GosuFieldStubImpl;

import java.io.IOException;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuEnumConstantElementType extends GosuStubElementType<GosuFieldStub, GosuEnumConstant>
{

  public GosuEnumConstantElementType()
  {
    super( "Enumeration constant" );
  }

  public PsiElement createElement( ASTNode node )
  {
    return new GosuEnumConstantImpl( (GosuCompositeElement)node );
  }

  public GosuEnumConstant createPsi( GosuFieldStub stub )
  {
    return new GosuEnumConstantImpl( stub );
  }

  @Override
  public GosuFieldStub createStub( GosuEnumConstant psi, StubElement parentStub )
  {
    final IGosuModifierList list = psi.getModifierList();
    String[] annNames;
    if( list == null )
    {
      annNames = ArrayUtil.EMPTY_STRING_ARRAY;
    }
    else
    {
      annNames = ContainerUtil.map( list.getAnnotations(), new Function<GosuAnnotation, String>()
      {
        public String fun( final GosuAnnotation grAnnotation )
        {
          final GosuCodeReferenceElement element = grAnnotation.getClassReference();
          if( element == null )
          {
            return null;
          }
          return element.getReferenceName();
        }
      }, ArrayUtil.EMPTY_STRING_ARRAY );
    }
    return new GosuFieldStubImpl( parentStub, StringRef.fromString( psi.getName() ), annNames, ArrayUtil.EMPTY_STRING_ARRAY, GosuElementTypes.ENUM_CONSTANT, GosuFieldStubImpl.buildFlags( psi ) );
  }

  public void serialize( GosuFieldStub stub, StubOutputStream dataStream ) throws IOException
  {
    serializeFieldStub( stub, dataStream );
  }

  public GosuFieldStub deserialize( StubInputStream dataStream, StubElement parentStub ) throws IOException
  {
    return GosuFieldElementType.deserializeFieldStub( dataStream, parentStub );
  }

  protected static void serializeFieldStub( GosuFieldStub stub, StubOutputStream dataStream ) throws IOException
  {
    GosuFieldElementType.serializeFieldStub( stub, dataStream );
  }

  public void indexStub( GosuFieldStub stub, IndexSink sink )
  {
    GosuFieldElementType.indexFieldStub( stub, sink );
  }
}
