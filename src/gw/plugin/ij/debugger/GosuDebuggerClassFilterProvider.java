package gw.plugin.ij.debugger;

/**
 * Copyright 2010 Guidewire Software, Inc.
 */
import com.intellij.ui.classFilter.ClassFilter;
import com.intellij.ui.classFilter.DebuggerClassFilterProvider;

import java.util.Arrays;
import java.util.List;


public class GosuDebuggerClassFilterProvider /*extends StackFrameFilter*/ implements DebuggerClassFilterProvider
{
  private static final ClassFilter[]
    FILTERS =
    {
      new ClassFilter( "gw.lang.*" ),
      new ClassFilter( "gw.config.*" ),
      new ClassFilter( "gw.util.*" ),
      new ClassFilter( "gw.internal.gosu.*" ),
    };

  public List<ClassFilter> getFilters()
  {
    return Arrays.asList( FILTERS );
  }

  public boolean isAuxiliaryFrame( String className, String methodName )
  {
//    if( className.equals( "gw.internal.whatever" ) ||
//        className.equals( "gw.internal.whichever" ) )
//    {
//      return false;
//    }
//
//    for( ClassFilter filter : FILTERS )
//    {
//      final String pattern = filter.getPattern();
//      if( className.startsWith( pattern.substring( 0, pattern.length() - 1 ) ) )
//      {
//        return true;
//      }
//    }
    return false;
  }
}
