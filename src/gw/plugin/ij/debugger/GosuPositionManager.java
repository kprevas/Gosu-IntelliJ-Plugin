package gw.plugin.ij.debugger;

import com.intellij.debugger.NoDataException;
import com.intellij.debugger.PositionManager;
import com.intellij.debugger.SourcePosition;
import com.intellij.debugger.engine.DebugProcess;
import com.intellij.debugger.requests.ClassPrepareRequestor;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectAndLibrariesScope;
import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.ClassLoaderReference;
import com.sun.jdi.ClassType;
import com.sun.jdi.Location;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.request.ClassPrepareRequest;
import gw.plugin.ij.GosuFileTypes;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuPositionManager implements PositionManager
{
  private static final Logger LOG = Logger.getInstance( "#" + GosuPositionManager.class.getName() );

  private DebugProcess _debugProcess;


  public GosuPositionManager( DebugProcess debugProcess )
  {
    _debugProcess = debugProcess;
  }

  public SourcePosition getSourcePosition( Location location ) throws NoDataException
  {
    try
    {
      String sourcePath = getRelativePath( location.sourcePath() );
      PsiFile file = findSourceFile( sourcePath, _debugProcess.getProject(), _debugProcess.getSearchScope() );
      if( file != null )
      {
        int lineNumber = location.lineNumber();
        return SourcePosition.createFromLine( file, lineNumber - 1 );
      }
    }
    catch( AbsentInformationException ignored )
    {
      // ignore?
    }
    catch( Throwable e )
    {
      LOG.info( e );
    }
    throw new NoDataException();
  }

  @NotNull
  public List<ReferenceType> getAllClasses( final SourcePosition classPosition ) throws NoDataException
  {
    if( !checkSourcePositionFileType( classPosition ) )
    {
      throw new NoDataException();
    }

    return
      ApplicationManager.getApplication().runReadAction(
        new Computable<List<ReferenceType>>()
        {
          public List<ReferenceType> compute()
          {
            String strClassName = classNameFromFile( classPosition );
            List<ReferenceType> referenceTypes = _debugProcess.getVirtualMachineProxy().classesByName( strClassName );
            List<ReferenceType> result = new ArrayList<ReferenceType>( 2 );
            for( ReferenceType referenceType : referenceTypes )
            {
              List<Location> locations = getLocationsOfClassAt( referenceType, classPosition );
              if( locations != null && locations.size() > 0 )
              {
                ReferenceType type = locations.get( 0 ).declaringType();
                result.add( type );
              }
            }
            return result;
          }
        } );
  }

  private String classNameFromFile( final SourcePosition classPosition )
  {
    String strPackage = getPackage( classPosition );
    String strFileName = classPosition.getFile().getName();
    String strName = strFileName.substring( 0, strFileName.indexOf( '.' ) );
    return strPackage + '.' + strName;
  }

  private String getPackage( SourcePosition classPosition )
  {
    PsiDirectory dir = classPosition.getFile().getParent();
    PsiPackage pkg = JavaDirectoryService.getInstance().getPackage( dir );
    if( pkg != null )
    {
      return pkg.getName();
    }
    String strPath = dir.getVirtualFile().getPath();
    return getPackage( strPath, "gsrc/", "gtest/", "config/rules/" );
  }

  private String getPackage( String strPath, String... hackDirs )
  {
    for( String strHack : hackDirs )
    {
      int iIndex = strPath.indexOf( strHack );
      if( iIndex >= 0 )
      {
        strPath = strPath.substring( iIndex + strHack.length() );
        return strPath.replace( '/', '.' );
      }
    }
    return null;
  }

  private boolean checkSourcePositionFileType( SourcePosition position )
  {
    FileType fileType = position.getFile().getFileType();
    return GosuFileTypes.TYPES.contains( fileType );
  }

  @NotNull
  public List<Location> locationsOfLine( ReferenceType type, SourcePosition position ) throws NoDataException
  {
    if( !checkSourcePositionFileType( position ) )
    {
      throw new NoDataException();
    }

    try
    {
      int iLine = position.getLine() + 1;
      List<Location> locations = type.locationsOfLine( DebugProcess.JAVA_STRATUM, null, iLine );
      if( locations.size() > 0 )
      {
        return locations;
      }
    }
    catch( AbsentInformationException e )
    {
      // ignore
    }

    return Collections.emptyList();
  }

  private List<Location> getLocationsOfClassAt( final ReferenceType type, final SourcePosition position )
  {
    if( !(type instanceof ClassType) )
    {
      return null;
    }

    ClassLoaderReference loader = type.classLoader();
    if( loader == null || !loader.type().name().endsWith( "GosuClassLoader" ) )
    {
      return null;
    }

    if( !checkSourcePositionFileType( position ) )
    {
      return Collections.emptyList();
    }

    return ApplicationManager.getApplication().runReadAction(
      new Computable<List<Location>>()
      {
        public List<Location> compute()
        {
          try
          {
            final List<String> relativePaths = getRelativeSourePathsByType( type );
            for( String relativePath : relativePaths )
            {
              PsiFile file = findSourceFile( relativePath, _debugProcess.getProject(), _debugProcess.getSearchScope() );
              if( file != null && file.equals( position.getFile() ) )
              {
                return getLocationsOfLine( type, getSourceName( file.getName(), type ), position.getLine() + 1 );
              }
            }
          }
          catch( AbsentInformationException e )
          {
            // ignore?
          }
          return null;
        }

        private String getSourceName( String strName, ReferenceType type ) throws AbsentInformationException
        {
          String strSourceName = type.sourceName();
          if( strSourceName.indexOf( strName ) >= 0 )
          {
            return strSourceName;
          }
          return strName;
        }
      } );
  }

  private List<String> getRelativeSourePathsByType( ReferenceType type ) throws AbsentInformationException
  {
    List<String> paths = type.sourcePaths( null );
    ArrayList<String> relativePaths = new ArrayList<String>();
    for( String path : paths )
    {
      relativePaths.add( getRelativePath( path ) );
    }
    return relativePaths;
  }

  private List<Location> getLocationsOfLine( ReferenceType type, String fileName, int lineNumber ) throws AbsentInformationException
  {
    List<Location> locations = type.locationsOfLine( null, fileName, lineNumber );
    if( locations.isEmpty() )
    {
      List<ReferenceType> innerTypes = type.nestedTypes();
      for( ReferenceType inner : innerTypes )
      {
        locations = getLocationsOfLine( inner, fileName, lineNumber );
        if( !locations.isEmpty() )
        {
          break;
        }
      }
    }
    return locations;
  }

  public ClassPrepareRequest createPrepareRequest( final ClassPrepareRequestor requestor, final SourcePosition position ) throws NoDataException
  {
    if( !checkSourcePositionFileType( position ) )
    {
      throw new NoDataException();
    }

    return _debugProcess.getRequestsManager().createClassPrepareRequest(
      new ClassPrepareRequestor()
      {
        public void processClassPrepare( DebugProcess debuggerProcess, ReferenceType referenceType )
        {
          onClassPrepare( debuggerProcess, referenceType, position, requestor );
        }
      }, "*" );
  }

  private void onClassPrepare( DebugProcess debuggerProcess,
                               ReferenceType referenceType,
                               SourcePosition position,
                               ClassPrepareRequestor requestor )
  {
    List<Location> locationsOfClassAt = getLocationsOfClassAt( referenceType, position );
    if( locationsOfClassAt != null && !locationsOfClassAt.isEmpty() )
    {
      requestor.processClassPrepare( debuggerProcess, referenceType );
    }
  }

  private String getRelativePath( String strSourcePath )
  {
    if( strSourcePath != null )
    {
      strSourcePath = strSourcePath.trim();
      if( strSourcePath.startsWith( "*" ) )
      {
        strSourcePath = strSourcePath.substring( "*".length() + 1 );
      }
    }

    return strSourcePath;
  }

  public static PsiFile findSourceFile( String strRelativePath, Project project, GlobalSearchScope searchScope )
  {
    if( !isGosuSourceFile( strRelativePath ) )
    {
      return null;
    }

    PsiManager manager = PsiManager.getInstance( project );

    Module runtimeModule = getRuntimeModule( project, searchScope );
    List<Module> modulesInGraph = runtimeModule != null
                                  ? getAllDependencyModules( runtimeModule )
                                  : Arrays.asList( ModuleManager.getInstance( project ).getModules() );

    for( Module module : modulesInGraph )
    {
      ModuleRootManager rootManager = ModuleRootManager.getInstance( module );

      VirtualFile[] roots = rootManager.getSourceRoots();
      for( VirtualFile root : roots )
      {
        VirtualFile relativeFile = VfsUtil.findRelativeFile( strRelativePath, root );
        if( relativeFile != null )
        {
          PsiFile file = manager.findFile( relativeFile );
          if( file != null )
          {
            return file;
          }
        }
      }

      roots = rootManager.getContentRoots();
      for( VirtualFile root : roots )
      {
        //## hack: hard-coding "gsrc" for now.. until we correctly mark all our source dirs in ij
        PsiFile file = findSourceFile( manager, root, strRelativePath, "gsrc" );
        if( file != null )
        {
          return file;
        }

        //## hack: hard-coding "gtest" for now.. until we correctly mark all our source dirs in ij
        file = findSourceFile( manager, root, strRelativePath, "gtest" );
        if( file != null )
        {
          return file;
        }

        //## hack: hard-coding "config" for now.. until we correctly mark all our source dirs in ij
        file = findSourceFile( manager, root, strRelativePath, "config" );
        if( file != null )
        {
          return file;
        }
      }
    }
    return null;
  }

  private static List<Module> getAllDependencyModules( Module module )
  {
    ArrayList<Module> modules = new ArrayList<Module>();
    _getAllDependencyModules( module, modules );
    return modules;
  }
  private static void _getAllDependencyModules( Module module, List<Module> result )
  {
    result.add( module );
    Module[] deps = ModuleRootManager.getInstance( module ).getDependencies();
    for( Module m : deps )
    {
      if( !result.contains( m ) )
      {
        _getAllDependencyModules( m, result );
      }
    }
  }

  private static Module getRuntimeModule( Project project, GlobalSearchScope runtimeScope )
  {
    if( runtimeScope instanceof ProjectAndLibrariesScope )
    {
      // A truly *global* scope, includes all root modules
      return null;
    }

    String strDisplayName = runtimeScope.getDisplayName();
    int iIndex = strDisplayName.lastIndexOf( " runtime scope" );
    String strModuleName;
    if( iIndex >= 0 )
    {
      strModuleName = strDisplayName.substring( 0, iIndex );
    }
    else
    {
      strModuleName = strDisplayName.substring( 0, strDisplayName.indexOf( ' ' ) );
    }
    return ModuleManager.getInstance( project ).findModuleByName( strModuleName );
  }

  private static PsiFile findSourceFile( PsiManager manager, VirtualFile root, String strRelativePath, String strDir )
  {
    VirtualFile subRoot = root.findChild( strDir );
    if( subRoot != null )
    {
      VirtualFile relativeFile = VfsUtil.findRelativeFile( strRelativePath, subRoot );
      if( relativeFile != null )
      {
        PsiFile file = manager.findFile( relativeFile );
        if( file != null )
        {
          return file;
        }
      }
    }
    return null;
  }

  private static boolean isGosuSourceFile( String strRelativePath )
  {
    for( FileType ftype : GosuFileTypes.TYPES )
    {
      if( strRelativePath.endsWith( ftype.getDefaultExtension() ) )
      {
        return true;
      }
    }
    return false;
  }
}