package gw.plugin.ij.actions;

import com.intellij.ide.IdeBundle;
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
public class CreateClassAction extends CreateTemplateInPackageAction<PsiFile>
{
  public CreateClassAction()
  {
    super( "Gosu Class", "Create new Gosu class", GosuIcons.FILE_CLASS, true );
  }

  protected void buildDialog( Project project, final PsiDirectory directory, com.intellij.ide.actions.CreateFileFromTemplateDialog.Builder builder )
  {
    builder.addKind( "Class", GosuIcons.FILE_CLASS, "Gosu.class" );
    builder.addKind( "Interface", GosuIcons.FILE_CLASS, "Gosu.interface" );
    builder.addKind( "Enum", GosuIcons.FILE_CLASS, "Gosu.enum" );
    //builder.addKind( "Annotation", Icons.ANNOTATION_TYPE_ICON, "Gosu.class" );
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
    String strTypeKind = templateName.substring( templateName.indexOf( '.' ) + 1 );
    String strFile = className + ".gs";
    dir.checkCreateFile( strFile );
    PsiFile file = dir.createFile( strFile );
    try
    {
      OutputStream os = file.getVirtualFile().getOutputStream( this );
      OutputStreamWriter writer = new OutputStreamWriter( os );
      writer.write(
        "package " + JavaDirectoryService.getInstance().getPackage( dir ).getQualifiedName() + "\n" +
        "\n" +
        strTypeKind + " " + className + " {\n\n" +
        "}"
      );
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
  protected void doCheckCreate( PsiDirectory dir, String className, String templateName ) throws IncorrectOperationException
  {
    JavaDirectoryService.getInstance().checkCreateClass( dir, className );
  }

  @Override
  protected boolean checkPackageExists( PsiDirectory psiDirectory )
  {
    return true;
  }
}
