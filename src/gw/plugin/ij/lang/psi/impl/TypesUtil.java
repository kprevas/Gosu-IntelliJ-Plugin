package gw.plugin.ij.lang.psi.impl;

import com.intellij.openapi.project.Project;
import com.intellij.pom.java.LanguageLevel;
import com.intellij.psi.CommonClassNames;
import com.intellij.psi.GenericsUtil;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiIntersectionType;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiSubstitutor;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeParameter;
import com.intellij.psi.ResolveState;
import com.intellij.psi.impl.PsiSubstitutorImpl;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.InheritanceUtil;
import com.intellij.psi.util.TypeConversionUtil;
import com.intellij.util.containers.ComparatorUtil;
import com.intellij.util.containers.HashMap;
import gnu.trove.THashMap;
import gnu.trove.TIntObjectHashMap;
import gnu.trove.TObjectIntHashMap;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static com.intellij.psi.CommonClassNames.*;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class TypesUtil
{

  public static PsiClassType createType(String fqName, PsiElement context) {
    JavaPsiFacade facade = JavaPsiFacade.getInstance(context.getProject());
    return facade.getElementFactory().createTypeByFQClassName(fqName, context.getResolveScope());
  }

  public static PsiClassType getJavaLangObject(PsiElement context) {
    return PsiType.getJavaLangObject(context.getManager(), context.getResolveScope());
  }

}
