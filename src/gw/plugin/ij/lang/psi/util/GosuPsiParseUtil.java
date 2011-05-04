package gw.plugin.ij.lang.psi.util;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.SingleRootFileViewProvider;
import com.intellij.testFramework.LightVirtualFile;
import gw.lang.reflect.TypeSystem;
import gw.plugin.ij.GosuProgramFileType;
import gw.plugin.ij.lang.psi.GosuFakeVirtualFile;
import gw.plugin.ij.lang.psi.impl.GosuClassFileImpl;
import gw.plugin.ij.lang.psi.impl.GosuProgramFileImpl;
import gw.plugin.ij.lang.psi.stubs.GosuFileStub;

/**
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuPsiParseUtil
{
//  public GosuProgramFileImpl createVirtualProgramFile( Project project, String strScript )
//  {
//    return (GosuProgramFileImpl)PsiFileFactory.getInstance( project ).createFileFromText( "transient_program.gsp", GosuProgramFileType.instance(),
//                                                                                          strScript, System.currentTimeMillis(), false );
//  }
//
//  public PsiElement createVirtualProgramFile( Project project, String strScript )
//  {
//    return (GosuProgramFileImpl)PsiFileFactory.getInstance( project ).createFileFromText( "transient_program.gsp", GosuProgramFileType.instance(),
//                                                                                          strScript, System.currentTimeMillis(), false );
//  }
//
  public static PsiElement parseExpression( String s, PsiManager manager )
  {
    LightVirtualFile fakeFile = new GosuFakeVirtualFile( s, "DummyProgram" );
    GosuFileStub gosuFileStub = new GosuFileStub( new GosuProgramFileImpl( new SingleRootFileViewProvider( manager, fakeFile, false ) ) );
    PsiElement[] children = gosuFileStub.getPsi().getChildren();
    //Ummmm, there has to be a better way to do this...
    return children[0].getChildren()[0];
  }

  public static PsiElement parseImport( String s, PsiManager manager )
  {
    LightVirtualFile fakeFile = new GosuFakeVirtualFile( "uses " + s, "DummyProgram" );
    GosuFileStub gosuFileStub = new GosuFileStub( new GosuProgramFileImpl( new SingleRootFileViewProvider( manager, fakeFile, false ) ) );
    PsiElement[] children = gosuFileStub.getPsi().getChildren();
    //Ummmm, there has to be a better way to do this...
    return children[0].getChildren()[0];
  }
}
