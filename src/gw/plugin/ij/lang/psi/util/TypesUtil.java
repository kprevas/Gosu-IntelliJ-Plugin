package gw.plugin.ij.lang.psi.util;

import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIntersectionType;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiSubstitutor;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeParameter;
import com.intellij.psi.impl.PsiSubstitutorImpl;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.TypeConversionUtil;
import com.intellij.util.containers.HashMap;
import gnu.trove.THashMap;
import gnu.trove.TIntObjectHashMap;
import gw.plugin.ij.lang.psi.IGosuPsiElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static com.intellij.psi.CommonClassNames.*;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class TypesUtil
{
  @NonNls
  public static final Map<String, PsiType> ourQNameToUnboxed = new HashMap<String, PsiType>();

  private TypesUtil()
  {
  }

//  @Nullable
//  private static PsiType getLeastUpperBoundForNumericType( @NotNull PsiType lType, @NotNull PsiType rType, Project project )
//  {
//    String lCanonical = lType.getCanonicalText();
//    String rCanonical = rType.getCanonicalText();
//    if( TYPE_TO_RANK.containsKey( lCanonical ) && TYPE_TO_RANK.containsKey( rCanonical ) )
//    {
//      int lRank = TYPE_TO_RANK.get( lCanonical );
//      int rRank = TYPE_TO_RANK.get( rCanonical );
//      int resultRank = Math.max( lRank, rRank );
//      String qName = RANK_TO_TYPE.get( resultRank );
//      if( qName == null )
//      {
//        return null;
//      }
//      assert lType instanceof PsiClassType;
//      assert rType instanceof PsiClassType;
//      //lType.getResolveScope()!=null && rType.getResolveScope()!=null
//      return JavaPsiFacade.getInstance( project ).getElementFactory().createTypeByFQClassName( qName, lType.getResolveScope().intersectWith(
//        rType.getResolveScope() ) );
//    }
//    return null;
//  }

  private static final Map<IElementType, String> ourPrimitiveTypesToClassNames = new HashMap<IElementType, String>();
  private static final String NULL = "null";
  private static final String JAVA_MATH_BIG_DECIMAL = "java.math.BigDecimal";
  private static final String JAVA_MATH_BIG_INTEGER = "java.math.BigInteger";

  static
  {
    ourQNameToUnboxed.put( JAVA_LANG_BOOLEAN, PsiType.BOOLEAN );
    ourQNameToUnboxed.put( JAVA_LANG_BYTE, PsiType.BYTE );
    ourQNameToUnboxed.put( JAVA_LANG_CHARACTER, PsiType.CHAR );
    ourQNameToUnboxed.put( JAVA_LANG_SHORT, PsiType.SHORT );
    ourQNameToUnboxed.put( JAVA_LANG_INTEGER, PsiType.INT );
    ourQNameToUnboxed.put( JAVA_LANG_LONG, PsiType.LONG );
    ourQNameToUnboxed.put( JAVA_LANG_FLOAT, PsiType.FLOAT );
    ourQNameToUnboxed.put( JAVA_LANG_DOUBLE, PsiType.DOUBLE );
  }


  private static final TIntObjectHashMap<String> RANK_TO_TYPE = new TIntObjectHashMap<String>();

  static
  {
    RANK_TO_TYPE.put( 1, JAVA_LANG_INTEGER );
    RANK_TO_TYPE.put( 2, JAVA_LANG_INTEGER );
    RANK_TO_TYPE.put( 3, JAVA_LANG_INTEGER );
    RANK_TO_TYPE.put( 4, JAVA_LANG_LONG );
    RANK_TO_TYPE.put( 5, JAVA_MATH_BIG_INTEGER );
    RANK_TO_TYPE.put( 6, JAVA_MATH_BIG_DECIMAL );
    RANK_TO_TYPE.put( 7, JAVA_LANG_DOUBLE );
    RANK_TO_TYPE.put( 8, JAVA_LANG_DOUBLE );
    RANK_TO_TYPE.put( 9, JAVA_LANG_NUMBER );
  }

  public static boolean isAssignable( PsiType lType, PsiType rType, PsiManager manager, GlobalSearchScope scope )
  {
    return isAssignable( lType, rType, manager, scope, true );
  }

  public static boolean isAssignable( PsiType lType, PsiType rType, PsiManager manager, GlobalSearchScope scope, boolean allowConversion )
  {
    if( allowConversion && isAssignableByMethodCallConversion( lType, rType, manager, scope ) )
    {
      return true;
    }

    return _isAssignable( lType, rType, manager, scope, allowConversion );
  }

  public static boolean isAssignable( PsiType lType, PsiType rType, IGosuPsiElement context )
  {
    return isAssignable( lType, rType, context, true );
  }

  public static boolean isAssignable( PsiType lType, PsiType rType, IGosuPsiElement context, boolean allowConversion )
  {
    if( rType instanceof PsiIntersectionType )
    {
      for( PsiType child : ((PsiIntersectionType)rType).getConjuncts() )
      {
        if( isAssignable( lType, child, context, allowConversion ) )
        {
          return true;
        }
      }
      return false;
    }
    if( lType instanceof PsiIntersectionType )
    {
      for( PsiType child : ((PsiIntersectionType)lType).getConjuncts() )
      {
        if( !isAssignable( child, rType, context, allowConversion ) )
        {
          return false;
        }
      }
      return true;
    }

    //## todo:
//    if( allowConversion && lType != null && rType != null )
//    {
//      for( GosuTypeConverter converter : GosuTypeConverter.EP_NAME.getExtensions() )
//      {
//        final Boolean result = converter.isConvertible( lType, rType, context );
//        if( result != null )
//        {
//          return result;
//        }
//      }
//    }
    return (allowConversion && isAssignableByMethodCallConversion( lType, rType, context )) ||
           _isAssignable( lType, rType, context.getManager(), context.getResolveScope(), true );
  }

  private static boolean _isAssignable( PsiType lType, PsiType rType, PsiManager manager, GlobalSearchScope scope, boolean allowConversion )
  {
    if( lType == null || rType == null )
    {
      return false;
    }

    if( allowConversion )
    {
      //all numeric types are assignable
      //## todo:
//      if( isNumericType( lType ) )
//      {
//        return isNumericType( rType ) || rType.equals( PsiType.NULL );
//      }
      if( typeEqualsToText( lType, JAVA_LANG_STRING ) )
      {
        return true;
      }
    }

    rType = boxPrimitiveType( rType, manager, scope );
    lType = boxPrimitiveType( lType, manager, scope );

    return lType.isAssignableFrom( rType );
  }

  public static boolean isAssignableByMethodCallConversion( PsiType lType, PsiType rType, IGosuPsiElement context )
  {
    if( lType == null || rType == null )
    {
      return false;
    }

    if( isAssignableByMethodCallConversion( lType, rType, context.getManager(), context.getResolveScope() ) )
    {
      return true;
    }

    //## todo:
//    for( GosuTypeConverter converter : GosuTypeConverter.EP_NAME.getExtensions() )
//    {
//      final Boolean result = converter.isConvertible( lType, rType, context );
//      if( result != null )
//      {
//        return result;
//      }
//    }

    return false;
  }

  public static boolean isAssignableByMethodCallConversion( PsiType lType, PsiType rType, PsiManager manager, GlobalSearchScope scope )
  {
    if( lType == null || rType == null )
    {
      return false;
    }

    //## todo:
//    if( typeEqualsToText( rType, GosuStringUtil.GROOVY_LANG_GSTRING ) )
//    {
//      final PsiClass javaLangString = JavaPsiFacade.getInstance( manager.getProject() ).findClass( JAVA_LANG_STRING, scope );
//      if( javaLangString != null &&
//          isAssignable( lType, JavaPsiFacade.getElementFactory( manager.getProject() ).createType( javaLangString ), manager, scope ) )
//      {
//        return true;
//      }
//    }

    //## todo:
//    if( isNumericType( lType ) && isNumericType( rType ) )
//    {
//      lType = unboxPrimitiveTypeWrapper( lType );
//      if( typeEqualsToText( lType, JAVA_MATH_BIG_DECIMAL ) )
//      {
//        lType = PsiType.DOUBLE;
//      }
//      rType = unboxPrimitiveTypeWrapper( rType );
//      if( typeEqualsToText( rType, JAVA_MATH_BIG_DECIMAL ) )
//      {
//        rType = PsiType.DOUBLE;
//      }
//    }
//    else
//    {
      rType = boxPrimitiveType( rType, manager, scope );
      lType = boxPrimitiveType( lType, manager, scope );
//    }

    return TypeConversionUtil.isAssignable( lType, rType );

  }

//  public static boolean isNumericType( PsiType type )
//  {
//    if( type instanceof PsiClassType )
//    {
//      return TYPE_TO_RANK.contains( type.getCanonicalText() );
//    }
//
//    return type instanceof PsiPrimitiveType && TypeConversionUtil.isNumericType( type );
//  }

  public static PsiType unboxPrimitiveTypeWraperAndEraseGenerics( PsiType result )
  {
    return TypeConversionUtil.erasure( unboxPrimitiveTypeWrapper( result ) );
  }

  public static PsiType unboxPrimitiveTypeWrapper( PsiType type )
  {
    if( type instanceof PsiClassType )
    {
      PsiType unboxed = ourQNameToUnboxed.get( type.getCanonicalText() );
      if( unboxed != null )
      {
        type = unboxed;
      }
    }
    return type;
  }

  public static PsiType boxPrimitiveType( PsiType result, PsiManager manager, GlobalSearchScope resolveScope )
  {
    if( result instanceof PsiPrimitiveType && result != PsiType.VOID )
    {
      PsiPrimitiveType primitive = (PsiPrimitiveType)result;
      String boxedTypeName = primitive.getBoxedTypeName();
      if( boxedTypeName != null )
      {
        return JavaPsiFacade.getInstance( manager.getProject() ).getElementFactory().createTypeByFQClassName( boxedTypeName, resolveScope );
      }
    }

    return result;
  }

//  @Nullable
//  public static PsiType getTypeForIncOrDecExpression( GosuUnaryExpression expr )
//  {
//    final IGosuExpression op = expr.getOperand();
//    if( op != null )
//    {
//      final PsiType opType = op.getType();
//      if( opType != null )
//      {
//        final PsiType overloaded = getOverloadedOperatorType( opType, expr.getOperationTokenType(), expr, PsiType.EMPTY_ARRAY );
//        if( overloaded != null )
//        {
//          return overloaded;
//        }
//        if( isNumericType( opType ) )
//        {
//          return opType;
//        }
//      }
//    }
//
//    return null;
//  }

  public static PsiClassType createType( String fqName, PsiElement context )
  {
    JavaPsiFacade facade = JavaPsiFacade.getInstance( context.getProject() );
    return facade.getElementFactory().createTypeByFQClassName( fqName, context.getResolveScope() );
  }

  public static PsiClassType getJavaLangObject( PsiElement context )
  {
    return PsiType.getJavaLangObject( context.getManager(), context.getResolveScope() );
  }

//  @Nullable
//  public static PsiType getLeastUpperBoundNullable( @Nullable PsiType type1, @Nullable PsiType type2, PsiManager manager )
//  {
//    if( type1 == null )
//    {
//      return type2;
//    }
//    if( type2 == null )
//    {
//      return type1;
//    }
//    if( type1.isAssignableFrom( type2 ) )
//    {
//      return type1;
//    }
//    if( type2.isAssignableFrom( type1 ) )
//    {
//      return type2;
//    }
//    return getLeastUpperBound( type1, type2, manager );
//  }

//  @Nullable
//  public static PsiType getLeastUpperBound( @NotNull PsiType type1, @NotNull PsiType type2, PsiManager manager )
//  {
//    if( type1 instanceof GosuTupleType && type2 instanceof GosuTupleType )
//    {
//      GosuTupleType tuple1 = (GosuTupleType)type1;
//      GosuTupleType tuple2 = (GosuTupleType)type2;
//      PsiType[] components1 = tuple1.getComponentTypes();
//      PsiType[] components2 = tuple2.getComponentTypes();
//      PsiType[] components3 = new PsiType[Math.min( components1.length, components2.length )];
//      for( int i = 0; i < components3.length; i++ )
//      {
//        PsiType c1 = components1[i];
//        PsiType c2 = components2[i];
//        if( c1 == null || c2 == null )
//        {
//          components3[i] = null;
//        }
//        else
//        {
//          components3[i] = getLeastUpperBound( c1, c2, manager );
//        }
//      }
//      return new GosuTupleType( components3, JavaPsiFacade.getInstance( manager.getProject() ),
//                              tuple1.getScope().intersectWith( tuple2.getResolveScope() ) );
//    }
//    else if( type1 instanceof GosuClosureType && type2 instanceof GosuClosureType )
//    {
//      GosuClosureType clType1 = (GosuClosureType)type1;
//      GosuClosureType clType2 = (GosuClosureType)type2;
//      GosuClosureSignature signature1 = clType1.getSignature();
//      GosuClosureSignature signature2 = clType2.getSignature();
//
//      GosuClosureParameter[] parameters1 = signature1.getParameters();
//      GosuClosureParameter[] parameters2 = signature2.getParameters();
//
//      if( parameters1.length == parameters2.length )
//      {
//        final GosuClosureSignature signature = GosuClosureSignatureImpl.getLeastUpperBound( signature1, signature2, manager );
//        if( signature != null )
//        {
//          GlobalSearchScope scope = clType1.getResolveScope().intersectWith( clType2.getResolveScope() );
//          final LanguageLevel languageLevel = ComparatorUtil.max( clType1.getLanguageLevel(), clType2.getLanguageLevel() );
//          return GosuClosureType.create( signature, manager, scope, languageLevel );
//        }
//      }
//    }
//    else if( GosuStringUtil.GROOVY_LANG_GSTRING.equals( type1.getCanonicalText() ) &&
//             CommonClassNames.JAVA_LANG_STRING.equals( type2.getInternalCanonicalText() ) )
//    {
//      return type2;
//    }
//    else if( GosuStringUtil.GROOVY_LANG_GSTRING.equals( type2.getCanonicalText() ) &&
//             CommonClassNames.JAVA_LANG_STRING.equals( type1.getInternalCanonicalText() ) )
//    {
//      return type1;
//    }
//    final PsiType result = getLeastUpperBoundForNumericType( type1, type2, manager.getProject() );
//    if( result != null )
//    {
//      return result;
//    }
//    return GenericsUtil.getLeastUpperBound( type1, type2, manager );
//  }

  @Nullable
  public static PsiType getPsiType( PsiElement context, IElementType elemType )
  {
    //## todo:
//    if( elemType == kNULL )
//    {
//      return PsiType.NULL;
//    }
    final String typeName = getPsiTypeName( elemType );
    if( typeName != null )
    {
      return JavaPsiFacade.getElementFactory( context.getProject() ).createTypeByFQClassName( typeName, context.getResolveScope() );
    }
    return null;
  }

  @Nullable
  public static String getPsiTypeName( IElementType elemType )
  {
    return ourPrimitiveTypesToClassNames.get( elemType );
  }


//  @NotNull
//  public static PsiType getLeastUpperBound( PsiClass[] classes, PsiManager manager )
//  {
//    PsiElementFactory factory = JavaPsiFacade.getElementFactory( manager.getProject() );
//
//    if( classes.length == 0 )
//    {
//      return factory.createTypeByFQClassName( JAVA_LANG_OBJECT );
//    }
//
//    PsiType type = factory.createType( classes[0] );
//
//    for( int i = 1; i < classes.length; i++ )
//    {
//      PsiType t = getLeastUpperBound( type, factory.createType( classes[i] ), manager );
//      if( t != null )
//      {
//        type = t;
//      }
//    }
//
//    return type;
//  }


  public static boolean typeEqualsToText( @NotNull PsiType type, @NotNull String text )
  {
    return text.endsWith( type.getPresentableText() ) && text.equals( type.getCanonicalText() );
  }

  public static PsiSubstitutor composeSubstitutors( PsiSubstitutor s1, PsiSubstitutor s2 )
  {
    final Map<PsiTypeParameter, PsiType> map = s1.getSubstitutionMap();
    Map<PsiTypeParameter, PsiType> result = new THashMap<PsiTypeParameter, PsiType>( map.size() );
    for( PsiTypeParameter parameter : map.keySet() )
    {
      result.put( parameter, s2.substitute( map.get( parameter ) ) );
    }
    return PsiSubstitutorImpl.createSubstitutor( result );
  }
}
