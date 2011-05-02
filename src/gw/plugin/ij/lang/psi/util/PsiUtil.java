package gw.plugin.ij.lang.psi.util;

import com.intellij.psi.CommonClassNames;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiModifierListOwner;
import com.intellij.psi.PsiSubstitutor;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeParameter;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import gw.plugin.ij.GosuFileTypes;
import gw.plugin.ij.lang.psi.GosuFileBase;
import gw.plugin.ij.lang.psi.api.expressions.IGosuExpression;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuTypeDefinition;
import gw.plugin.ij.lang.psi.api.types.GosuCodeReferenceElement;
import org.jetbrains.annotations.Nullable;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class PsiUtil
{
//  public static final Key<JavaIdentifier> NAME_IDENTIFIER = new Key<JavaIdentifier>("Java Identifier");

  private PsiUtil()
  {
  }

  public static boolean isAccessible( PsiElement place, PsiMember member )
  {
    if( !member.isPhysical() )
    {
      return true;
    }

//    if( place instanceof IGosuReferenceExpression && ((IGosuReferenceExpression)place).getQualifierExpression() == null )
//    {
//      if( member.getContainingClass() instanceof GroovyScriptClass )
//      { //calling toplevel script members from the same script file
//        return true;
//      }
//    }
    return com.intellij.psi.util.PsiUtil.isAccessible( member, place, null );
  }

  @Nullable
  public static String getQualifiedReferenceText( GosuCodeReferenceElement referenceElement )
  {
    StringBuilder builder = new StringBuilder();
    if( !appendName( referenceElement, builder ) )
    {
      return null;
    }

    return builder.toString();
  }

  private static boolean appendName( GosuCodeReferenceElement referenceElement, StringBuilder builder )
  {
    String refName = referenceElement.getReferenceName();
    if( refName == null )
    {
      return false;
    }
    GosuCodeReferenceElement qualifier = referenceElement.getQualifier();
    if( qualifier != null )
    {
      appendName( qualifier, builder );
      builder.append( "." );
    }

    builder.append( refName );
    return true;
  }

  @Nullable
  public static PsiClass getJavaLangClass( PsiElement resolved, GlobalSearchScope scope )
  {
    return JavaPsiFacade.getInstance( resolved.getProject() ).findClass( CommonClassNames.JAVA_LANG_CLASS, scope );
  }

  @Nullable
  public static PsiClass getContextClass( PsiElement context )
  {
    while( context != null )
    {
      if( context instanceof GosuTypeDefinition )
      {
        return (PsiClass)context;
      }
      else if( context instanceof GosuFileBase )
      {
        return ((GosuFileBase)context).getPsiClass();
      }

      context = context.getContext();
    }
    return null;
  }

  public static boolean isRawType( PsiType type, PsiSubstitutor substitutor )
  {
    if( type instanceof PsiClassType )
    {
      final PsiClass returnClass = ((PsiClassType)type).resolve();
      if( returnClass instanceof PsiTypeParameter )
      {
        final PsiTypeParameter typeParameter = (PsiTypeParameter)returnClass;
        return substitutor.substitute( typeParameter ) == null;
      }
    }
    return false;
  }

  public static boolean isStaticsOK( PsiModifierListOwner owner, PsiElement place )
  {
    return isStaticsOK( owner, place, owner );
  }

  public static boolean isStaticsOK( PsiModifierListOwner owner, PsiElement place, PsiElement resolveContext )
  {
    throw new UnsupportedOperationException( "Men at work" );
//    if( owner instanceof PsiMember )
//    {
//      if( place instanceof IGosuReferenceExpression )
//      {
//        IGosuExpression qualifier = ((IGosuReferenceExpression)place).getQualifierExpression();
//        if( qualifier != null )
//        {
//          PsiClass containingClass = ((PsiMember)owner).getContainingClass();
//          if( qualifier instanceof IGosuReferenceExpression )
//          {
//            //## todo: Gosu does not use .class
//            if( "class".equals( ((IGosuReferenceExpression)qualifier).getReferenceName() ) )
//            {
//              //invoke static members of class from A.foo()
//              final PsiType type = qualifier.getType();
//              if( type instanceof PsiClassType )
//              {
//                final PsiClass psiClass = ((PsiClassType)type).resolve();
//                if( psiClass != null && CommonClassNames.JAVA_LANG_CLASS.equals( psiClass.getQualifiedName() ) )
//                {
//                  final PsiType[] params = ((PsiClassType)type).getParameters();
//                  if( params.length == 1 && params[0] instanceof PsiClassType )
//                  {
//                    if( place.getManager().areElementsEquivalent( containingClass, ((PsiClassType)params[0]).resolve() ) )
//                    {
//                      return owner.hasModifierProperty( IGosuModifier.STATIC );
//                    }
//                  }
//                }
//              }
//
//            }
//            PsiElement qualifierResolved = ((IGosuReferenceExpression)qualifier).resolve();
//            if( qualifierResolved instanceof PsiClass || qualifierResolved instanceof PsiPackage )
//            { //static context
//              if( owner instanceof PsiClass )
//              {
//                return true;
//              }
//
//              //non-physical method, e.g. gdk
//              if( containingClass == null )
//              {
//                return true;
//              }
//
//              if( owner.hasModifierProperty( PsiModifier.STATIC ) )
//              {
//                return true;
//              }
//
//              //members from java.lang.Class can be invoked without ".class"
//              final String qname = containingClass.getQualifiedName();
//              if( qname != null && qname.startsWith( "java." ) )
//              {
//                if( CommonClassNames.JAVA_LANG_OBJECT.equals( qname ) || CommonClassNames.JAVA_LANG_CLASS.equals( qname ) )
//                {
//                  return true;
//                }
//
//                if( containingClass.isInterface() )
//                {
//                  PsiClass javaLangClass =
//                    JavaPsiFacade.getInstance( place.getProject() ).findClass( CommonClassNames.JAVA_LANG_CLASS, place.getResolveScope() );
//                  if( javaLangClass != null && javaLangClass.isInheritor( containingClass, true ) )
//                  {
//                    return true;
//                  }
//                }
//              }
//
//              return false;
//            }
//          }
//
//          else if (qualifier instanceof GosuThisReferenceExpression && ((GosuThisReferenceExpression)qualifier).getQualifier() == null) {
//            //static members may be invoked from this.<...>
//            final boolean isInStatic = isThisReferenceInStaticContext((GosuThisReferenceExpression)qualifier);
//            if (containingClass != null && CommonClassNames.JAVA_LANG_CLASS.equals(containingClass.getQualifiedName())) {
//              return !(owner.hasModifierProperty(IGosuModifier.STATIC) && !CodeInsightSettings.getInstance().SHOW_STATIC_AFTER_INSTANCE);
//            }
//            else if (isInStatic) return owner.hasModifierProperty(IGosuModifier.STATIC);
//          }
//
//          //instance context
//          if( owner instanceof PsiClass )
//          {
//            return false;
//          }
//          return !(owner.hasModifierProperty( PsiModifier.STATIC ) && !CodeInsightSettings.getInstance().SHOW_STATIC_AFTER_INSTANCE);
//        }
//        else
//        {
//          if( ((PsiMember)owner).getContainingClass() == null )
//          {
//            return true;
//          }
//          if( owner instanceof IGosuVariable && !(owner instanceof IGosuField) )
//          {
//            return true;
//          }
//          if( owner.hasModifierProperty( IGosuModifier.STATIC ) )
//          {
//            return true;
//          }
//
//          PsiElement stopAt = resolveContext != null ? PsiTreeUtil.findCommonParent( place, resolveContext ) : null;
//          while( place != null && place != stopAt && !(place instanceof IGosuMember) )
//          {
//            if( place instanceof PsiFile )
//            {
//              break;
//            }
//            place = place.getParent();
//          }
//          if( place == null || place instanceof PsiFile || place == stopAt )
//          {
//            return true;
//          }
//          if( place instanceof GosuTypeDefinition )
//          {
//            return !(((GosuTypeDefinition)place).hasModifierProperty( IGosuModifier.STATIC ) ||
//                     ((GosuTypeDefinition)place).getContainingClass() == null);
//          }
//          return !((IGosuMember)place).hasModifierProperty( IGosuModifier.STATIC );
//        }
//      }
//    }
//    return true;
  }

  @Nullable
  public static PsiType[] getArgumentTypes( PsiElement place, boolean nullAsBottom )
  {
    return getArgumentTypes( place, nullAsBottom, null );
  }

  @Nullable
  public static PsiType[] getArgumentTypes( PsiElement place, boolean nullAsBottom, @Nullable IGosuExpression stopAt )
  {
    throw new UnsupportedOperationException( "Men at work" );
//    PsiElement parent = place.getParent();
//    if( parent instanceof GosuCall )
//    {
//      List<PsiType> result = new ArrayList<PsiType>();
//      GosuCall call = (GosuCall)parent;
//
//      IGosuExpression[] expressions = call.getExpressionArguments();
//      for( IGosuExpression expression : expressions )
//      {
//        PsiType type = expression.getType();
//        if( type == null )
//        {
//          result.add( nullAsBottom ? PsiType.NULL : TypesUtil.getJavaLangObject( call ) );
//        }
//        else
//        {
//          result.add( type );
//        }
//        if( stopAt == expression )
//        {
//          return result.toArray( new PsiType[result.size()] );
//        }
//      }
//
//      GosuClosableBlock[] closures = call.getClosureArguments();
//      for (GosuClosableBlock closure : closures) {
//        PsiType closureType = closure.getType();
//        if (closureType != null) {
//          result.add(closureType);
//        }
//        if (stopAt == closure) {
//          break;
//        }
//      }
//
//      return result.toArray( new PsiType[result.size()] );
//
//    }
//    else if (parent instanceof GosuAnonymousClassDefinition) {
//      final IGosuArgumentList argList = ((GosuAnonymousClassDefinition)parent).getArgumentListGosu();
//      List<PsiType> result = new ArrayList<PsiType>();
//
//      IGosuNamedArgument[] namedArgs = argList.getNamedArguments();
//      if (namedArgs.length > 0) {
//        result.add(createMapType(place.getResolveScope()));
//      }
//
//      IGosuExpression[] expressions = argList.getExpressionArguments();
//      for (IGosuExpression expression : expressions) {
//        PsiType type = expression.getType();
//        if (type == null) {
//          result.add(nullAsBottom ? PsiType.NULL : TypesUtil.getJavaLangObject(argList));
//        } else {
//          result.add(type);
//        }
//        if (stopAt == expression) {
//          break;
//        }
//      }
//
//      return result.toArray(new PsiType[result.size()]);
//    }
//
//    return null;
  }

  public static SearchScope restrictScopeToGosuFiles( SearchScope originalScope )
  {
    if( originalScope instanceof GlobalSearchScope )
    {
      return GlobalSearchScope.getScopeRestrictedByFileTypes( (GlobalSearchScope)originalScope, GosuFileTypes.TYPES.<com.intellij.openapi.fileTypes.FileType>toArray( new com.intellij.openapi.fileTypes.FileType[GosuFileTypes.TYPES.size()] ) );
    }
    return originalScope;
  }
}
