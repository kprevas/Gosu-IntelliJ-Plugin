package gw.plugin.ij.run;

import com.intellij.execution.Executor;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.ModuleBasedConfiguration;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.runners.JavaProgramPatcher;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.LibraryOrderEntry;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.PathUtil;
import gw.internal.ext.org.apache.commons.cli.ParseException;

import java.io.File;
import java.net.URL;

public class GosuProgramPatcher extends JavaProgramPatcher
{
  @Override
  public void patchJavaParameters( Executor executor, RunProfile configuration, JavaParameters javaParameters )
  {
    if( javaParameters.getMainClass().equals( "com.intellij.rt.execution.junit.JUnitStarter" ) )
    {
      javaParameters.setMainClass( GosuEnabledJUnitStarter.class.getName() );
      if( configuration instanceof ModuleBasedConfiguration )
      {
        addGosuClasspath( (ModuleBasedConfiguration)configuration, javaParameters );
      }
      // add in starter resource
      javaParameters.getClassPath().add( PathUtil.getJarPathForClass( GosuEnabledJUnitStarter.class ) );

      // add in starter resource
      addGosuResources( javaParameters );
    }
  }

  private void addGosuClasspath( ModuleBasedConfiguration configuration, JavaParameters javaParameters )
  {
    Module[] modules = configuration.getModules();
    StringBuilder gosuCP = new StringBuilder();
    for( Module module : modules )
    {
      ModuleRootManager rootManager = ModuleRootManager.getInstance(module);
      VirtualFile[] sourceRoots = rootManager.getSourceRoots();
      for( VirtualFile sourceRoot : sourceRoots )
      {
        gosuCP.append( sourceRoot.getPath() );
      }
    }
    javaParameters.getVMParametersList().add( "-Dgosu.cp=\"" + gosuCP.toString() + "\"");
  }

  private void addGosuResources( JavaParameters javaParameters )
  {
    File gosuJar = new File( PathUtil.getJarPathForClass( ParseException.class ) );
    File[] files = gosuJar.getParentFile().listFiles();
    for( File file : files )
    {
      if( file.getName().startsWith( "gw-" ) && file.getName().endsWith( ".jar" ) )
      {
        javaParameters.getClassPath().add( file.getAbsolutePath() );
      }
    }
  }
}
