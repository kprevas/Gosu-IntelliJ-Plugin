package gw.plugin.ij.lang.psi.impl;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiClass;
import com.intellij.psi.impl.ElementBase;
import com.intellij.psi.stubs.StubElement;
import gw.lang.reflect.TypeSystem;
import gw.plugin.ij.GosuIcons;
import gw.plugin.ij.GosuProgramFileType;
import gw.plugin.ij.lang.parser.GosuElementTypes;
import gw.plugin.ij.lang.psi.GosuNamedElement;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuMethod;
import gw.plugin.ij.lang.psi.impl.synthetic.GosuSyntheticProgramClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuProgramFileImpl extends GosuClassFileImpl
{
  private GosuSyntheticProgramClass _synthClass;

  public GosuProgramFileImpl( FileViewProvider viewProvider )
  {
    super( viewProvider );
  }

  @Nullable
  public Icon getIcon( int flags )
  {
    Icon baseIcon = GosuIcons.FILE_PROG;
    return ElementBase.createLayeredIcon( baseIcon, ElementBase.transformFlags( this, flags ) );
  }

  @SuppressWarnings({"CloneDoesntDeclareCloneNotSupportedException"})
  protected GosuProgramFileImpl clone()
  {
    return (GosuProgramFileImpl)super.clone();
  }

  public String getExtension()
  {
    VirtualFile vfile = getFileFromPsi();
    String strName = vfile.getName();
    return strName.substring( strName.lastIndexOf( '.' ) );
  }

  @NotNull
  @Override
  public FileType getFileType()
  {
    return GosuProgramFileType.instance();
  }

  public String toString()
  {
    return "Gosu program: " + getName();
  }

  public GosuNamedElement[] getTopLevelDefinitions()
  {
    return findChildrenByClass( GosuNamedElement.class );
  }

  public GosuMethod[] getTopLevelMethods()
  {
    final StubElement<?> stub = getStub();
    if( stub != null )
    {
      return stub.getChildrenByType( GosuElementTypes.METHOD_DEFINITION, GosuMethod.ARRAY_FACTORY );
    }

    return calcTreeElement().getChildrenAsPsiElements( GosuElementTypes.METHOD_DEFINITION, GosuMethod.ARRAY_FACTORY );
  }

  public GosuSyntheticProgramClass getScriptClass()
  {
    if( _synthClass == null )
    {
      TypeSystem.lock();
      try
      {
        if( _synthClass == null )
        {
          _synthClass = new GosuSyntheticProgramClass( this );
        }
      }
      finally
      {
        TypeSystem.unlock();
      }
    }
    return _synthClass;
  }

  @NotNull
  @Override
  public PsiClass[] getClasses()
  {
    return new PsiClass[] {getScriptClass()};
  }
}
