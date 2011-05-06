package gw.plugin.ij.lang.psi.util;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.util.IncorrectOperationException;
import gw.lang.reflect.TypeSystem;
import gw.plugin.ij.GosuProgramFileType;
import gw.plugin.ij.lang.GosuElementType;
import gw.plugin.ij.lang.parser.GosuElementTypes;
import gw.plugin.ij.lang.psi.GosuFakeVirtualFile;
import gw.plugin.ij.lang.psi.IGosuPsiElement;
import gw.plugin.ij.lang.psi.impl.GosuClassFileImpl;
import gw.plugin.ij.lang.psi.impl.GosuProgramFileImpl;
import gw.plugin.ij.lang.psi.impl.GosuPsiElementImpl;
import gw.plugin.ij.lang.psi.impl.expressions.GosuReferenceExpressionImpl;
import gw.plugin.ij.lang.psi.impl.expressions.GosuTypeLiteralImpl;
import gw.plugin.ij.lang.psi.impl.statements.GosuUsesStatementImpl;
import gw.plugin.ij.lang.psi.impl.statements.GosuUsesStatementListImpl;
import gw.plugin.ij.lang.psi.stubs.GosuFileStub;

/**
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuPsiParseUtil
{
  public static GosuProgramFileImpl createVirtualProgramFile( IGosuPsiElement psi, String strScript )
  {
    GosuClassFileImpl file = (GosuClassFileImpl) psi.getContainingFile();
    ASTNode child = file.getNode().findChildByType(GosuElementTypes.ELEM_TYPE_UsesStatementList);
    if (child != null) {
      strScript = child.getText() + "\n" + strScript;
    }
    child = file.getNode().findChildByType(GosuElementTypes.ELEM_TYPE_NamespaceStatement);
    if (child != null) {
      String text = child.getText();
      text = text.replace("package", "uses") + ".*";
      strScript = text + "\n" + strScript;
    }

    PsiFileFactory factory = PsiFileFactory.getInstance(psi.getProject());
    return (GosuProgramFileImpl) factory.createFileFromText("transient_program.gsp", GosuProgramFileType.instance(),
        strScript, System.currentTimeMillis(), false);
  }

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

  public static PsiElement createReferenceNameFromText(GosuReferenceExpressionImpl referenceExpression, String refName) {
    GosuProgramFileImpl file = createVirtualProgramFile(referenceExpression, "return " + refName);
    PsiElement element = file.getChildren()[3].getChildren()[0].getFirstChild();
    return element;
  }

}
