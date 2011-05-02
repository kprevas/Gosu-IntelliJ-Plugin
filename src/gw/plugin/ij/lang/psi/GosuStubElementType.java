package gw.plugin.ij.lang.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import gw.plugin.ij.GosuLanguage;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public abstract class GosuStubElementType<S extends StubElement, T extends IGosuPsiElement> extends IStubElementType<S, T>
{
  public GosuStubElementType( String debugName )
  {
    super( debugName, GosuLanguage.instance() );
  }

  public abstract PsiElement createElement( final ASTNode node );

  public void indexStub( final S stub, final IndexSink sink )
  {
  }

  public String getExternalId()
  {
    return "gosu." + super.toString();
  }

}
