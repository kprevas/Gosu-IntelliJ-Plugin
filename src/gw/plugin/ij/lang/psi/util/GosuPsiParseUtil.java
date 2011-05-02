package gw.plugin.ij.lang.psi.util;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.SingleRootFileViewProvider;
import com.intellij.testFramework.LightVirtualFile;
import gw.lang.reflect.TypeSystem;
import gw.plugin.ij.lang.psi.GosuFakeVirtualFile;
import gw.plugin.ij.lang.psi.impl.GosuClassFileImpl;
import gw.plugin.ij.lang.psi.stubs.GosuFileStub;

/**
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuPsiParseUtil
{
  public static PsiElement parseExpression( String s, PsiManager manager )
  {
    TypeSystem.clearErrorTypes();
    LightVirtualFile fakeFile = new GosuFakeVirtualFile( "package foo class Bar { function doh(){ print(" + s, "foo.Bar" );
    GosuFileStub gosuFileStub = new GosuFileStub( new GosuClassFileImpl( new SingleRootFileViewProvider( manager, fakeFile ) ) );
    PsiElement[] children = gosuFileStub.getPsi().getChildren();
    //Ummmm, there has to be a better way to do this...
    return children[2].getChildren()[1].getChildren()[2].getChildren()[0].getChildren()[0];
  }

  public static PsiElement parseImport( String s, PsiManager manager )
  {
    TypeSystem.clearErrorTypes();
    LightVirtualFile fakeFile = new GosuFakeVirtualFile( "package foo \nuses " + s + " class Bar {}", "foo.Bar" );
    GosuFileStub gosuFileStub = new GosuFileStub( new GosuClassFileImpl( new SingleRootFileViewProvider( manager, fakeFile ) ) );
    PsiElement[] children = gosuFileStub.getPsi().getChildren();
    //Ummmm, there has to be a better way to do this...
    return children[2];
  }
}
