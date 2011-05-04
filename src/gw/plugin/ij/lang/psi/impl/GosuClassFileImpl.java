package gw.plugin.ij.lang.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.ElementBase;
import com.intellij.psi.impl.PsiManagerEx;
import com.intellij.psi.impl.file.impl.FileManagerImpl;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.indexing.FileBasedIndex;
import gw.plugin.ij.GosuIcons;
import gw.plugin.ij.GosuLanguage;
import gw.plugin.ij.lang.parser.GosuElementTypes;
import gw.plugin.ij.lang.psi.GosuFakeVirtualFile;
import gw.plugin.ij.lang.psi.GosuFile;
import gw.plugin.ij.lang.psi.api.GosuPackageDefinition;
import gw.plugin.ij.lang.psi.impl.expressions.GosuTypeLiteralImpl;
import gw.plugin.ij.lang.psi.impl.statements.GosuUsesStatementListImpl;
import gw.plugin.ij.lang.psi.util.GosuPsiParseUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuClassFileImpl extends GosuFileBaseImpl implements GosuFile
{
  private static final Logger LOG = Logger.getInstance( "gw.plugin.ij.lang.psi.impl.GosuClassFileImpl" );

  private PsiElement _context;


  public GosuClassFileImpl( FileViewProvider viewProvider )
  {
    super( viewProvider, GosuLanguage.instance() );
    //this.putUserData( BlockSupport.TREE_DEPTH_LIMIT_EXCEEDED, Boolean.TRUE );
  }

  @NotNull
  public String getPackageName()
  {
    return classNameFromFile().substring( 0, classNameFromFile().lastIndexOf( '.' ) );
//    final StubElement stub = getStub();
//    if( stub instanceof GosuFileStub )
//    {
//      return ((GosuFileStub)stub).getPackageName().toString();
//    }
//    GosuPackageDefinition packageDef = getPackageDefinition();
//    if( packageDef != null )
//    {
//      return packageDef.getPackageName();
//    }
//    return "";
  }

  public void setPackageName( String s ) throws IncorrectOperationException
  {
    throw new UnsupportedOperationException( "Men at work" );
  }

  public GosuPackageDefinition getPackageDefinition()
  {
    ASTNode node = calcTreeElement().findChildByType( GosuElementTypes.ELEM_TYPE_NamespaceStatement );
    return node != null ? (GosuPackageDefinition)node.getPsi() : null;
  }

  @Nullable
  public Icon getIcon( int flags )
  {
    Icon baseIcon = GosuIcons.FILE_CLASS;
    return ElementBase.createLayeredIcon( baseIcon, ElementBase.transformFlags( this, flags ) );
  }

  @Override
  public void subtreeChanged()
  {
    super.subtreeChanged();
  }

  public void clearCaches()
  {
    super.clearCaches();
//    myScriptClassInitialized = false;
//    synchronized( lock )
//    {
//      mySyntheticArgsParameter = null;
//    }
  }

  public PsiElement getContext()
  {
    if( _context != null )
    {
      return _context;
    }
    return super.getContext();
  }

  @SuppressWarnings({"CloneDoesntDeclareCloneNotSupportedException"})
  protected GosuClassFileImpl clone()
  {
    GosuClassFileImpl clone = (GosuClassFileImpl)super.clone();
    clone._context = _context;
    return clone;
  }

  public void setContext( PsiElement context )
  {
    if( context != null )
    {
      _context = context;
    }
  }

  public GlobalSearchScope getFileResolveScope()
  {
    final PsiElement context = getContext();
    if( context instanceof GosuFile )
    {
      return context.getResolveScope();
    }

    final VirtualFile vFile = getOriginalFile().getVirtualFile();
    if( vFile == null )
    {
      return GlobalSearchScope.allScope( getProject() );
    }

    return ((FileManagerImpl)((PsiManagerEx)getManager()).getFileManager()).getDefaultResolveScope( vFile );
  }

  public boolean importClass( PsiClass psiClass )
  {
    throw new UnsupportedOperationException();
  }

  public PsiClass getPsiClass()
  {
    try {
      return getClasses()[0];
    } catch(IndexOutOfBoundsException ex) {
      return null;
    }
  }

  public String getTypeName()
  {
    return classNameFromFile();
  }

  public String classNameFromFile()
  {
//    return "abc.Fubar";
    VirtualFile vfile = getFileFromPsi();
    if( vfile instanceof GosuFakeVirtualFile )
    {
      return vfile.getName();
    } else {
      String strPackage = getRelativeFile( vfile );
      String strName = strPackage.substring( 0, strPackage.lastIndexOf( '.' ) );
      return strName;
    }
  }

  public VirtualFile getFileFromPsi()
  {
    VirtualFile vfile = getUserData( FileBasedIndex.VIRTUAL_FILE );
    if( vfile == null )
    {
      vfile = getVirtualFile();
      if( vfile == null )
      {
        vfile = getOriginalFile().getVirtualFile();
      }
    }
    return vfile;
  }

  public String getExtension()
  {
    //return ".gs";
    VirtualFile vfile = getFileFromPsi();
    if( vfile instanceof GosuFakeVirtualFile )
    {
      return ".gs";
    } else {
      String strName = vfile.getName();
      return strName.substring( strName.lastIndexOf( '.' ) );
    }
  }

  private String getRelativeFile( VirtualFile vfile )
  {
    VirtualFile dir = vfile.getParent();
    //PsiPackage pkg = JavaDirectoryService.getInstance().getPackage( dir );
    String strPath = vfile.getPresentableUrl();

    //## todo: major hack
    String gosuCoreAPI = "classes";
    if( strPath.contains( gosuCoreAPI ) )
    {
      strPath = strPath.substring( strPath.toLowerCase().indexOf( gosuCoreAPI ) + gosuCoreAPI.length() + 1 );
    }
    else
    {
      strPath = strPath.substring( strPath.toLowerCase().indexOf( "src" ) + 4 );
    }
    return strPath.replace( File.separatorChar, '.' );
  }

  public void accept( GosuElementVisitor visitor )
  {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public void acceptChildren( GosuElementVisitor visitor )
  {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public void addImport( GosuTypeLiteralImpl tl )
  {
    GosuUsesStatementListImpl usesStatementList = findChildByClass( GosuUsesStatementListImpl.class );
    PsiElement usesStmt = GosuPsiParseUtil.parseImport( tl.getText(), getManager() );
    if( usesStatementList == null )
    {
      GosuPackageDefinition packagePosition = findChildByClass( GosuPackageDefinition.class );
      addAfter( usesStmt, packagePosition );
    }
    else
    {
      usesStatementList.add( usesStmt.getChildren()[0]);
    }
  }

  public String toString()
  {
    return "Gosu class: " + getName();
  }
}
