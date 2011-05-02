package gw.plugin.ij.lang.psi.stubs;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.impl.file.impl.JavaFileManagerImpl;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.ArrayUtil;
import com.intellij.util.containers.CollectionFactory;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.HashSet;
import gw.plugin.ij.lang.psi.GosuFile;
import gw.plugin.ij.lang.psi.impl.search.GosuSourceFilterScope;
import gw.plugin.ij.lang.psi.stubs.index.GosuClassNameIndex;
import gw.plugin.ij.lang.psi.stubs.index.GosuFieldNameIndex;
import gw.plugin.ij.lang.psi.stubs.index.GosuFullClassNameIndex;
import gw.plugin.ij.lang.psi.stubs.index.GosuMethodNameIndex;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuShortNamesCache extends PsiShortNamesCache {
  private final Project myProject;

  public GosuShortNamesCache(Project project) {
    myProject = project;
  }

  @NotNull
  public PsiClass[] getClassesByName(@NotNull @NonNls String name, @NotNull GlobalSearchScope scope) {
    Collection<PsiClass> allClasses = getAllScriptClasses(name, scope);
    if (allClasses.isEmpty()) return PsiClass.EMPTY_ARRAY;
    return allClasses.toArray(new PsiClass[allClasses.size()]);
  }

  @NotNull
  public PsiClass[] getClassesByFQName(@NotNull @NonNls String name, @NotNull GlobalSearchScope scope) {
    final ArrayList<PsiClass> result = new ArrayList<PsiClass>();

    final Collection<? extends PsiElement> classes = StubIndex.getInstance().get(GosuFullClassNameIndex.KEY, name.hashCode(), myProject, new GosuSourceFilterScope(scope));
    if (!classes.isEmpty()) {
      //hashcode doesn't guarantee equals
      for (PsiElement psiClass : classes) {
        if (!JavaFileManagerImpl.notClass(psiClass) && name.equals(((PsiClass)psiClass).getQualifiedName())) {
          result.add((PsiClass)psiClass);
        }
      }
    }

    return result.isEmpty() ? PsiClass.EMPTY_ARRAY : result.toArray(new PsiClass[result.size()]);
  }

  private Collection<PsiClass> getAllScriptClasses(String shortName, GlobalSearchScope scope) {
    final ArrayList<PsiClass> result = CollectionFactory.arrayList();
    for (GosuFile file : StubIndex.getInstance().get( GosuClassNameIndex.KEY, shortName, myProject, new GosuSourceFilterScope(scope))) {
      ContainerUtil.addIfNotNull(file.getPsiClass(), result);
    }
    return result;
  }

  @NotNull
  public String[] getAllClassNames() {
    return ArrayUtil.toStringArray(StubIndex.getInstance().getAllKeys( GosuClassNameIndex.KEY, myProject));
  }


  public void getAllClassNames(@NotNull HashSet<String> dest) {
    dest.addAll(StubIndex.getInstance().getAllKeys( GosuClassNameIndex.KEY, myProject));
  }

  @NotNull
  public PsiMethod[] getMethodsByName(@NonNls @NotNull String name, @NotNull GlobalSearchScope scope) {
    final Collection<? extends PsiMethod> methods = StubIndex.getInstance().get(GosuMethodNameIndex.KEY, name, myProject, new GosuSourceFilterScope(scope));
    if (methods.isEmpty()) return PsiMethod.EMPTY_ARRAY;
    return methods.toArray(new PsiMethod[methods.size()]);
  }

  @NotNull
  public PsiMethod[] getMethodsByNameIfNotMoreThan(@NonNls @NotNull String name, @NotNull GlobalSearchScope scope, int maxCount) {
    return getMethodsByName(name, scope);
  }

  @NotNull
  public String[] getAllMethodNames() {
    Collection<String> keys = StubIndex.getInstance().getAllKeys(GosuMethodNameIndex.KEY, myProject);
    return ArrayUtil.toStringArray(keys);
  }

  public void getAllMethodNames(@NotNull HashSet<String> set) {
    set.addAll(StubIndex.getInstance().getAllKeys(GosuMethodNameIndex.KEY, myProject));
  }

  @NotNull
  public PsiField[] getFieldsByName(@NotNull @NonNls String name, @NotNull GlobalSearchScope scope) {
    final Collection<? extends PsiField> fields = StubIndex.getInstance().get(GosuFieldNameIndex.KEY, name, myProject, new GosuSourceFilterScope(scope));
    if (fields.isEmpty()) return PsiField.EMPTY_ARRAY;
    return fields.toArray(new PsiField[fields.size()]);
  }

  @NotNull
  public String[] getAllFieldNames() {
    Collection<String> fields = StubIndex.getInstance().getAllKeys(GosuFieldNameIndex.KEY, myProject);
    return ArrayUtil.toStringArray(fields);
  }

  public void getAllFieldNames(@NotNull HashSet<String> set) {
    set.addAll(StubIndex.getInstance().getAllKeys(GosuFieldNameIndex.KEY, myProject));
  }

}
