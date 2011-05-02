package gw.plugin.ij.lang.psi.impl;

import com.intellij.ProjectTopics;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootEvent;
import com.intellij.openapi.roots.ModuleRootListener;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.PsiManagerEx;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.util.ConcurrencyUtil;
import com.intellij.util.Function;
import com.intellij.util.containers.ConcurrentWeakHashMap;
import com.intellij.util.containers.ContainerUtil;
import gw.plugin.ij.lang.psi.IGosuPsiElement;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuTypeDefinition;
import gw.plugin.ij.lang.psi.stubs.GosuShortNamesCache;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuPsiManager
{
  private static final Logger LOG = Logger.getInstance( "gw.plugin.ij.lang.psi.impl.GosuPsiManager" );
  private final Project myProject;

  private GosuTypeDefinition myArrayClass;

  private final ConcurrentWeakHashMap<IGosuPsiElement, PsiType> myCalculatedTypes = new ConcurrentWeakHashMap<IGosuPsiElement, PsiType>();
  private final GosuShortNamesCache myCache;

//  private final TypeInferenceHelper myTypeInferenceHelper;

  private static final String SYNTHETIC_CLASS_TEXT = "class __ARRAY__ { public int length }";

  public GosuPsiManager( Project project )
  {
    myProject = project;
    myCache = ContainerUtil.findInstance( project.getExtensions( PsiShortNamesCache.EP_NAME ), GosuShortNamesCache.class );

    ((PsiManagerEx)PsiManager.getInstance( myProject )).registerRunnableToRunOnAnyChange( new Runnable()
    {
      public void run()
      {
        dropTypesCache();
      }
    } );

//    myTypeInferenceHelper = new TypeInferenceHelper( myProject );

    myProject.getMessageBus().connect().subscribe( ProjectTopics.PROJECT_ROOTS,
      new ModuleRootListener()
      {
        public void beforeRootsChange( ModuleRootEvent event )
        {
        }

        public void rootsChanged( ModuleRootEvent event )
        {
          dropTypesCache();
        }
      } );
  }

//  public TypeInferenceHelper getTypeInferenceHelper()
//  {
//    return myTypeInferenceHelper;
//  }

  public void dropTypesCache()
  {
    myCalculatedTypes.clear();
  }


  public static GosuPsiManager getInstance( Project project )
  {
    return ServiceManager.getService( project, GosuPsiManager.class );
  }

  @Nullable
  public <T extends IGosuPsiElement> PsiType getType( T element, Function<T, PsiType> calculator )
  {
    PsiType type = myCalculatedTypes.get( element );
    if( type == null )
    {
      type = calculator.fun( element );
      if( type == null )
      {
        type = PsiType.NULL;
      }
      type = ConcurrencyUtil.cacheOrGet( myCalculatedTypes, element, type );
    }
    if( !type.isValid() )
    {
      LOG.error( "Type is invalid: " + type + "; element: " + element + " of class " + element.getClass() );
    }
    return PsiType.NULL.equals( type ) ? null : type;
  }

//  public GosuTypeDefinition getArrayClass()
//  {
//    if( myArrayClass == null )
//    {
//      try
//      {
//        myArrayClass = GosuPsiElementFactory.getInstance( myProject ).createTypeDefinition( SYNTHETIC_CLASS_TEXT );
//      }
//      catch( IncorrectOperationException e )
//      {
//        LOG.error( e );
//      }
//    }
//
//    return myArrayClass;
//  }

  private static final ThreadLocal<List<PsiElement>> myElementsWithTypesBeingInferred = new ThreadLocal<List<PsiElement>>()
  {
    protected List<PsiElement> initialValue()
    {
      return new ArrayList<PsiElement>();
    }
  };

  public GosuShortNamesCache getNamesCache()
  {
    return myCache;
  }
}
