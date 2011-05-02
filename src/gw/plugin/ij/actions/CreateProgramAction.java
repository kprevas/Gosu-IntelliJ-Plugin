package gw.plugin.ij.actions;

import com.intellij.ide.IdeBundle;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.ide.actions.CreateTemplateInPackageAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import gw.plugin.ij.GosuIcons;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class CreateProgramAction extends CreateTemplateInPackageAction<PsiFile>
{
  public CreateProgramAction()
  {
    super( "Gosu Program", "Create new Gosu program", GosuIcons.FILE_PROG, true );
  }

  @NotNull
  @Override
  protected void buildDialog( Project project, final PsiDirectory directory, com.intellij.ide.actions.CreateFileFromTemplateDialog.Builder builder )
  {
    builder.addKind( "Standard", GosuIcons.FILE_PROG, "Gosu.standard" );
  }

  @Override
  protected String getErrorTitle()
  {
    return IdeBundle.message( "title.cannot.create.class" );
  }


  @Override
  protected String getActionName( PsiDirectory directory, String newName, String templateName )
  {
    return IdeBundle.message( "progress.creating.class", JavaDirectoryService.getInstance().getPackage( directory ).getQualifiedName(), newName );
  }

  protected final PsiFile doCreate( PsiDirectory dir, String className, String templateName ) throws IncorrectOperationException
  {
    //String strTypeKind = templateName.substring( templateName.indexOf( '.' ) + 1 );
    String strFile = className + ".gsp";
    dir.checkCreateFile( strFile );
    String strPackage = JavaDirectoryService.getInstance().getPackage( dir ).getQualifiedName();
    PsiFile file = dir.createFile( strFile );
    try
    {
      OutputStream os = file.getVirtualFile().getOutputStream( this );
      OutputStreamWriter writer = new OutputStreamWriter( os );
      int iDepth = strPackage.split( "\\." ).length;
      String strClasspath = "";
      for( int i = 0; i < iDepth; i++ )
      {
        strClasspath += "../";
      }
      writer.write( "classpath \"" + strClasspath + "\"\n" );
      writer.close();
      return file;
    }
    catch( Exception e )
    {
      throw new RuntimeException( e );
    }
  }

  @Override
  protected PsiElement getNavigationElement( @NotNull PsiFile createdElement )
  {
    return createdElement;
  }

  @Override
  protected boolean checkPackageExists( PsiDirectory directory )
  {
    return true;
  }

  @Override
  protected void doCheckCreate( PsiDirectory dir, String className, String templateName ) throws IncorrectOperationException
  {
    dir.checkCreateFile( className );
  }
}
