package gw.plugin.ij.run;

import gw.lang.reflect.IType;
import gw.lang.reflect.TypeSystem;
import gw.lang.reflect.gs.IGosuClass;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class GosuEnabledJUnitStarter {

  public static void main(String[] args) throws Exception
  {
    Class<?> clzz = Class.forName( "com.intellij.rt.execution.junit.JUnitStarter", false, SleazyClassLoader.INSTANCE );
    initGosu();
    SleazyClassLoader.INSTANCE.setResolveGosu( true );
    clzz.getMethod( "main", String[].class ).invoke( null, (Object)args );
  }

  private static void initGosu() throws Exception
  {
//    System.out.println("!!!!!!");
    String property = System.getProperty( "gosu.cp" );
//    System.out.println( property );
//    System.out.println("!!!!!!");
    ArrayList<File> classpath = new ArrayList<File>();
    for( String splitPath : property.split( "," ) )
    {
      try
      {
        File file = new File( splitPath );
        classpath.add( file );
      }
      catch( Exception e )
      {
        System.out.println("Error with URI: " + splitPath );
        e.printStackTrace();
      }
    }
    String systemClassPath = System.getProperty( "java.class.path" );
    for( String s : systemClassPath.split( File.pathSeparator ) )
    {
      classpath.add( new File( s ) );
    }
    Class.forName( "gw.lang.shell.Gosu", false, SleazyClassLoader.INSTANCE ).getMethod( "init", List.class ).invoke( null, classpath );
  }

  private static class SleazyClassLoader extends URLClassLoader
  {
    public static SleazyClassLoader INSTANCE = new SleazyClassLoader();
    private boolean resolveGosu = false;

    private SleazyClassLoader()
    {
      super( stealClasspath(), null );
    }

    private static URL[] stealClasspath()
    {
      List<URL> classpath = new ArrayList<URL>();
      for( String s : System.getProperty( "java.class.path" ).split( File.pathSeparator ) )
      {
        try
        {
          classpath.add( new File( s ).toURI().toURL() );
        }
        catch( MalformedURLException e )
        {
          e.printStackTrace();
        }
      }
      return classpath.toArray( new URL[classpath.size()] );
    }

    @Override
    public Class<?> loadClass( String name ) throws ClassNotFoundException
    {
      try
      {
        return super.loadClass( name );
      }
      catch( ClassNotFoundException e )
      {
        if( resolveGosu )
        {
          try
          {
            Class gosuClass = getGosuClassResolver( name ).call();
            if( gosuClass != null )
            {
              return gosuClass;
            }
          }
          catch( Exception e2 )
          {
            e2.printStackTrace();
          }
        }
        throw e;
      }
    }

    public void setResolveGosu( boolean resolveGosu )
    {
      this.resolveGosu = resolveGosu;
    }

    public Callable<Class> getGosuClassResolver( String name ) throws Exception
    {
      return (Callable<Class>)loadClass( LoadFunc.class.getName() ).getConstructor( String.class ).newInstance( name );
    }

    public static class LoadFunc implements Callable<Class>
    {
      private String _name  ;

      public LoadFunc( String name )
      {
        _name = name;
      }

      @Override
      public Class call() throws Exception
      {
        IType type = TypeSystem.getByFullNameIfValid( _name );
        if( type instanceof IGosuClass )
        {
          return ((IGosuClass)type).getBackingClass();
        }
        return null;
      }
    }
  }
}
