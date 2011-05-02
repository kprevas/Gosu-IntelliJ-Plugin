package gw.plugin.ij.lang.psi.util;

import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.CommonClassNames;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiSubstitutor;
import com.intellij.psi.ResolveState;
import com.intellij.psi.impl.PsiClassImplUtil;
import com.intellij.psi.infos.CandidateInfo;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.MethodSignature;
import com.intellij.psi.util.PsiModificationTracker;
import com.intellij.psi.util.TypeConversionUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import gw.plugin.ij.lang.psi.api.statements.IGosuField;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuReferenceList;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuTypeDefinition;
import gw.plugin.ij.lang.psi.impl.PsiImplUtil;
import gw.plugin.ij.lang.psi.impl.statements.typedef.GosuTypeDefinitionImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Maxim.Medvedev
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuClassImplUtil
{
  private static final Condition<PsiClassType> IS_GOSU_OBJECT = new Condition<PsiClassType>()
  {
    public boolean value( PsiClassType psiClassType )
    {
      return TypesUtil.typeEqualsToText( psiClassType, GosuTypeDefinition.DEFAULT_BASE_CLASS_NAME );
    }
  };

  public static final String GOSU_OBJECT_SUPPORT = "gosu.lang.GosuObjectSupport";
  public static final String SYNTHETIC_METHOD_IMPLEMENTATION = "GosuSyntheticMethodImplementation";

  private GosuClassImplUtil()
  {
  }


  @Nullable
  public static PsiClass getSuperClass( GosuTypeDefinition grType )
  {
    final PsiClassType[] extendsList = grType.getExtendsListTypes();
    if( extendsList.length == 0 )
    {
      return getBaseClass( grType );
    }
    final PsiClass superClass = extendsList[0].resolve();
    return superClass != null ? superClass : getBaseClass( grType );
  }

  @Nullable
  public static PsiClass getBaseClass( GosuTypeDefinition grType )
  {
    if( grType.isEnum() )
    {
      return JavaPsiFacade.getInstance( grType.getProject() ).findClass( CommonClassNames.JAVA_LANG_ENUM, grType.getResolveScope() );
    }
    else
    {
      return JavaPsiFacade.getInstance( grType.getProject() ).findClass( CommonClassNames.JAVA_LANG_OBJECT, grType.getResolveScope() );
    }
  }

  private static final Key<CachedValue<Boolean>> HAS_GOSU_OBJECT_METHODS = Key.create( "has gosu object methods" );

  @NotNull
  public static PsiClassType[] getExtendsListTypes( GosuTypeDefinition grType )
  {
    final PsiClassType[] extendsTypes = getReferenceListTypes( grType.getExtendsClause() );
    if( grType.isInterface() /*|| extendsTypes.length > 0*/ )
    {
      return extendsTypes;
    }
    for( PsiClassType type : extendsTypes )
    {
      final PsiClass superClass = type.resolve();
      if( superClass instanceof GosuTypeDefinition && !superClass.isInterface() )
      {
        return extendsTypes;
      }
    }

    PsiClass grObSupport = JavaPsiFacade.getInstance( grType.getProject() ).findClass( GOSU_OBJECT_SUPPORT, grType.getResolveScope() );
    if( grObSupport != null )
    {
      return ArrayUtil.append( extendsTypes, JavaPsiFacade.getInstance( grType.getProject() ).getElementFactory().createType( grObSupport ) );
    }
    return extendsTypes;
  }

  private static boolean hasGosuObjectSupportInner( final PsiClass psiClass,
                                                    Set<PsiClass> visited,
                                                    PsiClass gosuObjSupport,
                                                    PsiManager manager )
  {
    final CachedValue<Boolean> userData = psiClass.getUserData( HAS_GOSU_OBJECT_METHODS );
    if( userData != null && userData.getValue() != null )
    {
      return userData.getValue();
    }

    if( manager.areElementsEquivalent( gosuObjSupport, psiClass ) )
    {
      return true;
    }

    final PsiClassType[] supers;
    if( psiClass instanceof GosuTypeDefinition )
    {
      supers = getReferenceListTypes( ((GosuTypeDefinition)psiClass).getExtendsClause() );
    }
    else
    {
      supers = psiClass.getExtendsListTypes();
    }

    boolean result = false;
    for( PsiClassType superType : supers )
    {
      PsiClass aSuper = superType.resolve();
      if( aSuper == null || visited.contains( aSuper ) )
      {
        continue;
      }
      visited.add( aSuper );
      if( hasGosuObjectSupportInner( aSuper, visited, gosuObjSupport, manager ) )
      {
        result = true;
        break;
      }
    }
    final boolean finalResult = result;
    psiClass.putUserData( HAS_GOSU_OBJECT_METHODS,
                          CachedValuesManager.getManager( manager.getProject() ).createCachedValue( new CachedValueProvider<Boolean>()
                          {
                            public Result<Boolean> compute()
                            {
                              return Result.create( finalResult, PsiModificationTracker.JAVA_STRUCTURE_MODIFICATION_COUNT );
                            }
                          }, false ) );
    return finalResult;
  }

  @NotNull
  public static PsiClassType[] getImplementsListTypes( GosuTypeDefinition grType )
  {
    final PsiClassType[] implementsTypes = getReferenceListTypes( grType.getImplementsClause() );
    if( !grType.isInterface() &&
        !ContainerUtil.or( implementsTypes, IS_GOSU_OBJECT ) &&
        !ContainerUtil.or( getReferenceListTypes( grType.getExtendsClause() ), IS_GOSU_OBJECT ) )
    {
      return ArrayUtil.append( implementsTypes, getGosuObjectType( grType ) );
    }
    return implementsTypes;
  }

  private static PsiClassType getGosuObjectType( GosuTypeDefinition grType )
  {
    return JavaPsiFacade.getInstance( grType.getProject() ).getElementFactory()
      .createTypeByFQClassName( GosuTypeDefinition.DEFAULT_BASE_CLASS_NAME, grType.getResolveScope() );
  }

  @NotNull
  public static PsiClassType[] getSuperTypes( GosuTypeDefinition grType )
  {
    PsiClassType[] extendsList = grType.getExtendsListTypes();
    if( extendsList.length == 0 )
    {
      extendsList = new PsiClassType[]{createBaseClassType( grType )};
    }

    return ArrayUtil.mergeArrays( extendsList, grType.getImplementsListTypes(), PsiClassType.class );
  }

  public static PsiClassType createBaseClassType( GosuTypeDefinition grType )
  {
    if( grType.isEnum() )
    {
      return JavaPsiFacade.getInstance( grType.getProject() ).getElementFactory()
        .createTypeByFQClassName( CommonClassNames.JAVA_LANG_ENUM, grType.getResolveScope() );
    }
    else
    {
      return JavaPsiFacade.getInstance( grType.getProject() ).getElementFactory()
        .createTypeByFQClassName( CommonClassNames.JAVA_LANG_OBJECT, grType.getResolveScope() );
    }
  }

  @NotNull
  public static PsiMethod[] getAllMethods( GosuTypeDefinition grType )
  {
    List<PsiMethod> allMethods = new ArrayList<PsiMethod>();
    getAllMethodsInner( grType, allMethods, new HashSet<PsiClass>() );

    return allMethods.toArray( new PsiMethod[allMethods.size()] );
  }

  private static void getAllMethodsInner( PsiClass clazz, List<PsiMethod> allMethods, HashSet<PsiClass> visited )
  {
    if( visited.contains( clazz ) )
    {
      return;
    }
    visited.add( clazz );

    ContainerUtil.addAll( allMethods, clazz.getMethods() );

    final PsiField[] fields = clazz.getFields();
    for( PsiField field : fields )
    {
      if( field instanceof IGosuField )
      {
        final IGosuField gosuField = (IGosuField)field;
        //## todo:
//        if( gosuField.isProperty() )
//        {
//          PsiMethod[] getters = gosuField.getGetters();
//          if( getters.length > 0 )
//          {
//            ContainerUtil.addAll( allMethods, getters );
//          }
//          PsiMethod setter = gosuField.getSetter();
//          if( setter != null )
//          {
//            allMethods.add( setter );
//          }
//        }
      }
    }

    final PsiClass[] supers = clazz.getSupers();
    for( PsiClass aSuper : supers )
    {
      getAllMethodsInner( aSuper, allMethods, visited );
    }
  }

  private static PsiClassType[] getReferenceListTypes( @Nullable GosuReferenceList list )
  {
    if( list == null )
    {
      return PsiClassType.EMPTY_ARRAY;
    }
    return list.getReferenceTypes();
  }


  public static PsiClass[] getInterfaces( GosuTypeDefinition grType )
  {
    final PsiClassType[] implementsListTypes = grType.getImplementsListTypes();
    List<PsiClass> result = new ArrayList<PsiClass>( implementsListTypes.length );
    for( PsiClassType type : implementsListTypes )
    {
      final PsiClass psiClass = type.resolve();
      if( psiClass != null )
      {
        result.add( psiClass );
      }
    }
    return result.toArray( new PsiClass[result.size()] );
  }

  @NotNull
  public static PsiClass[] getSupers( GosuTypeDefinition grType )
  {
    PsiClassType[] superTypes = grType.getSuperTypes();
    List<PsiClass> result = new ArrayList<PsiClass>();
    for( PsiClassType superType : superTypes )
    {
      PsiClass superClass = superType.resolve();
      if( superClass != null )
      {
        result.add( superClass );
      }
    }

    return result.toArray( new PsiClass[result.size()] );
  }

  public static boolean processDeclarations( @NotNull GosuTypeDefinition grType,
                                             @NotNull PsiScopeProcessor processor,
                                             @NotNull ResolveState state,
                                             PsiElement lastParent,
                                             @NotNull PsiElement place )
  {
    //## todo:
    return true;
//    for( final PsiTypeParameter typeParameter : grType.getTypeParameters() )
//    {
//      if( !ResolveUtil.processElement( processor, typeParameter, state ) )
//      {
//        return false;
//      }
//    }
//
//    NameHint nameHint = processor.getHint( NameHint.KEY );
//    //todo [DIANA] look more carefully
//    String name = nameHint == null ? null : nameHint.getName( state );
//    ClassHint classHint = processor.getHint( ClassHint.KEY );
//    final PsiSubstitutor substitutor = state.get( PsiSubstitutor.KEY );
//    final PsiElementFactory factory = JavaPsiFacade.getElementFactory( place.getProject() );
//
//    if( classHint == null || classHint.shouldProcess( ClassHint.ResolveKind.PROPERTY ) )
//    {
//      Map<String, CandidateInfo> fieldsMap = CollectClassMembersUtil.getAllFields( grType );
//      if( name != null )
//      {
//        CandidateInfo fieldInfo = fieldsMap.get( name );
//        if( fieldInfo != null )
//        {
//          final PsiField field = (PsiField)fieldInfo.getElement();
//          if( !isSameDeclaration( place, field ) )
//          { //the same variable declaration
//            final PsiSubstitutor finalSubstitutor = PsiClassImplUtil
//              .obtainFinalSubstitutor( field.getContainingClass(), fieldInfo.getSubstitutor(), grType, substitutor, place, factory );
//            if( !processor.execute( field, state.put( PsiSubstitutor.KEY, finalSubstitutor ) ) )
//            {
//              return false;
//            }
//          }
//        }
//      }
//      else
//      {
//        for( CandidateInfo info : fieldsMap.values() )
//        {
//          final PsiField field = (PsiField)info.getElement();
//          if( !isSameDeclaration( place, field ) )
//          {  //the same variable declaration
//            final PsiSubstitutor finalSubstitutor = PsiClassImplUtil
//              .obtainFinalSubstitutor( field.getContainingClass(), info.getSubstitutor(), grType, substitutor, place, factory );
//            if( !processor.execute( field, state.put( PsiSubstitutor.KEY, finalSubstitutor ) ) )
//            {
//              return false;
//            }
//          }
//        }
//      }
//    }
//
//    if( classHint == null || classHint.shouldProcess( ClassHint.ResolveKind.METHOD ) )
//    {
//      Map<String, List<CandidateInfo>> methodsMap = CollectClassMembersUtil.getAllMethods( grType, true );
//      boolean isPlaceGosu = place.getLanguage() == GosuFileType.GOSU_FILE_TYPE.getLanguage();
//      if( name == null )
//      {
//        for( List<CandidateInfo> list : methodsMap.values() )
//        {
//          for( CandidateInfo info : list )
//          {
//            PsiMethod method = (PsiMethod)info.getElement();
//            if( !isSameDeclaration( place, method ) && isMethodVisible( isPlaceGosu, method ) )
//            {
//              final PsiSubstitutor finalSubstitutor = PsiClassImplUtil
//                .obtainFinalSubstitutor( method.getContainingClass(), info.getSubstitutor(), grType, substitutor, place, factory );
//              if( !processor.execute( method, state.put( PsiSubstitutor.KEY, finalSubstitutor ) ) )
//              {
//                return false;
//              }
//            }
//          }
//        }
//      }
//      else
//      {
//        List<CandidateInfo> byName = methodsMap.get( name );
//        if( byName != null )
//        {
//          for( CandidateInfo info : byName )
//          {
//            PsiMethod method = (PsiMethod)info.getElement();
//            if( !isSameDeclaration( place, method ) && isMethodVisible( isPlaceGosu, method ) )
//            {
//              final PsiSubstitutor finalSubstitutor = PsiClassImplUtil
//                .obtainFinalSubstitutor( method.getContainingClass(), info.getSubstitutor(), grType, substitutor, place, factory );
//              if( !processor.execute( method, state.put( PsiSubstitutor.KEY, finalSubstitutor ) ) )
//              {
//                return false;
//              }
//            }
//          }
//        }
//      }
//    }
//
//    if( !isSuperClassReferenceResolving( grType, lastParent ) )
//    {
//      if( classHint == null || classHint.shouldProcess( ClassHint.ResolveKind.CLASS ) )
//      {
//        for( CandidateInfo info : CollectClassMembersUtil.getAllInnerClasses( grType, false ).values() )
//        {
//          final PsiClass innerClass = (PsiClass)info.getElement();
//          assert innerClass != null;
//          final String innerClassName = innerClass.getName();
//          if( nameHint != null && !innerClassName.equals( nameHint.getName( state ) ) )
//          {
//            continue;
//          }
//
//          if( !processor.execute( innerClass, state ) )
//          {
//            return false;
//          }
//        }
//      }
//    }
//
//
//    return true;
  }

//  private static boolean isSuperClassReferenceResolving( GosuTypeDefinition grType, PsiElement lastParent )
//  {
//    return lastParent instanceof GosuReferenceList ||
//           grType.isAnonymous() && lastParent == ((GosuAnonymousClassDefinition)grType).getBaseClassReferenceGosu();
//  }


  private static boolean isSameDeclaration( PsiElement place, PsiElement element )
  {
    if( !(element instanceof IGosuField) )
    {
      return false;
    }
    while( place != null )
    {
      place = place.getParent();
      if( place == element )
      {
        return true;
      }
    }
    return false;
  }

  private static boolean isMethodVisible( boolean isPlaceGosu, PsiMethod method )
  {
    return isPlaceGosu;
  }

  @Nullable
  public static PsiMethod findMethodBySignature( GosuTypeDefinition grType, PsiMethod patternMethod, boolean checkBases )
  {
    final MethodSignature patternSignature = patternMethod.getSignature( PsiSubstitutor.EMPTY );
    for( PsiMethod method : findMethodsByName( grType, patternMethod.getName(), checkBases, false ) )
    {
      final PsiClass clazz = method.getContainingClass();
      if( clazz == null )
      {
        continue;
      }
      PsiSubstitutor superSubstitutor = TypeConversionUtil.getClassSubstitutor( clazz, grType, PsiSubstitutor.EMPTY );
      if( superSubstitutor == null )
      {
        continue;
      }
      final MethodSignature signature = method.getSignature( superSubstitutor );
      if( signature.equals( patternSignature ) )
      {
        return method;
      }
    }

    return null;
  }

  private static PsiMethod[] findMethodsByName( GosuTypeDefinition grType,
                                                String name,
                                                boolean checkBases,
                                                boolean includeSyntheticAccessors )
  {
    if( !checkBases )
    {
      List<PsiMethod> result = new ArrayList<PsiMethod>();
      for( PsiMethod method : includeSyntheticAccessors ? grType.getMethods() : grType.getMethods() )
      {
        if( name.equals( method.getName() ) )
        {
          result.add( method );
        }
      }

      return result.toArray( new PsiMethod[result.size()] );
    }

    Map<String, List<CandidateInfo>> methodsMap = CollectClassMembersUtil.getAllMethods( grType, includeSyntheticAccessors );
    return PsiImplUtil.mapToMethods( methodsMap.get( name ) );
  }

  @NotNull
  public static PsiMethod[] findMethodsBySignature( GosuTypeDefinition grType, PsiMethod patternMethod, boolean checkBases )
  {
    return findMethodsBySignature( grType, patternMethod, checkBases, true );
  }

  @NotNull
  public static PsiMethod[] findCodeMethodsBySignature( GosuTypeDefinition grType, PsiMethod patternMethod, boolean checkBases )
  {
    return findMethodsBySignature( grType, patternMethod, checkBases, false );
  }

  @NotNull
  public static PsiMethod[] findMethodsByName( GosuTypeDefinition grType, @NonNls String name, boolean checkBases )
  {
    return findMethodsByName( grType, name, checkBases, true );
  }

  private static PsiMethod[] findMethodsBySignature( GosuTypeDefinition grType,
                                                     PsiMethod patternMethod,
                                                     boolean checkBases,
                                                     boolean includeSynthetic )
  {
    ArrayList<PsiMethod> result = new ArrayList<PsiMethod>();
    final MethodSignature patternSignature = patternMethod.getSignature( PsiSubstitutor.EMPTY );
    for( PsiMethod method : findMethodsByName( grType, patternMethod.getName(), checkBases, includeSynthetic ) )
    {
      final PsiClass clazz = method.getContainingClass();
      if( clazz == null )
      {
        continue;
      }
      PsiSubstitutor superSubstitutor = TypeConversionUtil.getClassSubstitutor( clazz, grType, PsiSubstitutor.EMPTY );
      if( superSubstitutor == null )
      {
        continue;
      }

      final MethodSignature signature = method.getSignature( superSubstitutor );
      if( signature.equals( patternSignature ) ) //noinspection unchecked
      {
        result.add( method );
      }
    }
    return result.toArray( new PsiMethod[result.size()] );
  }

  @NotNull
  public static PsiMethod[] findCodeMethodsByName( GosuTypeDefinition grType, @NonNls String name, boolean checkBases )
  {
    return findMethodsByName( grType, name, checkBases, false );
  }

  @NotNull
  public static List<Pair<PsiMethod, PsiSubstitutor>> findMethodsAndTheirSubstitutorsByName( GosuTypeDefinition grType,
                                                                                             String name,
                                                                                             boolean checkBases )
  {
    final ArrayList<Pair<PsiMethod, PsiSubstitutor>> result = new ArrayList<Pair<PsiMethod, PsiSubstitutor>>();

    if( !checkBases )
    {
      final PsiMethod[] methods = grType.findMethodsByName( name, false );
      for( PsiMethod method : methods )
      {
        result.add( new Pair<PsiMethod, PsiSubstitutor>( method, PsiSubstitutor.EMPTY ) );
      }
    }
    else
    {
      final Map<String, List<CandidateInfo>> map = CollectClassMembersUtil.getAllMethods( grType, true );
      final List<CandidateInfo> candidateInfos = map.get( name );
      if( candidateInfos != null )
      {
        for( CandidateInfo info : candidateInfos )
        {
          final PsiElement element = info.getElement();
          result.add( new Pair<PsiMethod, PsiSubstitutor>( (PsiMethod)element, info.getSubstitutor() ) );
        }
      }
    }

    return result;
  }

  @NotNull
  public static List<Pair<PsiMethod, PsiSubstitutor>> getAllMethodsAndTheirSubstitutors( GosuTypeDefinition grType )
  {
    final Map<String, List<CandidateInfo>> allMethodsMap = CollectClassMembersUtil.getAllMethods( grType, true );
    List<Pair<PsiMethod, PsiSubstitutor>> result = new ArrayList<Pair<PsiMethod, PsiSubstitutor>>();
    for( List<CandidateInfo> infos : allMethodsMap.values() )
    {
      for( CandidateInfo info : infos )
      {
        result.add( new Pair<PsiMethod, PsiSubstitutor>( (PsiMethod)info.getElement(), info.getSubstitutor() ) );
      }
    }

    return result;
  }

  @Nullable
  public static PsiField findFieldByName( GosuTypeDefinition grType, String name, boolean checkBases )
  {
    if( !checkBases )
    {
      for( PsiField field : grType.getFields() )
      {
        if( name.equals( field.getName() ) )
        {
          return field;
        }
      }

      return null;
    }

    Map<String, CandidateInfo> fieldsMap = CollectClassMembersUtil.getAllFields( grType );
    final CandidateInfo info = fieldsMap.get( name );
    return info == null ? null : (PsiField)info.getElement();
  }

  public static PsiField[] getAllFields( GosuTypeDefinition grType )
  {
    Map<String, CandidateInfo> fieldsMap = CollectClassMembersUtil.getAllFields( grType );
    return ContainerUtil.map2Array( fieldsMap.values(), PsiField.class, new Function<CandidateInfo, PsiField>()
    {
      public PsiField fun( CandidateInfo entry )
      {
        return (PsiField)entry.getElement();
      }
    } );
  }

  public static boolean isClassEquivalentTo( GosuTypeDefinitionImpl definition, PsiElement another )
  {
    return PsiClassImplUtil.isClassEquivalentTo( definition, another );
  }
}
